package liang.lollipop.insidenotificationdemo

import android.app.Activity
import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import liang.lollipop.insidenotification.NotificationController
import liang.lollipop.insidenotification.NotificationManager
import liang.lollipop.insidenotification.provider.InsideNotification
import liang.lollipop.insidenotification.provider.NotificationProvider

/**
 * @date: 2018/09/09 00:29
 * @author: lollipop
 * 测试demo的应用上下文
 */
class MyApplication: Application() {

    private lateinit var notificationManager: NotificationManager<NotificationBean>

    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManager.init(this, MyNotificationProvider())
    }

    class MyNotificationProvider: NotificationProvider<NotificationBean>(){
        override fun createNotificationView(layoutInflater: LayoutInflater, parent: ViewGroup, type: Int): InsideNotification<NotificationBean> {

            return when(type){
                1 -> {
                    InsideNotificationDemo1.create(layoutInflater,parent)
                }
                2 -> {
                    InsideNotificationDemo2.create(layoutInflater,parent)
                }
                3 -> {
                    InsideNotificationDemo3.create(layoutInflater,parent)
                }
                else -> {
                    InsideNotificationDemo0.create(layoutInflater,parent)
                }
            }
        }

        override fun getClickableViewIds(type: Int): IntArray {
            if(type == 3){
                return intArrayOf(R.id.enterBtn,R.id.cancelBtn)
            }
            return super.getClickableViewIds(type)
        }

        override fun getNotificationType(data: NotificationBean): Int {
            return data.type
        }

        override fun getNotificationDelayed(type: Int): Long {
            if(type == 3){
                return -1
            }
            return super.getNotificationDelayed(type)
        }
    }

    fun getNotificationController(activity: Activity): NotificationController<NotificationBean>?{
        return notificationManager.getController(activity)
    }

    private class InsideNotificationDemo0(view: View): InsideNotification<NotificationBean>(view){

        companion object {
            fun create(layoutInflater: LayoutInflater, parent: ViewGroup): InsideNotificationDemo0{
                return InsideNotificationDemo0(layoutInflater.inflate(R.layout.item_notification,parent,false))
            }
        }

        private val textView: TextView = view.findViewById(R.id.toastNotification)

        override fun onBind(data: NotificationBean) {
            textView.text = data.msg
        }

    }

    private class InsideNotificationDemo1(view: View): InsideNotification<NotificationBean>(view){

        companion object {
            fun create(layoutInflater: LayoutInflater, parent: ViewGroup): InsideNotificationDemo1{
                return InsideNotificationDemo1(layoutInflater.inflate(R.layout.item_notification1,parent,false))
            }
        }

        private val textView: TextView = view.findViewById(R.id.toastNotification)

        override fun onBind(data: NotificationBean) {
            textView.text = data.msg
        }

    }

    private class InsideNotificationDemo2(view: View): InsideNotification<NotificationBean>(view){

        companion object {
            fun create(layoutInflater: LayoutInflater, parent: ViewGroup): InsideNotificationDemo2{
                return InsideNotificationDemo2(layoutInflater.inflate(R.layout.item_notification2,parent,false))
            }
        }

        private val textView: TextView = view.findViewById(R.id.toastNotification)

        override fun onBind(data: NotificationBean) {
            textView.text = data.msg
        }

    }

    private class InsideNotificationDemo3(view: View): InsideNotification<NotificationBean>(view){

        companion object {
            fun create(layoutInflater: LayoutInflater, parent: ViewGroup): InsideNotificationDemo3{
                return InsideNotificationDemo3(layoutInflater.inflate(R.layout.item_notification3,parent,false))
            }
        }

        private val textView: TextView = view.findViewById(R.id.msgView)

        init {
            canSwipe = false
        }

        override fun onBind(data: NotificationBean) {
            textView.text = data.msg
        }

    }

}