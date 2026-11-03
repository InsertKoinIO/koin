// Extracts the Kotlin blocks marked with {/* check: scenario */} from the docs into
// snippet-tests/.generated, next to the fixture of each scenario, so that Gradle compiles
// and runs the code exactly as the pages show it. See snippet-tests/README.md.
import { cpSync, mkdirSync, readdirSync, readFileSync, rmSync, statSync, writeFileSync } from 'node:fs';
import { basename, dirname, join } from 'node:path';

const root = new URL('..', import.meta.url).pathname;
const docs = join(root, 'src/content/docs');
const tests = join(root, 'snippet-tests');
const generated = join(tests, '.generated');
const projects = readdirSync(join(tests, 'fixtures'));

const walk = (d) => readdirSync(d).flatMap((f) => {
  const p = join(d, f);
  return statSync(p).isDirectory() ? walk(p) : /\.mdx?$/.test(p) ? [p] : [];
});

const fail = (message) => {
  console.error(`check:snippets: ${message}`);
  process.exit(1);
};

// A fixture can pin its package with a first line such as: // package: com.example.sdk
const scenarios = new Map();
for (const project of projects) {
  const dir = join(tests, 'fixtures', project);
  for (const file of readdirSync(dir).filter((f) => f.endsWith('.kt'))) {
    const name = basename(file, '.kt');
    if (scenarios.has(name)) fail(`scenario "${name}" has a fixture in two projects`);
    const fixture = readFileSync(join(dir, file), 'utf8');
    const pkg = fixture.match(/^\/\/ package: ([\w.]+)/)?.[1] ?? `snippets.${name.replaceAll('-', '_')}`;
    if (!/^(internal )?fun check\(\)/m.test(fixture)) fail(`${file} has no check() function`);
    scenarios.set(name, { project, fixture, pkg, snippets: [] });
  }
}

// Multiplatform scenarios: fixtures-kmp/<scenario>/<sourceSet>/*.kt. A block without a package line
// goes to com.example.app. A first line such as "// androidMain, MuseumApp.kt" sets the source set
// and file name.
const kmpRoot = join(tests, 'fixtures-kmp');
const kmp = new Map(readdirSync(kmpRoot).map((name) => [name, []]));

// Android scenarios: fixtures-android/<scenario>/{main,test}, plus fixtures-android/_shared.
// Kotlin blocks go to the package org.koin.sample, the one the Annotations tab scans.
const androidRoot = join(tests, 'fixtures-android');
const android = new Map(readdirSync(androidRoot).filter((name) => !name.startsWith('_')).map((name) => [name, []]));

// Multi-module scenarios, one real Gradle module per folder of fixtures-multimodule/<style>. A block goes
// to the module named on its first line, such as: // Gradle module ":feature:login"
const multimoduleRoot = join(tests, 'fixtures-multimodule');
const multimodule = new Map(readdirSync(multimoduleRoot).map((style) => [`multimodule-${style}`, []]));

