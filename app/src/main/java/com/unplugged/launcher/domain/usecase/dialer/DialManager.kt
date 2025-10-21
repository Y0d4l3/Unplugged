package com.unplugged.launcher.domain.usecase.dialer

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DialerManager(private val context: Context) {

    private val _enteredNumber = MutableStateFlow("")
    val enteredNumber = _enteredNumber.asStateFlow()

    fun addDigit(digit: String) {
        _enteredNumber.update { it + digit }
    }

    fun deleteLastDigit() {
        _enteredNumber.update { it.dropLast(1) }
    }

    fun dialCurrentNumber() {
        val numberToDial = _enteredNumber.value
        if (numberToDial.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = "tel:$numberToDial".toUri()
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}