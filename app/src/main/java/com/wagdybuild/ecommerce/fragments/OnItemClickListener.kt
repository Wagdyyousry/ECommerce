package com.wagdybuild.ecommerce.fragments

import com.wagdybuild.ecommerce.models.CardModel

interface OnItemClickListener {
    fun onItemClick(cardItem: CardModel)
}