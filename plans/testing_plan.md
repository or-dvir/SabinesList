# Testing Plan - Comprehensive Coverage

## Summary
Add unit, repository, and UI tests to ensure the reliability and correctness of the Sabine's List application.

---

## 1. Dependencies
Add the following libraries to `build.gradle` (using BOM where available):
- **Unit Testing**: `MockK`, `Kotlinx Coroutines Test`, `Turbine`
- **UI/Integration Testing**: `Hilt Testing`

---

## 2. Phase 1: Unit Tests
Focus on business logic and state management in `ScreenModel`s.

### Tools & Utilities
- `MainDispatcherRule`: A JUnit 4 rule to swap `Dispatchers.Main` with a `TestDispatcher`.

### UserListsScreenModelTest
- Verify `usersListsFlow` filtering (search query, empty states).
- Verify event handling: `CreateNewList`, `RenameList`, `DeleteList`.
- Verify `SideEffect` delivery (e.g., Toast messages).

### ListItemsScreenModelTest
- Verify `listItemsFlow` filtering and sorting (All/Checked/Unchecked).
- Verify CRUD events: `CreateNewItem`, `RenameItem`, `DeleteItem`.
- Verify `ChangeItemCheckedState` updates repository.
- Verify `BottomNavigationItemClicked` updates state correctly.

### PreferencesScreenModelTest
- Verify `SetTheme` event updates the repository.

---

## 3. Phase 2: Repository Tests
Focus on data persistence and logic in repositories.

### UserListsRepositoryImplTest & ListItemsRepositoryImplTest
- Use in-memory Room database.
- Verify CRUD operations.
- Verify Flow emissions on data changes.

---

## 4. Phase 3: UI Tests
Focus on user interactions and visual states.

### UserListsScreenTest
- **Empty State**: Verify "Empty View" is visible when no lists exist.
- **Menu Visibility**: Verify "Search" is hidden when no lists exist.
- **Navigation**: Verify clicking a list navigates to `ListItemsScreen` with correct ID.
- **Search Mode**: 
    - Verify entering search mode displays correct results.
    - Verify closing search mode returns to normal view.
- **CRUD**: 
    - Verify adding a list updates the UI.
    - Verify renaming a list updates the UI.
    - Verify "Are you sure you want to delete" dialog appears.
    - Verify deleting a list updates the UI.

### ListItemsScreenTest
- **Filtering**: 
    - Verify bottom navigation filters items correctly (wiring check).
    - **Dynamic Filtering**: Verify that unchecking an item while in the "Checked" filter makes it disappear from the list.
- **Swipe Actions**: Verify swiping to delete/edit works as expected (visibility of dialogs).
- **CRUD**:
    - Verify adding/renaming items updates the list visually.
    - Verify "Are you sure you want to delete" dialog appears.
- **Checking**: Verify clicking an item's checkbox toggles its checked state.

### PreferencesScreenTest
- **Theme Selection**: 
    - Verify selecting a theme updates the radio buttons.
    - Verify theme selection persists in storage.
    - Verify the app theme actually changes (if possible via color checks).

---

## 5. Verification Commands
- **Unit Tests**: `./gradlew app:testDebugUnitTest`
- **Instrumented Tests**: `./gradlew app:connectedDebugAndroidTest`
