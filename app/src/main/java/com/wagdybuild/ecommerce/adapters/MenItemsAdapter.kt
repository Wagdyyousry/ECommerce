package com.wagdybuild.ecommerce.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.wagdybuild.ecommerce.R
import com.wagdybuild.ecommerce.databinding.ItemCardBinding
import com.wagdybuild.ecommerce.databinding.ItemSliderBinding
import com.wagdybuild.ecommerce.fragments.AddToCartListener
import com.wagdybuild.ecommerce.fragments.OnItemClickListener
import com.wagdybuild.ecommerce.models.CardModel

class MenItemsAdapter(
    context: Context,
    addItemsListeners: AddToCartListener,
    onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<MenItemsAdapter.ViewHolder>() {
    private var menItemList: ArrayList<CardModel>? = null
    private var addItemsListeners: AddToCartListener
    private val selectedItemsList: ArrayList<CardModel>
    private var onItemClickListener: OnItemClickListener
    private var context: Context

    init {
        this.context = context
        this.addItemsListeners = addItemsListeners
        this.onItemClickListener = onItemClickListener
        selectedItemsList = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (menItemList == null) {
            0
        } else {
            menItemList!!.size
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var addToCartCount = 0
        val item = menItemList!![position]

        /** put Data in fields **/
        Glide.with(context).load(item.itemImage)
            .placeholder(R.drawable.w4).into(holder.binding.shIvCardItem)

        holder.binding.itemNameCardItem.text = item.itemName
        holder.binding.itemQuantityCardItem.text = "Quantity: ${item.itemQuantity}"
        holder.binding.tvPriceCardItem.text = item.itemPrice.toString()
        holder.binding.itemDescriptionCardItem.text = item.itemDesc

        /** On buttons click **/
        holder.binding.btnAddToCartCardItem.setOnClickListener {
            addToCartCount++
            if (addToCartCount == 1) {
                holder.binding.btnAddToCartCardItem.setImageResource(R.drawable.ic_add)
                holder.binding.btnMinusCardItem.visibility = View.VISIBLE
                holder.binding.tvCountNumberCardItem.visibility = View.VISIBLE
            }
            holder.binding.tvCountNumberCardItem.text = addToCartCount.toString()
            selectedItemsList.add(item)
            addItemsListeners.onAddItemsToCart(item)

        }

        holder.binding.btnMinusCardItem.setOnClickListener {

            addToCartCount--
            holder.binding.tvCountNumberCardItem.text = addToCartCount.toString()

            selectedItemsList.remove(item)
            addItemsListeners.onRemoveItemFromCart(item)

            if (addToCartCount <= 0) {
                holder.binding.btnAddToCartCardItem.setImageResource(R.drawable.ic_add_cart)
                holder.binding.btnMinusCardItem.visibility = View.INVISIBLE
                holder.binding.tvCountNumberCardItem.visibility = View.INVISIBLE
            }


        }

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(item)
        }

    }

    fun setMenItemList(it: ArrayList<CardModel>) {
        this.menItemList = it
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: ItemCardBinding = ItemCardBinding.bind(view)
    }

}