# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**ax-permission** is an Android permission management library developed by the AX team. It provides a comprehensive UI-based permission request flow for Android applications, handling both standard runtime permissions and special system permissions that require navigating to Settings.

- **Language:** Kotlin
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 35 (Android 15)
- **Distribution:** Published via JitPack as `com.github.mojise:ax-permission`

## Build & Development Commands

### Building the Library
```bash
# Build the library module
gradlew :ax-permission:assembleRelease

# Build debug variant
gradlew :ax-permission:assembleDebug

# Build the sample app
gradlew :app:assembleDebug
```

### Testing & Verification
```bash
# Run tests
gradlew test

# Run instrumented tests
gradlew connectedAndroidTest

# Install sample app to device
gradlew :app:installDebug
```

### Publishing
```bash
# Publish to Maven Local (for local testing)
gradlew :ax-permission:publishToMavenLocal

# Generate release build
gradlew :ax-permission:assembleRelease
```

### Code Quality
```bash
# Clean build
gradlew clean

# Full rebuild
gradlew clean build
```

## Architecture

### Module Structure
- **ax-permission/** - Main library module containing all permission management logic
- **app/** - Sample application demonstrating library usage

### Core Architecture Pattern

The library follows a **reactive MVVM architecture** with StateFlow-based state management:

```
Entry Point: AxPermission (Singleton)
    ↓
AxPermissionComposer (Builder)
    ↓
PermissionActivity (Container)
    ↓
PermissionViewModel (StateFlow-based state)
    ↓
UI Components (Fragments, BottomSheets, Adapters)
```

### Key Components

**1. AxPermission Entry Point**
- `AxPermission.kt` - Singleton object providing the main API
- `AxPermissionComposer` - Builder class for configuring permission requests
- Builder pattern API: `AxPermission.from(context).setCallback().checkAndShow()`

**2. Permission Data Layer**
- `PermissionType.kt` - Enum defining available permissions (DrawOverlays, AccessNotifications, IgnoreBatteryOptimizations)
- `PermissionItemData.kt` - Generates UI items with localized strings and icons
- `PermissionChecker.kt` - Permission status verification utilities
- `Item.kt` - Sealed interface hierarchy (Header, Footer, Permission, Divider)

**3. ViewModel & State Management**
- `PermissionViewModel.kt` - Single source of truth using StateFlow
- Maintains `_items: MutableStateFlow<List<Item>>`
- Derived StateFlows: `permissionItems`, `isAllPermissionsGranted`, `isRequiredPermissionsAllGranted`, etc.
- All UI state derived reactively from the items StateFlow

**4. UI Layer**
- `PermissionActivity.kt` - Main container with RecyclerView and action buttons
- `PermissionBottomSheetFragment.kt` - ViewPager2-based detail view for permission requests
- `PermissionBottomSheetContentFragment.kt` - Individual permission detail pages
- `PermissionListAdapter.kt` - RecyclerView adapter using DiffUtil
- `FloatingBottomSheetDialogFragment.kt` - Base class for styled bottom sheets

**5. Permission Request Flow**
- `PermissionRequestHelper.kt` - Routes permission requests to system Settings intents
- Action-type permissions (e.g., DrawOverlays) require Settings navigation
- Normal permissions use standard runtime permission flow
- Auto-advancement after successful grants with configurable delays (300ms for action-type)

### Data Flow

```
User Action → ViewModel updates _items
                    ↓
            StateFlows emit new state
                    ↓
        UI collectors receive updates
                    ↓
            UI re-renders automatically
```

### Important Architectural Notes

- **StateFlow-Driven:** All UI state is derived from ViewModel StateFlows using `.map().stateIn()` pattern
- **Reactive Updates:** Permission status changes trigger automatic UI updates via Flow collectors
- **Edge-to-Edge Design:** Uses WindowInsets API for modern full-screen layouts
- **Type-Safe Items:** Sealed interface pattern for RecyclerView items prevents runtime errors
- **Builder Limitation:** Current implementation has empty placeholder methods for `setRequiredPermissions()`/`setOptionalPermissions()` - permissions are hardcoded in ViewModel initialization

## Package Name Change

**CRITICAL:** This project recently underwent a package name refactoring from `kr.co.permission.*` to `com.ax.library.ax_permission.*`. When working with this codebase:

- All file paths now use `com/ax/library/ax_permission/` structure
- Old package references may exist in git history
- AndroidManifest.xml files have been updated with new package names
- Ensure all new code uses `com.ax.library.ax_permission` package

## Resource Privacy

**All library resources are private** and not exposed to consuming applications. This is enforced via:

1. **public.xml** - Located at `ax-permission/src/main/res/values/public.xml`
   - Empty public.xml means all resources are private by default
   - Only resources explicitly declared in this file are exposed to library consumers
   - Currently no resources are public as the library only exposes code-based APIs

2. **resourcePrefix** - In `ax-permission/build.gradle.kts:41`
   - **ENABLED:** `resourcePrefix = "ax_permission_"`
   - Enforces that all XML resource names (strings, colors, dimens, etc.) start with `ax_permission_`
   - File names (layouts, drawables) are not enforced by this setting
   - Build will fail if new resources don't follow the naming convention

**When adding new resources:**
- **REQUIRED:** All resource names in values XML files MUST use `ax_permission_` prefix
  - ✓ `<string name="ax_permission_example">...</string>`
  - ✗ `<string name="example">...</string>` (Build error)
- **RECOMMENDED:** Use `ax_permission_` prefix for file names (layouts, drawables)
  - ✓ `activity_ax_permission.xml`, `ic_ax_permission_icon.png`
  - Note: File names are not enforced but maintain consistency
- Do NOT add resources to public.xml unless they need to be exposed
- Resources used internally by the library remain private automatically

## Visibility Modifiers (접근 제한자)

**CRITICAL:** This is a library project, so **visibility modifiers must be strictly controlled** to prevent exposing internal implementation details to library consumers.

### Guiding Principles

1. **Public API Surface**: Only expose what library consumers need to use
2. **Internal by Default**: All implementation details should be `internal` or `private`
3. **Explicit Public**: Only explicitly mark as public what needs to be in the public API

### Current Public API

The library exposes a minimal, clean public API:

**Public Components:**
- `AxPermission` - Singleton object (entry point)
- `AxPermission.Callback` - Interface for permission result callbacks
- `AxPermission.from(Context)` - Static method to create composer
- `AxPermissionComposer` - Builder class (class is public, but constructor is `internal`)
- Public methods in `AxPermissionComposer`:
  - `setOnlyDayTheme()`
  - `setOnlyNightTheme()`
  - `setDayAndNightTheme()`
  - `setCallback(Callback)`
  - `checkAndShow()`

### Internal Components

**All other components are marked `internal`** to hide implementation:

- **UI Components**: All Activities, Fragments, ViewModels, Adapters
  - `PermissionActivity`
  - `PermissionViewModel`, `PermissionViewModelFactory`
  - `PermissionBottomSheetFragment`, `PermissionBottomSheetContentFragment`
  - `PermissionExitBottomSheet`
  - `PermissionListAdapter`
  - `FloatingBottomSheetDialogFragment`
  - Custom views: `InteractiveConstraintLayout`, `InteractiveTextView`

- **Models**: All data models and enums
  - `PermissionType` (enum)
  - `PermissionTheme` (enum)
  - `Item` (sealed interface and all implementations)

- **Utilities**: All helper objects and functions
  - `PermissionChecker` (object)
  - `PermissionItemData` (object)
  - `PermissionRequestHelper` (object)
  - `Constants` (object)
  - All extension functions (ActivityTransitionExts, LifecycleExts, etc.)

### Rules When Adding New Code

**When creating new classes, objects, or interfaces:**

1. **Ask: Does this need to be used outside the library?**
   - NO → Mark as `internal` (most cases)
   - YES → Keep as public, but document why in code comments

2. **For public classes:**
   - Mark constructors as `internal` if users shouldn't directly instantiate them
   - Only expose necessary methods as public
   - Keep internal implementation methods as `private`

3. **Examples:**

```kotlin
// ✓ CORRECT: Public API entry point
object AxPermission {
    internal var callback: Callback? = null  // Internal storage

    @JvmStatic
    fun from(context: Context) = AxPermissionComposer(context)  // Public method

    interface Callback {  // Public interface (users implement this)
        fun onRequiredPermissionsAllGranted()
        fun onRequiredPermissionsAnyOneDenied()
    }
}

// ✓ CORRECT: Public builder with internal constructor
class AxPermissionComposer internal constructor(private val context: Context) {
    fun setCallback(callback: Callback) = apply { ... }  // Public method
    private fun internalHelper() { ... }  // Private helper
}

// ✓ CORRECT: Internal implementation
internal class PermissionActivity : AppCompatActivity() { ... }

// ✓ CORRECT: Internal model
internal enum class PermissionType { ... }

// ✓ CORRECT: Internal utility
internal object PermissionChecker {
    fun check(context: Context, type: PermissionType): Boolean { ... }
}

// ✗ INCORRECT: Exposing internal implementation
class PermissionActivity : AppCompatActivity() { ... }  // Missing 'internal'!

// ✗ INCORRECT: Exposing internal model
enum class PermissionType { ... }  // Missing 'internal'!
```

### Verification

To verify visibility modifiers are correct:

```bash
# Check for classes without visibility modifiers (should all be internal or in AxPermission.kt)
find ax-permission/src/main/java -name "*.kt" -exec grep -H "^class \|^object \|^enum class \|^interface \|^sealed " {} \;
```

All files except `AxPermission.kt` should have `internal` or `private` visibility modifiers on top-level declarations.

## ProGuard Configuration

When integrating this library, consumers must add these ProGuard rules:

```proguard
# gson TypeToken
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Parcelable
-keep interface org.parceler.**
-keep @org.parceler.* class * { *; }
-keep class **$$Parcelable { *; }
-keep class org.parceler.** { *; }
-keep class * implements android.os.Parcelable { *; }
```

## Key Files Reference

- Entry point: `ax-permission/src/main/java/com/ax/library/ax_permission/AxPermission.kt`
- ViewModel: `ax-permission/src/main/java/com/ax/library/ax_permission/ui/PermissionViewModel.kt`
- Main Activity: `ax-permission/src/main/java/com/ax/library/ax_permission/ui/PermissionActivity.kt`
- Permission types: `ax-permission/src/main/java/com/ax/library/ax_permission/model/PermissionType.kt`
- UI item models: `ax-permission/src/main/java/com/ax/library/ax_permission/model/Item.kt`

## Library Dependencies

Core dependencies used in the library module:
- AndroidX Core KTX
- AndroidX AppCompat
- AndroidX Activity & Fragment KTX
- AndroidX Lifecycle ViewModel KTX
- Material Design Components
- View Binding & Data Binding enabled

## Publishing Information

- Published via JitPack: `com.github.mojise:ax-permission`
- Current version: 1.2.2
- Artifact ID: `Ax-Permission`
- Uses `maven-publish` plugin with both debug and release variants

## Adding New Permissions (권한 추가 가이드라인)

When adding new permissions to the library, follow these guidelines to ensure proper granularity and combination cases.

### Permission Granularity Principles

**1. Individual vs. Combined Permissions**

Always consider whether a permission should be:
- **Individual**: Single permission for specific use case
- **Combined**: Multiple permissions requested together for common scenarios

**Key Question:** "Will developers commonly need just one of these, or multiple together?"

### Permission Naming Convention

**Companion object names must match Android SDK constants:**

**For Runtime Permissions:**
- Single permission: Use exact `Manifest.permission.*` name
  - `Manifest.permission.CAMERA` → `Camera()`
  - `Manifest.permission.RECORD_AUDIO` → `RecordAudio()`

- Multiple permissions: Add `Group` suffix or descriptive name
  - `READ_MEDIA_IMAGES` + `READ_MEDIA_VIDEO` + `READ_MEDIA_AUDIO` → `ReadMediaAll()`
  - `READ_MEDIA_IMAGES` + `READ_MEDIA_VIDEO` → `ReadMediaVisual()`
  - `ACCESS_FINE_LOCATION` + `ACCESS_COARSE_LOCATION` → `AccessFineAndCoarseLocation()`

**For Special Permissions:**
- Match `Settings.ACTION_*` name exactly
  - `Settings.ACTION_MANAGE_OVERLAY_PERMISSION` → `ActionManageOverlayPermission()`
  - `Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS` → `ActionNotificationListenerSettings()`

### Granularity Examples

**Example 1: Media/Storage Permissions**

Android 13+ introduced granular media permissions, but **READ_MEDIA_IMAGES and READ_MEDIA_VIDEO are grouped together** as "Visual Media":

```kotlin
// Visual Media (Photos and Videos - GROUPED PERMISSION)
ReadMediaVisual()        // Images + Videos (사진 및 동영상 액세스)
                        // Android groups these together - requesting one grants both

// Audio (Separate permission)
ReadMediaAudio()         // Music and audio files (음악 및 오디오 액세스)

// Combined (All media types)
ReadMediaAll()           // Full access (Visual + Audio)

// Legacy support
ReadExternalStorage()    // Android 12 and below
WriteExternalStorage()   // Android 9 and below
```

**Rationale:**
- **Visual Media is ONE permission group**: READ_MEDIA_IMAGES and READ_MEDIA_VIDEO show the same dialog
- Even if you request only READ_MEDIA_IMAGES, the system dialog says "사진 및 동영상"
- Granting access to images automatically grants access to videos (and vice versa)
- Audio permissions remain separate from visual media
- **DO NOT create separate ReadMediaImages() or ReadMediaVideo()** - they're redundant

**Example 2: Location Permissions**

```kotlin
// Individual permissions
AccessCoarseLocation()   // Approximate location only (weather apps)
AccessFineLocation()     // Precise location (implicitly includes COARSE on Android)

// Explicit combination (for clarity/compatibility)
AccessFineAndCoarseLocation()  // Explicitly request both

// Background permission (separate)
AccessBackgroundLocation()  // Always separate, never auto-combined
```

**Rationale:**
- Weather apps need only approximate location
- Navigation apps need precise location
- Some apps explicitly request both for compatibility
- Background location is ALWAYS a separate permission request per Android guidelines

**Example 3: Read/Write Permissions**

```kotlin
// Individual permissions
ReadContacts()           // Read-only contact viewing apps
WriteContacts()          // Apps that only create/edit contacts

// Common combination
ReadWriteContacts()      // Contact management apps (most common case)
```

**Rationale:**
- Some apps only read contacts (contact pickers)
- Most contact apps need both read and write
- Provide both options for flexibility

### Android Version Considerations

**Handle SDK version differences:**

```kotlin
val PostNotifications get() = Runtime(
    manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        emptyList() // Auto-granted on Android 12 and below
    }
)

val ReadMediaImages get() = Runtime(
    manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        // Fallback to legacy permission
        listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
)
```

**Version Cutoff Rules:**
- Use `Build.VERSION.SDK_INT >= VERSION_CODE` for permissions introduced in specific versions
- Provide fallback to legacy permissions when applicable
- Return `emptyList()` if permission doesn't apply to older versions

### KDoc Documentation Requirements

**Every companion object function and property must have KDoc:**

```kotlin
/** 런타임 권한 - 카메라 ([Manifest.permission.CAMERA]) **/
@JvmStatic
fun Camera() = Camera

