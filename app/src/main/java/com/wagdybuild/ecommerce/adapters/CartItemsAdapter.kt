package com.wagdybuild.ecommerce.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wagdybuild.ecommerce.R
import com.wagdybuild.ecommerce.databinding.CartItemClickDialogBinding
import com.wagdybuild.ecommerce.databinding.ItemCartBinding
import com.wagdybuild.ecommerce.fragments.AddToCartListener
import com.wagdybuild.ecommerce.fragments.OnItemClickListener
import com.wagdybuild.ecommerce.models.CardModel


class CartItemsAdapter(context: Context, onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<CartItemsAdapter.ViewHolder>() {
    private var cartList: ArrayList<CardModel>
    private var context: Context
    private var onItemClickListener: OnItemClickListener
    private var itemCount = 0

    init {
        this.context = context
        this.onItemClickListener = onItemClickListener
        cartList = ArrayList()
    }

    fun setCartItemList(it: ArrayList<CardModel>) {
        this.cartList = it
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cartList[position]
        itemCount = item.itemCount
        /** put Data in fields **/
        holder.binding.itemName.text = item.itemName
        holder.binding.itemQuantity.text = item.itemCount.toString()
        holder.binding.itemPrice.text = item.itemPrice.toString()
        Glide.with(context).load(item.itemImage)
            .placeholder(R.drawable.w4).into(holder.binding.itemRiv)

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(item)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: ItemCartBinding = ItemCartBinding.bind(view)

    }


}