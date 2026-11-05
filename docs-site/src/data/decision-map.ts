// Decision map shown on the homepage. Links are docs collection ids, checked at
// build time by DecisionMap.astro, which also reads their titles and descriptions.

export interface Answer {
  label: string;
  hint?: string;
  /** Id of the next step or result. */
  next: string;
}

export interface Step {
  id: string;
  question: string;
  answers: Answer[];
}

export interface Link {
  slug: string;
  /** Replaces the page title when it does not make sense out of context. */
  title?: string;
}

export interface Result {
  id: string;
  /** Names the route in the list shown without JavaScript. */
  route: string;
  links: Link[];
  /** Adds the pointer to the dependency injection basics, for newcomers. */
  showDiNote?: boolean;
}

export const steps: Step[] = [
  {
    id: 'start',
    question: 'Where are you coming from?',
    answers: [
      { label: "I'm new to Koin", next: 'new-platform' },
      { label: 'I already use Koin', next: 'user-need' },
      { label: "I'm coming from Hilt or Dagger", next: 'from-hilt-dagger' },
    ],
  },
  {
    id: 'new-platform',
    question: 'What are you building for?',
    answers: [
      { label: 'Android', hint: 'Jetpack Compose', next: 'new-android' },
      { label: 'Kotlin Multiplatform', hint: 'Shared logic, native UI', next: 'new-kmp' },
      { label: 'Compose Multiplatform', hint: 'Shared logic and UI', next: 'new-cmp' },
      { label: 'Ktor', hint: 'Server applications', next: 'new-ktor' },
      { label: 'Kotlin / JVM', hint: 'Any JVM application', next: 'new-jvm' },
    ],
  },
  {
    id: 'user-need',
    question: 'What do you need?',
    answers: [
      { label: 'Add Koin to a platform', next: 'user-platform' },
      { label: 'Upgrade Koin', next: 'user-upgrade' },
      { label: 'Test my code', next: 'user-test' },
      { label: 'Fix an error', next: 'user-fix' },
      { label: 'Look something up', next: 'user-reference' },
    ],
  },
];

export const diNote = {
  text: 'New to dependency injection?',
  slug: 'docs/concepts/what-is-di',
  label: 'Start with the basics',
};

export const results: Result[] = [
  {
    id: 'new-android',
    route: 'New to Koin, Android',
    links: [{ slug: 'docs/get-started' }, { slug: 'docs/platforms/android/tutorial' }],
    showDiNote: true,
  },
  {
    id: 'new-kmp',
    route: 'New to Koin, Kotlin Multiplatform',
    links: [
      { slug: 'docs/get-started' },
      { slug: 'docs/platforms/kmp', title: 'Kotlin Multiplatform overview' },
      { slug: 'docs/platforms/kmp/install' },
      { slug: 'docs/platforms/kmp/swift' },
    ],
    showDiNote: true,
  },
  {
    id: 'new-cmp',
    route: 'New to Koin, Compose Multiplatform',
    links: [{ slug: 'docs/get-started' }, { slug: 'docs/platforms/kmp/walkthrough' }, { slug: 'docs/platforms/kmp/compose-multiplatform' }],
    showDiNote: true,
  },
  {
    id: 'new-ktor',
    route: 'New to Koin, Ktor',
    links: [{ slug: 'docs/get-started' }, { slug: 'docs/platforms/ktor/tutorial' }],
    showDiNote: true,
  },
  {
    id: 'new-jvm',
    route: 'New to Koin, Kotlin / JVM',
    links: [{ slug: 'docs/get-started' }, { slug: 'docs/platforms/kotlin/tutorial' }],
    showDiNote: true,
  },
  {
    id: 'from-hilt-dagger',
    route: 'Coming from Hilt or Dagger',
    links: [
      { slug: 'docs/migration/from-hilt' },
      { slug: 'docs/migration/from-dagger' },
      { slug: 'docs/migration/koin-vs-hilt' },
    ],
  },
  {
    id: 'user-platform',
    route: 'Add Koin to a platform',
    links: [
      { slug: 'docs/platforms/android' },
      { slug: 'docs/platforms/kmp/install', title: 'Kotlin Multiplatform: install' },
      { slug: 'docs/platforms/kmp/compose-multiplatform' },
      { slug: 'docs/platforms/ktor' },
      { slug: 'docs/platforms/kotlin' },
    ],
  },
  {
    id: 'user-upgrade',
    route: 'Upgrade Koin',
    links: [
      { slug: 'docs/migration/upgrade-4-2' },
      { slug: 'docs/migration/upgrade-from-3' },
      { slug: 'docs/migration/from-ksp' },
    ],
  },
  {
    id: 'user-test',
    route: 'Test my code',
    links: [{ slug: 'docs/testing/first-test' }, { slug: 'docs/testing' }, { slug: 'docs/testing/verify' }],
  },
  {
    id: 'user-fix',
    route: 'Fix an error',
    links: [{ slug: 'docs/support/troubleshooting' }, { slug: 'docs/reference/diagnostics' }],
  },
  {
    id: 'user-reference',
    route: 'Look something up',
    links: [
      { slug: 'docs/reference/dsl' },
      { slug: 'docs/reference/annotations' },
      { slug: 'docs/reference/artifacts' },
    ],
  },
];
