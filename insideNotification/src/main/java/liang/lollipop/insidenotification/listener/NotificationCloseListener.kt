package liang.lollipop.insidenotification.listener

import liang.lollipop.insidenotification.utils.Identity

/**
 * @date: 2018/09/09 00:22
 * @author: lollipop
 * 消息关闭监听方法
 */
interface NotificationCloseListener {

    /**
     * 当消息被关闭的时候，将触发此监听器
     * @param id 创建消息时，反馈的id
     */
    fun onNotificationClosed(id: Long, identity: Identity)

}