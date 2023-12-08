package com.wagdybuild.ecommerce.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.wagdybuild.ecommerce.R
import com.wagdybuild.ecommerce.databinding.ItemCategoryBinding
import com.wagdybuild.ecommerce.models.Category

class CategoryAdapter(context: Context,catList:ArrayList<Category>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private var catList: ArrayList<Category>?=null
    private var context: Context
    /** nav Controller */
    private lateinit var navController: NavController

    init {
        this.catList = catList
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        /** Nav controller */
        navController = Navigation.findNavController(parent)
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false))
    }

    override fun getItemCount(): Int {
        return catList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = catList!![position]

        holder.binding.ivItemCategory.setImageResource(category.catImage)
        holder.binding.categoryNameItemCategory.text=category.catName

        holder.itemView.setOnClickListener {
            when (category.catName) {
                "Grocery" -> {
                    navController.navigate(R.id.nav_home_to_groceries)
                }
                "Men" -> {
                    navController.navigate(R.id.nav_home_to_men)
                }
                "Women" -> {
                    navController.navigate(R.id.nav_home_to_women)
                }
                "Kids" -> {
                    navController.navigate(R.id.nav_home_to_kids)
                }
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: ItemCategoryBinding = ItemCategoryBinding.bind(view)
    }
}