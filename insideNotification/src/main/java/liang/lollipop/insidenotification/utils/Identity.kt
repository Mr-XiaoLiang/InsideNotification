package liang.lollipop.insidenotification.utils

/**
 * @date: 2018/09/09 20:30
 * @author: lollipop
 * 操作身份
 */
enum class Identity(val value: Int) {

    /**
     * 系统执行操作
     */
    SYSTEM(0),
    /**
     * 操作由用户执行
     */
    USER(1),
    /**
     * 操作由应用，服务提供者移除，即应用主动移除
     */
    SELLER(2)

}