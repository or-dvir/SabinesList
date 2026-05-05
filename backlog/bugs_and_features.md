# Backlog: Bugs & Feature Requests

This file tracks identified bugs and future feature ideas.

---

## 🐞 Bugs

### [BUG-002] Swipe-to-Dismiss Sensitivity
*   **Description**: The threshold for triggering the "Are you sure?" delete dialog is too sensitive. Even though it's set to ~70%, it triggers even on a tiny drag.
*   **Note**: Might be a bug with the system/Material implementation or how the threshold is being interpreted.
*   **Note**: i am seeing this message on `import androidx.compose.material.FractionalThreshold`: 
"'data class FractionalThreshold : ThresholdConfig' is deprecated. Material's Swipeable has been 
replaced by Foundation's AnchoredDraggable APIs".
is there a new "library" for swipeable items?

---

## ✨ Feature Requests

### [FEAT-001] Nested/Master Items (Sub-lists)
*   **Description**: Ability to create "Master" items that contain sub-items.
*   **Use Case**: In a "Pack for Vacation" list, have a "Kids" group that can be expanded/collapsed and toggled as a whole.
*   **UI Idea**: Expandable rows within the list view.

### [FEAT-002] Contextual Search Hint
*   **Description**: The search screen hint is too generic ("Search"). It should specify the search context.
*   **Improvement**: Change hint to "Search <List Name>" when searching within a specific list, or "Search all lists" if applicable.

### [FEAT-003] Migrate to Firebase DB (Needs Refinement)
*   **Description**: Migrate the local database to Firebase (Realtime Database or Firestore).
*   **Goal**: Provide automated backup and possibly sync between devices.
*   **To Be Refined / Considered**:
    *   **Data Migration**: How to move existing local user data to the cloud on first run?
    *   **User Accounts**: Will users need to sign in (Firebase Auth)? How to handle guest mode?
    *   **Offline Support**: Ensuring the app still works perfectly without internet (Firestore's offline persistence).
    *   **Setup Complexity**: Creating Firebase projects, adding `google-services.json`, and handling API keys.
    *   **Privacy/Security**: Implementation of Firebase Security Rules to protect user data.

### [FEAT-004] add a change log file to develop (and main?) branch(es)
*   **Description**: since not every feature will result in a new release, we should keep track of 
features added so that when we do make a release, we can include accurate release notes.