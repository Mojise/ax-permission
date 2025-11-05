package com.ax.library.ax_permission.model

import android.Manifest
import android.os.Build
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ax.library.ax_permission.R
import java.io.Serializable

/**
 * # 권한
 *
 * Android 권한을 나타내는 sealed interface입니다.
 * - [Permission.Runtime]: 런타임 권한 (사용자가 앱 실행 중에 허용/거부)
 * - [Permission.Special]: 특별 권한 (설정 화면으로 이동하여 허용)
 *
 * @see <a href="https://developer.android.com/guide/topics/permissions/overview?hl=ko">[Android Developers] Android에서의 권한 - 개요</a>
 */
sealed interface Permission : Serializable {

    /**
     * 권한 아이콘 Drawable 리소스 ID
     */
    @get:DrawableRes
    val iconResId: Int

    /**
     * 권한 이름 문자열 리소스 ID
     */
    @get:StringRes
    val titleResId: Int

    /**
     * 권한 설명 문자열 리소스 ID
     */
    @get:StringRes
    val descriptionResId: Int

    /**
     * 권한이 비어있는지 여부
     */
    val isEmptyPermissions: Boolean
        get() = when (this) {
            is Runtime -> manifestPermissions.isEmpty()
            is Special -> settingsAction.isEmpty()
        }

    // ===== 런타임 권한 (Runtime Permissions) =====

    /**
     * # 런타임 권한
     *
     * - 사용자가 앱 실행 중에 시스템 다이얼로그를 통해 허용/거부할 수 있는 권한입니다.
     */
    class Runtime private constructor(
        override val iconResId: Int,
        override val titleResId: Int,
        override val descriptionResId: Int,
        val manifestPermissions: List<String>,
    ) : Permission {

        @JvmOverloads
        fun copy(
            @DrawableRes iconResId: Int = this.iconResId,
            @StringRes titleResId: Int = this.titleResId,
            @StringRes descriptionResId: Int = this.descriptionResId,
        ): Runtime = Runtime(
            iconResId = iconResId,
            titleResId = titleResId,
            descriptionResId = descriptionResId,
            manifestPermissions = manifestPermissions,
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Runtime

            return manifestPermissions == other.manifestPermissions
        }

        override fun hashCode(): Int {
            return manifestPermissions.hashCode()
        }

        override fun toString(): String = "Permission.Runtime(${manifestPermissions.joinToString { it.split(".").lastOrNull().toString() }})"

        companion object {

            /** 런타임 권한 - 카메라 ([Manifest.permission.CAMERA]) **/
            @JvmStatic
            fun Camera() = Runtime(
                iconResId = R.drawable.ic_ax_permission_camera,
                titleResId = R.string.ax_permission_camera_name,
                descriptionResId = R.string.ax_permission_camera_description,
                manifestPermissions = listOf(Manifest.permission.CAMERA)
            )

            /** 런타임 권한 - 마이크 ([Manifest.permission.RECORD_AUDIO]) **/
            @JvmStatic
            fun RecordAudio() = Runtime(
                iconResId = R.drawable.ic_ax_permission_microphone,
                titleResId = R.string.ax_permission_microphone_name,
                descriptionResId = R.string.ax_permission_microphone_description,
                manifestPermissions = listOf(Manifest.permission.RECORD_AUDIO)
            )

            /** 런타임 권한 - 위치 (정확한 위치) ([Manifest.permission.ACCESS_FINE_LOCATION]) **/
            @JvmStatic
            fun AccessFineLocation() = Runtime(
                iconResId = R.drawable.ic_ax_permission_location,
                titleResId = R.string.ax_permission_location_fine_name,
                descriptionResId = R.string.ax_permission_location_fine_description,
                manifestPermissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION)
            )

            /** 런타임 권한 - 위치 (대략적 위치) ([Manifest.permission.ACCESS_COARSE_LOCATION]) **/
            @JvmStatic
            fun AccessCoarseLocation() = Runtime(
                iconResId = R.drawable.ic_ax_permission_location,
                titleResId = R.string.ax_permission_location_coarse_name,
                descriptionResId = R.string.ax_permission_location_coarse_description,
                manifestPermissions = listOf(Manifest.permission.ACCESS_COARSE_LOCATION)
            )

            /** 런타임 권한 - 위치 (정확한 위치 + 대략적 위치) ([Manifest.permission.ACCESS_FINE_LOCATION], [Manifest.permission.ACCESS_COARSE_LOCATION]) **/
            // TODO: 해당 권한 조합의 경우에는 ACCESS_COARSE_LOCATION만 허용되어도 허용 = true로 간주해야 함
            @JvmStatic
            fun AccessFineAndCoarseLocation() = Runtime(
                iconResId = R.drawable.ic_ax_permission_location,
                titleResId = R.string.ax_permission_location_fine_name,
                descriptionResId = R.string.ax_permission_location_fine_description,
                manifestPermissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )

            /** 런타임 권한 - 위치 (백그라운드) ([Manifest.permission.ACCESS_BACKGROUND_LOCATION], Android 10+) **/
            @JvmStatic
            fun AccessBackgroundLocation() = Runtime(
                iconResId = R.drawable.ic_ax_permission_location,
                titleResId = R.string.ax_permission_location_background_name,
                descriptionResId = R.string.ax_permission_location_background_description,
                manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                } else {
                    emptyList()
                }
            )

