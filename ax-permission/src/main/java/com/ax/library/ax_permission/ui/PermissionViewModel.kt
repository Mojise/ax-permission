package com.ax.library.ax_permission.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.ax.library.ax_permission.model.Item
import com.ax.library.ax_permission.model.Permission
import com.ax.library.ax_permission.permission.PermissionItemData

internal class PermissionViewModel(
    private val application: Application,
    private val requiredPermissions: List<Permission>,
    private val optionalPermissions: List<Permission>,
) : AndroidViewModel(application) {

    private val _items: MutableStateFlow<List<Item>> = MutableStateFlow(emptyList())

    /**
     * 리스트 아이템 목록 (헤더, 권한 아이템, 푸터)
     */
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    /**
     * 권한 아이템 목록
     */
    val permissionItems: StateFlow<List<Item.PermissionItem>> = items
        .map { it.filterIsInstance<Item.PermissionItem>() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /**
     * 모든 권한이 허용되었는지 여부
     */
    val isAllPermissionsGranted: StateFlow<Boolean> = items
        .map { it.filterIsInstance<Item.PermissionItem>().all(Item.PermissionItem::isGranted) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    /**
     * 필수 권한 모두 허용 여부
     */
    val isRequiredPermissionsAllGranted: StateFlow<Boolean> = items
        .map {
            it.filterIsInstance<Item.PermissionItem>()
                .filter(Item.PermissionItem::isRequired)
                .all(Item.PermissionItem::isGranted)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    /**
     * 선택 권한 모두 허용 여부
     */
    val isOptionalPermissionsAllGranted: StateFlow<Boolean> = items
        .map {
            it.filterIsInstance<Item.PermissionItem>()
                .filter(Item.PermissionItem::isOptional)
                .all(Item.PermissionItem::isGranted)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    /**
     * 전체 권한 목록 중, 첫 번째로 허용되지 않은 권한 아이템의 Index
     */
    val firstNotGrantedPermissionItemIndex: Int?
        get() = permissionItems.value
            .indexOfFirst(Item.PermissionItem::isNotGranted)
            .takeIf { it >= 0 }


    init {
        _items.value = PermissionItemData.generateInitialItems(
            context = application,
            requiredPermissionTypes = requiredPermissions,
            optionalPermissionTypes = optionalPermissions,
        )
    }

    /**
     * 특정 권한의 허용 상태를 업데이트합니다.
     */
    fun updatePermissionGrantedState(permissionType: Permission, isGranted: Boolean) = viewModelScope.launch {
        _items.update {
            it.map { item ->
                if (item is Item.PermissionItem && item.permission == permissionType) {
                    item.copy(isGranted = isGranted)
                } else {
                    item
                }
            }
        }
    }
}

internal class PermissionViewModelFactory constructor(
    private val application: Application,
    private val requiredPermissions: List<Permission>,
    private val optionalPermissions: List<Permission>,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PermissionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PermissionViewModel(application, requiredPermissions, optionalPermissions) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}