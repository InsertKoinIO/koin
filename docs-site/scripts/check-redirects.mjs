// Run after a build. Checks that every old URL from the migration map either
// resolves to a real page in dist/ or redirects to one, with no redirect chains.
import { existsSync, readFileSync } from 'node:fs';
import { join } from 'node:path';

const dist = new URL('../dist/', import.meta.url).pathname;
const map = JSON.parse(readFileSync(new URL('../src/data/redirects.json', import.meta.url), 'utf8'));
const unchanged = JSON.parse(readFileSync(new URL('./unchanged-urls.json', import.meta.url), 'utf8'));

const pageFile = (url) => join(dist, url, 'index.html');
const isRedirect = (html) => /http-equiv="refresh"/.test(html.slice(0, 500));
const failures = [];
let external = 0;

for (const [from, to] of Object.entries(map)) {
  if (from === '/') continue;
  if (!existsSync(pageFile(from))) {
    failures.push(`${from}: no redirect page in dist`);
    continue;
  }
  if (to.startsWith('https://legacy.insert-koin.io/')) {
    external += 1;
    continue;
  }
  if (to.startsWith('http')) {
    failures.push(`${from}: unexpected external target ${to}`);
    continue;
  }
  if (!existsSync(pageFile(to))) failures.push(`${from}: target ${to} does not exist`);
  else if (isRedirect(readFileSync(pageFile(to), 'utf8'))) failures.push(`${from}: target ${to} is itself a redirect`);
}

for (const url of unchanged) {
  if (!existsSync(pageFile(url))) failures.push(`${url}: expected unchanged URL is missing`);
  else if (isRedirect(readFileSync(pageFile(url), 'utf8'))) failures.push(`${url}: expected a page, found a redirect`);
}

const total = Object.keys(map).length + unchanged.length;
console.log(`${total} old URLs checked: ${Object.keys(map).length} redirects (${external} to the legacy site), ${unchanged.length} unchanged.`);
if (failures.length) {
  console.error(`${failures.length} failure(s):\n${failures.join('\n')}`);
  process.exit(1);
}
console.log('All old URLs resolve.');
