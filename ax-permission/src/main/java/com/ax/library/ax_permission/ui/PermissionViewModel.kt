package com.ax.library.ax_permission.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ax.library.ax_permission.model.Item
import com.ax.library.ax_permission.model.Permission
import com.ax.library.ax_permission.util.launched
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * 권한 요청 워크플로우 상태
 */
internal sealed interface PermissionWorkflowState {

    /**
     * 대기 상태
     */
    data object Idle : PermissionWorkflowState

    /**
     * 권한 요청 진행 중
     * @param permissionItemIds 요청할 권한 ID 목록
     * @param currentId 현재 요청 진행 중인 권한 ID
     */
    data class Running(
        val permissionItemIds: List<Int>,
        val currentIndex: Int,
        val currentId: Int,
    ) : PermissionWorkflowState {
        override fun toString(): String = buildString {
            appendLine("Running(")
            appendLine("    permissionItemIds=$permissionItemIds, ")
            appendLine("    currentIndex=$currentIndex, ")
            appendLine("    currentId=$currentId,")
            append(")")
        }
    }
}

internal class PermissionViewModel(
    initialItems: List<Item>,
) : ViewModel() {

    private val _items: MutableStateFlow<List<Item>> = MutableStateFlow(initialItems)

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

    private val _workflowState: MutableStateFlow<PermissionWorkflowState> = MutableStateFlow(PermissionWorkflowState.Idle)

    /**
     * 권한 요청 워크플로우 상태
     */
    val workflowState: StateFlow<PermissionWorkflowState> = _workflowState.asStateFlow()

    /**
     * 특정 권한의 허용 상태를 업데이트합니다.
     */
    fun updatePermissionGrantedState(permissionType: Permission, isGranted: Boolean) {
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

    /**
     * 특정 권한 아이템 하이라이트 처리
     */
    private fun highlightPermissionItem(permissionItemId: Int?) {
        _items.update {
            it.map { item ->
                when {
                    item is Item.PermissionItem && item.id == permissionItemId -> {
                        item.copy(isHighlights = true)
                    }
                    item is Item.PermissionItem && item.isHighlights -> {
                        item.copy(isHighlights = false)
                    }
                    else -> item
                }
            }
        }
    }

    /**
     * 권한 요청 Workflow 시작 (권한 모두 허용하기)
     * 아직 허용되지 않은 모든 권한을 순차적으로 요청합니다.
     */
    fun startRequestPermissionsWorkFlow() {
        val notGrantedPermissions = permissionItems.value.filter { it.isGranted.not() }
        if (notGrantedPermissions.isEmpty()) {
            return
        }

        val firstPermission = notGrantedPermissions.first()

        _workflowState.value = PermissionWorkflowState.Running(
            permissionItemIds = notGrantedPermissions.map { it.id },
            currentIndex = 0,
            currentId = firstPermission.id,
        )
        addEmptySpaceFooter()
        highlightPermissionItem(firstPermission.id)
    }

    /**
     * 단일 권한 요청 Workflow 시작
     *
     * @param permissionItem 단일 권한 아이템
     */
    fun startRequestPermissionsWorkflow(permissionItem: Item.PermissionItem) {
        if (permissionItem.isGranted) {
            return // 이미 허용된 권한은 요청하지 않음
        }

        _workflowState.value = PermissionWorkflowState.Running(
            permissionItemIds = permissionItems.value
                .filter { it.id == permissionItem.id }
                .map { it.id },
            currentIndex = 0,
            currentId = permissionItem.id,
        )
        addEmptySpaceFooter()
        highlightPermissionItem(permissionItem.id)
    }

    /**
     * 워크플로우에서 다음 권한으로 진행
     */
    fun proceedToNextPermissionInWorkflow(delay: Long = 0L) {
        val currentState = _workflowState.value
        if (currentState !is PermissionWorkflowState.Running) {
            return
        }

        launched {
            if (delay > 0) {
                delay(delay)
            }

            val currPermissionItemId = currentState.currentId
            val currIndex = currentState.permissionItemIds.indexOf(currPermissionItemId)
            val nextIndex = currIndex + 1
            val nextPermissionItemId = currentState.permissionItemIds.getOrNull(nextIndex)
            if (nextPermissionItemId != null) {
                // 다음 권한으로 진행
                _workflowState.value = PermissionWorkflowState.Running(
                    permissionItemIds = currentState.permissionItemIds,
                    currentIndex = nextIndex,
                    currentId = nextPermissionItemId,
                )
                highlightPermissionItem(nextPermissionItemId)
            } else {
                // 모든 권한 처리 완료
                finishWorkflow()
            }
        }
    }

    /**
     * 배치 요청 중 현재 연속된 특별 권한 ID 목록 반환
     */
    fun getConsecutiveSpecialPermissionIds(): List<Int> {
        val currentState = _workflowState.value
        if (currentState !is PermissionWorkflowState.Running) {
            return emptyList()
        }

        val specialPermissionIds = mutableListOf<Int>()

        for (id in currentState.permissionItemIds.drop(currentState.currentIndex)) {
            val permissionItem = permissionItems.value.find { it.id == id }
            if (permissionItem?.permission !is Permission.Special) {
                break
            }
            specialPermissionIds.add(id)
        }

        return specialPermissionIds
    }

    /**
     * 워크플로우 완료 (Idle 상태로 복귀)
     */
    fun finishWorkflow() {
        _workflowState.value = PermissionWorkflowState.Idle
        removeEmptySpaceFooter()
        highlightPermissionItem(null)
    }

    /**
     * 빈 공간 푸터 추가 (워크플로우가 Running 상태일 때, 하이라이팅 아이템을 상단으로 스크롤하기 위한 하단 여백)
     */
    private fun addEmptySpaceFooter() {
        _items.update {
            it + Item.EmptySpaceFooter(id = Int.MAX_VALUE)
        }
    }

    /**
     * 빈 공간 푸터 제거 (워크플로우 완료 시)
     */
    private fun removeEmptySpaceFooter() {
        _items.update {
            it.filterNot { item -> item is Item.EmptySpaceFooter }
        }
    }
}

internal class PermissionViewModelFactory constructor(
    private val items: List<Item>,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PermissionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PermissionViewModel(items) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}