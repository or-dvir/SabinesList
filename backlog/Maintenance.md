# Maintenance Tasks

This file tracks general code maintenance tasks.

---

### [TASK-001] 
*   **Description**: go over visibility of all functions/classes. surely they shouldn't all be `public`.

### [TASK-002]
*   **Description**: i have multiple warnings throughout the project about annotations like
`@StringRes` and `@drawableRes` (but not only) which say 
"This annotation is currently applied to the value parameter only, but in the future it will also be applied to field." 