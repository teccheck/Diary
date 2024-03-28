package io.github.teccheck.diary

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class DiaryStorage(context: Context) {

    private val folder = getDiaryFolder(context)

    private var currentDiaryEntry = ""

    init {
        folder.mkdirs()
        val file = getCurrentDiaryFile()
        if (file.exists())
            currentDiaryEntry = file.readText()
    }

    fun getDiaryEntryNames(): Array<String>? {
        return folder.list()
    }

    fun readDiaryEntry(filename: String): String {
        return File(folder, filename).readText()
    }

    fun getCurrentDiaryText(): String {
        return currentDiaryEntry
    }

    fun setCurrentDiaryText(text: String) {
        currentDiaryEntry = text
    }

    fun writeCurrentDiary() {
        Log.d(TAG, "write")
        val file = getCurrentDiaryFile()

        if (!file.exists() && currentDiaryEntry.isEmpty())
            return

        if (!file.exists())
            file.createNewFile()

        file.writeText(currentDiaryEntry)
    }

    fun exportBackup(context: Context, uri: Uri) {
        val outputStream = requireNotNull(context.contentResolver.openOutputStream(uri))
        (outputStream as FileOutputStream).channel.truncate(0)
        val zipStream = ZipOutputStream(outputStream)

        folder.listFiles()?.forEach {
            val entry = ZipEntry(it.name)
            zipStream.putNextEntry(entry)

            val inputStream = FileInputStream(it)
            inputStream.copyTo(zipStream)
            inputStream.close()

            zipStream.closeEntry()
        }

        zipStream.close()
    }

    fun importBackup(context: Context, uri: Uri) {
        val inputStream = requireNotNull(context.contentResolver.openInputStream(uri))
        val zipStream = ZipInputStream(inputStream)

        while (true) {
            val entry = zipStream.nextEntry ?: break
            importBackupFile(entry.name, zipStream)
            zipStream.closeEntry()
        }

        zipStream.close()
        inputStream.close()
    }

    private fun importBackupFile(name: String, zipInputStream: ZipInputStream) {
        val files = folder.list() ?: arrayOf()

        val dot = name.lastIndexOf('.')
        val name = if (dot < 0) name else name.substring(0, dot)
        var counter = 0

        while (files.contains(getDuplicateFileName(name, counter))) {
            counter++
        }

        val outputFile = File(folder, getDuplicateFileName(name, counter))
        outputFile.createNewFile()

        val outputStream = FileOutputStream(outputFile)
        zipInputStream.copyTo(outputStream)
        outputStream.close()
    }

    private fun getDuplicateFileName(name: String, num: Int): String {
        if (num == 0) return String.format(DIARY_FILE_NAME_FORMAT, name)
        return String.format(DIARY_FILE_NAME_FORMAT_DUPLICATE, name, num)
    }


    private fun getCurrentDiaryFile(): File {
        val format = DiaryUtils.getDateFormat()
        val date = Date(System.currentTimeMillis())
        val filename = String.format(DIARY_FILE_NAME_FORMAT, format.format(date))
        return File(folder, filename)
    }

    private fun getDiaryFolder(context: Context): File {
        return File(context.filesDir, DIARY_SUBFOLDER)
    }

    companion object {
        private const val TAG = "DiaryStorage"
        private const val DIARY_SUBFOLDER = "diary"
        const val DIARY_FILE_NAME_FORMAT = "%s.md"
        const val DIARY_FILE_NAME_FORMAT_DUPLICATE = "%s_(%d).md"
    }
}