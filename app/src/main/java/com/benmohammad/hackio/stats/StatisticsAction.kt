package com.benmohammad.hackio.stats

import com.benmohammad.hackio.mvibase.MviAction

sealed class StatisticsAction: MviAction {

    object LoadStatisticsAction: StatisticsAction()
}