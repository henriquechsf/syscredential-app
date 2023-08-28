package com.github.henriquechsf.syscredentialapp.util

import android.content.Intent
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.github.henriquechsf.syscredentialapp.data.model.Event
import com.github.henriquechsf.syscredentialapp.data.model.RegistrationUI
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class CsvGenerator(
    private val activity: FragmentActivity,
    private val event: Event
) {

    fun createCsvDataIntent(
        registrations: List<RegistrationUI>,
        header: String,
        filename: String
    ): Intent {
        val csvData = StringBuilder().apply {
            append("$header\n")
            registrations.forEach {
                append("${it.personName},${it.personInfo1},${formatDateString(it.createdAt)},${event.title}\n")
            }
        }
        val csvFile = saveCsvFile(csvData.toString(), filename)
        return generateCsvIntent(csvFile)
    }

    private fun saveCsvFile(csvData: String, csvFileName: String): File {
        val csvFile = File(activity.getExternalFilesDir(null), csvFileName)

        try {
            BufferedWriter(
                OutputStreamWriter(
                    FileOutputStream(csvFile),
                    StandardCharsets.ISO_8859_1
                )
            )
                .use { writer ->
                    writer.append(csvData)
                    writer.flush()
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return csvFile
    }

    private fun generateCsvIntent(csvFile: File): Intent {
        val csvUri = FileProvider.getUriForFile(
            activity,
            "com.github.henriquechsf.syscredentialapp.fileprovider",
            csvFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, csvUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        return Intent.createChooser(shareIntent, "Share CSV")
    }
}