package com.unplugged.launcher.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun currentTime(): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

fun currentDate(): String =
    SimpleDateFormat("EEEE, dd.MM.yyyy", Locale.getDefault()).format(Date())
    