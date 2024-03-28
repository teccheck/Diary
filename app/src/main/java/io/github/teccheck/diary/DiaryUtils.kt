package io.github.teccheck.diary

import android.util.Log
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

object DiaryUtils {
    private const val TAG = "DiaryUtils"
    private const val DATE_FORMAT = "yyyy-MM-dd"

    fun getDateFormat(): DateFormat {
        return SimpleDateFormat(DATE_FORMAT)
    }

    fun getLocalizedDateString(filename: String): String {
        val dot = filename.lastIndexOf('.')
        val name = if (dot < 0) filename else filename.substring(0, dot)

        val parseDateFormat = getDateFormat()
        parseDateFormat.isLenient = false
        val localDateFormat = DateFormat.getDateInstance()

        try {
            val date = parseDateFormat.parse(name) ?: return filename
            Log.d(TAG, date.toString())
            return localDateFormat.format(date)
        } catch (e: ParseException) {
            Log.e(TAG, e.message, e)
            return filename
        }
    }
}