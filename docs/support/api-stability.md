---
title: API Stability & Release Types
custom_edit_url: null
---

## API Stability

The Koin project is committed to maintaining a high level of compatibility between versions. Kotzilla team and all active maintainers works to ensure that any changes, enhancements, or optimizations introduced in new releases do not break existing applications. 
We understand that a stable and predictable upgrade path is critical to our users, and we strive to minimize disruptions when evolving our APIs.

### Experimental APIs - @KoinExperimentalAPI
To foster innovation while gathering valuable community feedback, we introduce new features and APIs under the `@KoinExperimentalAPI` annotation. This designation indicates that:

- **Under active development**: The API is still in its design phase and is subject to change.
- **Feedback encouraged**: We invite developers to test these features and share their experiences, helping us refine and improve the design.
- **Potential breaking changes**: Because these APIs are experimental, they might be modified or removed in subsequent releases as we iterate based on community input.

### Deprecation Policy - @Deprecated

To ensure a smooth transition when parts of the API are being phased out, Koin uses the `@Deprecated` annotation to clearly mark these areas. Our deprecation strategy includes:

Clear warnings: Deprecated APIs come with a message indicating the recommended alternative or the reason for deprecation.

Deprecation levels:
- **Warning**: Signals that while the API is still available, its use is discouraged and should be replaced at the earliest opportunity.
- **Error**: Denotes that the API is no longer intended for use and will not compile, ensuring that important changes are addressed promptly.

This approach helps developers identify and update code that relies on outdated APIs, reducing technical debt and paving the way for a cleaner, more robust codebase.
`ReplaceWith` can be provided with the API, depending on the complexity of the update. 

### Internal APIs - @KoinInternalAPI

For functionalities that are strictly intended for internal use within the Koin framework, we introduce the `@KoinInternalAPI` annotation. These APIs are not part of the public contract and:

- **Internal use only**: Are designed solely for the internal mechanisms of Koin.
- **Subject to change**: May undergo modifications or be removed in future releases without prior notice.
- **Avoid external usage**: Developers are discouraged from using these APIs in their application code to maintain long-term compatibility.

### Opting In with Kotlin's @OptIn Annotation

Both experimental and deprecated API usages in Koin requires an opt-in, ensuring that developers are fully aware of the API’s status and potential risks. 
By using Kotlin's `@OptIn` annotation, you explicitly acknowledge that your code depends on APIs that are experimental or marked for deprecation.

## Release Types

Koin adheres to semantic versioning (SemVer) with additional prefix identifiers that signal the maturity and intended use of each release. The prefixes we use include:

- **Release Candidate (RC)**: These releases are feature-complete candidates for stable versions. They undergo final testing and refinement. While RC versions are intended to be highly compatible, minor changes may still occur based on final feedback before an official release.
- **Alpha / Beta**: Alpha and beta versions are made available primarily for testing and feedback. They often contain experimental features and may not fully conform to stable API guarantees. Developers are encouraged to try these releases in non-production environments to help identify potential issues and guide future improvements.

