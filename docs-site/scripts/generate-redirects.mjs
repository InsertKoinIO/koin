// Writes public/_redirects so hosts that support it serve real 301s, which keep
// #fragments. Astro's own redirect pages remain as the fallback for static hosting.
import { readFileSync, writeFileSync } from 'node:fs';

const map = JSON.parse(readFileSync(new URL('../src/data/redirects.json', import.meta.url), 'utf8'));
const lines = Object.entries(map).flatMap(([from, to]) =>
  from === '/' ? [] : [`${from} ${to} 301`, `${from}/ ${to} 301`],
);
writeFileSync(new URL('../public/_redirects', import.meta.url), `${lines.join('\n')}\n`);
console.log(`generate-redirects: ${Object.keys(map).length} URLs`);