const marker = /\{\/\*\s*check:\s*([\w\s,-]+?)\s*\*\/\}\s*\n+\s*```kotlin[^\n]*\n([\s\S]*?)```/g;
let markers = 0;
for (const file of walk(docs)) {
  const text = readFileSync(file, 'utf8');
  const rel = file.replace(`${docs}/`, '');
  const found = [...text.matchAll(marker)];
  // A marker that is not directly followed by a Kotlin block would silently test nothing.
  if ((text.match(/\{\/\*\s*check:/g) ?? []).length !== found.length) fail(`${rel} has a check marker with no Kotlin block after it`);
  for (const [, names, code] of found) {
    markers++;
    for (const name of names.split(/[\s,]+/)) {
      if (multimodule.has(name)) {
        multimodule.get(name).push({ rel, code });
        continue;
      }
      if (android.has(name)) {
        android.get(name).push(code);
        continue;
      }
      if (kmp.has(name)) {
        kmp.get(name).push({ rel, code });
        continue;
      }
      if (!scenarios.has(name)) fail(`${rel} names scenario "${name}", which has no fixture`);
      scenarios.get(name).snippets.push({ rel, code });
    }
  }
}

rmSync(generated, { recursive: true, force: true });
for (const project of projects) {
  const entries = [...scenarios].filter(([, s]) => s.project === project);
  const out = join(generated, project);
  mkdirSync(out, { recursive: true });
  const calls = [];
  for (const [name, { fixture, pkg, snippets }] of entries) {
    const dir = join(out, name);
    mkdirSync(dir, { recursive: true });
    const header = `package ${pkg}\n\n`;
    snippets.forEach(({ rel, code }, i) => {
      writeFileSync(join(dir, `Snippet${i}.kt`), `${header}// From ${rel}\n${code}`);
    });
    writeFileSync(join(dir, 'Fixture.kt'), header + fixture.replaceAll('__PACKAGE__', pkg));
    calls.push(`    run("${name}") { ${pkg}.check() }`);
  }
  writeFileSync(join(out, 'Runner.kt'), `import org.koin.core.context.stopKoin
import kotlin.system.exitProcess

private var failures = 0

private fun run(name: String, block: () -> Unit) {
    try {
        block()
        println("PASS $name")
    } catch (e: Throwable) {
        failures++
        println("FAIL $name: $e")
    } finally {
        stopKoin()
    }
}

fun main() {
${calls.join('\n')}
    if (failures > 0) exitProcess(1)
    exitProcess(0)
}
`);
}

for (const [name, snippets] of kmp) {
  const out = join(generated, 'kmp', name);
  snippets.forEach(({ rel, code }, i) => {
    const hint = code.match(/^\/\/ (\w+(?:Main|Test))(?:, ([\w/.]+))?/);
    const file = join(out, hint?.[1] ?? 'commonMain', hint?.[2] ?? `Snippet${i}.kt`);
    mkdirSync(dirname(file), { recursive: true });
    writeFileSync(file, /^package /m.test(code) ? code : `package com.example.app\n\n${code}`);
  });
  for (const sourceSet of readdirSync(join(kmpRoot, name))) {
    const dir = join(out, sourceSet);
    mkdirSync(dir, { recursive: true });
    for (const file of readdirSync(join(kmpRoot, name, sourceSet))) {
      writeFileSync(join(dir, file), readFileSync(join(kmpRoot, name, sourceSet, file), 'utf8'));
    }
  }
}

for (const [name, snippets] of android) {
  const out = join(generated, 'android', name);
  for (const dir of [join(androidRoot, '_shared'), join(androidRoot, name)]) cpSync(dir, out, { recursive: true });
  mkdirSync(join(out, 'main/kotlin'), { recursive: true });
  snippets.forEach((code, i) => {
    writeFileSync(join(out, 'main/kotlin', `Snippet${i}.kt`), /^package /m.test(code) ? code : `package org.koin.sample\n\n${code}`);
  });
}

for (const [name, snippets] of multimodule) {
  const style = name.replace('multimodule-', '');
  const out = join(generated, 'multimodule', style);
  cpSync(join(multimoduleRoot, style), out, { recursive: true });
  snippets.forEach(({ rel, code }, i) => {
    const module = code.match(/^\/\/ Gradle module ":([\w:-]+)"/)?.[1];
    if (!module) fail(`${rel}: a ${name} block must start with // Gradle module ":name"`);
    const dir = join(out, module.replaceAll(':', '-'), 'main');
    mkdirSync(dir, { recursive: true });
    writeFileSync(join(dir, `Snippet${i}.kt`), /^package /m.test(code) ? code : `package org.koin.sample\n\n${code}`);
  });
}

// Coverage of the platform pages, one line per page with --coverage.
const blocks = walk(join(docs, 'docs/platforms')).sort().map((file) => {
  const text = readFileSync(file, 'utf8');
  return {
    page: file.replace(`${docs}/docs/platforms/`, ''),
    all: (text.match(/^```kotlin/gm) ?? []).length,
    marked: (text.match(/\{\/\*\s*check:/g) ?? []).length,
  };
});
if (process.argv.includes('--coverage')) {
  for (const { page, all, marked } of blocks) console.log(`${String(marked).padStart(3)} / ${String(all).padEnd(3)} ${page}`);
}
const sum = (key) => blocks.reduce((n, b) => n + b[key], 0);
console.log(`Platform pages: ${sum('marked')} of ${sum('all')} Kotlin blocks are checked. Use --coverage for the list.`);

const total = [...scenarios.values()];
console.log(`${markers} snippets extracted into ${total.length} JVM, ${kmp.size} multiplatform and ${android.size} Android and ${multimodule.size} multi-module scenarios (${total.filter((s) => s.snippets.length === 0).length} check prose claims only).`);
