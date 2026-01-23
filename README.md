# ax-permission

[![](https://jitpack.io/v/mojise/ax-permission.svg)](https://jitpack.io/#mojise/ax-permission)
![Min SDK](https://img.shields.io/badge/Min%20SDK-26-green)
![Target SDK](https://img.shields.io/badge/Target%20SDK-35-blue)

Android 앱에서 **런타임 권한**과 **특별 권한**을 UI 기반으로 쉽게 요청할 수 있는 라이브러리입니다.

- ✅ 30+ 권한에 대한 **아이콘, 제목, 설명 자동 제공**
- ✅ **필수/선택 권한** 분리 지원
- ✅ Settings 이동이 필요한 **특별 권한** 자동 처리
- ✅ **라이트/다크/시스템 테마** 지원
- ✅ 색상, 코너, 패딩 등 **UI 커스터마이징**
- ✅ **Kotlin DSL** 기반의 직관적인 API

---

## 📱 스크린샷

<!-- 스크린샷/GIF를 여기에 추가하세요 -->

---

## 🚀 빠른 시작

```kotlin
AxPermission.from(this)
    .setAppName(R.string.app_name)
    .setRequiredPermissions {
        add(Manifest.permission.CAMERA)
        add(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    }
    .setCallback(object : AxPermission.Callback {
        override fun onRequiredPermissionsAllGranted(context: Context) {
            // 모든 필수 권한이 허용됨 → 다음 화면으로 이동
        }
        override fun onRequiredPermissionsAnyOneDenied() {
            // 필수 권한 중 하나라도 거부됨
        }
    })
    .checkAndShow()
```

---

## 📦 설치

### 1. settings.gradle.kts

```kotlin
dependencyResolutionManagement {
    repositories {
        // ...
        maven { url = uri("https://jitpack.io") }
    }
}
```

### 2. build.gradle.kts (app)

```kotlin
dependencies {
    implementation("com.github.mojise:ax-permission:2.0.3")
}
```

---

## 📖 사용법

### 기본 사용법

```kotlin
AxPermission.from(this)
    .setAppName(R.string.app_name)  // 필수: 앱 이름
    .setRequiredPermissions {
        // 단일 권한
        add(Manifest.permission.CAMERA)

        // 권한 그룹 (여러 권한을 하나의 항목으로 표시)
        add(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )

        // 특별 권한 (Settings 이동 필요)
        add(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
    }
    .setOptionalPermissions {
        add(Manifest.permission.READ_CONTACTS)
    }
    .setCallback(object : AxPermission.Callback {
        override fun onRequiredPermissionsAllGranted(context: Context) {
            // 성공: 다음 화면으로 이동
            startActivity(Intent(context, MainActivity::class.java))
        }
        override fun onRequiredPermissionsAnyOneDenied() {
            // 실패: 앱 종료 또는 안내
            finish()
        }
    })
    .checkAndShow()
```

### 테마 설정

```kotlin
AxPermission.from(this)
    .setDayNightTheme()    // 시스템 테마 따름 (기본값)
    // .setOnlyDayTheme()  // 라이트 테마 고정
    // .setOnlyNightTheme() // 다크 테마 고정
    // ...
```

### UI 커스터마이징

```kotlin
AxPermission.from(this)
    .setAppName(R.string.app_name)
    .setIconPaddingsDp(12)              // 아이콘 패딩 (기본: 10dp)
    .setCornerRadiusDp(12)              // 코너 반경 (기본: 8dp)
    .setBottomSheetCornerRadiusDp(20)   // 바텀시트 코너 (기본: 16dp)
    .setPrimaryColor(R.color.my_primary)
    .setGrantedItemBackgroundColor(R.color.my_granted_bg)
    .setHighlightColor(R.color.my_highlight)  // 진행 중 권한 하이라이트 색상
    // ...
```

### 커스텀 리소스 사용

기본 제공되지 않는 권한이나 커스텀 아이콘/텍스트를 사용하려면:

```kotlin
.setRequiredPermissions {
    add(
        Manifest.permission.RECORD_AUDIO,
        iconResId = R.drawable.ic_custom_mic,
        titleResId = R.string.custom_mic_title,
        descriptionResId = R.string.custom_mic_description,
    )
}
```

### Android 버전별 권한 처리

```kotlin
.setRequiredPermissions {
    // 미디어 권한 (Android 버전별 분기)
    add(
        permissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
            )
            else -> arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        }
    )
}
```

---

## 🔐 지원 권한

### 런타임 권한

| 분류 | 권한 | 설명 |
|------|------|------|
| **카메라** | `CAMERA` | 카메라 |
| **마이크** | `RECORD_AUDIO` | 마이크 (오디오 녹음) |
| **위치** | `ACCESS_FINE_LOCATION` | 정확한 위치 |
| | `ACCESS_COARSE_LOCATION` | 대략적인 위치 |
| | `ACCESS_BACKGROUND_LOCATION` | 백그라운드 위치 |
| | `ACCESS_MEDIA_LOCATION` | 미디어 위치 정보 |
| **미디어** | `READ_MEDIA_IMAGES` | 사진 (Android 13+) |
| | `READ_MEDIA_VIDEO` | 동영상 (Android 13+) |
| | `READ_MEDIA_AUDIO` | 오디오 (Android 13+) |
| | `READ_MEDIA_VISUAL_USER_SELECTED` | 선택된 미디어 (Android 14+) |
| **저장소** | `READ_EXTERNAL_STORAGE` | 저장소 읽기 (Android 12 이하) |
| | `WRITE_EXTERNAL_STORAGE` | 저장소 쓰기 (Android 9 이하) |
| **알림** | `POST_NOTIFICATIONS` | 알림 (Android 13+) |
| **연락처** | `READ_CONTACTS` | 연락처 읽기 |
| | `WRITE_CONTACTS` | 연락처 쓰기 |
| **캘린더** | `READ_CALENDAR` | 캘린더 읽기 |
| | `WRITE_CALENDAR` | 캘린더 쓰기 |
| **전화** | `READ_PHONE_STATE` | 전화 상태 읽기 |
| | `READ_PHONE_NUMBERS` | 전화번호 읽기 |
| | `CALL_PHONE` | 전화 걸기 |
| | `SEND_SMS` | SMS 전송 |
| **블루투스** | `BLUETOOTH_CONNECT` | 블루투스 연결 (Android 12+) |
| | `BLUETOOTH_SCAN` | 블루투스 스캔 (Android 12+) |
| **기타** | `NEARBY_WIFI_DEVICES` | 근처 Wi-Fi 기기 (Android 13+) |
| | `ACTIVITY_RECOGNITION` | 활동 인식 |

### 특별 권한 (Settings 이동)

| Settings Action | 설명 |
|-----------------|------|
| `ACTION_MANAGE_OVERLAY_PERMISSION` | 다른 앱 위에 표시 |
| `ACTION_NOTIFICATION_LISTENER_SETTINGS` | 알림 접근 |
| `ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | 배터리 최적화 제외 |
| `ACTION_ACCESSIBILITY_SETTINGS` | 접근성 서비스 |
| `ACTION_USAGE_ACCESS_SETTINGS` | 사용 정보 접근 |
| `ACTION_NFC_SETTINGS` | NFC 설정 |
| `ACTION_MANAGE_WRITE_SETTINGS` | 시스템 설정 변경 |
| `ACTION_REQUEST_SCHEDULE_EXACT_ALARM` | 정확한 알림 (Android 12+) |

---

## 🔧 API 레퍼런스

### AxPermission

| 메서드 | 설명 |
|--------|------|
| `from(activity)` | 권한 요청 빌더 생성 |

### AxPermissionComposer

| 메서드 | 설명 | 기본값 |
|--------|------|--------|
| `setAppName(strResId)` | 앱 이름 설정 **(필수)** | - |
| `setDayNightTheme()` | 시스템 테마 따름 | ✅ |
| `setOnlyDayTheme()` | 라이트 테마 고정 | - |
| `setOnlyNightTheme()` | 다크 테마 고정 | - |
| `setIconPaddingsDp(dp)` | 아이콘 패딩 | 10dp |
| `setCornerRadiusDp(dp)` | 코너 반경 | 8dp |
| `setBottomSheetCornerRadiusDp(dp)` | 바텀시트 코너 반경 | 16dp |
| `setPrimaryColor(colorResId)` | Primary 색상 | - |
| `setGrantedItemBackgroundColor(colorResId)` | 허용된 권한 배경색 | - |
| `setHighlightColor(colorResId)` | 진행 중 권한 하이라이트 색상 | - |
| `setRequiredPermissions { }` | 필수 권한 설정 | - |
| `setOptionalPermissions { }` | 선택 권한 설정 | - |
| `setCallback(callback)` | 결과 콜백 설정 | - |
| `checkAndShow()` | 권한 확인 및 UI 표시 | - |

### PermissionBuilder (DSL)

| 메서드 | 설명 |
|--------|------|
| `add(vararg permissions)` | 권한 추가 (기본 리소스 사용) |
| `add(permissions, iconResId, titleResId, descriptionResId)` | 커스텀 리소스로 권한 추가 |

---

## 📋 요구 사항

- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35 (Android 15)
- **Kotlin**: 1.9.0+

---

## 📄 라이선스

```
Copyright 2024 AX Team

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
