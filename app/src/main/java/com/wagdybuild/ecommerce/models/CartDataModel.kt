package com.wagdybuild.ecommerce.models

data class CartDataModel(
    var menList: ArrayList<CardModel>? = null,
    var womenList: ArrayList<CardModel>? = null,
    var kidsList: ArrayList<CardModel>? = null,
    var groceryList: ArrayList<CardModel>? = null,
    var bestOfferList: ArrayList<CardModel>? = null

)
