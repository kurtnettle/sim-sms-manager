package com.kurtnettle.simsmsmanager.data.repository

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// com.android.providers.telephony.SmsProvider.java
class SimCardRepository(private val context: Context) {
    private val subscriptionManager: SubscriptionManager by lazy {
        context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    }

    /**
     * Gets information about all available SIM cards on device.
     * @return List of SubscriptionInfo or empty if no subscriptions are available
     * @throws SecurityException if permissions are missing
     */
    fun getAllSubInfo(): List<SubscriptionInfo> {
        return try {
            when {
                Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU -> {
                    subscriptionManager.allSubscriptionInfoList
                }

                else -> {
                    subscriptionManager.activeSubscriptionInfoList.orEmpty()
                }
            }
        } catch (e: SecurityException) {
            throw SecurityException("Permission denied while accessing subscription info", e)
        } catch (e: Exception) {
            throw SecurityException("Unexpected error getting subscription info", e)
        }
    }

    /**
     * Retrieves all SMS messages for a specific SIM card.
     * @param subId The subscription ID of the SIM card
     * @return List of SMS as maps of column names to values
     * @throws SecurityException if READ_SMS permission is missing
     * @throws IllegalStateException if query fails
     */
    suspend fun getSimMessages(subId: Int): List<Map<String, String>> {
        val uri = "content://sms/icc_subId/$subId".toUri()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                throw SecurityException("READ_SMS permission is required while accessing sim sms.")
            }
        }

        val smsList = ArrayList<Map<String, String>>()
        withContext(Dispatchers.IO) {
            val contentResolver: ContentResolver = context.contentResolver
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val colNames = cursor.columnNames
                while (cursor.moveToNext()) {
                    val sms = mutableMapOf<String, String>()
                    for (colIdx in colNames.indices) {
                        sms[colNames[colIdx]] = cursor.getString(colIdx)
                    }
                    smsList.add(sms)
                }

//                Log.d("SSM", DatabaseUtils.dumpCursor(cursor).toString())
            }
        }

        return smsList
    }
}