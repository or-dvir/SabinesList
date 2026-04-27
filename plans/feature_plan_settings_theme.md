# Feature Plan: System Theme & Settings Screen

## Goal
Transition from a simple Light/Dark theme toggle to a dedicated Settings screen that supports "System Default", "Light", and "Dark" themes, and includes an "About" section with credits and app version.

## 1. Data Layer Changes (`UserPreferencesRepository`)
*   **New Enum**: Create an `AppTheme` enum: `SYSTEM`, `LIGHT`, `DARK`.
*   **Update Interface**: 
    *   Replace `isDarkMode(): Flow<Boolean>` with `getAppTheme(): Flow<AppTheme>`.
    *   Replace `setDarkMode(Boolean)` with `setAppTheme(AppTheme)`.
*   **Update Implementation**:
    *   Change the DataStore key from `booleanPreferencesKey` to `intPreferencesKey`.
    *   Map the integer values to the `AppTheme` enum (e.g., 0=System, 1=Light, 2=Dark).

## 2. ViewModel & Theme Logic (`MainActivityViewModel` & `MainActivity`)
*   **Update ViewModel**: Expose `appThemeFlow` instead of `isDarkModeFlow`.
*   **MainActivity Logic**:
    *   Observe `appTheme`.
    *   Determine the final `isDark` boolean:
        *   If `SYSTEM`: Use `isSystemInDarkTheme()`.
        *   If `LIGHT`: `false`.
        *   If `DARK`: `true`.
    *   Pass this boolean to `SabinesListTheme`.

## 3. Navigation & UI
*   **New Screen**: `SettingsScreen : Screen` (using Voyager).
*   **UI Components in Settings**:
    *   **Theme Category**: A list item that opens a selection (either a sub-menu, a dialog, or RadioButtons) for System/Light/Dark.
    *   **About Category**:
        *   "Credits" item (move logic from `SharedMenu`).
        *   "Version" item (display `BuildConfig.VERSION_NAME`).
*   **Update `SharedMenu`**:
    *   Remove the "Change Theme" logic.
    *   **Decision**: Remove the "More" overflow menu entirely.
    *   **Decision**: Replace it with a direct "Settings" icon (`ic_settings`) that navigates to the `SettingsScreen`.
    *   Ensure any other items previously in the overflow menu (like "Credits") are now accessible only via the `SettingsScreen`.

## 4. Resources
*   Add strings for "System Default", "Settings", "Theme", "App Version", etc., to `strings.xml`.
*   Import a settings icon (`ic_settings.xml`) if not already present.

## Implementation Steps (Order)
1. Define `AppTheme` enum.
2. Update `UserPreferencesRepository`.
3. Update `MainActivityViewModel` and `MainActivity` to handle the new enum.
4. Create the `SettingsScreen` skeleton.
5. Update `SharedMenu` to navigate to `SettingsScreen`.
6. Fill in `SettingsScreen` details (Theme selection, Credits, Version).
