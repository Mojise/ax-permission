package com.ax.library.ax_permission.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

//import com.ax.library.ax_permission.R;
import com.ax.library.ax_permission.AxPermission;
import com.ax.library.ax_permission.model.Permission;

public class IntroActivityJava extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_container), (v, insets) -> {
            v.setPadding(
                    insets.getInsets(android.view.WindowInsets.Type.systemBars()).left,
                    insets.getInsets(android.view.WindowInsets.Type.systemBars()).top,
                    insets.getInsets(android.view.WindowInsets.Type.systemBars()).right,
                    insets.getInsets(android.view.WindowInsets.Type.systemBars()).bottom
            );
            return insets;
        });

        // 3초 뒤에 권한 체크
        findViewById(R.id.root_container)
                .postDelayed(this::checkPermissions, 3000);
    }

    private void checkPermissions() {


        AxPermission.from(this)
                .setDayNightTheme()
                .setAppName(R.string.app_name)
                .setRequiredPermissions(
                        // 다른 앱 위에 그리기 권한
                        Permission.Special.ActionManageOverlayPermission(),
                        // 알림 접근 권한
                        //Permission.Special.ActionNotificationListenerSettings(),
                        // 배터리 최적화 제외 권한
                        Permission.Special.ActionRequestIgnoreBatteryOptimizations(),
                        // 위치 권한
                        //Permission.Runtime.AccessFineLocation(),
                        Permission.Runtime.AccessCoarseLocation(),
                        // 저장소 권한
//                        Permission.Runtime.ReadMediaVideo()
//                        Permission.Runtime.ReadMediaAudio()
                        Permission.Runtime.ReadMediaAll()
                )
                .setOptionalPermissions(
                        //Permission.Runtime.Camera()
                )
                .setCallback(new AxPermission.Callback() {
                    @Override
                    public void onRequiredPermissionsAllGranted(@NonNull Context context) {
                        context.startActivity(new Intent(IntroActivityJava.this, MainActivity.class));
                    }

                    @Override
                    public void onRequiredPermissionsAnyOneDenied() {
                        finishAffinity();
                    }
                })
                .checkAndShow();

        finish();
    }
}
