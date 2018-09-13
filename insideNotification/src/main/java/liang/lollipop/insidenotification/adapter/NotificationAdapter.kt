package liang.lollipop.insidenotification.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import liang.lollipop.insidenotification.bean.NotificationInfo
import liang.lollipop.insidenotification.holder.NotificationHolder
import liang.lollipop.insidenotification.listener.HolderClickListener
import liang.lollipop.insidenotification.provider.NotificationProvider

/**
 * @date: 2018/09/09 19:34
 * @author: lollipop
 * 消息列表的适配器
 */
class NotificationAdapter<T>(private val data: ArrayList<NotificationInfo<T>>,
                             private val provider: NotificationProvider<T>,
                             private val clickListener: HolderClickListener,
                             private val layoutInflater: LayoutInflater): RecyclerView.Adapter<NotificationHolder<T>>() {

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): NotificationHolder<T> {
        return NotificationHolder(provider.createNotificationView(layoutInflater,parent, type)).apply {
            clickBind(provider.getClickableViewIds(type))
            setClickCallback(clickListener)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: NotificationHolder<T>, position: Int) {
        holder.onBind(data[position])
    }

    override fun getItemViewType(position: Int): Int {
        return provider.getNotificationType(data[position].data)
    }
}