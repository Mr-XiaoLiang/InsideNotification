package liang.lollipop.insidenotification.listener

/**
 * @date: 2018/09/09 00:27
 * @author: lollipop
 * 消息被点击后的监听器
 */
interface NotificationClickListener {

    /**
     * 当消息被点击时，触发的监听方法
     * @param id 消息id，此id的作用范围为页面内，不同页面的id可能重复
     * @param viewId 被点击的View的id
     */
    fun onNotificationClick(id: Long, type: Int, viewId: Int)

}