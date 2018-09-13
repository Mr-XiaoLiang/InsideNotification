package liang.lollipop.insidenotification.utils

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import liang.lollipop.insidenotification.holder.NotificationHolder

/**
 * Created by lollipop on 2018/1/2.
 * @author Lollipop
 * ItemTouchHelper的回调函数
 */
class LItemTouchCallback(private val listener: OnItemTouchCallbackListener): ItemTouchHelper.Callback() {

    private var stateChangedListener: OnItemTouchStateChangedListener? = null

    interface OnItemTouchCallbackListener {
        /**
         * 当某个Item被滑动删除的时候
         *
         * @param adapterPosition item的position
         */
        fun onSwiped(adapterPosition: Int)

        /**
         * 当两个Item位置互换的时候被回调
         *
         * @param srcPosition    拖拽的item的position
         * @param targetPosition 目的地的Item的position
         * @return 开发者处理了操作应该返回true，开发者没有处理就返回false
         */
        fun onMove(srcPosition: Int, targetPosition: Int): Boolean
    }

    interface OnItemTouchStateChangedListener {
        fun onItemTouchStateChanged(viewHolder: RecyclerView.ViewHolder?, status: Int)
    }

    /**
     * 是否可以拖拽
     */
    private var isCanDrag = false
    /**
     * 是否可以被滑动
     */
    private var isCanSwipe = false

    fun setCanDrag(canDrag: Boolean) {
        isCanDrag = canDrag
    }

    fun setCanSwipe(canSwipe: Boolean) {
        isCanSwipe = canSwipe
    }

    override fun isLongPressDragEnabled(): Boolean {
        return isCanDrag
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return isCanSwipe
    }

    fun setStateChangedListener(stateChangedListener: OnItemTouchStateChangedListener){
        this.stateChangedListener = stateChangedListener
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        stateChangedListener?.onItemTouchStateChanged(viewHolder, actionState)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val layoutManager = recyclerView.layoutManager
        val flags = when (layoutManager) {
            is GridLayoutManager -> {// GridLayoutManager
                // flag如果值是0，相当于这个功能被关闭
                val dragFlag = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlag = if (layoutManager.orientation == GridLayoutManager.VERTICAL) {
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                } else {
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN
                }
                intArrayOf(dragFlag,swipeFlag)
            }

            is LinearLayoutManager -> {
                // linearLayoutManager
                val orientation = layoutManager.orientation

                val dragFlag = if(orientation == LinearLayoutManager.HORIZONTAL){
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                }else{
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN
                }


                val swipeFlag = if(orientation == LinearLayoutManager.HORIZONTAL){
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN
                }else{
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                }
                intArrayOf(dragFlag,swipeFlag)
            }

            is StaggeredGridLayoutManager -> {
                // flag如果值是0，相当于这个功能被关闭
                val dragFlag = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlag = if (layoutManager.orientation == StaggeredGridLayoutManager.VERTICAL) {
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                } else {
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN
                }
                intArrayOf(dragFlag,swipeFlag)
            }

            else -> intArrayOf(0,0)
        }.apply {
            if (viewHolder is NotificationHolder<*>) {
                if (!viewHolder.canMove) {
                    this[0] = 0
                }
                if (!viewHolder.canSwipe) {
                    this[1] = 0
                }
            }
        }
        return ItemTouchHelper.Callback.makeMovementFlags(flags[0], flags[1])
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return listener.onMove(viewHolder.adapterPosition, target.adapterPosition)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onSwiped(viewHolder.adapterPosition)
    }
}