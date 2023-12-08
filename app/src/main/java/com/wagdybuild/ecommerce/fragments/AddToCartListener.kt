package com.wagdybuild.ecommerce.fragments

import com.wagdybuild.ecommerce.models.CardModel


interface AddToCartListener {
    fun onAddItemsToCart(itemCard: CardModel)
    fun onRemoveItemFromCart(itemCard: CardModel)

}