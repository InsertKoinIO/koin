// The docs promise that the Compiler Plugin fails the build on a mistake. A scenario must compile, so
// these cases break a generated copy of the code, then require the compilation to fail with the expected
// diagnostic. A green build here would mean the plugin no longer checks anything.
import { spawnSync } from 'node:child_process';
import { readdirSync, readFileSync, statSync, writeFileSync } from 'node:fs';
import { join } from 'node:path';

const root = new URL('..', import.meta.url).pathname;
const generated = join(root, 'snippet-tests/.generated');
const dropSingleton = (name) => new RegExp(`@Singleton\\s*\\n?\\s*((?:internal )?class ${name}\\b)`);

const cases = [
  { name: 'Quickstart', dir: 'with-plugin/quickstart', project: '', task: ':with-plugin:compileKotlin', remove: /.*single<UserRepository>\(\)\n/ },
  { name: 'Kotlin tutorial, DSL', dir: 'with-plugin/kotlin-tutorial-dsl', project: '', task: ':with-plugin:compileKotlin', remove: /.*single<UserRepositoryImpl>\(\) bind.*\n/ },
  { name: 'Kotlin tutorial, Annotations', dir: 'with-plugin-kotlin-annotations/kotlin-tutorial-annotations', project: '', task: ':with-plugin-kotlin-annotations:compileKotlin', remove: dropSingleton('UserRepositoryImpl'), keep: '$1' },
  { name: 'Ktor tutorial, DSL', dir: 'with-plugin/ktor-tutorial-dsl', project: '', task: ':with-plugin:compileKotlin', remove: /.*single<UserRepositoryImpl>\(\) bind.*\n/ },
  { name: 'Ktor tutorial, Annotations', dir: 'with-plugin-ktor-annotations/ktor-tutorial-annotations', project: '', task: ':with-plugin-ktor-annotations:compileKotlin', remove: dropSingleton('UserRepositoryImpl'), keep: '$1' },
  { name: 'Android tutorial, DSL', dir: 'android/android-tutorial-dsl', project: 'android', task: ':android-tutorial-dsl:compileDebugKotlin', remove: /.*single<UserRepositoryImpl>\(\) bind.*\n/ },
  { name: 'Android tutorial, Annotations', dir: 'android/android-tutorial-annotations', project: 'android', task: ':android-tutorial-annotations:compileDebugKotlin', remove: dropSingleton('UserRepositoryImpl'), keep: '$1' },
  { name: 'KMP walkthrough, DSL', dir: 'kmp/kmp-walkthrough-dsl', project: 'kmp', task: ':kmp-walkthrough-dsl:compileKotlinJvm', remove: /.*single<InMemoryMuseumStorage>\(\) bind.*\n/ },
  { name: 'KMP walkthrough, Annotations', dir: 'kmp/kmp-walkthrough-annotations', project: 'kmp', task: ':kmp-walkthrough-annotations:compileKotlinJvm', remove: dropSingleton('InMemoryMuseumStorage'), keep: '$1' },
  { name: 'Multi-module: :app does not load dataModule', dir: 'multimodule/dsl/app', project: 'multimodule', task: ':dsl:app:compileDebugKotlin', remove: /includes\(dataModule, /, keep: 'includes(' },
  { name: 'Multi-module: DataModule is not @Configuration', dir: 'multimodule/annotations/core-data', project: 'multimodule', task: ':annotations:app:compileDebugKotlin', remove: /@Configuration\nclass DataModule/, keep: 'class DataModule' },
];

const walk = (d) => readdirSync(d).flatMap((f) => {
  const p = join(d, f);
  return statSync(p).isDirectory() ? walk(p) : p.endsWith('.kt') ? [p] : [];
});

const gradle = (project, task) => spawnSync(join(root, '../projects/gradlew'), ['-p', join('snippet-tests', project), '--console=plain', task], { cwd: root, encoding: 'utf8' });

let failures = 0;
for (const { name, dir, project, task, remove, keep = '' } of cases) {
  const file = walk(join(generated, dir)).find((f) => remove.test(readFileSync(f, 'utf8')));
  if (!file) {
    console.log(`FAIL ${name}: the code to remove is gone, update this case`);
    failures++;
    continue;
  }
  const original = readFileSync(file, 'utf8');
  writeFileSync(file, original.replace(remove, keep));
  const { status, stdout, stderr } = gradle(project, task);
  writeFileSync(file, original);
  const reported = /\[Koin\]\[KOIN-D001\] Missing dependency/.test(stdout + stderr);
  if (status !== 0 && reported) {
    console.log(`PASS ${name}: KOIN-D001`);
  } else {
    console.log(`FAIL ${name}: expected a failed build with KOIN-D001, got exit code ${status}`);
    console.log((stdout + stderr).split('\n').filter((l) => l.startsWith('e:')).slice(0, 5).join('\n'));
    failures++;
  }
}
if (failures > 0) process.exit(1);
