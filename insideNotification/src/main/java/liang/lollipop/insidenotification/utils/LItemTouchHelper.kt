package liang.lollipop.insidenotification.utils

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import liang.lollipop.insidenotification.holder.NotificationHolder

/**
 * Created by lollipop on 2018/1/2.
 * @author Lollipop
 * 用来做RecyclerView滑动删除和拖拽排序
 * 以及点击事件
 *
 */
class LItemTouchHelper(private val callback: LItemTouchCallback):ItemTouchHelper(callback) {

    companion object {

        fun newInstance(recyclerView: RecyclerView, onItemTouchCallbackListener: LItemTouchCallback.OnItemTouchCallbackListener): LItemTouchHelper {
            return newInstance(recyclerView, true, true, onItemTouchCallbackListener)
        }

        fun newInstance(recyclerView: RecyclerView, canDrag: Boolean, canSwipe: Boolean, onItemTouchCallbackListener: LItemTouchCallback.OnItemTouchCallbackListener): LItemTouchHelper {
            val callback = LItemTouchCallback(onItemTouchCallbackListener)
            val helper = LItemTouchHelper(callback)
            helper.setCanDrag(canDrag)
            helper.setCanSwipe(canSwipe)
            helper.attachToRecyclerView(recyclerView)
            return helper
        }

    }

    fun setCanDrag(canDrag: Boolean) {
        callback.setCanDrag(canDrag)
    }

    fun setCanSwipe(canSwipe: Boolean) {
        callback.setCanSwipe(canSwipe)
    }

    fun onSwiped(holder: NotificationHolder<*>) {
        callback.onSwiped(holder, 0)
    }

    fun setStateChangedListener(stateChangedListener: LItemTouchCallback.OnItemTouchStateChangedListener) {
        this.callback.setStateChangedListener(stateChangedListener)
    }

}