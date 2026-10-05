package com.pemmob.margocoffe.viewmodel

import com.pemmob.margocoffe.data.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object CartStore {

    private val _items =
        MutableStateFlow<List<CartItem>>(
            emptyList()
        )

    val items: StateFlow<List<CartItem>> =
        _items.asStateFlow()

    fun add(item: CartItem) {

        _items.update { current ->

            val index =
                current.indexOfFirst {
                    it.coffee.id == item.coffee.id &&
                            it.selectedSize ==
                            item.selectedSize &&
                            it.selectedIceLevel ==
                            item.selectedIceLevel &&
                            it.selectedSweetness ==
                            item.selectedSweetness
                }

            if (index >= 0) {

                current.toMutableList().apply {
                    this[index] =
                        this[index].copy(
                            quantity =
                                this[index].quantity +
                                        item.quantity
                        )
                }

            } else {

                current + item
            }
        }
    }

    fun remove(index: Int) {

        _items.update { current ->

            current.toMutableList().apply {

                if (index in indices) {
                    removeAt(index)
                }
            }
        }
    }

    fun clear() {
        _items.value = emptyList()
    }

    fun count(): Int =
        _items.value.sumOf {
            it.quantity
        }
}