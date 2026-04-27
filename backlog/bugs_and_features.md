# Backlog: Bugs & Feature Requests

This file tracks identified bugs and future feature ideas. It is tracked by Git on development branches but blocked from merging into `main` via local hooks and GitHub Actions.

---

## 🐞 Bugs

### [BUG-001] Action Bar Title Spacing (Multiline)
*   **Description**: When the Action Bar title spans two lines, it feels cramped. It needs more vertical spacing at the bottom (and possibly the top).
*   **Improvement**: Consider limiting the maximum length of the title or adding padding to the multiline layout.

### [BUG-002] Swipe-to-Dismiss Sensitivity
*   **Description**: The threshold for triggering the "Are you sure?" delete dialog is too sensitive. Even though it's set to ~70%, it triggers even on a tiny drag.
*   **Note**: Might be a bug with the system/Material implementation or how the threshold is being interpreted.

---

## 🛠️ Infrastructure / DevOps

### [INFRA-001] Version Bump Requirement
*   **Description**: 
    Every Pull Request merged into `main` must include an increase in `versionCode` and a change in `versionName`.
*   **Safeguard**: 
    Implemented via GitHub Action (`check-version-bump.yml`) and required status checks.

---

## ✨ Feature Requests

### [FEAT-001] Nested/Master Items (Sub-lists)
*   **Description**: Ability to create "Master" items that contain sub-items.
*   **Use Case**: In a "Pack for Vacation" list, have a "Kids" group that can be expanded/collapsed and toggled as a whole.
*   **UI Idea**: Expandable rows within the list view.

### [FEAT-002] Contextual Search Hint
*   **Description**: The search screen hint is too generic ("Search"). It should specify the search context.
*   **Improvement**: Change hint to "Search <List Name>" when searching within a specific list, or "Search all lists" if applicable.
