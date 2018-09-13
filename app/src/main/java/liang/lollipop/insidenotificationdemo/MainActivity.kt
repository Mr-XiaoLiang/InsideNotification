package liang.lollipop.insidenotificationdemo

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import liang.lollipop.insidenotification.NotificationController
import liang.lollipop.insidenotification.listener.NotificationClickListener
import java.util.*

class MainActivity : AppCompatActivity() {

    private var notificationController: NotificationController<NotificationBean>? = null

    private val toastValueArray = arrayOf("In my dual profession", "I have worked with numerous children ", "hello my world", "In my dual profession as an educator and health care provider,")

    private val random = Random()

    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val application = application
        if(application is MyApplication){
            notificationController = application.getNotificationController(this)
        }

        notificationController?.addClickListener(object : NotificationClickListener{
            override fun onNotificationClick(id: Long, type: Int, viewId: Int) {
                if(type == 3){
                    when(viewId){
                        R.id.enterBtn -> {
                            val bean = NotificationBean(0, "你点击了消息的确定方法")
                            notificationController?.sendNotification(bean)
                            notificationController?.removeNotification(id)
                        }
                        R.id.cancelBtn -> {
                            notificationController?.removeNotification(id)
                        }
                    }
                }
            }
        })

        fab.setOnClickListener {
            handler.sendEmptyMessage(110)
        }
    }

    // 因为demo中为了简化代码，以这种方式使用Handler，但是这种方式并不受推荐
    private val handler = object : Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when(msg?.what){
                110->{
                    val bean = NotificationBean(index,toastValueArray[random.nextInt(toastValueArray.size)])
                    notificationController?.sendNotification(bean)
                    if(index < 4){
                        sendEmptyMessageDelayed(110,1000)
                    }else{
                        index = 0
                        return
                    }
                    index++
                }
            }
        }
    }

}
