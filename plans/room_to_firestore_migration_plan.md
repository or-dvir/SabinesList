# Room → Firestore Migration Plan
## Sabine's List

---

## Firestore Structure

```
users/{uid}/
    lists/{listId}/
        items/{itemId}
```

No user document. `uid` comes from Firebase Auth.

### Document Fields

**UserListDocument**
- `id: String` (annotated with `@DocumentId`)
- `name: String`

**ListItemDocument**
- `id: String` (annotated with `@DocumentId`)
- `name: String`
- `isChecked: Boolean`

---

## Phase 1 — Firebase Setup

### Gradle

- ✅ firebase-bom
- ✅ firebase-firestore
- ✅ firebase-auth
- ✅ play-services-auth (for Google Sign-In)

### Configuration
- ✅ Add `google-services.json` to production app
- ✅ Add `google-services.json` to test app
- ✅ Apply `com.google.gms.google-services` plugin in `app/build.gradle.kts`

### Hilt Module — `FirebaseModule`
- ✅ Provides `FirebaseAuth.getInstance()`
- ✅ Provides `FirebaseFirestore.getInstance()`
- ✅ Enable offline persistence on the `FirebaseFirestore` instance (call once on init)

### Firestore Security Rules

- ✅ Apply a security rule for the production firebase project which only allows authenticated 
users to read/write only their own data

- ❌❌❌ ONCE AUTHENTICATION IS FULLY IMPLEMENTED, Apply a security rule for the test firebase 
project which only allows yourself to read/write.

---

## Phase 1.5 — Crashlytics

### Add firebase-crashlytics dependency
- ❌ Apply com.google.firebase.crashlytics plugin in `app/build.gradle` (or `.kts`)
- ❌ Enable in Firebase console
- ❌ Log caught exceptions manually where needed: FirebaseCrashlytics.getInstance().recordException(e)

---

## Phase 2 — Firestore Data Layer

### Data Classes
- ✅ `UserListDocument` and `ListItemDocument`
- ✅ Annotate `id` fields with `@DocumentId`
- ✅ Remove `@PropertyName` from `id` fields to ensure clean Firestore autogeneration (ID stays in metadata only)

### Prepare app-level models for migration
- **Reverted**: 
    - *Original Plan*: Change `UserList.id` and `ListItem.id` from `Int` to `String`.
    - *Actual Execution*: Kept `UserList.id` and `ListItem.id` as `Int` to maintain 100% compatibility with Room until migration. 
    - This allows for a "Parallel Repository" strategy where Room remains stable.
- `ListItem.listId` is still needed for backwards compatibility with Room.

### Mappers
- ✅ `UserListEntity.toDocument()` and `ListItemEntity.toDocument()`
- ✅ `UserList.toDocument()` and `ListItem.toDocument()`
- ✅ Reverse mappers for reading from Firestore back into app-layer models (`toUserList`, `toListItem`)
- ✅ Mappers handle the conversion between `Int` (app) and `String` (Firestore) IDs (via `toString()` and `toIntOrNull()`).

### Repositories
Firestore repos use **separate interfaces** from Room repos to allow for Firestore-specific optimizations (String IDs, `Result<Unit>` returns, custom Error types) without touching stable Room code.

- ✅ `BaseFirestoreRepository`: Shared infrastructure for logging, `doOnCollection` helpers, and coroutine cancellation.
- ✅ `FirestoreListItemsRepository`:
    - ✅ `getAllByAlphabet(listId: String): Flow<ListItemsResult>`
    - ✅ `insert(listItem: ListItem, listId: String): Result<Unit>`
    - ✅ `update(listItem: ListItem, listId: String): Result<Unit>`
    - ✅ `markAllUnchecked(listId: String): Result<Unit>`
    - ✅ `delete(listItemId: String, listId: String): Result<Unit>`
- ✅ `FirestoreUserListsRepository`:
    - ✅ `getAllSortedByAlphabet(): Flow<UserListsResult>`
    - ✅ `insert(list: UserList): Result<Unit>`
    - ✅ `update(list: UserList): Result<Unit>`
    - ✅ `delete(listId: String): Result<Unit>`

### Hilt
- ✅ Bind Firestore repo implementations in `RepositoriesModule`.
- ✅ Parallel bindings: both Room and Firestore implementations are available for injection via their respective interfaces.

