# Koin documentation site

Astro and Starlight. Package manager: **npm**. Node 22.23 or later.

```bash
npm install
npm run dev              # local server with hot reload
npm run build            # production build into dist/, fails on broken links
npm run preview          # serve dist/
npm run check:redirects  # every old URL resolves or redirects (after a build)
npm run check:symbols    # Koin symbols used in the docs exist in ../projects
npm run check:snippets   # compile and run the marked code blocks, see snippet-tests/README.md
npm run check:snippets:kmp  # same for multiplatform pages, needs ANDROID_HOME
npm run check:snippets:android  # same for the Android tutorial, with Robolectric, needs ANDROID_HOME
```

## Add a page

1. Create `src/content/docs/docs/<section>/<name>.mdx` with `title` and a one-sentence `description`.
2. Add it to `src/sidebar.ts`. The sidebar is explicit, and it is the only navigation.
3. If it replaces an old URL, add the URL to `src/data/redirects.json`.

Structure and the old-to-new URL map are in `IA.md`. The audit behind them is in `AUDIT.md`.

## Writing conventions

**Page template**

- Guides and tutorials open with "What you will do" and prerequisites.
- Recommended approach first, alternatives after.
- End with "Next steps" or related links.
- One canonical page per topic: link to it, do not re-explain it.

**Code tabs**

```mdx
import { Tabs, TabItem } from '@astrojs/starlight/components';

<Tabs syncKey="koin-style">
  <TabItem label="Compiler Plugin DSL">...</TabItem>
  <TabItem label="Annotations">...</TabItem>
  <TabItem label="Constructor / Classic DSL">...</TabItem>
</Tabs>
```

Keep these labels and this order, because the reader's choice follows them across pages. Skip tabs if all three are identical.

**Gradle**

Never hand-write dependency snippets or versions. Use `<Install>` and `<Version>`; versions live only in `src/data/versions.ts`.

```mdx
import Install from '~/components/Install.astro';

<Install artifacts={['koin-android']} plugin bom />
```

**Accuracy**

Every function, annotation, artifact and plugin id must exist in `../projects`, the Compiler Plugin repository or Maven Central. If a behavior was not checked with a build, say so or leave it out. Mark the code blocks of new JVM pages for `check:snippets`, and add a fixture for any behavior you state in prose. The adoption figures in `why-koin` are a dated snapshot: refresh them and their date together.

**Vocabulary** (see `reference/glossary`)

- Style: Koin DSL or Koin Annotations.
- DSL form: Compiler Plugin DSL, Constructor DSL, Classic DSL.
- Koin Compiler Plugin, then Compiler Plugin. Never "Koin Gradle plugin": `koin-gradle-plugin` is a different, older artifact.
- KSP processor for `koin-ksp-compiler`.
- Do not use the em dash character.
