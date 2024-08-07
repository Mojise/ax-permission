package kr.co.permission.ax_permission.listener

import kr.co.permission.ax_permission.model.AxPermissionModel

interface AxPermissionItemClickListener {
    fun onPerClick(permissionModel: AxPermissionModel, adapterPosition:Int)
}