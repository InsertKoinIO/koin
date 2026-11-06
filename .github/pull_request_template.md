## What & why

<!-- Issue: Fix #NNNN (GitHub issue), if any. Target `main` for stable-line fixes, `4.3.0` for next-minor work. -->

## Public API

- [ ] `./gradlew apiCheck` passes
- [ ] No public API change — **or** additions only, `.api` dumps regenerated (`./gradlew apiDump`) and reviewed with `./compare_apis.sh`
- [ ] Removals / signature changes: not in a patch release, deprecated first (`@Deprecated` + `ReplaceWith`)

## Behavior change

<!-- Required if anything in core/resolution/ or resolution order/matching changed: old behavior vs new behavior. Otherwise write "None". -->

## Tests

- [ ] Bug fix: regression test that fails without the fix (in `commonTest` when the code is common)
- [ ] New behavior: tests covering it, including the edge that motivated it
- [ ] `commonMain` touched without a test change? Justify here:

## Verified beyond the JVM (for `commonMain` changes)

- [ ] `allTests`, **or** a native target + `wasmJs` compile — command and result:

## Docs

- [ ] No user-visible change — **or** `docs/` updated in this PR
