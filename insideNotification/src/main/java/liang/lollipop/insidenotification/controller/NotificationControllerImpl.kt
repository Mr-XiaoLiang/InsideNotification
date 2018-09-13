package liang.lollipop.insidenotification.controller

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Message
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import liang.lollipop.insidenotification.NotificationController
import liang.lollipop.insidenotification.adapter.NotificationAdapter
import liang.lollipop.insidenotification.bean.NotificationInfo
import liang.lollipop.insidenotification.holder.NotificationHolder
import liang.lollipop.insidenotification.listener.HolderClickListener
import liang.lollipop.insidenotification.listener.NotificationClickListener
import liang.lollipop.insidenotification.listener.NotificationCloseListener
import liang.lollipop.insidenotification.provider.BackgroundType
import liang.lollipop.insidenotification.provider.NotificationProvider
import liang.lollipop.insidenotification.utils.BlurUtil
import liang.lollipop.insidenotification.utils.Identity
import liang.lollipop.insidenotification.utils.LItemTouchCallback
import liang.lollipop.insidenotification.utils.LItemTouchHelper
import java.util.*

/**
 * @date: 2018/09/09 19:51
 * @author: lollipop
 * 消息控制器的实体类
 */
class NotificationControllerImpl<T>(private val context: Activity,
                                    private val provider: NotificationProvider<T>):
        NotificationController<T>,
        HolderClickListener,
        LItemTouchCallback.OnItemTouchCallbackListener,
        LItemTouchCallback.OnItemTouchStateChangedListener,
        ValueAnimator.AnimatorUpdateListener,
        Animator.AnimatorListener{

    companion object {

        private const val WHAT_REMOVE = 500

    }

    private var isReady = false

    private var lastNotificationId = 0L

    private var touchPosition = -1

    private lateinit var adapter: NotificationAdapter<T>

    private val notificationList = ArrayList<NotificationInfo<T>>()

    private val closeListenerList = ArrayList<NotificationCloseListener>()

    private val clickListenerList = ArrayList<NotificationClickListener>()

    private val delayedHandler = DelayedHandler(object :HandlerCallback{
        override fun onHandler(msg: Message) {
            when(msg.what){
                WHAT_REMOVE -> {
                    val id = msg.obj
                    if(id is Long){
                        if(provider.touchPreventTimeOut && touchPosition >= 0 && notificationList[touchPosition].id == id){
                            // 如果允许手势拦截，并且当前处于手势操作中，那么放弃销毁动作
                            return
                        }
                        removeNotification(id, Identity.SYSTEM)
                    }
                }
            }

        }
    })

    private val recyclerView = RecyclerView(context)

    private val backgroundView = ImageView(context)

    private val backgroundAnimator = ValueAnimator()

    private var backgroundProgress = 0F

    init {
        backgroundAnimator.addUpdateListener(this)
        backgroundAnimator.addListener(this)
    }

    private fun findRootView(): View{
        //获取根节点View
        return context.findViewById<ViewGroup>(android.R.id.content)
    }

    fun initView(){
        if(isReady){
            return
        }
        val rootView = findRootView()
        isReady = true
        val frameLayout = FrameLayout(context)
        (rootView as ViewGroup).addView(frameLayout)
        frameLayout.addView(backgroundView,FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT)
        frameLayout.addView(recyclerView)

        backgroundView.scaleType = ImageView.ScaleType.FIT_XY

        setPadding()
        hideGroup(false)
        recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        recyclerView.layoutManager = provider.getNewLayoutManager(context)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            recyclerView.elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,24F, recyclerView.resources.displayMetrics)
        }

        adapter = NotificationAdapter(notificationList,provider,this,context.layoutInflater)

        recyclerView.adapter = adapter

        LItemTouchHelper.newInstance(recyclerView, provider.canMove, provider.canSwipe, this)
                .setStateChangedListener(this)

        adapter.notifyDataSetChanged()
    }

    private fun setPadding(){
        val rootView = findRootView()
        val width = rootView.width
        val height = rootView.height
        if(width == 0 || height == 0){
            return
        }
        val left = provider.padding[0].let {
            if(it < 1){
                (width * provider.paddingWeight[0]).toInt()
            }else{
                it
            }
        }
        val top = provider.padding[1].let {
            if(it < 1){
                (height * provider.paddingWeight[1]).toInt()
            }else{
                it
            }
        }
        val right = provider.padding[2].let {
            if(it < 1){
                (width * provider.paddingWeight[2]).toInt()
            }else{
                it
            }
        }
        val bottom = provider.padding[3].let {
            if(it < 1){
                (height * provider.paddingWeight[3]).toInt()
            }else{
                it
            }
        }
        recyclerView.setPadding(left, top, right, bottom)
    }

    fun onResumed(){
        checkGroupSize()
    }

    override fun onSwiped(adapterPosition: Int) {
        removeNotification(adapterPosition, Identity.USER)
    }

    override fun onMove(srcPosition: Int, targetPosition: Int): Boolean {
        Collections.swap(notificationList,srcPosition,targetPosition)
        adapter.notifyItemMoved(srcPosition, targetPosition)
        return true
    }

    override fun onItemTouchStateChanged(viewHolder: RecyclerView.ViewHolder?, status: Int) {
        touchPosition = when(status){
            ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.ACTION_STATE_SWIPE -> viewHolder?.adapterPosition?:-1
            else -> -1
        }
    }

    override fun sendNotification(value: T): Long {
        recyclerView.scrollToPosition(0)
        var id = lastNotificationId + 1
        if(id < notificationList.size){
            id = notificationList.size.toLong()
        }
        lastNotificationId = id
        val info = NotificationInfo(id, value)
        notificationList.add(0, info)

        checkGroupSize()
        showGroup()

        adapter.notifyItemInserted(0)
        delayedToRemove(id, provider.getNotificationDelayed(provider.getNotificationType(value)))
        return id
    }

    override fun removeNotification(id: Long) {
        removeNotification(id, Identity.SELLER)
    }

    private fun removeNotification(id: Long, identity: Identity){
        var position = -1
        for(index in 0 until notificationList.size){
            if(notificationList[index].id == id){
                position = index
                break
            }
        }
        removeNotification(position, identity)
    }

    private fun removeNotification(position: Int, identity: Identity){
        if(position < 0){
            return
        }
        val bean = notificationList.removeAt(position)
        adapter.notifyItemRemoved(position)

        for(listener in closeListenerList){
            listener.onNotificationClosed(bean.id, identity)
        }
        if(notificationList.isEmpty()){
            hideGroup(true)
        }
    }

    override fun onHolderClick(holder: NotificationHolder<*>, view: View) {
        val position = holder.adapterPosition
        val id = notificationList[position].id
        val viewId = view.id
        for(listener in clickListenerList){
            listener.onNotificationClick(id, holder.itemViewType, viewId)
        }
    }

    override fun addCloseListener(listener: NotificationCloseListener) {
        closeListenerList.add(listener)
    }

    override fun removeCloseListener(listener: NotificationCloseListener) {
        closeListenerList.remove(listener)
    }

    override fun addClickListener(listener: NotificationClickListener) {
        clickListenerList.add(listener)
    }

    override fun removeClickListener(listener: NotificationClickListener) {
        clickListenerList.remove(listener)
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        when(animation){
            backgroundAnimator -> {
                backgroundProgress = (animation.animatedValue as Float)
                backgroundView.alpha = backgroundProgress
            }
        }
    }

    override fun onAnimationRepeat(animation: Animator?) {
    }

    override fun onAnimationEnd(animation: Animator?) {
        when(animation){
            backgroundAnimator -> if(notificationList.isEmpty()){
                hideBackground(false)
            }
        }
    }

    override fun onAnimationCancel(animation: Animator?) {
    }

    override fun onAnimationStart(animation: Animator?) {
        when(animation){
            backgroundAnimator -> {
                showBackground(false)
            }
        }
    }


    private class DelayedHandler(private val callback: HandlerCallback): Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if(msg != null){
                callback.onHandler(msg)
            }
        }
    }

    private interface HandlerCallback{
        fun onHandler(msg: Message)
    }

    private fun checkGroupSize(){
        val parent = recyclerView.parent?:return
        if(parent is ViewGroup){
            val viewWidth = recyclerView.width
            val viewHeight = recyclerView.height

            val standardWidth = (parent.width * provider.widthWeight).toInt()
            val standardHeight = (parent.height * provider.heightWeight).toInt()

            if(viewWidth != standardWidth || viewHeight != standardHeight){
                val layoutParams = recyclerView.layoutParams
                layoutParams.width = standardWidth
                layoutParams.height = standardHeight

                val gravity = provider.gravity
                if(layoutParams is FrameLayout.LayoutParams){
                    layoutParams.gravity = gravity
                }
                recyclerView.layoutParams = layoutParams
            }
        }
        setPadding()
    }

    private fun showGroup(){
        if(recyclerView.isShown){
            return
        }
        recyclerView.visibility = View.VISIBLE
        when(provider.backgroundType){
            BackgroundType.COLOR -> {
                showForColorBG()
            }

            BackgroundType.BLUR -> {
                showForBlurBG()
            }

            BackgroundType.NONE -> {
                hideBackground(false)
            }
        }

    }

    private fun hideGroup(isAnimator: Boolean){
        recyclerView.visibility = View.INVISIBLE
        hideBackground(isAnimator)
    }

    private fun showForColorBG(){
        backgroundView.setImageResource(0)
        backgroundView.setBackgroundColor(provider.backgroundColor)
        showBackground(true)
    }

    private fun showForBlurBG(){
        val rootView = findRootView()
        if(rootView is ViewGroup && rootView.childCount > 1){
            val bodyView = rootView.getChildAt(0)
            val srcBitmap = BlurUtil.createBitmap(bodyView)
            BlurUtil.blurImageMiniAsyn(srcBitmap,context,provider.blurLevel.toFloat(),object :BlurUtil.BlurCallback{
                override fun onUISuccess(result: Bitmap) {
                    srcBitmap.recycle()
                    backgroundView.setBackgroundColor(Color.WHITE)
                    backgroundView.setImageBitmap(result)
                    showBackground(true)
                }

                override fun onUIError(e: Exception, code: Int, msg: String) {
                    showForColorBG()
                }

            })
        }else{
            showForColorBG()
        }
    }

    private fun showBackground(isAnimator: Boolean){
        if(isAnimator){
            backgroundAnimator.cancel()
            backgroundAnimator.duration = (provider.backgroundDelayed * (1 - backgroundProgress)).toLong()
            backgroundAnimator.setFloatValues(backgroundProgress, 1F)
            backgroundAnimator.start()
        }else{
            backgroundView.visibility = View.VISIBLE
        }
    }

    private fun hideBackground(isAnimator: Boolean){
        if(isAnimator){
            backgroundAnimator.cancel()
            backgroundAnimator.duration = (provider.backgroundDelayed * backgroundProgress).toLong()
            backgroundAnimator.setFloatValues(backgroundProgress, 0F)
            backgroundAnimator.start()
        }else{
            backgroundView.visibility = View.INVISIBLE
        }
    }

    private fun delayedToRemove(id: Long, delayed: Long){
        if(delayed < 0){
            return
        }
        val message = delayedHandler.obtainMessage(WHAT_REMOVE)
        message.obj = id
        delayedHandler.sendMessageDelayed(message,delayed)
    }
}