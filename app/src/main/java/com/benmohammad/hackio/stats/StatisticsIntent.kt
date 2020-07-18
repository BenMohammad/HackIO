package com.benmohammad.hackio.stats

import com.benmohammad.hackio.mvibase.MviIntent

sealed class StatisticsIntent: MviIntent {

    object InitialIntent : StatisticsIntent()
}