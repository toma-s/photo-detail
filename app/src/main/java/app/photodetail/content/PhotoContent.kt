package app.photodetail.content

import android.os.Environment
import java.io.File
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import app.photodetail.model.Photo
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
object PhotoContent {

    val photos: MutableList<Photo> = ArrayList()
    val photoToId: MutableMap<String, Photo> = HashMap()
    private var filePaths: MutableList<String> = ArrayList()
    private val extensions = arrayOf("jpg", "jpeg")
    private var loadedCount = 0

    init {
        val directory: File = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        findFiles(directory)
        sortFiles()
    }

    private fun sortFiles() {
        val sortedPaths = filePaths.sortedWith(Comparator<String>{o1, o2 ->
            val name1 = o1.split("/").last()
            val name2 = o2.split("/").last()
            when {
                name1 < name2 -> 1
                name1 > name2 -> -1
                else -> 0
            }
        })
        filePaths = sortedPaths.toMutableList()
    }

    private fun findFiles(directory: File) {
        if (directory.exists() && directory.isDirectory) {
            val files = directory.listFiles()
            if (files != null) {
                for (file: File in files) {
                    Log.d("mylog", file.absolutePath)
                    if (file.isDirectory) {
                        if (file.name[0] != '.') {
//                            Log.d("mylog", "starting with directory")
                            findFiles(file)
                        }
                    }
                    if (file.isFile) {
                        val fileName = file.name.toLowerCase().split(".")
                        val extention = fileName[fileName.size - 1]

                        if (extensions.contains(extention)) {
                            filePaths.add(file.absolutePath)
//                            filesArray.add(file)
                            Log.d("mylog", "added ${file.absolutePath}")
                        }

                    }
                }
//                Log.d("mylog", "finished with directory")
                return
            }
        }
    }

    fun loadPhotos() {
        try {
            Log.d("mylog", "loading file $loadedCount: " + filePaths[loadedCount])
            addPhoto(createPhoto(loadedCount, File(filePaths[loadedCount])))
            loadedCount++
        } catch (e: IndexOutOfBoundsException) {
            Log.d("mylog", "out of bounds: filesArray")
        }
    }

    private fun addPhoto(photo: Photo) {
        Log.d("mylog", photo.detail)
        photos.add(photo)
        photoToId[photo.id.toString()] = photo
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createPhoto(position: Int, file: File): Photo {
        val photo = Photo(position, file)
        photo.loadBitmap()
        return photo
    }



}