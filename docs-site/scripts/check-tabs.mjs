// Checks the koin-style tabs. A tab only holds what differs between styles, and tabs sync by exact
// label, so a label outside this set would stop the reader's choice from following them.
// A Constructor tab may only be left out when its code is the same as the Compiler Plugin DSL tab,
// and that tab must then say so, because a Constructor reader lands on it.
import { readdirSync, readFileSync, statSync } from 'node:fs';
import { join } from 'node:path';

const LABELS = ['Compiler Plugin DSL', 'Annotations', 'Constructor / Classic DSL'];
const SAME_CODE = 'Same code with the Constructor / Classic DSL.';

const docs = new URL('../src/content/docs/docs', import.meta.url).pathname;
const walk = (d) => readdirSync(d).flatMap((f) => {
  const p = join(d, f);
  return statSync(p).isDirectory() ? walk(p) : /\.mdx$/.test(p) ? [p] : [];
});
// Check markers name the style, so they would make identical tabs look different.
const normalize = (s) => s.replace(/\{\/\*[\s\S]*?\*\/\}/g, '').replace(/\s+/g, ' ').trim();

let errors = 0;
const report = (where, message) => {
  errors++;
  console.error(`${where}: ${message}`);
};

for (const file of walk(docs)) {
  const text = readFileSync(file, 'utf8');
  for (const group of text.matchAll(/<Tabs syncKey="koin-style">([\s\S]*?)<\/Tabs>/g)) {
    const where = `${file.replace(`${docs}/`, '')}:${text.slice(0, group.index).split('\n').length}`;
    const tabs = [...group[1].matchAll(/<TabItem label="([^"]+)">([\s\S]*?)<\/TabItem>/g)];
    const labels = tabs.map(([, label]) => label);

    for (const label of labels) {
      if (!LABELS.includes(label)) report(where, `unknown tab label "${label}"`);
    }
    if (labels[0] !== LABELS[0] || labels[1] !== LABELS[1]) {
      report(where, `tabs must start with "${LABELS[0]}" then "${LABELS[1]}"`);
    }

    const seen = new Map();
    for (const [, label, body] of tabs) {
      const key = normalize(body);
      if (seen.has(key)) report(where, `"${seen.get(key)}" and "${label}" are identical`);
      else seen.set(key, label);
    }

    if (!labels.includes(LABELS[2])) {
      const [, , dsl] = tabs.find(([, label]) => label === LABELS[0]) ?? [];
      if (dsl && !dsl.includes(SAME_CODE)) report(where, `no Constructor tab, so the first tab must say "${SAME_CODE}"`);
    }
  }
}

if (errors > 0) {
  console.error(`${errors} tab problems.`);
  process.exit(1);
}
console.log('Tabs: no identical tabs, labels in sync.');
