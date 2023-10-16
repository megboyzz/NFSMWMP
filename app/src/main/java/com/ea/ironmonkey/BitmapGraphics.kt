package com.ea.ironmonkey

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Typeface

//Весь текст в игре отрисовывается от сюда
class BitmapGraphics(width: Int, height: Int) {

    private val bitmap: Bitmap
    private val canvas = Canvas()

    // get метод сам не создается, его нужно указывать явно
    fun getBitmap(): Bitmap = bitmap
    init {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
    }

    fun clear() = canvas.drawColor(0, PorterDuff.Mode.CLEAR)

    //Здесь отрисовывается весь текст в игре
    fun drawString(paint: Paint, text: String, x: Int, y: Int) = 
        canvas.drawText(text, x.toFloat(), y.toFloat(), paint)

    companion object {

        private infix fun Typeface.withSize(textSize: Float): Paint{
            val paint = Paint()
            paint.setTypeface(this)
            paint.textSize = textSize
            paint.setColor(-1)
            paint.isAntiAlias = true
            return paint
        }
        
        

        @JvmStatic
        fun createPaintFromFamilyName(familyName: String, textSize: Float): Paint {
            return Typeface.create(familyName, Typeface.BOLD_ITALIC) withSize textSize
        }

        @JvmStatic
        fun createPaintFromFile(path: String, textSize: Float): Paint {
            return Typeface.createFromFile(path) withSize textSize
        }
    }
}
