## Translation Rules

- Never hardcode user-facing strings in composables. Always use `s.` from `LocalUiStrings.current` (the `UiStrings` data class).
- When adding new UI text, add a new field to `UiStrings` in `app/src/main/java/com/historytracers/app/ui/UiStrings.kt` and provide translations for all three locales: English (`EnStrings`), Portuguese (`PtStrings`), and Spanish (`EsStrings`).
- Brand names that are identical across languages (e.g., "Patreon", "PayPal") still need entries in `UiStrings` for consistency — use the same name in all three locales.