/** 런타임 권한 - 카메라 ([Manifest.permission.CAMERA]) **/
val Camera get() = Runtime(
    iconResId = R.drawable.ic_ax_permission_camera,
    titleResId = R.string.ax_permission_camera_name,
    descriptionResId = R.string.ax_permission_camera_description,
    manifestPermissions = listOf(Manifest.permission.CAMERA)
)
```

**KDoc Format:**
- `/** 런타임 권한 - <설명> ([Manifest.permission.PERMISSION_NAME]) **/` for Runtime
- `/** 특별 권한 - <설명> ([Settings.ACTION_NAME]) **/` for Special
- Include Android version if applicable: `Android 13+`, `Android 10+`
- Use `여러 권한:` prefix for multiple permissions

### Checklist for Adding New Permissions

When adding a new permission, verify:

- [ ] **Naming**: Companion object name matches Android SDK constant
- [ ] **Granularity**: Considered individual vs. combined cases
- [ ] **KDoc**: Both function and property have proper KDoc comments
- [ ] **@JvmStatic**: Function has `@JvmStatic` annotation for Java compatibility
- [ ] **Version Check**: Proper SDK version handling with fallbacks
- [ ] **Resources**: Icon, title, description resources exist with `ax_permission_` prefix
- [ ] **Common Combinations**: Added combination cases for common use patterns
- [ ] **Individual Options**: Provided individual permissions for specific use cases

### Common Permission Patterns

**Pattern 1: Single Permission (No Variants)**
```kotlin
/** 런타임 권한 - 카메라 ([Manifest.permission.CAMERA]) **/
@JvmStatic
fun Camera() = Camera
val Camera get() = Runtime(...)
```

**Pattern 2: Read + Write Pair**
```kotlin
ReadContacts()       // Individual read
WriteContacts()      // Individual write
ReadWriteContacts()  // Combined (most common)
```

**Pattern 3: Media Permissions (Android 13+)**
```kotlin
ReadMediaVisual()    // Visual media (Images + Video are GROUPED)
ReadMediaAudio()     // Audio media (separate permission)
ReadMediaAll()       // Full access (Visual + Audio)
```

**Note:** Do NOT create separate `ReadMediaImages()` or `ReadMediaVideo()` - they are the same permission group.

**Pattern 4: Location Hierarchy**
```kotlin
AccessCoarseLocation()          // Approximate only
AccessFineLocation()            // Precise (includes coarse on Android)
AccessFineAndCoarseLocation()   // Explicit both
AccessBackgroundLocation()      // Separate background permission
```

### Reference: Current Permission List

**Runtime Permissions (16 total):**
- Camera
- RecordAudio
- AccessFineLocation, AccessCoarseLocation, AccessFineAndCoarseLocation, AccessBackgroundLocation
- ReadMediaVisual, ReadMediaAudio, ReadMediaAll
- ReadExternalStorage, WriteExternalStorage
- PostNotifications
- ReadContacts, WriteContacts, ReadWriteContacts
- ReadPhoneState, CallPhone
- ReadCalendar, WriteCalendar, ReadWriteCalendar

**Special Permissions (3 total):**
- ActionManageOverlayPermission
- ActionNotificationListenerSettings
- ActionRequestIgnoreBatteryOptimizations

### Important Notes on Media Permissions

**Visual Media Permission Group (Android 13+):**
- `READ_MEDIA_IMAGES` and `READ_MEDIA_VIDEO` are **NOT separate permissions**
- They are grouped together as "Visual Media" by the Android system
- Requesting either one shows the same dialog: "사진 및 동영상 액세스 허용"
- Granting one automatically grants the other
- **Always use `ReadMediaVisual()` for photos/videos** - never create separate Image/Video permissions

**Audio Permission (Android 13+):**
- `READ_MEDIA_AUDIO` is a **separate permission** from visual media
- Shows dialog: "음악 및 오디오 액세스 허용"
- Independent from image/video access

**Android 14+ Enhancement:**
- Adds `READ_MEDIA_VISUAL_USER_SELECTED` for partial photo/video access
- Automatically added to manifest when requesting READ_MEDIA_IMAGES or READ_MEDIA_VIDEO
- Allows users to select specific photos/videos instead of granting full access
