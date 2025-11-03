package com.unplugged.launcher.domain.dialer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DialerViewModel(app: Application) : AndroidViewModel(app) {

    private val dialerManager: DialerManager by lazy { DialerManager(app) }

    val uiState: StateFlow<DialerUiState> = dialerManager.enteredNumber
        .map { number ->
            DialerUiState(enteredNumber = number)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = DialerUiState()
        )

    fun onNumberClicked(digit: String) {
        dialerManager.addDigit(digit)
    }

    fun onDeleteClicked() {
        dialerManager.deleteLastDigit()
    }

    fun onCallClicked() {
        dialerManager.dialCurrentNumber()
    }
}

data class DialerUiState(
    val enteredNumber: String = ""
)
