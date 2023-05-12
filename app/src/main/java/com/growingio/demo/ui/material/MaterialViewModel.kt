package com.growingio.demo.ui.material

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.growingio.demo.data.MaterialItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MaterialViewModel @Inject constructor(
    private val materialItems: MutableSet<MaterialItem>
) : ViewModel() {

    private val _materialItemState = MutableStateFlow<MaterialItemState>(MaterialItemState.Empty)

    val materialItemState = _materialItemState
        .asStateFlow().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MaterialItemState.Empty
        )

    fun refreshData() {
        val list = materialItems.sortedBy { it.sort }.toMutableSet()
        viewModelScope.launch {
            _materialItemState.emit(MaterialItemState.MaterialItemSet(list))
        }
    }

    fun clear() {
        viewModelScope.launch {
            _materialItemState.emit(MaterialItemState.Empty)
        }
    }


}

sealed interface MaterialItemState {
    object Empty : MaterialItemState

    data class MaterialItemSet(val set: MutableSet<MaterialItem>) : MaterialItemState
}
