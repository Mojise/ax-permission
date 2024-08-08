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
            .setSubmitButtonColors(R.color.colorAccent , R.color.colorPrimary) 
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

# RestartPermission


https://github.com/user-attachments/assets/6fb3dbc5-148b-4c4a-b5f0-4ca5fd469d0a


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

# OptionalPermission


https://github.com/user-attachments/assets/2394a771-d4a4-48ce-b805-849ffa68b3c7

### kotiln
```
        AxOptionalPermissionsPopUp.getInstance(this)
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
        AxOptionalPermissionsPopUp.Companion.getInstance(this)
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
