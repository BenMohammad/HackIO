package com.benmohammad.hackio.stats

import com.benmohammad.hackio.mvibase.MviIntent

sealed class StatisticsResult: MviIntent {

    sealed class LoadStatisticsResult : StatisticsResult() {
        data class Success(val activeCOunt: Int, val completedCount: Int): LoadStatisticsResult()
        data class Failure(val error: Throwable): LoadStatisticsResult()
        object InFlight : LoadStatisticsResult()
    }
}