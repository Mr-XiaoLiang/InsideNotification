package liang.lollipop.insidenotification.provider

import android.view.View

/**
 * @date: 2018/09/09 19:21
 * @author: lollipop
 * 页内消息的抽象对象
 */
abstract class InsideNotification<T>(val notificationView: View) {

    var canMove = false

    var canSwipe = true

    abstract fun onBind(data: T)

}