package com.wagdybuild.ecommerce.models

import java.io.Serializable


data class CardModel(
    var itemImage: String = "",
    var itemCategory: String = "",
    var itemId: String = "",
    var itemName: String = "",
    var itemDesc: String = "",
    var itemPrice: Double = 0.0,
    var itemQuantity: String = "",
    var itemDiscount: String = "",
    var itemTime: Long = 0,
    var itemCount: Int = 0
) : Serializable
