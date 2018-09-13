package liang.lollipop.insidenotification

import liang.lollipop.insidenotification.listener.NotificationClickListener
import liang.lollipop.insidenotification.listener.NotificationCloseListener
import liang.lollipop.insidenotification.listener.NotificationStatusListener

/**
 * @date: 2018/09/09 00:18
 * @author: lollipop
 * 页内消息控制器
 */
interface NotificationController<T> {

    /**
     * 发送消息的方法，传入数据之后，返回新消息的id
     */
    fun sendNotification(value: T): Long

    /**
     * 移除指定的消息
     */
    fun removeNotification(id: Long)

    /**
     * 添加消息关闭的监听器
     */
    fun addCloseListener(listener: NotificationCloseListener)

    /**
     * 移除指定的消息关闭监听器
     */
    fun removeCloseListener(listener: NotificationCloseListener)

    /**
     * 添加消息点击事件的监听器
     */
    fun addClickListener(listener: NotificationClickListener)

    /**
     * 移除指定的消息监听器
     */
    fun removeClickListener(listener: NotificationClickListener)

    /**
     * 添加消息图层状态的监听器
     */
    fun addStatusListener(listener: NotificationStatusListener)

    /**
     * 移除指定的消息监听器
     */
    fun removeStatusListener(listener: NotificationStatusListener)


}