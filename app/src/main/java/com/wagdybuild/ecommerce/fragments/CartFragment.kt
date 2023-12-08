package com.wagdybuild.ecommerce.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wagdybuild.ecommerce.R
import com.wagdybuild.ecommerce.adapters.CartItemsAdapter
import com.wagdybuild.ecommerce.databinding.CartItemClickDialogBinding
import com.wagdybuild.ecommerce.databinding.FragmentCartBinding
import com.wagdybuild.ecommerce.models.CardModel
import com.wagdybuild.ecommerce.models.CartDataModel
import com.wagdybuild.ecommerce.viewModel.FireStoreViewModel
import java.util.Collections


class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var fireStoreViewModel: FireStoreViewModel
    private lateinit var cartList: ArrayList<CardModel>
    private var totalPrice: Double = 0.0
    private var itemCount: Int = 0

    /** Cart Lists */
    private var userCartList: CartDataModel? = null

    /** nav Controller */
    private lateinit var navController: NavController

    /** FireBase */
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mStorage: FirebaseStorage
    private lateinit var mFireStore: FirebaseFirestore

    /** Custom dialog */
    private lateinit var cartDialogBinding: CartItemClickDialogBinding
    private lateinit var builder: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        /** Nav controller */
        navController = Navigation.findNavController(container!!)
        binding.btnBack.setOnClickListener { navController.navigate(R.id.nav_cart_to_home) }

        /** Initialize all variables*/
        init()

        /** Grocery items Layout*/
        gettingCartItems()

        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    private fun init() {
        /** Firebase references */
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mStorage = FirebaseStorage.getInstance()
        mFireStore = FirebaseFirestore.getInstance()

        cartList = ArrayList()
        /** view model */
        fireStoreViewModel = ViewModelProvider(this)[FireStoreViewModel::class.java]

        /** Custom dialog */
        cartDialogBinding =
            CartItemClickDialogBinding.inflate(LayoutInflater.from(requireContext()))
        builder = AlertDialog.Builder(context).setView(cartDialogBinding.root).create()
        builder.window!!.setBackgroundDrawable(ColorDrawable(android.R.color.transparent))
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun gettingCartItems() {
        val cartAdapter = CartItemsAdapter(requireContext(), object : OnItemClickListener {
            override fun onItemClick(cardItem: CardModel) {
                itemCount = cardItem.itemCount
                builder.show()
                showDialog(cardItem)
            }

        })
        binding.rvCart.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCart.setHasFixedSize(true)
        binding.rvCart.adapter = cartAdapter

        /** getting data from view model*/
        fireStoreViewModel.getCartItemsLiveData()!!.observe(viewLifecycleOwner) {
            this.userCartList = it
            val mixedCartItemList = ArrayList<CardModel>()
            if (it.groceryList != null && it.groceryList!!.size > 0) {
                for (item in it.groceryList!!) {
                    mixedCartItemList.add(item)
                }
            }
            if (it.kidsList != null && it.kidsList!!.size > 0) {
                for (item in it.kidsList!!) {
                    mixedCartItemList.add(item)
                }
            }
            if (it.menList != null && it.menList!!.size > 0) {
                for (item in it.menList!!) {
                    mixedCartItemList.add(item)
                }
            }
            if (it.womenList != null && it.womenList!!.size > 0) {
                for (item in it.womenList!!) {
                    mixedCartItemList.add(item)
                }
            }
            if (it.bestOfferList != null && it.bestOfferList!!.size > 0) {
                for (item in it.bestOfferList!!) {
                    mixedCartItemList.add(item)
                }
            }

            val cartList = ArrayList<CardModel>()
            if (mixedCartItemList.size > 0) {
                for (item in mixedCartItemList.distinct()) {
                    item.itemCount = Collections.frequency(mixedCartItemList, item)
                    cartList.add(item)
                }
                cartAdapter.setCartItemList(cartList)
                cartAdapter.notifyDataSetChanged()

                for (item in cartList) {
                    totalPrice += item.itemCount * item.itemPrice
                }

                binding.tvTotalPrice.text = String.format("%.2f", totalPrice)


            }

        }

    }

    private fun addItem(itemCard: CardModel) {
        if (userCartList!!.menList == null) {
            userCartList!!.menList = ArrayList()
        }
        if (userCartList!!.womenList == null) {
            userCartList!!.womenList = ArrayList()
        }
        if (userCartList!!.kidsList == null) {
            userCartList!!.kidsList = ArrayList()
        }
        if (userCartList!!.groceryList == null) {
            userCartList!!.groceryList = ArrayList()
        }
        if (userCartList!!.bestOfferList == null) {
            userCartList!!.bestOfferList = ArrayList()
        }

        when (itemCard.itemCategory) {
            "Grocery" -> {
                userCartList!!.groceryList!!.add(itemCard)

                mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                    .update("groceryList", userCartList!!.groceryList)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener {
                        mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                            .set(cartList)
                    }
            }

            "Women" -> {
                userCartList!!.womenList!!.add(itemCard)

                mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                    .update("womenList", userCartList!!.womenList)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener {
                        mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                            .set(cartList)
                    }
            }

            "Kids" -> {
                userCartList!!.kidsList!!.add(itemCard)
                mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                    .update("kidsList", userCartList!!.kidsList)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener {
                        mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                            .set(cartList)
                    }
            }

            "Men" -> {
                userCartList!!.menList!!.add(itemCard)
                mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                    .update("menList", userCartList!!.menList!!)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener {
                        mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                            .set(cartList)
                    }
            }

            "Best Offer" -> {
                userCartList!!.bestOfferList!!.add(itemCard)
                mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                    .update("bestOfferList", userCartList!!.bestOfferList!!)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT)
                            .show()
                    }
            }

        }
    }

    private fun removeItem(itemCard: CardModel) {
        when (itemCard.itemCategory) {
            "Grocery" -> {
                userCartList!!.groceryList!!.remove(itemCard)
                mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                    .update("groceryList", userCartList!!.groceryList)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Removed from cart", Toast.LENGTH_SHORT)
                            .show()
                    }
            }

            "Women" -> {
                userCartList!!.womenList!!.remove(itemCard)
                mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                    .update("womenList", userCartList!!.womenList)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Removed from cart", Toast.LENGTH_SHORT)
                            .show()
                    }
            }

            "Kids" -> {
                userCartList!!.kidsList!!.remove(itemCard)
                mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                    .update("kidsList", userCartList!!.kidsList)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Removed from cart", Toast.LENGTH_SHORT)
                            .show()
                    }
            }

            "Men" -> {
                userCartList!!.menList!!.remove(itemCard)
                mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                    .update("menList", userCartList!!.menList!!)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Removed from cart", Toast.LENGTH_SHORT)
                            .show()
                    }
            }

            "Best Offer" -> {
                userCartList!!.bestOfferList!!.remove(itemCard)
                mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                    .update("bestOfferList", userCartList!!.bestOfferList!!)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Removed from cart", Toast.LENGTH_SHORT)
                            .show()
                    }
            }

        }
    }

    private fun showDialog(item: CardModel) {
        cartDialogBinding.itemCount.text = itemCount.toString()
        Glide.with(requireContext()).load(item.itemImage).placeholder(R.drawable.ic_image)
            .into(cartDialogBinding.itemRiv)

        cartDialogBinding.btnAdd.setOnClickListener {
            itemCount++
            cartDialogBinding.itemCount.text = itemCount.toString()
            addItem(item)
        }

        cartDialogBinding.btnMinus.setOnClickListener {
            itemCount--
            if (itemCount >= 0) {
                cartDialogBinding.itemCount.text = itemCount.toString()
                removeItem(item)
            } else {
                Toast.makeText(requireContext(), "Item Empty", Toast.LENGTH_SHORT).show()
            }
        }

        cartDialogBinding.btnSave.setOnClickListener {
            builder.dismiss()
            navController.navigate(R.id.nav_cart_to_itself)
        }


    }


}