## Line Ending Rules

- Always use LF line endings when creating or editing files. Never use CRLF. The repo enforces this via `.gitattributes` (`* text=auto eol=lf`); a CRLF working copy triggers git warnings and creates diff noise.

## Score Rules

- Never create a local score variable. Always update the global score via `onScoreChanged(currentScore + N)` (the `currentScore` / `onScoreChanged` pattern wired through `AppNavigation.kt`), which persists to DataStore and displays in the top app bar across all screens.

## Streak Rules

- **Every time the user reaches the end of any multi-screen group, update the day's streak — unless it was already updated today.**
- `UserPreferences.recordLessonCompletion()` (`app/src/main/java/com/historytracers/app/data/UserPreferences.kt`) does exactly this: it is idempotent (early-returns if today's date is already in `completedDates`) and increments/keeps the streak while handling missed selected days. Never write a custom streak routine; always reuse it.
- **Any group of screens launched from a hub must trigger the streak when its last screen is shown** — not on every screen, only on the group's conclusion/last screen. This includes sm_game story groups (Socrates, Numbers, Toward Infinity, Limits (Min and Max), Where Are They?, etc.), not just exercise screens. Single screens do not need it.
- Implementation pattern (shared loader with a `(onNavigateToFirstSteps: (() -> Unit)? = null)` style last-screen parameter):
  1. Add the import `com.historytracers.app.data.UserPreferences`.
  2. In the loader, next to `val repo = remember { ContentRepository(context) }`, add `val preferences = remember { UserPreferences(context) }`.
  3. In the loader's `LaunchedEffect(content)` block (where the arrival score is awarded), when the group's last screen is reached call `preferences.recordLessonCompletion()`, e.g.:
     ```kotlin
     LaunchedEffect(content) {
         val node = content
         if (node != null) {
             award(node.score)
             if (onNavigateToFirstSteps != null) {
                 preferences.recordLessonCompletion()
             }
         }
     }
     ```
  The `recordLessonCompletion()` call is a suspend function and runs inside the `LaunchedEffect` coroutine scope (no extra `rememberCoroutineScope()` needed).
- When adding a new group, always verify the streak updates once per day by reaching the group's final screen.

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
- **English titles use Title Case.** Screen titles and hub button labels (both the top app bar and the label below each button) must capitalize every word except short function words — articles (`a`, `an`, `the`), conjunctions (`and`, `or`, `but`), and prepositions (`in`, `on`, `at`, `to`, `of`, `for`, `with`, `by`, `from`, `without`) — which stay lowercase. The first and last word are always capitalized. Examples: "Building Game", "First Steps (Counting)", "Learning in Layers", "Multiplying with Yupana", "Complement to 10", "Where Are We From?". This rule applies to English only; Portuguese and Spanish follow their own conventions.

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

## Main Screen Hub Completion Colors

- Every main screen (`IndexScreen.kt`) button that opens a hub (First Steps, I Am (Not) Like You, Workout, Abacus, Yupana) must change color when the user finishes all tasks available in that hub — mirroring how internal buttons turn dark when their section is completed.
- The button switches from `ButtonYellow` (incomplete) to `ButtonYellowDark` (all sections done), keeping `OnButtonYellow` as the content color.
- Completion is driven by the per-hub section sets already persisted in `UserPreferences` (`first_steps_sections`, `i_am_not_like_you_sections`, `workout_sections`, `abacus_sections`, `yupana_sections`).
- A hub's `SectionIds` list must match **exactly** the sections whose internal buttons record completion via `mark[Hub]SectionCompleted(...)`. Never include a section that cannot be completed, or the main button would never turn dark.
- Implementation pattern (as in `IndexScreen.kt`):
  1. Define a `private val <hub>SectionIds = listOf(...)` for each hub listing every markable section.
  2. Collect each completed flow with `preferences.completed<Hub>Sections.collectAsState(initial = emptySet())`.
  3. Derive the completed flag from the observed state: `val <hub>Done = <hub>SectionIds.all { it in completed<Hub> }`.
  4. On the hub button, set `containerColor = if (<hub>Done) ButtonYellowDark else ButtonYellow`.
- Do not gate the main-screen button color on `LevelGroupController.allCompleted`: it reads a `MutableStateFlow` that Compose does not observe, so the color would not refresh after the persisted state loads. Always derive the flag from a `collectAsState`-observed set.
- When a new section/button is added to a hub, the section must record completion in its exercise screen (via `mark[Hub]SectionCompleted`) **and** be added to the corresponding `<hub>SectionIds` list on the main screen — otherwise the main button never reflects full completion.
- The rule only applies to buttons that lead to real hubs; buttons without internal screens (e.g. unimplemented placeholders) keep a static color.
- Current section lists to keep in sync:
  - **First Steps** (19): `i_dont_know`, `learning_in_shells`, `how_do_i_learn`, `my_hands`, `first_hands`, `first_voice`, `my_body`, `drawing`, `numbers`, `the_zero`, `sequence_game`, `family_part1`, `sequence_game_families`, `building`, `natural_families_part2`, `sequence_game_orders`, `going_to_infinity`, `limits_min_max`, `where_are_they`
  - **I Am (Not) Like You** (5): `to_be_or_not_to_be`, `totally_equal`, `equality_in_history_metate`, `equality_in_history`, `equal_same_group_or_different`
  - **Workout** (5): `exercising_hands`, `exercising_feet_and_hands`, `exercising_addition`, `exercising_multiplication`, `exercising_multiplication_l2`
  - **Abacus** (14): `soroban_writing`, `suanpan_writing`, `schyoty_writing`, `large_numbers_writing`, `adding_with_abacus`, `complement_to_ten`, `adding_large_numbers`, `practicing_addition`, `multiplication_table`, `carrying`, `multiplying_with_abacus`, `multiplying_with_abacus_l2`, `multiplying_without_limits`, `subtracting_with_abacus`
  - **Yupana** (2): `hands_on_yupana`, `moving_in_yupana`
  - **Road to Somewhere** (6): `walk_among_numbers`, `carrying_in_addition`, `order_of_addition`, `playing_with_axioms`, `running_among_numbers`, `practicing_addition`

## Latest Addition (Main Menu)

- The main menu (drawer) must always contain a "Latest addition" entry (`Screen.LatestAddition` → `LatestAdditionScreen.kt`), so users get one-tap access to the newest content.
- `LatestAdditionScreen` lists the **5 most recently added screens**, with the latest always on top. The list is a hardcoded ordered list in `LatestAdditionScreen.kt`; it is **not** derived at runtime.
- Each entry is a button that navigates directly to that screen and marks its section completed on tap (`mark[Hub]SectionCompleted(sectionId)`); its color switches from `ButtonYellow` to `ButtonYellowDark` when that section is completed, exactly like internal buttons.
- Each entry's `sectionId` must be a real section key recorded via `mark[Hub]SectionCompleted(...)` and must also be present in the corresponding `<hub>SectionIds` list on the main screen (`IndexScreen.kt`).
- **Whenever a new screen is added to the app, update the list:** insert the new screen at the top and drop the oldest, so the screen always shows exactly the 5 most recent screens.
- Current list (latest first): `running_among_numbers` (Running Among Numbers), `playing_with_axioms` (Practicing the Axioms of Addition), `order_of_addition` (The Order of Addition), `practicing_addition` (Practicing Addition), `carrying_in_addition` (Carrying in Addition).

## New Main-Screen Buttons (Sun Badge)

- Whenever a new, fully functional (complete) button is added to the main screen (`IndexScreen.kt`), it must:
  - show a sun icon (`Icons.Filled.WbSunny`) in its **top-right corner** to flag it as new;
  - return to the normal (light) `ButtonYellow` color, overriding the completed/dark state, until the user accesses it;
  - hide the sun once the user taps the button. The "seen" state is persisted per hub in `UserPreferences.seenNewHubs` (`markNewHubSeen(hubId)`), so the sun stays hidden on later visits.
- Implementation pattern (as in `IndexScreen.kt`):
  1. Add the new hub's id to the `newHubIds` set at the top of `IndexScreen.kt`.
  2. Compute `val <hub>New = isNewHub("<hubId>", seenNewHubs)`.
  3. Wrap the button in a `Box(modifier = Modifier.padding(horizontal = 32.dp))`, put the button inside, and render `NewHubSunBadge()` (a `BoxScope` composable aligning `TopEnd`) only when `<hub>New` is true.
  4. Set `containerColor = if (<hub>New) ButtonYellow else if (<hub>Controller.allCompleted) ButtonYellowDark else ButtonYellow`.
  5. In the button's `onClick`, call `scope.launch { preferences.markNewHubSeen("<hubId>") }` when `<hub>New` before navigating.
- When a hub is no longer considered "new", remove its id from `newHubIds`; no data migration is needed — the persisted `seenNewHubs` set simply stops being consulted for that hub.
- The rule applies only to main-screen buttons; it does not apply to internal hub buttons or the "Latest addition" entries.

## Return-to-Hub Buttons

- **Every multi-screen group launched from a hub (principal/main screen) must end with a button on its last screen that returns directly to that hub.** Groups with a single screen do not need this button.
- **First Steps (Counting):** always add this button to the last screen of each multi-screen group launched from it (e.g. Socrates Conclusion, Learning in Layers Conclusion, How Do I Learn Decision, My Hands Conclusion).
- **Generic rule (any hub):** the same applies to groups launched from Workout, Abacus, Yupana, or any other hub — whenever a group has 2+ screens, add the return-to-hub button to its last screen.
- Implementation pattern (used in `SocratesScreens.kt`, `LearningInLayersScreens.kt`, `HowDoILearnScreens.kt`, `MyHandsScreens.kt`):
  1. Add an optional `onNavigateTo<Hub>` callback (e.g. `onNavigateToFirstSteps: (() -> Unit)? = null`) to the shared loader composable and to its last-screen wrapper, passing it through. Non-last screens leave it unset so the button does not render.
  2. In the loader, below the Previous/Next `Row`, render the button only when the callback is non-null:
     - A green `FilledTonalButton` using `ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.White)` (same style as Previous/Next).
     - Label it with the hub's localized title (e.g. `val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)` then `Text(hts.firstSteps)`). Reuse the existing hub title string; do not create a new string for the label.
     - Precede it with `Spacer(Modifier.height(16.dp))`.
  3. In `AppNavigation.kt`, wire the callback to pop back to the hub route: `if (!navController.popBackStack(Screen.<Hub>.route, false)) { navController.navigate(Screen.<Hub>.route) { popUpTo(0) { inclusive = true }; launchSingleTop = true } }`.

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

## Creating Screens from Smartphone Game JSON (sm_game)

When building screens from `historytracers/lang/{lang}/smartphone/<uuid>.json` files (parsed by `SMGameFile`/`SMGameContent` in the `common` module, `type = "sm_game"`), follow this checklist:

### 1. Understand the data model
- `SMGameFile` has `title` and `content` (list of `SMGameContent`). Each `SMGameContent` is one screen, identified by its `id`.
- Fields to handle per content: `text` (list of `HTText`), `answer` (expected Yes/No answer or null), `smile` (values like `"thinking"`, `"happy"`, `"nerd"`, `"shocking"`, `"party"`, `"inlove"`; see the emoji mapping below), `source_menu` (list of `HTSource`), `score`, and the position in the `content` vector drives next/prev navigation.
- `HTText.format` is `"markdown"` or `"html"` — render markdown text with `MarkdownText` (`#### heading` → bold title, `===x===`/`**x**` → bold, `*x*` → italic); other formats use `TextRenderer`.
- **`<img>` tags must be rendered as actual images, never shown as raw text.** `HTText` with `format == "html"` may contain `<img src="...">` tags. `TextRenderer` handles this automatically: it splits the HTML, renders non-tag segments as `Text`, and renders each `<img>` with Coil's `AsyncImage` (`ContentScale.Fit`, `HTText.imgdesc` as content description). When a screen renders HTML, always route it through `TextRenderer` (do not pass `<img>` blocks to a plain `Text`).
- **Keep screens scroll-free: images must be responsive.** Prefer layouts that fit on a single phone screen without scrolling, keeping the navigation buttons (Previous/Next) visible. In particular, when a screen's `<img>` is large (e.g. a full-width photo), do not rely on `TextRenderer`'s fixed 480dp max height — render the image through a dedicated responsive composable that caps its height to a fraction of the screen (e.g. `LocalConfiguration.screenHeightDp * 0.4f` with `ContentScale.Fit` and full width), keyed to that screen's `contentId`, so the buttons stay on screen without scrolling.
- **Image captions** (the text block that follows an `<img>`): a markdown line that starts with a single `*` (a closing `*` is optional) and contains no other asterisks (e.g. `*Image taken during a visit to ...` or `*Image made by the Taï Chimpanzee Project*`) is an image caption. `MarkdownText` renders it centered (`TextAlign.Center`), italic (`FontStyle.Italic`), and smaller than the normal body text (`bodySmall`), stripping the outer `*`s. Keep this convention in the JSON: captions must start with a single `*`.
- `HTSource` has `text` (menu label) and `page` (URL). When `page` starts with `"index.html"`, prefix it with `"https://www.historytracers.org/"`.

### 2. Smartphone game JSON comes from the common submodule
- The smartphone game JSONs are the single source of truth in `historytracers/lang/{en-US,pt-BR,es-ES}/smartphone/<uuid>.json`. They are bundled into the app from the `common` submodule: after the user updates them in the `historytracers` repo, sync the `common/` submodule so the files are at `common/src/smartphone/{lang}/<uuid>.json`.
- `app/build.gradle.kts` adds `common/src/smartphone` as an assets source directory, so the JSONs are bundled at the asset root as `{lang}/<uuid>.json`.
- Load with `ContentRepository.loadAndParse("${LocalAppLanguage.current}/<uuid>")` (no `smartphone/` prefix, no `lang/` prefix) and handle `ContentResult.SMGame` / `ContentResult.Error`.
- Do not copy smartphone JSONs into `app/src/main/assets/` — that would create duplicates of the common submodule files.

### 3. Keep Gson mapping in sync (common module)
- JSON uses snake_case (`source_menu`); Java fields need matching annotations — `SMGameContent.sourceMenu` requires `@SerializedName("source_menu")`. When editing `common/src/android/.../com/historytracers/common/*.java`, update the Go counterpart in `common/src/go/data-type.go` in sync.

### 4. Create the feature strings file
- Create `app/src/main/java/com/historytracers/app/ui/features/<Feature>ScreenStrings.kt` (shared across the feature's screens): data class, `En/Pt/Es` objects, `LocalXxxScreenStrings`, `xxxScreenStringsForLanguage(language)`, translations in all three locales.
- Lesson text comes from the JSON; only UI chrome strings (title, wrong-answer feedback, etc.) go in the strings file.

### 5. Create the screens file
- Create one file with a public composable per `SMGameContent` delegating to a shared private loader composable keyed by `contentId`.
- Each screen must include:
  - Top bar (back arrow + title) using `s.common.*`.
  - Text rendered via `MarkdownText` when `format == "markdown"`, else `TextRenderer` (this renders any `<img>` tags as images). Markdown lines starting with a single `*` are image captions and are automatically styled centered/italic/smaller by `MarkdownText`.
  - Green buttons (`#4CAF50` container, `Color.White` content) everywhere buttons act on the game flow:
    - Previous/Next use `ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.White)`.
    - Previous shows `Icons.AutoMirrored.Filled.ArrowBack` before the label, Next shows `Icons.AutoMirrored.Filled.ArrowForward` after the label. Show them according to the `content` vector position (first screen: only Next; middle screens: both; last screen: only Previous).
  - When `answer != null`: green Yes/No buttons (`ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50), contentColor = Color.White)`) — both buttons green, no selected-color switching. On submit show the result once (no retry):
    - Correct → `🎉 Correct! 🎉` (`s.common.correct` wrapped in `\uD83C\uDF89`) in green (`Color(0xFF2E7D32)`), followed by a localized message (`xs.scoreDoubledMessage`) informing the user that their score for this screen will be doubled (correct answers award the full `score` on top of the arrival award).
    - Wrong → the localized wrong-answer message (`xs.wrongAnswerMessage`) in red (`MaterialTheme.colorScheme.error`).
  - When `smile` is non-empty: smile emoji at `Alignment.BottomEnd`. Map smile values with a `smileEmoji(smile)` helper: `"thinking"`/`"think"` → 🤔 (`\uD83E\uDD14`), `"happy"` → 😊 (`\uD83D\uDE0A`), `"nerd"` → 🤓 (`\uD83E\uDD13`), `"shocking"`/`"surprise"` → 😲 (`\uD83D\uDE32`), `"party"` → 🥳 (`\uD83E\uDD73`), `"inlove"` → 😍 (`\uD83D\uDE0D`); default to 😊 for any other value.
  - **Always use the `source_menu` array to populate a Source menu on screen** whenever `source_menu` is present (not `null`) — do not hardcode sources and do not skip the menu. Render a Sources menu at `Alignment.BottomStart` with a book icon + `s.common.sources` label; tapping it shows one first-level `DropdownMenuItem` per source (label = `source.text`) each opening a Copy URL / Go to URL submenu using the (prefixed) `source.page`.

### 6. Scoring
- Award each screen's default `score` as soon as the user reaches it (arrival award) — every screen, including question screens.
- For question screens, additionally award based on the user's answer:
  - Correct answer → the full `score` value.
  - Wrong answer → half of it (`score / 2`).
  - Example: a question with `score = 2` gives +2 on arrival, then +2 on a correct answer or +1 on a wrong answer.
- `onScoreChanged` sets an absolute value, so accumulate awards per screen: `val initialScore = remember { currentScore }`, keep a `totalAwarded` counter, and call `onScoreChanged(initialScore + totalAwarded)` for every award (arrival and answer).

### 7. Wire navigation
- Add one `data object` route per content in `Screen.kt`.
- Add imports and composable blocks in `AppNavigation.kt`; wire back → hub and `onNavigatePrev`/`onNavigateNext` between the screens. Add the routes to the `simpleRoutes` set.
- In the hub screen add an `onNavigateTo*` parameter and call it from the exercise button's `onClick`, marking the section complete there too.

### 8. Build & verify
- Run `.\gradlew.bat assembleDebug` and fix unresolved references.
- Confirm the JSON was bundled under `app/build/intermediates/assets/debug/mergeDebugAssets/{lang}/<uuid>.json`.
