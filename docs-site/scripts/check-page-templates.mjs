// Keeps the platform pages on the templates described in IA.md, "Page templates": a tutorial opens with
// "What you will do" and "Prerequisites", and every page ends with "Next steps".
import { readdirSync, readFileSync, statSync } from 'node:fs';
import { join } from 'node:path';

const root = new URL('../src/content/docs/docs/platforms', import.meta.url).pathname;
const walk = (d) => readdirSync(d).flatMap((f) => {
  const p = join(d, f);
  return statSync(p).isDirectory() ? walk(p) : p.endsWith('.mdx') ? [p] : [];
});

const failures = [];
for (const file of walk(root)) {
  const rel = file.replace(`${root}/`, '');
  const text = readFileSync(file, 'utf8');
  const headings = [...text.matchAll(/^## (.+)$/gm)].map((m) => m[1]);
  const isTutorial = /(^|\/)(tutorial|walkthrough)\.mdx$/.test(rel);
  if (headings.at(-1) !== 'Next steps') failures.push(`${rel}: the last section must be "Next steps", found "${headings.at(-1)}"`);
  if (headings.includes('Related')) failures.push(`${rel}: use "Next steps", not "Related"`);
  if (isTutorial && headings[0] !== 'What you will do') failures.push(`${rel}: a tutorial must open with "What you will do"`);
  if (isTutorial && !headings.includes('Prerequisites')) failures.push(`${rel}: a tutorial needs "Prerequisites"`);
  if (!isTutorial && headings.includes('What you will do')) failures.push(`${rel}: only a tutorial has "What you will do"`);
  if (/^\*\*(What you will do|Prerequisites):\*\*/m.test(text)) failures.push(`${rel}: use a heading, not a bold label`);
}
if (failures.length) {
  console.error(failures.join('\n'));
  process.exit(1);
}
console.log('All platform pages follow the template.');
