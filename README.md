# InsideNotification
一个简单的页内消息提示组件

#### 特点：
> * 消息列表与Activity关联，在Activity内部，不需要额外的消息通知权限
> * Activity之间的消息分割开，不会互相干扰
> * 消息的多样化，高度自定义
> * 事件逻辑的多样化，适应更多场景
> * 数据格式开放，对现有项目要求低

#### 适应场景：
> * 与页面关联性强的提示信息
> * 与页面关联性强的对话信息

#### 不适应场景：
> * 应用位于后台时消息提醒
> * 应用全局性消息提示

#### 支持特性：
> * 自定义消息数据类型
> * 自定义消息样式
> * 自定义消息点击事件
> * 自定义消息超时（超时后消息自动移除，也可以不自动移除）
> * 自定义消息手势（滑动删除）
> * 自定义消息位置
> * 自定义消息布局方式
> * 自定义背景模式（目前支持：无，纯色，高斯模糊）
> * 支持多类型消息混搭
> * 支持在Application中初始化后，直接在Activity中使用
> * 自动关联Activity生命周期

#### 预览：
效果预览视频：[视频地址](https://github.com/Mr-XiaoLiang/InsideNotification/blob/master/video/video-2018-09-13-.mov) <br>
**效果图后是使用说明** <br>
静态效果图：<br>
![效果预览](https://github.com/Mr-XiaoLiang/InsideNotification/blob/master/video/20180913-224226.png)

#### 使用说明：

引用方式：
```
implementation "liang.lollipop.insidenotification:insideNotification:1.0.0"
```

1. 需要在`Application`中初始化`NotificationManager`。
``` Kotlin
    override fun onCreate() {
        super.onCreate()
        // 传入消息提供器，将返回消息管理器的实例对象
        notificationManager = NotificationManager.init(this, object :NotificationProvider<T>(){
                                          // 生成消息的方法，将返回消息对象，及消息的展示的View
                                          override fun createNotificationView(layoutInflater: LayoutInflater, parent: ViewGroup, type: Int): InsideNotification<T> {
                                              TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                          }
                                      })
    }
```

2. 初始化完成后，即可通过`NotificationManager`的实例对象获取`Activity`对应的`NotificationController`。
``` Kotlin
    // 建议在Application中保留此方法。不建议写做静态方法。
    fun getNotificationController(activity: Activity): NotificationController<NotificationBean>?{
        return notificationManager.getController(activity)
    }
```

3. `Activity`在从`Application`中获取到`NotificationController`后，即可直接发送消息了：
``` Kotlin
    notificationController = application.getNotificationController(this)
    notificationController.sendNotification(bean: T)
```

最后，InsideNotification的实现相当简单：
``` Kotlin
    // 只需要重写onBind方法即可，在onBind中将数据绑定到View上，即可
    private class InsideNotificationDemo0(view: View): InsideNotification<T>(view){

        override fun onBind(data: T) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
```

以上即为"页内消息组件"的基本使用方法，上面提到的自定义项，均在`NotificationProvider`中，重写相应方法或修改相应属性即可。
在这里，希望你能使用愉快。如果有疑问，欢迎提问，如果有更好的建议，也欢迎告诉我。
