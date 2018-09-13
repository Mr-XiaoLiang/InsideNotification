package liang.lollipop.insidenotification.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import liang.lollipop.insidenotification.bean.NotificationInfo
import liang.lollipop.insidenotification.listener.HolderClickListener
import liang.lollipop.insidenotification.provider.InsideNotification

/**
 * @date: 2018/09/09 00:41
 * @author: lollipop
 * 消息的View个体
 */
class NotificationHolder<T>(
        private val insideNotification: InsideNotification<T>) : RecyclerView.ViewHolder(insideNotification.notificationView), View.OnClickListener{

    val canMove
        get() = insideNotification.canMove

    val canSwipe
        get() = insideNotification.canSwipe

    private var clickListener: HolderClickListener? = null

    fun setClickCallback(lis: HolderClickListener){
        this.clickListener = lis
    }

    fun clickBind(ids: IntArray){
        for(id in ids){
            itemView.findViewById<View>(id)?.setOnClickListener(this)
        }
    }

    fun onBind(info: NotificationInfo<T>){
        insideNotification.onBind(info.data)
    }

    override fun onClick(v: View?) {
        if(v == null){ return }
        clickListener?.onHolderClick(this@NotificationHolder, v)
    }

}