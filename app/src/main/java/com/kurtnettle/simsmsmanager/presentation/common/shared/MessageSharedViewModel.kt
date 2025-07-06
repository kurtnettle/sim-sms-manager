package com.kurtnettle.simsmsmanager.presentation.common.shared

import android.telephony.SubscriptionInfo
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kurtnettle.simsmsmanager.data.repository.SimCardRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MessageSharedViewModel(
    private val simCardRepository: SimCardRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _selectedSubId = MutableStateFlow(0)
    private val _selectedMsgIds = mutableStateListOf<String>()
    private val _deletedMsgIds = mutableStateListOf<String>()
    private val _allSubInfo = MutableStateFlow<List<SubscriptionInfo>>(emptyList())
    private val _simMessages = MutableStateFlow<List<Map<String, String>>?>(null)
    private val _toastChannel = Channel<String>(10)

    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val selectedSubId: StateFlow<Int> = _selectedSubId.asStateFlow()
    val selectedMsgIds: List<String> = _selectedMsgIds
    val deletedMsgIds: List<String> = _deletedMsgIds
    val allSubInfo: StateFlow<List<SubscriptionInfo>> = _allSubInfo.asStateFlow()
    val simMessages: StateFlow<List<Map<String, String>>?> = _simMessages.asStateFlow()
    val toastFlow = _toastChannel.receiveAsFlow()

    init {
        getAllSubInfo()
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastChannel.send(message)
        }
    }

    fun getAllSubInfo() {
        viewModelScope.launch {
            try {
                _allSubInfo.value = simCardRepository.getAllSubInfo()
            } catch (e: Exception) {
                val msg = "Failed to fetch SIM cards"
                Log.e("SIM_ERROR", msg, e)
                showToast("$msg: ${e.localizedMessage}")
            }
        }
    }

    fun getSimMessages() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _simMessages.value = simCardRepository.getSimMessages(selectedSubId.value)
            } catch (e: Exception) {
                val msg = "Failed to fetch SIM messages"
                Log.e("SIM_ERROR", msg, e)
                showToast("$msg: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSelectedMessages() {
        _selectedMsgIds.clear()
    }

    fun updateSelectedMessages(messageId: String) {
        Log.d("SSManager", "updateSelectedMessages: $messageId")
        if (messageId in _selectedMsgIds) {
            _selectedMsgIds.remove(messageId)
        } else {
            _selectedMsgIds.add(messageId)
        }
    }

    fun deleteSimMessages() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val deletedMessages = simCardRepository.deleteSimMessages(
                    selectedSubId.value,
                    selectedMsgIds
                )

                if (deletedMessages.size != selectedMsgIds.size) {
                    Log.w("SSManager", "some messages weren't deleted.")
                }

                deletedMessages.forEach { msgId ->
                    Log.d("SSManager", "removed: $msgId")
                    _selectedMsgIds.remove(msgId)
                    _deletedMsgIds.add(msgId)
                }
            } catch (e: Exception) {
                val msg = "Failed to delete messages"
                Log.e("SSManager", msg, e)
                showToast("$msg: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSelectedSim(subId: Int) {
        if (_selectedSubId.value == subId || _isLoading.value) return

        _isLoading.value = true
        _selectedSubId.value = subId

        try {
            getSimMessages()
        } catch (e: Exception) {
            Log.e("e", e.toString())
        }
    }
}
