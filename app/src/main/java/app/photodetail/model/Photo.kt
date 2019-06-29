package app.photodetail.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import java.io.File
import java.lang.Exception

data class Photo(val id: Int, val file: File) {
    val path = file.absolutePath
    val name = file.name
    val size = String.format("%.1f MB", file.length() / (1024*1024.0))
    var bitmap: Bitmap? = null
    var content: String? = ""
    var detail: String? = ""

    fun loadBitmap() {

        try {
            bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(path), 600, 600, false)
        } catch (e: Exception) {
            Log.d("mylog", "Error at loadBitmap: $e")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun makeDescription() {
        val ei = ExifInterface(file.absolutePath)
        val date = ei.getAttribute(ExifInterface.TAG_DATETIME)
        val focLen = ei.getAttribute(ExifInterface.TAG_FOCAL_LENGTH)
        val aperture = ei.getAttribute(ExifInterface.TAG_F_NUMBER)
        val exposureTime = ei.getAttribute(ExifInterface.TAG_EXPOSURE_TIME)
        val iso = ei.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS)

        content = "Photo $name"
        val builder = StringBuilder()
            .append("Name:\t$name\n")
            .append("Path:\t$path\n")
            .append("Size:\t$size\n")
        if (date == null) builder.append("Date:\tNo Data\n")
        else builder.append("Date:\t$date\n")

        if (focLen == null) builder.append("Focal length:\tNo Data\n")
        else builder.append("Focal length:\t$focLen mm\n")

        if (aperture == null) builder.append("Aperture:\tNo Data\n")
        else builder.append("Aperture:\tf/$aperture\n")

        if (exposureTime == null) builder.append("Exposure time:\tNo Data\n")
        else builder.append("Exposure time:\t$exposureTime\n")

        if (iso == null) builder.append("ISO:\tNo Data\n")
        else builder.append("ISO:\t$iso\n")
        detail = builder.toString()
    }

}