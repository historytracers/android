## Score Rules

- Never create a local score variable. Always update the global score via `onScoreChanged(currentScore + N)` (the `currentScore` / `onScoreChanged` pattern wired through `AppNavigation.kt`), which persists to DataStore and displays in the top app bar across all screens.

## Abacus Game Rules

- When the "Correct!" feedback message is shown (step completed), the abacus must be frozen (non-interactive) until the user clicks "Next Step", "Next Level", or "New Exercise". Implement this by adding `stepCompleted` to `pointerInput` keys and returning early with `if (stepCompleted) return@detectTapGestures`.

## Translation Rules

- Never hardcode user-facing strings in composables.
- **Always create a per-screen strings file.** Every screen must own its strings in `app/src/main/java/com/historytracers/app/ui/features/<Screen>ScreenStrings.kt` (e.g. `ClapScreenStrings`, `HandsOnYupanaScreenStrings`). This is mandatory for every new or modified screen — even screens with only a few strings. Never add screen-specific strings to the global `UiStrings.kt`/`UiStringGroups.kt` or to a shared feature file.
- **Global texts** (back, next, score, sources, originalText, copyUrl, goToUrl, newExercise, nextStep, nextLevel, number, value, write, etc.) are declared in `AppCommonStrings` (`app/src/main/java/com/historytracers/app/ui/UiStringGroups.kt`) with translations in `UiStrings.kt` (`EnStrings`/`PtStrings`/`EsStrings`). Access them via `s.common.*` from `LocalUiStrings.current`.
- **Feature-shared strings** (used by 2+ screens of one feature) live in `app/src/main/java/com/historytracers/app/ui/features/*Strings.kt` (e.g. `HubTitleStrings`, `YupanaSharedStrings`, `BodyExerciseStrings`, `AbacusWriteStrings`, `PracticingAdditionStrings`, `MwStrings`, `SbwStrings`, `PlaceValueStrings`, `MiscStrings`). Each file defines a data class, `En/Pt/Es` objects, `LocalXxxStrings`, and `xxxStringsForLanguage(language)`.
- **Screen-specific strings** (used by exactly one screen) go in the screen's own `<Screen>ScreenStrings.kt`, never into a shared file or `UiStrings.kt`.
- Each screen's strings file must follow the same structure: a data class, `En/Pt/Es` value objects, `LocalXxxScreenStrings` (staticCompositionLocalOf defaulting to the `En` object), and `xxxScreenStringsForLanguage(language)`.
- In a screen composable, resolve strings with e.g. `val s = LocalUiStrings.current`, `val xs = clapScreenStringsForLanguage(LocalAppLanguage.current)`, `val bs = bodyExerciseStringsForLanguage(LocalAppLanguage.current)`. Use `LocalAppLanguage.current` (provided by `AppNavigation.kt`) to get the active language.
- When adding new UI text, always provide translations for all three locales: English (`En*`), Portuguese (`Pt*`), and Spanish (`Es*`).
- Brand names that are identical across languages (e.g., "Patreon", "PayPal") still need entries in all three locales for consistency — use the same name in all three.
- Use `%d` / `%s` format specifiers (Kotlin style) for interpolated values.

## Sources Menu

- Every exercise screen (Clap, FeetAndHands, ExercisingAddition, ExercisingMultiplication, etc.) can have a Sources menu in the bottom-left corner.
- The Sources menu has a book icon + "Sources" label. When tapped, it shows a cascading submenu: "Main Text" (with arrow icon) → Copy URL / Go to URL.
- **"Original Text" must always be the first option from bottom to top** — i.e., the last item at the bottom of the first-level menu. Author/person entries (Jessica, APal, Tomoko, DhavitPrem, etc.) come before it.
- To add a Sources menu to a screen:
  1. Add these imports if not present: `ClipData`, `ClipboardManager`, `Context`, `Toast`, `clickable`, `KeyboardArrowRight`, `Book`, `LocalUriHandler`
  2. Add state vars: `showSourcesMenu` and `showMainTextSubmenu` (both `mutableStateOf(false)`)
  3. If the screen uses a root `Column` instead of a `Box`, wrap the entire content in `Box(modifier = Modifier.fillMaxSize())` — place the existing `Column` inside, then add overlays after it
  4. The completion message overlay (if any) should use `.align(Alignment.BottomCenter)` inside the Box
  5. Add the Sources overlay Box at `Alignment.BottomStart` with `padding(bottom = 8.dp, start = 8.dp)` containing:
     - A clickable Column with `Icons.Filled.Book` (32.dp) and `s.common.sources` label below
     - A first-level `DropdownMenu` showing `s.common.originalText` with `KeyboardArrowRight` trailing icon, sets `showMainTextSubmenu = true`
     - A second-level `DropdownMenu` showing `s.common.copyUrl` and `s.common.goToUrl` items, each copying or opening the link
  6. Use `s.common.copyUrl` and `s.common.goToUrl` from the global `AppCommonStrings` (no new strings needed beyond `sources` and `mainText` if not already present)

