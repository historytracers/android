## Score Rules

- Never create a local score variable. Always update the global score via `onScoreChanged(currentScore + N)` (the `currentScore` / `onScoreChanged` pattern wired through `AppNavigation.kt`), which persists to DataStore and displays in the top app bar across all screens.

## Abacus Game Rules

- When the "Correct!" feedback message is shown (step completed), the abacus must be frozen (non-interactive) until the user clicks "Next Step", "Next Level", or "New Exercise". Implement this by adding `stepCompleted` to `pointerInput` keys and returning early with `if (stepCompleted) return@detectTapGestures`.

## Translation Rules

- Never hardcode user-facing strings in composables. Always use `s.` from `LocalUiStrings.current` (the `UiStrings` data class).
- When adding new UI text, add a new field to `UiStrings` in `app/src/main/java/com/historytracers/app/ui/UiStrings.kt` and provide translations for all three locales: English (`EnStrings`), Portuguese (`PtStrings`), and Spanish (`EsStrings`).
- Brand names that are identical across languages (e.g., "Patreon", "PayPal") still need entries in `UiStrings` for consistency — use the same name in all three locales.

## Sources Menu

- Every exercise screen (Clap, FeetAndHands, ExercisingAddition, ExercisingMultiplication, etc.) can have a Sources menu in the bottom-left corner.
- The Sources menu has a book icon + "Sources" label. When tapped, it shows a cascading submenu: "Main Text" (with arrow icon) → Copy URL / Go to URL.
- To add a Sources menu to a screen:
  1. Add these imports if not present: `ClipData`, `ClipboardManager`, `Context`, `Toast`, `clickable`, `KeyboardArrowRight`, `Book`, `LocalUriHandler`
  2. Add state vars: `showSourcesMenu` and `showMainTextSubmenu` (both `mutableStateOf(false)`)
  3. If the screen uses a root `Column` instead of a `Box`, wrap the entire content in `Box(modifier = Modifier.fillMaxSize())` — place the existing `Column` inside, then add overlays after it
  4. The completion message overlay (if any) should use `.align(Alignment.BottomCenter)` inside the Box
  5. Add the Sources overlay Box at `Alignment.BottomStart` with `padding(bottom = 8.dp, start = 8.dp)` containing:
     - A clickable Column with `Icons.Filled.Book` (32.dp) and `s.sources` label below
     - A first-level `DropdownMenu` showing `s.mainText` with `KeyboardArrowRight` trailing icon, sets `showMainTextSubmenu = true`
     - A second-level `DropdownMenu` showing `s.copyUrl` and `s.goToUrl` items, each copying or opening the link
  6. Use `s.copyUrl` and `s.goToUrl` from the existing `UiStrings` (no new strings needed beyond `sources` and `mainText` if not already present)

## Level Group Controllers

- Every Principal screen (hub screen listing exercises before "Next Level" buttons) must use `LevelGroupController` to gate the "Next Level" flag buttons.
- Create one `LevelGroupController` per level group (the items before each "Next Level" button).
- Add the corresponding section completion key (`workout_sections`, `first_steps_sections`, `abacus_sections`) to `UserPreferences.kt` with a `stringSetPreferencesKey`.
- Add a `Flow<Set<String>>` getter and a `suspend fun mark[Screen]SectionCompleted(section: String)` setter in `UserPreferences.kt`.
- In the hub screen: collect the flow, create controllers via `remember { LevelGroupController(sectionIds, completedSections) }`, sync via `LaunchedEffect(completedSections)`, and add `enabled = controller.allCompleted` to the flag button.
- Each section's `onClick` must call `controller.markCompleted(id)` and `scope.launch { preferences.mark[Screen]SectionCompleted(id) }`.
