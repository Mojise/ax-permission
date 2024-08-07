package kr.co.permission.ax_permission

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kr.co.permission.ax_permission.listener.AxPermissionItemClickListener
import kr.co.permission.ax_permission.model.AxPermissionModel

class AxPermissionAdapter(private val context: Context, private val axPermissionItemClickListener: AxPermissionItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_BODY = 1
    }

    private var perItemMap: Map<String, List<AxPermissionModel>> = mutableMapOf()
    private var headers: List<String> = listOf()
    private var itemCount: Int = 0

    @SuppressLint("NotifyDataSetChanged")
    fun setPerItemMap(map: Map<String, List<AxPermissionModel>>) {
        this.perItemMap = map
        this.headers = map.keys.toList()
        this.itemCount = map.values.sumBy { it.size } + headers.size
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = inflater.inflate(R.layout.adapter_permission_header_item, parent, false)
                PerHeaderViewHolder(view)
            }
            VIEW_TYPE_BODY -> {
                val view = inflater.inflate(R.layout.adapter_permission_body_item, parent, false)
                PerBodyViewHolder(view, context, axPermissionItemClickListener)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var currentPosition = 0
        for (header in headers) {
            if (currentPosition == position) {
                (holder as PerHeaderViewHolder).headerBind(header,perItemMap)
                return
            }
            currentPosition++
            val items = perItemMap[header]
            items?.let {
                if (position < currentPosition + it.size) {
                    (holder as PerBodyViewHolder).bodyBind(it[position - currentPosition])
                    return
                }
                currentPosition += it.size
            }
        }
    }

    override fun getItemCount(): Int = itemCount

    override fun getItemViewType(position: Int): Int {
        var currentPosition = 0
        for (header in headers) {
            if (currentPosition == position) {
                return VIEW_TYPE_HEADER
            }
            currentPosition++
            val items = perItemMap[header]
            items?.let {
                if (position < currentPosition + it.size) {
                    return VIEW_TYPE_BODY
                }
                currentPosition += it.size
            }
        }
        throw IllegalArgumentException("Invalid position")
    }

    class PerHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val headerTitle: TextView = view.findViewById(R.id.header_title)

        fun headerBind(header: String , map:Map<String, List<AxPermissionModel>>) {
            if(map[header]?.isEmpty() == true){
                headerTitle.isVisible = false
            }else{
                headerTitle.isVisible = true
            }
            headerTitle.text = header
        }
    }

    class PerBodyViewHolder(
        view: View,
        private val context: Context,
        private val axPermissionItemClickListener: AxPermissionItemClickListener
    ) : RecyclerView.ViewHolder(view) {

        private val perTitleText: TextView = view.findViewById(R.id.perTitleText)
        private val perContentText: TextView = view.findViewById(R.id.perContentText)
        private val perSetText: TextView = view.findViewById(R.id.perSetText)
        private val perSetBtn: ConstraintLayout = view.findViewById(R.id.perSetBtn)
        private val perLayout: ConstraintLayout = view.findViewById(R.id.perLayout)

        fun bodyBind(axPermissionModel: AxPermissionModel?) {
            axPermissionModel?.let {
                perTitleText.text = it.perTitle
                perContentText.text = it.perContent

                if (it.perState) {
                    perSetText.setTextColor(ContextCompat.getColor(context, R.color.white))
                } else {
                    perSetText.setTextColor(ContextCompat.getColor(context, R.color.colorTextDark))
                }

                perLayout.setOnClickListener {
                    axPermissionItemClickListener.onPerClick(axPermissionModel , adapterPosition)
                }

                perSetBtn.isSelected = it.perState
                perLayout.isSelected = it.perState
            }
        }
    }
}