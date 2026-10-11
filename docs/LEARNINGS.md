# Learnings: crash causes and how to avoid them

## 1. Startup crash: `NullPointerException` in `ScreenCatalog.find` (Gson + `lazy`)

**Symptom:** the app crashed right after launch, as soon as the splash screen looked up the first screen.

**Cause:** `ScreenCatalog` had a property initializer, `private val byId by lazy { ... }`. Gson builds
Kotlin classes with `Unsafe` when there is no no-arg constructor, so constructors and property initializers
never run. The `lazy` delegate stayed `null`, and the first `find()` call threw an NPE.

**Fix:** `find()` searches `screens` directly (`screens.firstOrNull { it.id == id }`).

**Rules for classes parsed by Gson (`model/ScreenModels.kt`):**
- Keep them plain data holders: no `lazy`, no initialised private properties, no `init` blocks.
- Put derived or cached data in a separate object built after parsing, for example in `ScreenRepository`.
- Give every optional JSON field a default value in the constructor, which Gson ignores, and treat it as nullable.
- Missing JSON fields arrive as `null` even for non-null Kotlin types, so declare them nullable.

**Regression test:** `ScreenCatalogTest` parses `assets/screens.json` the same way the app does and calls `find()`.

## 2. How to diagnose a startup crash quickly

1. Open Logcat, filter on `AndroidRuntime` or the `Switsh/` tag, and read the first `Caused by:`.
2. Navigation and actions are logged by `ScreenFragment` (`show`, `navigate`, `action`), so the last `Switsh/` line shows which screen was active.
3. If the IDE console has no stack trace (a debug launch that disconnects), re-run without the debugger and read Logcat.
4. Reproduce it as a plain JVM unit test when the cause is in parsing or model code. It is faster than a device run.

## 3. Pitfalls met while building this app

| Pitfall | Lesson |
|---|---|
| `androidx.fragment.app.commit {}` was unresolved | It lives in `fragment-ktx`. Use `beginTransaction()` or add the dependency. |
| `git reset` to another commit discarded the generated renderer | Check `git reflog` and restore with `git checkout <sha> -- <paths>`. Commit work before switching branches. |
| Generated files can go stale | `assets/screens.json` and `ic_*.xml` come from `tools/prototype-converter`. Rerun `node convert.js` instead of editing them by hand. |
| `screens.json` only holds exported sections | Links to sections not in `INCLUDE` show "Section à venir". Extend `INCLUDE` in `convert.js`. |
| Placeholder PIN | Any 4 digits unlock the demo account. This is not real authentication. |
