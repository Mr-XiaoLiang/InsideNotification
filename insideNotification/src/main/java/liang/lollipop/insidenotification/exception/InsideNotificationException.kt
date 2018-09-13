package liang.lollipop.insidenotification.exception

/**
 * @date: 2018/09/09 21:17
 * @author: lollipop
 * 消息的异常
 */
class InsideNotificationException: RuntimeException {

    constructor(message: String): super(message)

    constructor(message: String, throwable: Throwable): super(message, throwable)

}