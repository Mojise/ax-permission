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
