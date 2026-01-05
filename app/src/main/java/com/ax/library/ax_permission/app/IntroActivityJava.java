package com.ax.library.ax_permission.app;

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
                // 필수 권한 설정
//                .setRequiredPermissions(builder -> {
//                    builder.add(Manifest.permission.ACCESS_FINE_LOCATION);
//                    return Unit.INSTANCE;
//                })
                // 선택 권한 설정
//                .setOptionalPermissions(builder -> {
//                    builder.add(Manifest.permission.CAMERA);
//                    return Unit.INSTANCE;
//                })
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
