package liang.lollipop.insidenotification

import android.app.Activity
import android.app.Application
import android.os.Bundle
import liang.lollipop.insidenotification.controller.NotificationControllerImpl
import liang.lollipop.insidenotification.provider.NotificationProvider

/**
 * @date: 2018/09/09 19:44
 * @author: lollipop
 * 页内消息管理器
 */
class NotificationManager<T>
    private constructor(application: Application, private val provider: NotificationProvider<T>): Application.ActivityLifecycleCallbacks {

    companion object {

        fun <T> init(application: Application, provider: NotificationProvider<T>): NotificationManager<T>{
            return NotificationManager(application, provider)
        }

    }

    private val controllerMap = HashMap<String, NotificationController<T>>()

    init {
        application.registerActivityLifecycleCallbacks(this)
    }

    fun getController(activity: Activity): NotificationController<T>?{
        return controllerMap[activity.toString()]
    }

    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {
        getControllerImpl(activity)?.onResumed()
    }

    override fun onActivityStarted(activity: Activity?) {
        getControllerImpl(activity)?.initView()
    }

    override fun onActivityDestroyed(activity: Activity?) {
        if(activity == null){
            return
        }
        controllerMap.remove(activity.toString())
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        if(activity == null){
            return
        }
        val controller = NotificationControllerImpl(activity, provider)
        controllerMap[activity.toString()] = controller
    }

    private fun getControllerImpl(activity: Activity?): NotificationControllerImpl<T>?{
        val controller = controllerMap[activity.toString()]?:return null
        if(controller is NotificationControllerImpl){
            return controller
        }
        return null
    }


}