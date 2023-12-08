package com.wagdybuild.ecommerce.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wagdybuild.ecommerce.R
import com.wagdybuild.ecommerce.databinding.FragmentItemBinding
import com.wagdybuild.ecommerce.models.CardModel
import com.wagdybuild.ecommerce.models.CartDataModel
import com.wagdybuild.ecommerce.models.User
import com.wagdybuild.ecommerce.viewModel.FireStoreViewModel

class ItemFragment : Fragment() {
    private lateinit var binding: FragmentItemBinding
    private lateinit var navController: NavController
    private lateinit var fireStoreViewModel: FireStoreViewModel
    private var cartCount = 0

    /** Cart Lists */
    private var userCartList: CartDataModel? = null
    private lateinit var groceryList: ArrayList<CardModel>
    private lateinit var kidsList: ArrayList<CardModel>
    private lateinit var womenList: ArrayList<CardModel>
    private lateinit var menList: ArrayList<CardModel>

    /** Firebase references */
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mStorage: FirebaseStorage
    private lateinit var mFireStore: FirebaseFirestore
    private lateinit var cartList: ArrayList<CardModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemBinding.inflate(inflater, container, false)

        /** initialize all variables */
        init()

        /** getting bundle data  */
        val bundle: Bundle? = arguments
        val cardItem: CardModel = bundle!!.getSerializable("cardItem") as CardModel
        getData(cardItem)

        /** Nav controller */
        navController = Navigation.findNavController(container!!)
        binding.btnBack.setOnClickListener { navController.navigate(R.id.nav_itemDetails_to_home) }


        binding.btnAddToCart.setOnClickListener {
            cartCount++

            addCartItem(cardItem)
            binding.tvCartCountNumber.text = cartCount.toString()
            binding.tvCountNumber.text = cartCount.toString()

            if (cartCount == 1) {
                binding.tvCartCountNumber.visibility = View.VISIBLE
                binding.tvCountNumber.visibility = View.VISIBLE
                binding.btnMinus.visibility = View.VISIBLE

            }

        }

        binding.btnMinus.setOnClickListener {
            cartCount--
            removeCartItem(cardItem)
            binding.tvCartCountNumber.text = cartCount.toString()
            binding.tvCountNumber.text = cartCount.toString()

            if (cartCount == 0) {
                binding.tvCartCountNumber.visibility = View.INVISIBLE
                binding.tvCountNumber.visibility = View.INVISIBLE
                binding.btnMinus.visibility = View.INVISIBLE

            }
        }

        binding.btnCart.setOnClickListener {
            navController.navigate(R.id.nav_itemDetails_to_cart)
        }

        return binding.root
    }

    private fun init() {
        /** Firebase references */
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mStorage = FirebaseStorage.getInstance()
        mFireStore = FirebaseFirestore.getInstance()

        cartList = ArrayList()

        /** Cart Lists */
        groceryList = ArrayList()
        kidsList = ArrayList()
        womenList = ArrayList()
        menList = ArrayList()

        /** view model */
        fireStoreViewModel = ViewModelProvider(this)[FireStoreViewModel::class.java]

        /** Getting User Cart List */
        gettingUserCartList()

    }

    private fun gettingUserCartList() {
        fireStoreViewModel.getCartItemsLiveData()!!.observe(viewLifecycleOwner) {
            this.userCartList = it
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getData(cardItem: CardModel) {
        Glide.with(requireContext()).load(cardItem.itemImage)
            .placeholder(R.drawable.ic_image).into(binding.shIvCardItem)
        binding.tvItemName1.text = cardItem.itemName
        binding.tvItemName2.text = cardItem.itemName
        binding.tvItemDesc.text = cardItem.itemDesc
        binding.tvItemPrice.text = cardItem.itemPrice.toString()
        binding.tvItemQuantity.text = "Pieces: ${cardItem.itemQuantity}"

        if (cardItem.itemDiscount.isNotEmpty()) {
            binding.tvItemDiscount.visibility = View.VISIBLE
            binding.tvItemDiscount.text = "Discount: ${cardItem.itemDiscount}"
        }
    }

    private fun addCartItem(item: CardModel) {
        val cartList = CartDataModel()
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
        cartList.groceryList = ArrayList()
        cartList.menList = ArrayList()
        cartList.kidsList = ArrayList()
        cartList.womenList = ArrayList()
        cartList.bestOfferList = ArrayList()


        when (item.itemCategory) {
            "Grocery" -> {
                cartList.groceryList!!.add(item)
                userCartList!!.groceryList!!.add(item)

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
                cartList.womenList!!.add(item)
                userCartList!!.womenList!!.add(item)

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
                cartList.kidsList!!.add(item)
                userCartList!!.kidsList!!.add(item)
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
                cartList.menList!!.add(item)
                userCartList!!.menList!!.add(item)
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
                cartList.bestOfferList!!.add(item)
                userCartList!!.bestOfferList!!.add(item)

                mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                    .update("bestOfferList", userCartList!!.bestOfferList!!)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener {
                        mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                            .set(cartList)
                    }
            }


        }
    }

    private fun removeCartItem(item: CardModel) {
        when (item.itemCategory) {
            "Grocery" -> {
                userCartList!!.groceryList!!.remove(item)
                mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                    .update("groceryList", userCartList!!.groceryList)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Removed from cart", Toast.LENGTH_SHORT)
                            .show()
                    }
            }

            "Women" -> {
                userCartList!!.womenList!!.remove(item)
                mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                    .update("womenList", userCartList!!.womenList)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Removed from cart", Toast.LENGTH_SHORT)
                            .show()
                    }
            }

            "Kids" -> {
                userCartList!!.kidsList!!.remove(item)
                mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                    .update("kidsList", userCartList!!.kidsList)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Removed from cart", Toast.LENGTH_SHORT)
                            .show()
                    }
            }

            "Men" -> {
                userCartList!!.menList!!.remove(item)
                mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                    .update("menList", item)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Removed from cart", Toast.LENGTH_SHORT)
                            .show()
                    }
            }

            "Best Offer"->{
                userCartList!!.bestOfferList!!.remove(item)
                mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
                    .update("bestOfferList", userCartList!!.bestOfferList!!)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Removed from cart", Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }

    }

}