package liang.lollipop.insidenotification.utils

import android.content.Context
import android.graphics.*
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.onComplete
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import android.graphics.Bitmap




/**
 * Created by lollipop on 2018/1/11.
 * @author Lollipop
 * 高斯模糊用的工具类
 */
object BlurUtil {
    /**
     * 高斯模糊
     * @param context
     * @param bitmap
     * @return
     */
    fun blurBitmap(context: Context, bitmap: Bitmap,radius: Float): Bitmap {
        return blurBitmap(RenderScript.create(context),bitmap,radius)
    }

    fun blurBitmap(rs: RenderScript, bitmap: Bitmap,radius: Float): Bitmap {
        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        val outBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        //Create an Intrinsic Blur Script using the Renderscript
        val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        val allIn = Allocation.createFromBitmap(rs, bitmap)
        val allOut = Allocation.createFromBitmap(rs, outBitmap)
        //Set the radius of the blur
        blurScript.setRadius(radius)
        //Perform the Renderscript
        blurScript.setInput(allIn)
        blurScript.forEach(allOut)
        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap)
        //recycle the original bitmap
        bitmap.recycle()
        //After finishing everything, we destroy the Renderscript.
        rs.destroy()
        return outBitmap
    }

    /**
     * 获取一个模糊处理之后的图片
     * @param context
     * @param rec
     * @return
     */
    fun blurBitmap(context: Context, rec: Int, radius: Float): Bitmap {
        val res = context.resources
        return blurBitmap(context, BitmapFactory.decodeResource(res, rec), radius)
    }

    /**
     * 快速高斯模糊一个图片
     * 做模糊处理前先压缩图片
     */
    fun quickBlurBitmapMini(context: Context, bitmap: Bitmap,scale:Float, radius: Float): Bitmap {
        val matrix = Matrix()
        matrix.postScale(scale, scale) //长和宽放大缩小的比例
        val bitmapMini = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return blurBitmap(context, bitmapMini, radius)
    }

    fun quickBlurBitmap(context: Context, bitmap: Bitmap,scale:Float, radius: Float): Bitmap {
        val matrix = Matrix()
        matrix.postScale(scale, scale) //长和宽放大缩小的比例
        var bitmapBlur = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmapBlur = blurBitmap(context, bitmapBlur,radius)
        matrix.postScale(1 / scale,1 / scale)
		val result = Bitmap.createBitmap(bitmapBlur,0,0,bitmapBlur.width,bitmapBlur.height,matrix,true)
        bitmapBlur.recycle()
        return result
    }

    fun blurImageAsyn(bitmap: Bitmap,context: Context,radius: Float,callback: BlurCallback){
        blurImageAsyn(bitmap, context,0.2F,radius,callback)
    }



    fun blurImageAsyn(bitmap: Bitmap,context: Context,scale:Float, radius: Float,callback: BlurCallback){
        doAsync {
            try {
                val result = quickBlurBitmap(context,bitmap,scale, radius)
                onComplete {
                    callback.onUISuccess(result)
                }
            }catch (e: Exception){
                onComplete {
                    callback.onUIError(e, 0, e.localizedMessage)
                }
            }
        }
    }

    fun blurImageMiniAsyn(bitmap: Bitmap,context: Context,radius: Float,callback: BlurCallback){
        blurImageMiniAsyn(bitmap, context,0.2F,radius,callback)
    }

    fun blurImageMiniAsyn(bitmap: Bitmap,context: Context,scale:Float, radius: Float,callback: BlurCallback){
        doAsync {
            try {
                val result = quickBlurBitmapMini(context,bitmap,scale, radius)
                onComplete {
                    callback.onUISuccess(result)
                }
            }catch (e: Exception){
                onComplete {
                    callback.onUIError(e, 0, e.localizedMessage)
                }
            }
        }
    }

    fun blurAndSaveImageAsyn(bitmap: Bitmap,context: Context,scale:Float,radius: Float,
                             srcPath: String,srcPicName:String,
                             path: String,picName:String,
                             callback: BlurCallback){

        doAsync {
            try {
                saveBitmap(bitmap,srcPicName,srcPath)
                val result = quickBlurBitmap(context,bitmap,scale,radius)
                saveBitmap(bitmap,picName,path)
                onComplete {
                    callback.onUISuccess(result)
                }
            }catch (e: Exception){
                onComplete {
                    callback.onUIError(e, 0, e.localizedMessage)
                }
            }
        }

    }

    fun createBitmap(view: View): Bitmap{
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return bitmap
    }

    public interface BlurCallback{
        fun onUISuccess(result: Bitmap)

        fun onUIError(e: Exception, code: Int, msg: String)
    }

    /**
     * 保存方法
     */
    fun saveBitmap(bm: Bitmap, picName: String, path: String) {
        val pathFile = File(path)
        val f = File(path, picName)
        if (!pathFile.exists()) {
            pathFile.mkdirs()
        }
        if (f.exists()) {
            f.delete()
        }
        try {
            val out = FileOutputStream(f)
            bm.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}