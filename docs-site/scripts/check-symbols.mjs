// Lists Koin symbols used in Kotlin snippets across the docs and confirms each
// one exists in ../projects, or in a sibling repo passed with --ref <dir>.
// A grep-level check: it proves a name exists, not that a signature matches.
import { execFileSync } from 'node:child_process';
import { readdirSync, readFileSync, statSync } from 'node:fs';
import { join } from 'node:path';

const root = new URL('..', import.meta.url).pathname;
const docs = join(root, 'src/content/docs');
const refs = [join(root, '../projects')];
process.argv.forEach((a, i) => a === '--ref' && refs.push(process.argv[i + 1]));

// Names from Kotlin, AndroidX, Compose or the sample code in the docs, not Koin.
const ignore = new Set(readFileSync(new URL('./symbols-allowlist.txt', import.meta.url), 'utf8').split('\n').map((l) => l.trim()).filter((l) => l && !l.startsWith('#')));

const walk = (d) => readdirSync(d).flatMap((f) => {
  const p = join(d, f);
  return statSync(p).isDirectory() ? walk(p) : p.endsWith('.mdx') ? [p] : [];
});

const imports = new Map();
const annotations = new Map();
const artifacts = new Map();
const add = (m, k, f) => m.set(k, (m.get(k) ?? new Set()).add(f));

for (const file of walk(docs)) {
  const text = readFileSync(file, 'utf8');
  const rel = file.replace(`${docs}/`, '');
  for (const [, block] of text.matchAll(/```kotlin[^\n]*\n([\s\S]*?)```/g)) {
    for (const [, path] of block.matchAll(/^import (org\.koin\.[\w.]+)(?:\.\*)?\s*(?:as \w+)?$/gm)) add(imports, path, rel);
    for (const [, a] of block.matchAll(/(?<![\w])@([A-Z]\w+)/g)) add(annotations, a, rel);
  }
  for (const [, a] of text.matchAll(/\bio\.insert-koin:(koin-[a-z0-9-]+)/g)) add(artifacts, a, rel);
  for (const [, list] of text.matchAll(/(?:artifacts|test)=\{\[([^\]]*)\]\}/g))
    for (const [, a] of list.matchAll(/'(koin-[a-z0-9-]+)'/g)) add(artifacts, a, rel);
}

const grep = (pattern, include) => {
  for (const dir of refs) {
    try {
      execFileSync('grep', ['-rqE', pattern, ...include.map((i) => `--include=${i}`), dir]);
      return true;
    } catch { /* not found here */ }
  }
  return false;
};

const missing = [];
for (const [path, files] of imports) {
  const parts = path.split('.');
  const name = parts.at(-1);
  const pkg = parts.slice(0, -1).join('.');
  const asPackage = grep(`^package ${path.replaceAll('.', '\\.')}\\s*$`, ['*.kt']);
  const asSymbol = grep(`^package ${pkg.replaceAll('.', '\\.')}\\s*$`, ['*.kt'])
    && grep(`(fun|class|object|interface|val|var|typealias)( <[^>]*>)? ?([A-Za-z.<>?*, ]+\\.)?${name}\\b`, ['*.kt']);
  if (!asPackage && !asSymbol && !ignore.has(path)) missing.push(`import ${path}  (${[...files].slice(0, 3).join(', ')})`);
}
for (const [a, files] of annotations) {
  if (ignore.has(a)) continue;
  if (!grep(`annotation class ${a}\\b`, ['*.kt'])) missing.push(`@${a}  (${[...files].slice(0, 3).join(', ')})`);
}
const settings = refs.map((d) => { try { return readFileSync(join(d, 'settings.gradle.kts'), 'utf8'); } catch { return ''; } }).join('\n');
for (const [a, files] of artifacts) {
  if (ignore.has(a) || settings.includes(a)) continue;
  missing.push(`artifact ${a}  (${[...files].slice(0, 3).join(', ')})`);
}

console.log(`Checked ${imports.size} imports, ${annotations.size} annotations, ${artifacts.size} artifacts.`);
if (missing.length) {
  console.error(`${missing.length} not found:\n${missing.sort().join('\n')}`);
  process.exit(1);
}
console.log('All symbols found.');
