package com.kurtnettle.simsmsmanager.presentation.messages.components

import android.os.Build
import android.telephony.SubscriptionInfo
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kurtnettle.simsmsmanager.R
import com.kurtnettle.simsmsmanager.presentation.common.components.dialogs.PropertiesDialog
import java.util.regex.Matcher
import java.util.regex.Pattern


@Composable
fun SimCardChooserRow(
    allSubs: List<SubscriptionInfo>?,
    selectedSubId: Int,
    onSimSelected: (Int) -> Unit,
) {
    val scrollState = rememberScrollState()
    var showPropertiesDialog by remember { mutableStateOf(false) }

    fun getCarrierName(sub: SubscriptionInfo): String {
        return sub.carrierName?.toString()?.trim()?.takeIf { it.isNotEmpty() }
            ?: sub.displayName?.toString() ?: "Unknown Carrier"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.Center
    ) {
        if (showPropertiesDialog) {
            PropertiesDialog(
                title = stringResource(R.string.sim_card_properties),
                parseSubscriptionInfoString(allSubs?.getBySubId(selectedSubId).toString()),
//                allSubs?.getBySubId(selectedSubId)?.asMap().orEmpty(),
//                mapOf(
//                    "props" to allSubs?.getBySubId(selectedSubId)?.toString()
//                ),
                { showPropertiesDialog = false })
        }

        Log.d("ss", allSubs?.getBySubId(selectedSubId).toString())

        allSubs?.forEach { sub ->
            val isSelected = sub.subscriptionId == selectedSubId
            val interactionSource = remember { MutableInteractionSource() }
            val indication = LocalIndication.current

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = if (isSelected) 1.dp else 0.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.outline else Color.Transparent,
                        shape = MaterialTheme.shapes.medium
                    )
                    .combinedClickable(
                        interactionSource = interactionSource,
                        indication = indication,
                        onClick = { onSimSelected(sub.subscriptionId) },
                        onLongClick = {
                            if (isSelected) showPropertiesDialog = true
                        })
                    .padding(vertical = 8.dp, horizontal = 16.dp)
                    .animateContentSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getCarrierName(sub),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(Modifier.padding(horizontal = 4.dp))
        }
    }
}

fun List<SubscriptionInfo>.getBySubId(subId: Int): SubscriptionInfo? {
    return firstOrNull { it.subscriptionId == subId }
}

fun parseSubscriptionInfoString(infoString: String): Map<String, Any?> {
    val resultMap: MutableMap<String, Any?> = HashMap<String, Any?>()

    val cleanedString = infoString.removePrefix("[SubscriptionInfo: ").removeSuffix("]")
    val pattern = Pattern.compile("(\\w+)=((?:\\[[^]]*?]|[^ ]*?)(?=\\s|$))") // DeepSeek
    val matcher: Matcher = pattern.matcher(cleanedString)

    while (matcher.find()) {
        val key = matcher.group(1)
        var value = matcher.group(2)

        if (value == "null") value = null
        if (key != null) resultMap.put(key, value)
    }

    return resultMap.toSortedMap()
}

fun SubscriptionInfo.asMap(): Map<String, Any?> {
    return buildMap {
        put("iccId", iccId)
        put("simSlotIndex", simSlotIndex)
        put("carrierName", carrierName)
        put("displayName", displayName)
        put("dataRoaming", dataRoaming)
        put("countryIso", countryIso)
        put("mcc", mcc)
        put("mnc", mnc)
        put("number", number)
        put("subscriptionId", subscriptionId)
        put("iconTint", iconTint)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            put("isEmbedded", isEmbedded.toString())
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put("cardId", cardId.toString())
            put("carrierId", carrierId)
            put("groupUuid", groupUuid)
            put("mccString", mccString)
            put("mncString", mncString)
            put("subscriptionType", subscriptionType)
            put("isOpportunistic", isOpportunistic)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            put("portIndex", portIndex)
            put("usageSetting", usageSetting)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            put("serviceCapabilities", serviceCapabilities.toString())
            put("isOnlyNonTerrestrialNetwork", isOnlyNonTerrestrialNetwork)
        }
    }.toSortedMap()
}


//@Preview
//@Composable
//fun ExampleScreen() {
//    val sims = listOf(SimCard(0, "Unknown"), SimCard(1, "Unknown1"))
//    var selectedSim by remember { mutableStateOf(1) }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        SimCardChooserRow(
//            sims = sims,
//            selectedSubId = sims[selectedSim].id,
//            onSimSelected = { selectedSim = it },
//        )
//    }
//}