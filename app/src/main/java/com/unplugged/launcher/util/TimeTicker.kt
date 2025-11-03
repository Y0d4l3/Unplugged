package com.unplugged.launcher.util

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.GlobalScope

object TimeTicker {
    private val ticker = flow {
        while (true) {
            emit(Unit)
            delay(1000 - (System.currentTimeMillis() % 1000))
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    val time: StateFlow<String> = ticker
        .map { currentTime() }
        .distinctUntilChanged()
        .stateIn(
            scope = GlobalScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = currentTime()
        )
    @OptIn(DelicateCoroutinesApi::class)
    val date: StateFlow<String> = ticker
        .map { currentDate() }
        .distinctUntilChanged()
        .stateIn(
            scope = GlobalScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = currentDate()
        )
}
