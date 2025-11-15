package dev.cloudants.iulat.lib.components.context

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