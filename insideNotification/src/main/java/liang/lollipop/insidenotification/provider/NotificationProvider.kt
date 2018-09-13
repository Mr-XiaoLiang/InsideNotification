package liang.lollipop.insidenotification.provider

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * @date: 2018/09/09 00:38
 * @author: lollipop
 * 消息提供器
 */
abstract class NotificationProvider<T> {

    /**
     * 宏观的设置消息是否可以拖拽重排序
     * 不建议开启
     * InsideNotification 中可以针对当前消息做设置
     */
    var canMove = false

    /**
     * 宏观的设置消息是否可以滑动删除
     * 默认开启
     * InsideNotification 中可以针对当前消息做设置
     */
    var canSwipe = true

    /**
     * 当设置显示范围非全屏时生效
     * 用于决定容器在屏幕上的位置
     */
    var gravity = Gravity.END or Gravity.TOP

    /**
     * 宽度的权重，默认占满全屏
     */
    var widthWeight = 1.0F

    /**
     * 高度的权重，默认占满全屏
     */
    var heightWeight = 1.0F

    /**
     * 内补白的具体参数值设置
     * 默认为0
     * 当为0是，会使用paddingWeight的值
     */
    val padding = arrayOf(0,0,0,0)

    /**
     * 内补白的权重参数值的设置
     * 默认为0，最大为1
     * 当padding为0时，才会生效
     */
    val paddingWeight = arrayOf(0F,0F,0F,0F)

    /**
     * 背景模式
     */
    var backgroundType = BackgroundType.BLUR

    /**
     * 如果背景模式为颜色时，生效
     * 背景颜色，默认为半透明的黑色
     */
    var backgroundColor = 0x88000000.toInt()

    /**
     * 如果背景模式为模糊时生效
     * 模糊的半径,
     * 取值范围为0~25
     */
    var blurLevel = 18

    /**
     * 手指操作将阻止超时的销毁动作
     */
    var touchPreventTimeOut = true

    /**
     * 背景色的渐变动画
     */
    var backgroundDelayed = 300L

    /**
     * 返回消息的排版方式，默认是列表式排版
     */
    open fun getNewLayoutManager(context: Context): RecyclerView.LayoutManager{
        return LinearLayoutManager(context)
    }

    /**
     * 创建消息体的方法，必须实现
     */
    abstract fun createNotificationView(layoutInflater: LayoutInflater, parent: ViewGroup, type: Int): InsideNotification<T>

    /**
     * 获取绑定点击事件的id
     */
    open fun getClickableViewIds(type: Int): IntArray{
        return IntArray(0)
    }

    /**
     * 返回消息的类型，默认为0
     * 当需要多种消息类型的时候，需要重写此方法
     */
    open fun getNotificationType(data: T): Int{
        return 0
    }

    /**
     * 返回消息超时时间
     * 默认为15s
     */
    open fun getNotificationDelayed(type: Int): Long{
        return 15000L
    }

}