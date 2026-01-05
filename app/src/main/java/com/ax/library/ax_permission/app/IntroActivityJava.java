package com.ax.library.ax_permission.app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ax.library.ax_permission.ax.AxPermission;
import com.ax.library.ax_permission.model.Permission;

import org.jetbrains.annotations.NotNull;

public class IntroActivityJava extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 3초 뒤에 권한 체크
        findViewById(R.id.root_container)
                .postDelayed(this::checkPermissions, 1500);
    }

    private void checkPermissions() {
        AxPermission.from(this)
                .setDayNightTheme()
                .setAppName(R.string.app_name)
//                .setRequiredPermissions2((PermissionBuilder) -> {
//
//                    return null;
//                })

                // 필수 권한 설정
//                .setRequiredPermissions(
//                        READ_MEDIA_IMAGES
//                )
//                .setRequiredPermissions(
//                        Permission.Runtime.ACCESS_FINE_LOCATION
//                )
//                .setRequiredPermissions(
//                        Permission.Special.ACTION_MANAGE_OVERLAY_PERMISSION
//                                .withResources(),
//
//                        Permission.Special.ACTION_NOTIFICATION_LISTENER_SETTINGS,
//                        Permission.Special.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
//
//                        Permission.Runtime.CAMERA,
//
////                        Permission.runtimeGroup(
////                                Permission.Runtime.ACCESS_FINE_LOCATION,
////                                Permission.Runtime.ACCESS_COARSE_LOCATION
////                        ).withResources(),
//
//                        Permission.Runtime.READ_CALENDAR
//                )
                // 선택 권한 설정
                .setOptionalPermissions(

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

//        AxPermissionHelper
//                .checkAndShow(this, new AxPermission.Callback() {
//                    @Override
//                    public void onRequiredPermissionsAllGranted(@NotNull Context context) {
//
//                    }
//
//                    @Override
//                    public void onRequiredPermissionsAnyOneDenied() {
//
//                    }
//                });
    }

    private void goMain() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
