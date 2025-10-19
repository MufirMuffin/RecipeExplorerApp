package com.example.recipeexplorerapp1.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import java.io.File
import java.util.Random

object AppUtils {
    fun showToast(context: Context, @StringRes text: Int, isLong: Boolean) {
        showToast(context, context.getString(text), isLong)
    }

    fun showToast(context: Context?, text: String?, isLong: Boolean) {
        Toast.makeText(context, text, if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }

    private const val IS_DEBUG = false

    //public static final String DOC_ALERT_MSG = "You have to allow permission to access this feature";
    const val START_POINT = "Source file path is \n ***/***/"
    fun getRandomImageFileName(context: Context): String {
        val mediaStorageDir = context.filesDir
        val random = Random().nextInt(8997)
        val mImageName = "Braver_Img_$random.jpg"
        return File(mediaStorageDir.path + "/" + mImageName).absolutePath
    }

    /**
     * @param tag - Contains class name
     * @param msg - Log message as String
     * Method used to print log in console for development
     */
    fun printLogConsole(tag: String, msg: String?) {
        if (IS_DEBUG) {
            Log.d("##@$tag", msg!!)
        }
    }

    /**
     * This method is a string return method
     * Method used get file name from local file path
     */
    fun getFileNameFromPath(filePath: String): String {
        var fileName = ""
        try {
            fileName = filePath.substring(filePath.lastIndexOf('/') + 1)
        } catch (e: Exception) {
            printLogConsole("getFileNameFromPath", "Exception-------->" + e.message)
        }
        return fileName
    }

    fun openDocument(context: Context, filePath: String) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                File(filePath)
            )
            val intent = Intent(Intent.ACTION_VIEW)
            if (filePath.contains(".doc") || filePath.contains(".docx")) {
                intent.setDataAndType(uri, "application/msword")
            } else if (filePath.contains(".pdf")) {
                intent.setDataAndType(uri, "application/pdf")
            } else if (filePath.contains(".ppt") || filePath.contains(".pptx")) {
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
            } else if (filePath.contains(".xls") || filePath.contains(".xlsx")) {
                intent.setDataAndType(uri, "application/vnd.ms-excel")
            } else if (filePath.contains(".zip") || filePath.contains(".rar")) {
                intent.setDataAndType(uri, "application/x-wav")
            } else if (filePath.contains(".rtf")) {
                intent.setDataAndType(uri, "application/rtf")
            } else if (filePath.contains(".wav") || filePath.contains(".mp3")) {
                intent.setDataAndType(uri, "audio/x-wav")
            } else if (filePath.contains(".gif")) {
                intent.setDataAndType(uri, "image/gif")
            } else if (filePath.contains(".jpg") || filePath.contains(".jpeg") || filePath.contains(
                    ".png"
                )
            ) {
                intent.setDataAndType(uri, "image/jpeg")
            } else if (filePath.contains(".txt")) {
                intent.setDataAndType(uri, "text/plain")
            } else if (filePath.contains(".3gp") || filePath.contains(".mpg") || filePath.contains(".mpeg") || filePath.contains(
                    ".mpe"
                ) || filePath.contains(".mp4") || filePath.contains(".avi")
            ) {
                intent.setDataAndType(uri, "video/*")
            } else {
                intent.setDataAndType(uri, "*/*")
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            printLogConsole("openDocument", "Exception-------->" + e.message)
        }
    }
}
