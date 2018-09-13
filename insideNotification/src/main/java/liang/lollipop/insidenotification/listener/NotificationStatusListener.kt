package liang.lollipop.insidenotification.listener

/**
 * @date: 2018/09/13 22:53
 * @author: lollipop
 * 消息提示图层状态监听
 */
interface NotificationStatusListener {

    /**
     * 当消息图层显示时得到回调
     */
    fun onInsideNotificationShown()

    /**
     * 当消息图层隐藏时被调用
     */
    fun onInsideNotificationHide()

}