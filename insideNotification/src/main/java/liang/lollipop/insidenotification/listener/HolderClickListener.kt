package liang.lollipop.insidenotification.listener

import android.view.View
import liang.lollipop.insidenotification.holder.NotificationHolder

/**
 * @date: 2018/09/09 00:54
 * @author: lollipop
 * Holder的点击事件监听
 */
interface HolderClickListener {

    /**
     * 当Holder点击时，触发
     * @param holder 点击的item包装对象
     * @param view 被点击的View对象
     */
    fun onHolderClick(holder: NotificationHolder<*>, view: View)

}