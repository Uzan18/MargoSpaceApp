package com.pemmob.margocoffe.viewmodel

import com.pemmob.margocoffe.data.IceLevelOption
import com.pemmob.margocoffe.data.SizeOption
import com.pemmob.margocoffe.data.SweetnessOption

sealed interface DetailEvent {

    data class SelectSize(
        val size: SizeOption
    ) : DetailEvent

    data class SelectIceLevel(
        val iceLevel: IceLevelOption
    ) : DetailEvent

    data class SelectSweetness(
        val sweetness: SweetnessOption
    ) : DetailEvent

    data object IncrementQuantity : DetailEvent

    data object DecrementQuantity : DetailEvent

    data object AddToCart : DetailEvent
}