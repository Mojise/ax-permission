# ax-permission
AX개발팀 권한 관리 라이브러리

## Setup

1. setting.gradle
```
repositories {
	mavenCentral()
	maven { url 'https://jitpack.io' }
}
```
2. bulid.gradle
   
![Release](https://jitpack.io/v/mojise/ax-permission.svg)
```
dependencies {
	implementation 'com.github.mojise:ax-permission:Tag'
}
```
3. proguard-rules.pro
```
# gson TypeToken 예외처리
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Parcelable 예외 처리
-keep interface org.parceler.**
-keep @org.parceler.* class * { *; }
-keep class **$$Parcelable { *; }
-keep class org.parceler.** { *; }
-keep class * implements android.os.Parcelable { *; }
```

*****
# CheckPermission

https://github.com/user-attachments/assets/75205274-a4c2-4814-821f-1ad88d28753e

### kotiln 
```
        /*필수 권한 리스트*/
        val requiredPermissions = AxPermissionList()

        /*선택 권한 리스트*/
        val optionalPermissions = AxPermissionList()
        
        /* title , content 변경 */
        requiredPermissions.add(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, "${title}","${content}")

        /* content 변경 */
        requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION ,"" , "${content}")

        /* defult 값 사용 */
        requiredPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        optionalPermissions.add(Manifest.permission.CAMERA)

        AxPermission.create(this)
            .setPermissionListener(permissionListener) //리스너 등록
            .setRequiredPermissions(requiredPermissions) //필수 권한 리스트 등록
            .setOptionalPermissions(optionalPermissions) //선택 권한 리스트 등록
            .setSubmitButtonColors(
                buttonBackgroundColor = R.color.purple_200 , //확인 버튼 색상
                textColor = R.color.black //확인 버튼 텍스트 색상
            )
            .check() //실행
```
```
    private var permissionListener: AxPermissionListener = object : AxPermissionListener {
        override fun onPermissionGranted() {
            /*성공 콜백 리스너*/
        }

        override fun onPermissionDenied() {
            /*실패 콜백 리스너*/
            finishAffinity()
            exitProcess(0)
        }
    }      

```

### java
```
        /*필수 권한 리스트*/
        AxPermissionList requiredPermissions = new AxPermissionList();

        /*선택 권한 리스트*/
        AxPermissionList optionalPermissions = new AxPermissionList();

        /* title , content 변경 */
        requiredPermissions.add(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, "${title}","${content}")

        /* content 변경 */
        requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION ,"" , "${content}")

        /* defult 값 사용 */
        requiredPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        optionalPermissions.add(Manifest.permission.CAMERA)

        AxPermission.Companion.create(this)
            .setPermissionListener(permissionListener) //리스너 등록
            .setRequiredPermissions(requiredPermissions)//필수 권한 리스트 등록
            .setOptionalPermissions(optionalPermissions) //선택 권한 리스트 등록
            .setSubmitButtonColors(${backgroundcolor} , ${textcolor}) 
            .check();
```
```
    private final AxPermissionListener permissionListener = new AxPermissionListener() {
        @Override
        public void onPermissionGranted() {
            /*성공 콜백 리스너*/
        }

        @Override
        public void onPermissionDenied() {
            /*실패 콜백 리스너*/
            finishAffinity();
            System.exit(0);
        }
    };
```
*****
# RestartPermission


https://github.com/user-attachments/assets/6fb3dbc5-148b-4c4a-b5f0-4ca5fd469d0a


### permission image root
ax-permission/src/main/res/drawable-xxhdpi/quick_authority.png 
### permission image
![quick_authority](https://github.com/user-attachments/assets/d03ca4ff-e7d7-478c-bb2b-a64a52006401)


### kotiln
```
        AxPermission.create(this)
            .setPermissionListener(configPermissionListener)
            .onReStart()
```
```
    private var configPermissionListener: AxPermissionListener = object : AxPermissionListener {
        override fun onPermissionGranted() {
            /*성공 콜백 리스너*/
        }

        override fun onPermissionDenied() {
            /*실패 콜백 리스너*/
            finishAffinity()
            exitProcess(0)
        }
    }
```

### java
```
        AxPermission.Companion.create(this)
                .setPermissionListener(configPermissionListener)
                .onReStart(); //권한 화면 재시작
            
```
```
    private final AxPermissionListener configPermissionListener = new AxPermissionListener() {
        @Override
        public void onPermissionGranted() {
            /*성공 콜백 리스너*/
        }

        @Override
        public void onPermissionDenied() {
            /*실패 콜백 리스너*/
            finishAffinity();
            System.exit(0);
        }
    };
```
*****
# OptionalPermission


https://github.com/user-attachments/assets/2394a771-d4a4-48ce-b805-849ffa68b3c7

### kotiln
```
        AxOptionalPermissionsPopUp.getInstance(this) //fragment 일경우 requireActivity()
            .optionalPermissionsPopUp(
                listOf(
                    Manifest.permission.CAMERA
                ),
                onOptionalPermissionGranted = {
                    //권한 허용 콜백
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
                },
                onOptionalPermissionDenied = {
                    //권한 거부 콜백
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
                }
            )
```
### java
```
        AxOptionalPermissionsPopUp.Companion.getInstance(this) //fragment 일경우 Activity
                .optionalPermissionsPopUp(
                        Collections.singletonList(Manifest.permission.CAMERA), //또는 List<String> 타입 변수
                        new Runnable() {
                            @Override
                            public void run() {
                                // 권한 허용 콜백
                                Toast.makeText(YourActivity.this, "Permissions granted", Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                // 권한 거부 콜백
                                Toast.makeText(YourActivity.this, "Permissions denied", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
```
*****
# Available Permissions in AxPermission

## Action Permissions
|Manifest Code|Permission Title|Permission Content|
|---------|---------|-----------|
|<code>Settings.ACTION_MANAGE_OVERLAY_PERMISSION</code>| 앱 위에 그리기 |앱이 다른 앱 위에 표시되도록 허용하여, 더욱 편리한 사용자 경험을 제공합니다.|
|<code>Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS</code>|배터리 최적화 무시 설정|앱이 백그라운드에서 원활하게 실행될 수 있도록 배터리 최적화에서 제외하여, 실시간 알림이나 업데이트 등을 놓치지 않도록 합니다.|
|<code>Settings.ACTION_NFC_SETTINGS</code>|NFC 설정|NFC 기능을 사용하여 간편하게 기기 간 데이터를 주고받거나 결제 등을 할 수 있도록 합니다.|
|<code>Settings.ACTION_ACCESSIBILITY_SETTINGS</code>|접근성 설정|앱이 시스템 설정에 접근하여 사용자의 접근성을 향상시키는 기능을 제공합니다. 예를 들어, 시각 장애인을 위한 화면 읽기 기능을 지원하거나, 특정 기능을 자동화할 수 있습니다.|
|<code>Manifest.permission.CHANGE_WIFI_STATE</code>|WiFi 상태 변경|WiFi 상태를 변경하기 위해 필요한 권한입니다.|
|<code>Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS</code>|알람 접근 설정|앱이 알림을 접근하고 관리하기 위해 필요한 권한입니다.이 권한을 통해 앱은 다른 앱에서 오는 알림을 읽고, 특정 알림에 기반한 자동 응답 또는 작업을 수행할 수 있습니다.|
|<code>Settings.ACTION_USAGE_ACCESS_SETTINGS</code>|사용정보 접근 설정|스마트폰 사용 정보를 읽어오기 위해 사용정보 접근 권한이 필요합니다. 권한을 동의하지 않을 경우, 스마트폰 사용정보 를 읽어오실 수 없습니다.|

## Access Permissions
|Manifest Code|Permission Title|Permission Content|
|---------|---------|-----------|
|<code>Manifest.permission.CALL_PHONE</code>|전화 걸기|앱이 사용자의 기기에서 직접 전화를 걸 수 있도록 허용하는 권한입니다. 예를 들어, 저장된 연락처에 바로 전화를 걸거나, 특정 번호로 자동 연결하는 기능을 사용할 수 있습니다.|
|<code>Manifest.permission.POST_NOTIFICATIONS</code>|알림|앱이 사용자에게 중요한 정보를 알리기 위해 알림을 표시할 수 있도록 허용하는 권한입니다. 이 권한을 통해 새로운 메시지, 이벤트, 업데이트 등 중요한 정보를 알림으로 전달합니다. 예를 들어, 새로운 메시지가 도착했을 때 알림창을 통해 알려줍니다.|
|<code>Manifest.permission.PACKAGE_USAGE_STATS</code>|사용자 기기 상태 접근|앱이 사용자가 설치한 다른 앱들의 사용 시간, 빈도 등의 통계 정보를 수집할 수 있도록 허용하는 권한입니다. 이 권한을 통해 사용자의 앱 사용 패턴을 분석하여 더욱 개인화된 서비스를 제공합니다. 예를 들어, 사용자가 자주 사용하는 앱을 기반으로 맞춤형 추천 기능을 제공할 수 있습니다.|
|<code>Manifest.permission.CAMERA</code>|카메라|앱이 카메라를 사용하여 사진이나 동영상을 촬영하거나 스캔 기능을 제공하기 위해 필요한 권한입니다.|
|<code>Manifest.permission.WRITE_EXTERNAL_STORAGE</code>|저장소 쓰기|앱이 사용자의 사진, 동영상, 파일 등을 저장하거나 불러오기 위해 필요한 권한입니다. 예를 들어, 갤러리 앱에서 사진을 편집하거나, 파일 관리 앱에서 파일을 이동할 때 사용됩니다.|
|<code>Manifest.permission.READ_EXTERNAL_STORAGE</code>|저장소 읽기|앱이 사용자의 사진, 동영상, 파일 등을 저장하거나 불러오기 위해 필요한 권한입니다. 예를 들어, 갤러리 앱에서 사진을 편집하거나, 파일 관리 앱에서 파일을 이동할 때 사용됩니다.|
|<code>Manifest.permission.READ_MEDIA_IMAGES</code>|사진 및 동영상|앱이 사용자의 사진이나 동영상 파일을 읽어서 보여주거나 편집하기 위해 필요한 권한입니다.|
|<code>Manifest.permission.READ_MEDIA_VIDEO</code>|사진 및 동영상|앱이 사용자의 사진이나 동영상 파일을 읽어서 보여주거나 편집하기 위해 필요한 권한입니다.|
|<code>Manifest.permission.VIBRATE</code>|진동 사용|앱이 진동 기능을 사용하여 알림이나 사용자에게 특정 상황을 알리기 위해 필요한 권한입니다.|
|<code>Manifest.permission.RECORD_AUDIO</code>|오디오|앱이 마이크를 사용하여 음성을 녹음하거나 음성 인식 기능을 제공하기 위해 필요한 권한입니다.|
|<code>Manifest.permission.READ_PHONE_NUMBERS</code>|전화번호 가져오기|앱이 사용자의 전화번호 정보를 읽거나 통화 상태를 확인하여 관련 기능을 제공하기 위해 필요한 권한입니다.|
|<code>Manifest.permission.READ_PHONE_STATE</code>|전화번호 정보 읽기|앱이 사용자의 전화번호 정보를 읽거나 통화 상태를 확인하여 관련 기능을 제공하기 위해 필요한 권한입니다.|
|<code>Manifest.permission.ACCESS_MEDIA_LOCATION</code>|미디어 위치 접근|앱이 사용자의 정확한 위치 정보를 얻어서 위치 기반 서비스를 제공하기 위해 필요한 권한입니다.|
|<code>Manifest.permission.BLUETOOTH_CONNECT</code>|블루투스 연결|앱이 블루투스 기능을 사용하여 다른 기기와 연결하거나 데이터를 주고받기 위해 필요한 권한입니다.|
|<code>Manifest.permission.BLUETOOTH_SCAN</code>|블루투스 스캔|앱이 블루투스 기능을 사용하여 다른 기기와 연결하거나 데이터를 주고받기 위해 필요한 권한입니다.|
|<code>Manifest.permission.BLUETOOTH_ADMIN</code>|블루투스 연결|앱이 블루투스 기능을 사용하여 다른 기기와 연결하거나 데이터를 주고받기 위해 필요한 권한입니다.|
|<code>Manifest.permission.BLUETOOTH</code>|블루투스 스캔|앱이 블루투스 기능을 사용하여 다른 기기와 연결하거나 데이터를 주고받기 위해 필요한 권한입니다.|
|<code>Manifest.permission.READ_CONTACTS</code>|연락처 읽기|연락처 정보를 읽기 위해 필요한 권한입니다.|
|<code>Manifest.permission.SEND_SMS</code>|SMS 전송|앱이 사용자의 기기에서 SMS 메시지를 보낼 수 있도록 허용하는 권한입니다. 예를 들어, 문자 메시지로 인증번호를 받거나 친구에게 메시지를 보낼 수 있습니다.|
|<code>Manifest.permission.MODIFY_AUDIO_SETTINGS</code>|오디오 설정 수정|앱이 시스템의 오디오 설정을 변경하기 위해 필요한 권한입니다.|
|<code>Manifest.permission.ACCESS_NOTIFICATION_POLICY</code>|알림 정책 접근|알림 정책에 접근하기 위해 필요한 권한입니다.|
|<code>Manifest.permission.WRITE_SETTINGS</code>|설정 쓰기|앱이 시스템 알림 설정을 변경하거나 시스템 설정에 접근하여 특정 기능을 수행하기 위해 필요한 권한입니다.|
|<code>Manifest.permission.ACCESS_WIFI_STATE</code>|WiFi 상태 접근|앱이 Wi-Fi 연결 상태를 확인하여 더욱 안정적인 서비스를 제공하기 위해 필요한 권한입니다.|
|<code>Manifest.permission.ACTIVITY_RECOGNITION</code>|활동 인식|앱이 사용자의 활동을 인식하여 맞춤형 서비스를 제공하기 위해 필요한 권한입니다.|
|<code>Manifest.permission.SET_ALARM</code>|알람 설정|앱이 사용자 기기에서 알람을 설정하거나 정확한 시간에 작업을 예약할 수 있도록 허용하는 권한입니다. 이 권한을 통해 알람, 타이머, 일정 관리 등의 기능을 제공합니다.|
|<code>Manifest.permission.SCHEDULE_EXACT_ALARM</code>|정확한 알람 일정|앱이 사용자 기기에서 알람을 설정하거나 정확한 시간에 작업을 예약할 수 있도록 허용하는 권한입니다. 이 권한을 통해 알람, 타이머, 일정 관리 등의 기능을 제공합니다.|
|<code>Manifest.permission.ACCESS_FINE_LOCATION</code>|위치 정보|앱이 사용자의 정확한 위치 정보를 얻어서 위치 기반 서비스를 제공하기 위해 필요한 권한입니다.|
|<code>Manifest.permission.NEARBY_WIFI_DEVICES</code>|WIFI 설정|Android 13(API 33) 이상에서 사용되며, Wi-Fi 네트워크와 관련된 기능(예: Wi-Fi 스캔, 네트워크 연결, 네트워크 제안 등)을 사용하기 위해 필요한 권한 입니다.|
*****