### Tests
- ✅ Unit tests for Firestore repos using MockK.
    - Verified `BaseFirestoreRepository` infrastructure (Auth checks, Error wrapping, Cancellation re-throwing).
    - Verified `markAllUnchecked` batch logic (fetch sequence, commit gatekeeping).
    - Verified `toDocument` mapping integration.

---

## Phase 3 — Auth

### AuthRepository
- `signInWithGoogle(idToken: String): Result<FirebaseUser>`
- `signInWithEmail(email: String, password: String): Result<FirebaseUser>`
- `registerWithEmail(email: String, password: String): Result<FirebaseUser>`
- `sendVerificationEmail()`
- `isEmailVerified(): Boolean`
- `logout() 
- `deleteAccount()` → see Phase 7

### Hilt
- `AuthModule` provides `AuthRepository`

### Login/Signup Screen (Voyager)
- `AuthScreen` with toggle: Google / Email
- Google: use `ActivityResultContracts` + `GoogleSignInClient`
- Email: fields for email + password, register vs login toggle
- After registration: prompt user to verify email, block access until verified
- On successful login → launch routing logic (Phase 5)

### MainActivityViewModel
- Observes `FirebaseAuth.authStateChanges()`
- Exposes `isLoggedIn: StateFlow<Boolean>`
- Controls whether to show `AuthScreen` or main app

---

## Phase 4 — Migration (Existing Users)

### Migration Flag
- No explicit flag needed
- If Room DB file exists on device → migration has not happened
- After successful migration → delete Room DB file
- Check via: `context.getDatabasePath("your_db_name").exists()`

### MigrationRepository
- `migrateToFirestore(): Flow<MigrationProgress>`
- `MigrationProgress`: sealed class with `InProgress(percent: Float)`, `Success`, `Failure(error: Throwable)`
- Reads all `UserListEntity` from Room
- For each list: uploads `UserListDocument` to Firestore, then uploads all its `ListItemEntity` as `ListItemDocument`
- Uses Firestore batch writes where possible (max 500 ops per batch)
- On success: delete Room DB file
- On failure: do NOT touch Room DB — data is safe, show retry option

### Migration Walkthrough Screen (Voyager)
- Shown only when: user is logged in AND Room DB file exists
- Page 1: explanation ("Your data will now be stored securely in the cloud")
- Page 2: "Migrate Now" button + "Later" button
    - "Later" dismisses and re-shows on next launch
    - "Migrate Now" → navigates to progress screen
- Page 3 (progress): shows % progress from `MigrationProgress` flow
- Page 4: success message OR failure message + retry button

---

## Phase 5 — Launch Routing

On app launch, `MainActivityViewModel` determines the start destination:

```
Not logged in                   → AuthScreen
Logged in + Room DB exists      → MigrationWalkthroughScreen
Logged in + no Room DB          → UserListsScreen (Firestore)
```

---

## Phase 6 — Remove Room *(after all known users migrated)*

- Remove Room entities, DAOs, repositories, `AppDatabase`
- Remove `ListItem.listId` as it is not needed by firebase
- Remove Room Hilt module / bindings
- Remove Room Gradle dependencies
- Remove Room unit tests
- Expand and solidify Firestore unit/UI tests
- Remove migration-related code (`MigrationRepository`, walkthrough screen, migration flag check)
- Remove launch routing condition for Room DB existence

---

## Phase 7 — Account Deletion

### Flow
1. User taps "Delete Account" in preferences
2. Confirmation dialog (irreversible warning)
3. Re-authenticate user (Firebase requires recent login before deletion)
    - Show re-auth dialog: Google re-sign-in or email+password prompt
    - Handle `FirebaseAuthRecentLoginRequiredException`
4. Delete all Firestore data:
    - For each list under `users/{uid}/lists`:
        - Delete all items in `users/{uid}/lists/{listId}/items`
        - Delete the list document
5. Delete Firebase Auth account: `FirebaseAuth.currentUser?.delete()`
6. Navigate to `AuthScreen`, clear back stack

### Notes
- Deletion of subcollections must be done manually (Firestore does not cascade)
- Do Firestore deletion before Auth deletion — if Auth deletion fails, user can retry; if Firestore deletion fails, data isn't orphaned without an account
- Show loading state during deletion

---

## General Notes

- Room and Firestore coexist until Phase 6
- Room is the source of truth for existing users until migration completes
- Firestore is the source of truth for new users and post-migration users
- Offline support: built into Firestore, enabled in Phase 1
- Multi-device support: automatic once user logs in on another device
- Alphabetical sorting done client-side (no `orderBy` needed in Firestore queries)
