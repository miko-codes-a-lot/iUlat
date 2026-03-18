package dev.cloudants.iulat.lib.components.context

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import org.mindrot.jbcrypt.BCrypt
import java.text.SimpleDateFormat
import java.util.Locale


fun String.hashPassword(): String {
    return BCrypt.hashpw(this, BCrypt.gensalt())
}

fun String.verifyPassword(password: String): Boolean {
    return BCrypt.checkpw(password, this)
}


fun formatterDate(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return ""

    val possibleFormats = listOf(
        "EEE MMM dd HH:mm:ss z yyyy",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd"
    )

    for (format in possibleFormats) {
        try {
            val parser = SimpleDateFormat(format, Locale.getDefault())
            val date = parser.parse(dateString)

            if (date != null) {
                val display = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                return display.format(date)
            }
        } catch (_: Exception) { }
    }

    return ""
}

fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return null
        Base64.encodeToString(bytes, Base64.DEFAULT)
    } catch (e: Exception) {
        null
    }
}

fun base64ToBitmap(base64: String): Bitmap? {
    return try {
        val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        null
    }
}

fun parseToSortableDate(dateString: String?): Long {
    if (dateString.isNullOrEmpty()) return 0L
    val formats = listOf(
        "EEE MMM dd HH:mm:ss zzz yyyy",
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
        "yyyy-MM-dd HH:mm:ss"
    )
    for (format in formats) {
        try {
            val sdf = SimpleDateFormat(format, Locale.ENGLISH)
            return sdf.parse(dateString)?.time ?: 0L
        } catch (e: Exception) { continue }
    }
    return 0L
}

fun formatterToFilterMonth(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return ""

    val possibleFormats = listOf(
        "EEE MMM dd HH:mm:ss z yyyy",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd"
    )

    for (format in possibleFormats) {
        try {
            val parser = SimpleDateFormat(format, Locale.getDefault())
            val date = parser.parse(dateString)

            if (date != null) {
                val filterFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                return filterFormat.format(date)
            }
        } catch (_: Exception) { }
    }
    return ""
}

fun formatterToFilterWeek(dateString: String?): String {
    if (dateString.isNullOrEmpty()) return ""

    val possibleFormats = listOf(
        "EEE MMM dd HH:mm:ss z yyyy",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd"
    )

    for (format in possibleFormats) {
        try {
            val parser = SimpleDateFormat(format, Locale.getDefault())
            val date = parser.parse(dateString)

            if (date != null) {
                val filterFormat = SimpleDateFormat("'Week' W, MMMM yyyy", Locale.getDefault())
                return filterFormat.format(date)
            }
        } catch (_: Exception) { }
    }
    return ""
}

fun getCurrentWeekString(): String {
    val filterFormat = SimpleDateFormat("'Week' W, MMMM yyyy", Locale.getDefault())
    return filterFormat.format(java.util.Date())
}