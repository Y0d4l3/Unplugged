package com.unplugged.launcher.domain.dialer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unplugged.launcher.util.VibratorManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DialerViewModel(app: Application) : AndroidViewModel(app) {

    private val dialerManager: DialerManager by lazy { DialerManager(app) }
    private val vibratorManager: VibratorManager by lazy { VibratorManager(app) }


    val uiState: StateFlow<DialerUiState> = dialerManager.enteredNumber.map { number ->
            DialerUiState(enteredNumber = number)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = DialerUiState()
        )

    fun onNumberClicked(digit: String) {
        vibratorManager.vibrate()
        dialerManager.addDigit(digit)
    }

    fun onDeleteClicked() {
        vibratorManager.vibrate()
        dialerManager.deleteLastDigit()
    }

    fun onCallClicked() {
        vibratorManager.vibrate()
        dialerManager.dialCurrentNumber()
    }
}

data class DialerUiState(
    val enteredNumber: String = ""
)