            /** 런타임 권한 - 미디어 (시각 자료: 사진 및 동영상) ([Manifest.permission.READ_MEDIA_IMAGES], [Manifest.permission.READ_MEDIA_VIDEO], Android 13+)
             *
             * Android 13+에서 READ_MEDIA_IMAGES와 READ_MEDIA_VIDEO는 "Visual Media" 권한 그룹으로 묶여있어,
             * 하나만 요청해도 사용자에게는 "사진 및 동영상 액세스" 다이얼로그가 표시되고 둘 다 허용됩니다.
             * 따라서 이미지나 비디오 중 하나만 필요한 경우에도 이 권한을 사용하세요.
             */
            @JvmStatic
            fun ReadMediaVisual() = Runtime(
                iconResId = R.drawable.ic_ax_permission_storage,
                titleResId = R.string.ax_permission_storage_read_name,
                descriptionResId = R.string.ax_permission_storage_read_description,
                manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    listOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                    )
                } else {
                    listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            )

            /** 런타임 권한 - 미디어 (오디오) ([Manifest.permission.READ_MEDIA_AUDIO], Android 13+) **/
            @JvmStatic
            fun ReadMediaAudio() = Runtime(
                iconResId = R.drawable.ic_ax_permission_storage,
                titleResId = R.string.ax_permission_storage_read_name,
                descriptionResId = R.string.ax_permission_storage_read_description,
                manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    listOf(Manifest.permission.READ_MEDIA_AUDIO)
                } else {
                    listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            )

            /** 런타임 권한 - 미디어 (전체: 이미지 + 비디오 + 오디오) ([Manifest.permission.READ_MEDIA_IMAGES], [Manifest.permission.READ_MEDIA_VIDEO], [Manifest.permission.READ_MEDIA_AUDIO], Android 13+) **/
            @JvmStatic
            fun ReadMediaAll() = Runtime(
                iconResId = R.drawable.ic_ax_permission_storage,
                titleResId = R.string.ax_permission_storage_read_name,
                descriptionResId = R.string.ax_permission_storage_read_description,
                manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    listOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_AUDIO,
                    )
                } else {
                    listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            )

            /** 런타임 권한 - 저장소 (읽기) ([Manifest.permission.READ_EXTERNAL_STORAGE], Android 12 이하) **/
            @JvmStatic
            fun ReadExternalStorage() = Runtime(
                iconResId = R.drawable.ic_ax_permission_storage,
                titleResId = R.string.ax_permission_storage_read_name,
                descriptionResId = R.string.ax_permission_storage_read_description,
                manifestPermissions = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                    listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                } else {
                    emptyList()
                }
            )

            /** 런타임 권한 - 저장소 (쓰기) ([Manifest.permission.WRITE_EXTERNAL_STORAGE], Android 9 이하) **/
            @JvmStatic
            fun WriteExternalStorage() = Runtime(
                iconResId = R.drawable.ic_ax_permission_storage,
                titleResId = R.string.ax_permission_storage_write_name,
                descriptionResId = R.string.ax_permission_storage_write_description,
                manifestPermissions = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else {
                    emptyList()
                }
            )

            /** 런타임 권한 - 알림 ([Manifest.permission.POST_NOTIFICATIONS], Android 13+) **/
            @JvmStatic
            fun PostNotifications() = Runtime(
                iconResId = R.drawable.ic_ax_permission_notification,
                titleResId = R.string.ax_permission_notification_name,
                descriptionResId = R.string.ax_permission_notification_description,
                manifestPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    listOf(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    emptyList() // Android 12 이하에서는 자동으로 허용됨
                }
            )

            /** 런타임 권한 - 연락처 (읽기) ([Manifest.permission.READ_CONTACTS]) **/
            @JvmStatic
            fun ReadContacts() = Runtime(
                iconResId = R.drawable.ic_ax_permission_contacts,
                titleResId = R.string.ax_permission_contacts_read_name,
                descriptionResId = R.string.ax_permission_contacts_read_description,
                manifestPermissions = listOf(Manifest.permission.READ_CONTACTS)
            )

            /** 런타임 권한 - 연락처 (쓰기) ([Manifest.permission.WRITE_CONTACTS]) **/
            @JvmStatic
            fun WriteContacts() = Runtime(
                iconResId = R.drawable.ic_ax_permission_contacts,
                titleResId = R.string.ax_permission_contacts_write_name,
                descriptionResId = R.string.ax_permission_contacts_write_description,
                manifestPermissions = listOf(Manifest.permission.WRITE_CONTACTS)
            )

            /** 런타임 권한 - 연락처 (읽기 + 쓰기) ([Manifest.permission.READ_CONTACTS], [Manifest.permission.WRITE_CONTACTS]) **/
            @JvmStatic
            fun ReadWriteContacts() = Runtime(
                iconResId = R.drawable.ic_ax_permission_contacts,
                titleResId = R.string.ax_permission_contacts_read_name,
                descriptionResId = R.string.ax_permission_contacts_read_description,
                manifestPermissions = listOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                )
            )

            /** 런타임 권한 - 전화 ([Manifest.permission.READ_PHONE_STATE]) **/
            @JvmStatic
            fun ReadPhoneState() = Runtime(
                iconResId = R.drawable.ic_ax_permission_phone,
                titleResId = R.string.ax_permission_phone_name,
                descriptionResId = R.string.ax_permission_phone_description,
                manifestPermissions = listOf(Manifest.permission.READ_PHONE_STATE)
            )

            /** 런타임 권한 - 전화 걸기 ([Manifest.permission.CALL_PHONE]) **/
            @JvmStatic
            fun CallPhone() = Runtime(
                iconResId = R.drawable.ic_ax_permission_call,
                titleResId = R.string.ax_permission_call_phone_name,
                descriptionResId = R.string.ax_permission_call_phone_description,
                manifestPermissions = listOf(Manifest.permission.CALL_PHONE)
            )

            /** 런타임 권한 - 캘린더 (읽기) ([Manifest.permission.READ_CALENDAR]) **/
            @JvmStatic
            fun ReadCalendar() = Runtime(
                iconResId = R.drawable.ic_ax_permission_calendar,
                titleResId = R.string.ax_permission_calendar_read_name,
                descriptionResId = R.string.ax_permission_calendar_read_description,
                manifestPermissions = listOf(Manifest.permission.READ_CALENDAR)
            )

            /** 런타임 권한 - 캘린더 (쓰기) ([Manifest.permission.WRITE_CALENDAR]) **/
            @JvmStatic
            fun WriteCalendar() = Runtime(
                iconResId = R.drawable.ic_ax_permission_calendar,
                titleResId = R.string.ax_permission_calendar_write_name,
                descriptionResId = R.string.ax_permission_calendar_write_description,
                manifestPermissions = listOf(Manifest.permission.WRITE_CALENDAR)
            )

            /** 런타임 권한 - 캘린더 (읽기 + 쓰기) ([Manifest.permission.READ_CALENDAR], [Manifest.permission.WRITE_CALENDAR]) **/
            @JvmStatic
            fun ReadWriteCalendar() = Runtime(
                iconResId = R.drawable.ic_ax_permission_calendar,
                titleResId = R.string.ax_permission_calendar_read_name,
                descriptionResId = R.string.ax_permission_calendar_read_description,
                manifestPermissions = listOf(
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR,
                )
            )
        }
    }

    // ===== 특별 권한 (Special Permissions) =====

    /**
     * # 특별 권한
     *
     * - 설정 화면으로 이동하여 사용자가 수동으로 허용해야 하는 권한입니다.
     */
    class Special private constructor(
        override val iconResId: Int,
        override val titleResId: Int,
        override val descriptionResId: Int,
        val settingsAction: String,
    ) : Permission {

        @JvmOverloads
        fun copy(
            @DrawableRes iconResId: Int = this.iconResId,
            @StringRes titleResId: Int = this.titleResId,
            @StringRes descriptionResId: Int = this.descriptionResId,
        ): Special = Special(
            iconResId = iconResId,
            titleResId = titleResId,
            descriptionResId = descriptionResId,
            settingsAction = settingsAction,
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Special

            return settingsAction == other.settingsAction
        }

        override fun hashCode(): Int {
            return settingsAction.hashCode()
        }

        override fun toString(): String = "Permission.Special(${settingsAction.split(".").lastOrNull().toString()})"

        companion object {

            /** 특별 권한 - 다른 앱 위에 표시 ([Settings.ACTION_MANAGE_OVERLAY_PERMISSION]) **/
            @JvmStatic
            fun ActionManageOverlayPermission() = Special(
                iconResId = R.drawable.ic_ax_permission_draw_overlays,
                titleResId = R.string.ax_permission_draw_overlays_name,
                descriptionResId = R.string.ax_permission_draw_overlays_description,
                settingsAction = Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            )

            /** 특별 권한 - 알림 접근 ([Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS]) **/
            @JvmStatic
            fun ActionNotificationListenerSettings() = Special(
                iconResId = R.drawable.ic_ax_permission_alarm,
                titleResId = R.string.ax_permission_access_notification_name,
                descriptionResId = R.string.ax_permission_access_notification_description,
                settingsAction = Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS,
            )

            /** 특별 권한 - 배터리 최적화 무시 ([Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS]) **/
            @JvmStatic
            fun ActionRequestIgnoreBatteryOptimizations() = Special(
                iconResId = R.drawable.ic_ax_permission_battery,
                titleResId = R.string.ax_permission_ignore_battery_optimization_name,
                descriptionResId = R.string.ax_permission_ignore_battery_optimization_description,
                settingsAction = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            )
        }
    }
}
