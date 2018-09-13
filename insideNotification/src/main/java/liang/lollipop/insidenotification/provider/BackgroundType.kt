package liang.lollipop.insidenotification.provider

/**
 * @date: 2018/09/12 21:04
 * @author: lollipop
 * 消息的背景模式
 */
enum class BackgroundType(val value: Int) {

    /**
     * 无，没有背景
     */
    NONE(-1),
    /**
     * 背景显示为颜色
     */
    COLOR(0),
    /**
     * 背景设置为高斯模糊
     */
    BLUR(1)

}