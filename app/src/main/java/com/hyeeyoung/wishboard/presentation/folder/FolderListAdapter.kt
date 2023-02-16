package com.hyeeyoung.wishboard.presentation.folder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hyeeyoung.wishboard.R
import com.hyeeyoung.wishboard.data.model.folder.FolderItem
import com.hyeeyoung.wishboard.databinding.ItemFolderHorizontalBinding
import com.hyeeyoung.wishboard.databinding.ItemFolderSquareBinding
import com.hyeeyoung.wishboard.databinding.ItemFolderVerticalBinding
import com.hyeeyoung.wishboard.databinding.ItemNewFolderBinding
import com.hyeeyoung.wishboard.presentation.folder.types.FolderListViewType

class FolderListAdapter(
    private val folderListViewType: FolderListViewType,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var inflater: LayoutInflater
    private val dataSet = arrayListOf<FolderItem>()
    private lateinit var listener: OnItemClickListener
    private var folderMoreDialogListener: OnFolderMoreDialogListener? = null
    private var newFolderListener: OnNewFolderClickListener? = null

    /** 링크 공유 전용, 선택된 폴더를 저장 */
    private var selectedFolder: FolderItem? = null

    /** 폴더리스트 다이얼로그 전용, 선택된 폴더 id를 저장
    TODO need refactoring : selectedFolder로 합치기 */
    private var selectedFolderId: Long? = null

    init {
        setHasStableIds(true)
        if (folderListViewType == FolderListViewType.SQUARE_VIEW_TYPE) { // TODO concat adapter 사용
            dataSet.add(FolderItem()) // "폴더 추가" 뷰를 위한 더미 데이터 삽입
            notifyItemChanged(0)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: FolderItem)
    }

    interface OnNewFolderClickListener {
        fun onItemClick()
    }

    interface OnFolderMoreDialogListener {
        fun onItemMoreButtonClick(item: FolderItem)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun setOnNewFolderClickListener(listener: OnNewFolderClickListener) {
        this.newFolderListener = listener
    }

    fun setOnFolderMoreDialogListener(listener: OnFolderMoreDialogListener) {
        this.folderMoreDialogListener = listener
    }

    inner class VerticalViewHolder(private val binding: ItemFolderVerticalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(folder: FolderItem) {
            with(binding) {
                this.item = folder
                thumbnail.clipToOutline = true
                thumbnail.load(folder.thumbnail)
                container.setOnClickListener {
                    listener.onItemClick(folder)
                }
                more.setOnClickListener {
                    folderMoreDialogListener?.onItemMoreButtonClick(folder)
                }
            }
        }
    }

    inner class HorizontalViewHolder(private val binding: ItemFolderHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(folder: FolderItem) {
            with(binding) {
                this.item = folder
                check.visibility = if (selectedFolderId == folder.id) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
                thumbnail.clipToOutline = true
                thumbnail.load(folder.thumbnail)
                container.setOnClickListener {
                    listener.onItemClick(folder)
                }
            }
        }
    }

    inner class SquareViewHolder(private val binding: ItemFolderSquareBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(folder: FolderItem) {
            with(binding) {
                this.item = folder

                if (selectedFolder == folder) {
                    check.visibility = View.VISIBLE
                    foreground.backgroundTintList =
                        ContextCompat.getColorStateList(foreground.context, R.color.transparent_700)
                } else {
                    check.visibility = View.INVISIBLE
                    foreground.backgroundTintList =
                        ContextCompat.getColorStateList(foreground.context, R.color.transparent_300)
                }
                thumbnail.clipToOutline = true
                thumbnail.load(folder.thumbnail)
                container.setOnClickListener {
                    changeSelectedFolder(folder)
                }
            }
        }
    }

    inner class NewFolderViewHolder(private val binding: ItemNewFolderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.container.setOnClickListener {
                newFolderListener?.onItemClick()
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (!::inflater.isInitialized)
            inflater = LayoutInflater.from(viewGroup.context)

        return when (viewType) {
            FolderListViewType.VERTICAL_VIEW_TYPE.ordinal ->
                VerticalViewHolder(ItemFolderVerticalBinding.inflate(inflater, viewGroup, false))
            FolderListViewType.HORIZONTAL_VIEW_TYPE.ordinal ->
                HorizontalViewHolder(
                    ItemFolderHorizontalBinding.inflate(inflater, viewGroup, false)
                )
            FolderListViewType.SQUARE_VIEW_TYPE.ordinal ->
                SquareViewHolder(ItemFolderSquareBinding.inflate(inflater, viewGroup, false))
            else ->
                NewFolderViewHolder(ItemNewFolderBinding.inflate(inflater, viewGroup, false))
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val folder = dataSet[position]
        when (viewHolder) {
            is VerticalViewHolder -> viewHolder.bind(folder)
            is HorizontalViewHolder -> viewHolder.bind(folder)
            is SquareViewHolder -> viewHolder.bind(folder)
            is NewFolderViewHolder -> viewHolder.bind()
        }
    }

    fun getData(): List<FolderItem> = dataSet

    override fun getItemCount(): Int = dataSet.size

    override fun getItemId(position: Int): Long = dataSet[position].id ?: 0

    override fun getItemViewType(position: Int): Int {
        return when (folderListViewType) {
            FolderListViewType.SQUARE_VIEW_TYPE -> {
                if (position == 0) {
                    FolderListViewType.NEW_FOLDER_VIEW_TYPE
                } else {
                    FolderListViewType.SQUARE_VIEW_TYPE
                }
            }
            else -> {
                folderListViewType
            }
        }.ordinal
    }

    fun setData(items: List<FolderItem>?) {
        if (folderListViewType != FolderListViewType.SQUARE_VIEW_TYPE) {
            dataSet.clear()
        }
        items?.let { dataSet.addAll(it) }
        notifyDataSetChanged()
    }

    fun addData(folderItem: FolderItem) {
        if (folderListViewType == FolderListViewType.SQUARE_VIEW_TYPE) {
            dataSet.add(1, folderItem) // 0번 포지션인 "폴더 추가" 다음에 폴더 삽입
            notifyItemInserted(1)
            changeSelectedFolder(folderItem)
        } else {
            dataSet.add(0, folderItem)
            notifyItemInserted(0)
        }
    }

    fun updateData(oldFolder: FolderItem, newFolder: FolderItem) {
        val position = dataSet.indexOf(oldFolder)
        dataSet[position] = newFolder
        notifyItemChanged(position)
    }

    fun deleteData(folderItem: FolderItem) {
        val position = dataSet.indexOf(folderItem)
        dataSet.remove(folderItem)
        notifyItemRemoved(position)
    }

    fun setSelectedFolder(folderId: Long) {
        selectedFolderId = folderId
    }

    /** 링크 공유 전용 선택된 폴더 바꾸기 */
    fun changeSelectedFolder(folder: FolderItem) {
        val unselectedFolder = selectedFolder
        selectedFolder = folder
        notifyItemChanged(dataSet.indexOf(unselectedFolder))
        notifyItemChanged(dataSet.indexOf(selectedFolder))
        listener.onItemClick(folder)
    }
}