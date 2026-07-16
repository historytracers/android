## Score Rules

- Never create a local score variable. Always update the global score via `onScoreChanged(currentScore + N)` (the `currentScore` / `onScoreChanged` pattern wired through `AppNavigation.kt`), which persists to DataStore and displays in the top app bar across all screens.

## Abacus Game Rules

- When the "Correct!" feedback message is shown (step completed), the abacus must be frozen (non-interactive) until the user clicks "Next Step", "Next Level", or "New Exercise". Implement this by adding `stepCompleted` to `pointerInput` keys and returning early with `if (stepCompleted) return@detectTapGestures`.

## Translation Rules

- Never hardcode user-facing strings in composables. Always use `s.` from `LocalUiStrings.current` (the `UiStrings` data class).
- When adding new UI text, add a new field to `UiStrings` in `app/src/main/java/com/historytracers/app/ui/UiStrings.kt` and provide translations for all three locales: English (`EnStrings`), Portuguese (`PtStrings`), and Spanish (`EsStrings`).
- Brand names that are identical across languages (e.g., "Patreon", "PayPal") still need entries in `UiStrings` for consistency — use the same name in all three locales.
