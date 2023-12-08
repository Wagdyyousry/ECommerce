package com.wagdybuild.ecommerce.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.wagdybuild.ecommerce.R
import com.wagdybuild.ecommerce.databinding.ItemCardBinding
import com.wagdybuild.ecommerce.databinding.ItemSliderBinding
import com.wagdybuild.ecommerce.fragments.OnItemClickListener
import com.wagdybuild.ecommerce.models.CardModel
import com.wagdybuild.ecommerce.models.Slider

class SliderAdapter(
    context: Context,
    viewPager: ViewPager2,
    onItemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<SliderAdapter.ViewHolder>() {
    private var sliderList: ArrayList<CardModel>? = null
    private var onItemClickListener: OnItemClickListener
    private var context: Context
    private var viewPager: ViewPager2

    init {
        this.context = context
        this.viewPager = viewPager
        this.onItemClickListener = onItemClickListener
        this.viewPager = viewPager
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slider, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (sliderList == null) {
            0
        } else {
            sliderList!!.size
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val slider = sliderList!![position]

        if (position == sliderList!!.size - 2) {
            viewPager.post(run)
        }

        Glide.with(context).load(slider.itemImage)
            .placeholder(R.drawable.w1).into(holder.binding.ivSliderItem)

        holder.binding.discountSliderItem.text = slider.itemDiscount

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(slider)
        }
    }

    fun setBestOfferItemList(it: ArrayList<CardModel>) {
        this.sliderList = it
    }

    @SuppressLint("NotifyDataSetChanged")
    val run = Runnable {
        sliderList!!.addAll(sliderList!!)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: ItemSliderBinding = ItemSliderBinding.bind(view)
    }
}
/*package com.wagdybuild.ecommerce.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.wagdybuild.ecommerce.R
import com.wagdybuild.ecommerce.models.CardModel
import com.wagdybuild.ecommerce.models.Slider

class SliderAdapter(context: Context,viewPager:ViewPager2 ,sliderList:ArrayList<CardModel>) :
    RecyclerView.Adapter<SliderAdapter.ViewHolder>() {
    private var sliderList: ArrayList<CardModel>?=null
    private var context: Context
    private var viewPager: ViewPager2

    init {
        this.context = context
        this.viewPager = viewPager
        this.sliderList = sliderList
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sliderImage: ImageView = view.findViewById(R.id.iv_slider_item)
        val sliderDiscount: TextView = view.findViewById(R.id.discount_slider_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slider, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sliderList!!.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val slider = sliderList!![position]

        if(position == sliderList!!.size-2) {
            viewPager.post(run)
        }

        Glide.with(context).load(slider.itemImage)
            .placeholder(R.drawable.ic_image)
            .into(holder.sliderImage)

        holder.sliderDiscount.text=slider.itemDiscount
    }
    @SuppressLint("NotifyDataSetChanged")
    val run = Runnable {
        sliderList.addAll(sliderList)
        notifyDataSetChanged()
    }
}*/