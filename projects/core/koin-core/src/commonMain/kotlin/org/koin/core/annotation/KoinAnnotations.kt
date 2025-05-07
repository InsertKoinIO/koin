/*
 * Copyright 2017-Present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.koin.core.annotation

/**
Internal APIs
@KoinInternalAPI annotation. Tag APIs are not part of the public contract and:

- **Internal use only**: Are designed solely for the internal mechanisms of Koin.
- **Subject to change**: May undergo modifications or be removed in future releases without prior notice.
- **Avoid external usage**: Developers are discouraged from using these APIs in their application code to maintain long-term compatibility.

 * @author Arnaud Giuliani
 */
@RequiresOptIn(message = "API marked as @KoinInternalAPI and is intended for internal framework use only. It may change or be removed in future releases without notice. External usage is strongly discouraged.", level = RequiresOptIn.Level.ERROR)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.CONSTRUCTOR,
)
annotation class KoinInternalApi

/**
Experimental APIs -
To foster innovation while gathering valuable community feedback, we introduce new features and APIs under the @KoinExperimentalAPI annotation. This designation indicates that:

- Under active development: The API is still in its design phase and is subject to change.
- Feedback encouraged: We invite developers to test these features and share their experiences, helping us refine and improve the design.
- Potential breaking changes: Because these APIs are experimental, they might be modified or removed in subsequent releases as we iterate based on community input.
 *
 * @author Arnaud Giuliani
 * @author Victor Alenkov
 */
@RequiresOptIn(message = "API marked as @KoinExperimentalAPI. The current API is actively under development, and may change or be removed without notice. Feedback will help stabilize this API.", level = RequiresOptIn.Level.WARNING)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.CONSTRUCTOR,
)
annotation class KoinExperimentalAPI