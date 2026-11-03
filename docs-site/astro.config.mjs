// @ts-check
import { readFileSync } from 'node:fs';
import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';
import starlightLinksValidator from 'starlight-links-validator';
import starlightLlmsTxt from 'starlight-llms-txt';
import { sidebar } from './src/sidebar.ts';

const redirects = JSON.parse(readFileSync(new URL('./src/data/redirects.json', import.meta.url), 'utf8'));

export default defineConfig({
  site: 'https://insert-koin.io',
  trailingSlash: 'ignore',
  redirects,
  integrations: [
    starlight({
      title: 'Koin',
      description: 'Dependency injection for Kotlin and Kotlin Multiplatform.',
      logo: { src: './src/assets/koin-logo.png', alt: 'Koin' },
      favicon: '/favicon.ico',
      customCss: ['./src/styles/custom.css'],
      social: [{ icon: 'github', label: 'GitHub', href: 'https://github.com/InsertKoinIO/koin' }],
      editLink: { baseUrl: 'https://github.com/InsertKoinIO/koin/edit/main/docs-site/' },
      components: { SocialIcons: './src/components/HeaderLinks.astro' },
      lastUpdated: true,
      disable404Route: true,
      sidebar,
      plugins: [
        // Fails the build on any broken internal link or anchor.
        starlightLinksValidator({ errorOnLocalLinks: true }),
        starlightLlmsTxt({
          projectName: 'Koin',
          description: 'Pragmatic dependency injection for Kotlin and Kotlin Multiplatform.',
          // Keep only the plain route list of the homepage decision map.
          customSelectors: { all: ['.decision-map-interactive'] },
        }),
      ],
    }),
  ],
});
