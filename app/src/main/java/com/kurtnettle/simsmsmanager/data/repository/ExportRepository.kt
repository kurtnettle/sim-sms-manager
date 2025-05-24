package com.kurtnettle.simsmsmanager.data.repository

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.telephony.SmsManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import java.io.File
import java.io.IOException


class ExportRepository {
    fun createRawJson(messages: List<Map<String, String>>): String {
        val exportJson = buildJsonArray {
            messages.forEach { map ->
                add(
                    buildJsonObject {
                        map.forEach { (key, value) ->
                            put(key, value)
                        }
                    }
                )
            }
        }

        return Json { prettyPrint = true }.encodeToString(exportJson)
    }


    // app/src/main/kotlin/org/fossify/messages/models/SmsBackup.kt
    fun createFossifyMessagesJson(messages: List<Map<String, String>>): String {
        val exportJson = buildJsonArray {
            messages.forEach { message ->
                add(
                    buildJsonObject {
                        put("subscriptionId", message["_id"]?.toInt())
                        put("address", message["address"])
                        put("body", message["body"])
                        put("date", message["date"]?.toLong())
                        put("dateSent", message["date"]?.toLong())
                        put("locked", message["locked"])
                        put("protocol", message["protocol"] ?: "0")
                        put("read", 1)
                        put(
                            "status",
                            message["error_code"]?.toInt() ?: SmsManager.STATUS_ON_ICC_READ
                        )
                        put("type", message["type"]?.toInt() ?: 1)
                        put("serviceCenter", message["service_center_address"])
                        put("backupType", "sms")
                    }
                )
            }
        }

        return Json { prettyPrint = true }.encodeToString(exportJson)
    }


    fun createQuikJson(messages: List<Map<String, String>>): String {
        val exportJson = buildJsonObject {
            put("messageCount", messages.size)
            putJsonArray("messages") {
                messages.forEach { message ->
                    add(
                        buildJsonObject {
                            put("type", message["type"]?.toInt() ?: 1)
                            put("address", message["address"])
                            put("date", message["date"]?.toLong())
                            put("dateSent", message["date"]?.toLong())
                            put("read", true)
                            put(
                                "status",
                                message["error_code"]?.toInt() ?: SmsManager.STATUS_ON_ICC_READ
                            )
                            put("body", message["body"])
                            put("protocol", message["protocol"] ?: "0")
                            put("locked", message["locked"].toBoolean())
                            put("subId", message["_id"]?.toInt())
                        }
                    )
                }
            }
        }

        return Json { prettyPrint = true }.encodeToString(exportJson)
    }

    // < 29
    fun saveFileLegacy(context: Context, filename: String, content: String): File {
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) && (
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED)
        ) {
            throw SecurityException("Storage permission required")
        }

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            throw IOException("Storage not available")
        }

        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs()
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            filename
        )

        file.writeText(content)
        return file
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveFileMediaStore(context: Context, filename: String, content: String): Uri {
        val resolver = context.contentResolver

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: throw IOException("Failed to create file")

        resolver.openOutputStream(uri)?.use { stream ->
            stream.write(content.toByteArray())
        } ?: throw IOException("Failed to write content")

        return uri
    }

    fun saveFile(context: Context, filename: String, content: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            saveFileLegacy(context, filename, content)
        } else {
            saveFileMediaStore(context, filename, content)
        }
    }
}
