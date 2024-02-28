package me.aljan.telpo_flutter_sdk

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.common.apiutil.printer.ThermalPrinter

enum class PrintType {
    Byte, Text, QR, PDF, WalkPaper,
}

class Utils {
    fun createByteImage(bytes: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun getAlignment(alignment: String?): Int {
        return when (alignment) {
            "left" -> {
                ThermalPrinter.ALGIN_LEFT
            }
            "center" -> {
                ThermalPrinter.ALGIN_MIDDLE
            }
            "right" -> {
                ThermalPrinter.ALGIN_RIGHT
            }
            else -> {
                ThermalPrinter.ALGIN_LEFT
            }
        }
    }

    fun getFontSize(fontSize: String): Int {
        return when (fontSize) {
            "size18" -> 18
            "size24" -> 24
            "size34" -> 34
            "size44" -> 44
            "size54" -> 54
            "size64" -> 64
            else -> 18
        }
    }

    fun getPrintType(type: String): PrintType {
        return when (type) {
            "byte" -> {
                PrintType.Byte
            }
            "text" -> {
                PrintType.Text
            }
            "qr" -> {
                PrintType.QR
            }
            "pdf" -> {
                PrintType.PDF
            }
            "walkpaper" -> {
                PrintType.WalkPaper
            }
            else -> {
                PrintType.WalkPaper
            }
        }
    }

}