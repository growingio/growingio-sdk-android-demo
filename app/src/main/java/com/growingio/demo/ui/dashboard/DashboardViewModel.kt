package com.growingio.demo.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.growingio.demo.BuildConfig
import com.growingio.demo.data.SdkIntroItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val sdkItems: MutableSet<SdkIntroItem>,
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "V" + BuildConfig.GROWINGIO_SDK_VERSION
    }
    private val _sdkItemState = MutableStateFlow<SdkItemState>(SdkItemState.Empty)

    init {
        Log.d("DashboardViewModel", "init")
    }

    val text: LiveData<String> = _text

    val sdkItemState = _sdkItemState
        .asStateFlow().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SdkItemState.Empty,
        )

    fun refreshData() {
        val list = sdkItems.sortedBy { it.id }.toMutableSet()
        viewModelScope.launch {
            _sdkItemState.emit(SdkItemState.SdkItemSet(list))
        }
    }

    fun clear() {
        viewModelScope.launch {
            _sdkItemState.emit(SdkItemState.Empty)
        }
    }
}

sealed interface SdkItemState {
    object Empty : SdkItemState

    data class SdkItemSet(val set: MutableSet<SdkIntroItem>) : SdkItemState
}
