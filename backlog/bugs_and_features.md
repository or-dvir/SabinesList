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