## Level Group Controllers

- Every Principal screen (hub screen listing exercises before "Next Level" buttons) must use `LevelGroupController` to gate the "Next Level" flag buttons.
- Create one `LevelGroupController` per level group (the items before each "Next Level" button).
- Add the corresponding section completion key (`workout_sections`, `first_steps_sections`, `abacus_sections`) to `UserPreferences.kt` with a `stringSetPreferencesKey`.
- Add a `Flow<Set<String>>` getter and a `suspend fun mark[Screen]SectionCompleted(section: String)` setter in `UserPreferences.kt`.
- In the hub screen: collect the flow, create controllers via `remember { LevelGroupController(sectionIds, completedSections) }`, sync via `LaunchedEffect(completedSections)`, and add `enabled = controller.allCompleted` to the flag button.
- Each section's `onClick` must call `controller.markCompleted(id)` and `scope.launch { preferences.mark[Screen]SectionCompleted(id) }`.

## Porting a JS Abacus App to Android

When porting a JS abacus-based tutorial/game from the `historytracers/js/` and `historytracers/lang/*/` repos to an Android Compose screen, follow this checklist:

### 1. Extract messages from the JSON files
- Read all three locale JSON files (`en-US/*.json`, `pt-BR/*.json`, `es-ES/*.json`) to extract user-facing strings.
- Look in the `"text"` array of the `SECTION_game` section for `<span id="txt_*">` elements — these are the messages.
- Map each `txt_*` id to a Kotlin string field using the pattern `sbw*` (or whatever prefix matches the screen name, e.g. `mw*` for multiplication).
- Preserve all emojis and formatting exactly as in the JS source.

### 2. Add strings to a screen string file
- Create `app/src/main/java/com/historytracers/app/ui/features/<Screen>ScreenStrings.kt` with a data class, `En/Pt/Es` value objects, `LocalXxxScreenStrings`, and `xxxScreenStringsForLanguage(language)` (model after an existing screen string file).
- Add values for all three locales.
- Use `%d` / `%s` format specifiers (Kotlin style) instead of JS `{placeholder}` syntax.
- In the screen composable, resolve them with `val xs = xxxScreenStringsForLanguage(LocalAppLanguage.current)`.

### 3. Create the screen file
- Model after an existing similar screen (e.g. `MultiplyingWithAbacusLevel2Screen.kt` or `MultiplyingWithoutLimitsScreen.kt`).
- The structure is:
  - Constants (`COLUMNS`, `SOROBAN_UPPER`/`LOWER`, `SUANPAN_UPPER`/`LOWER`, `MAX_DIGIT_LEVEL`, `MIN_DIGIT_LEVEL`)
  - Column state data class (upper/lower beads, normalize)
  - Value conversion function
  - Exercise generation (level-based number ranges)
  - Step building function (modeled after the JS `buildStepsForNumbers`)
  - Composable screen with:
    - Top bar (back + title)
    - Instruction text, level badge, exercise display (`{a} − {b} = ?`)
    - Soroban/Suanpan mode toggle
    - Abacus Canvas (with drawing functions renamed to `drawSbw*`)
    - Value display panel
    - Step instruction and step status
    - Buttons (New Exercise, Next Step, Next Level)
    - Feedback messages (correct, perfect, congrats, last level)
    - Sources menu (with correct UUID link)
- Copy the drawing functions (`draw*Background`, `draw*Frame`, `draw*Rod`, `draw*ColumnBeads`) from an existing screen and rename with the new prefix.
- Copy the tap handler and rename accordingly.
- The congratulation/feedback messages must be placed BELOW the buttons (not above).
- The Sources menu must be hidden when `finalCongratsShown` is true.
- The abacus must be frozen (`stepCompleted` in `pointerInput` keys) when a step is completed.

### 4. Wire navigation
- Add a `data object` route in `Screen.kt`.
- Add the import and composable block in `AppNavigation.kt` (with back-navigation to the hub screen).
- Add the `onNavigateTo*` parameter to the hub screen (`AbacusScreen.kt`) and wire it to the button's `onClick`.
- Optionally add the section completion key to `UserPreferences.kt` and add a `LevelGroupController` entry.

### 5. Build & verify
- Run the build script to ensure compilation succeeds.
- Fix any unresolved references (missing strings or imports).
