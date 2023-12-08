package com.wagdybuild.ecommerce.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.isNotEmpty
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wagdybuild.ecommerce.R
import com.wagdybuild.ecommerce.adapters.GroceryItemsAdapter
import com.wagdybuild.ecommerce.adapters.KidsItemsAdapter
import com.wagdybuild.ecommerce.databinding.FragmentGroceriesBinding
import com.wagdybuild.ecommerce.databinding.FragmentKidsBinding
import com.wagdybuild.ecommerce.models.CardModel
import com.wagdybuild.ecommerce.models.CartDataModel
import com.wagdybuild.ecommerce.models.User
import com.wagdybuild.ecommerce.viewModel.FireStoreViewModel
import java.util.Locale

class KidsFragment : Fragment(), OnItemClickListener {
    private lateinit var binding: FragmentKidsBinding
    private lateinit var fireStoreViewModel: FireStoreViewModel
    private var user: User? = null
    private var cartCounter: Int = 0
    private lateinit var itemList:ArrayList<CardModel>

    /** nav Controller */
    private lateinit var navController: NavController
    /** FireBase */
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mStorage: FirebaseStorage
    private lateinit var mFireStore: FirebaseFirestore
    /** Cart Lists */
    private var userCartList: CartDataModel?=null
    private lateinit var groceryList: ArrayList<CardModel>
    private lateinit var kidsList: ArrayList<CardModel>
    private lateinit var womenList: ArrayList<CardModel>
    private lateinit var menList: ArrayList<CardModel>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentKidsBinding.inflate(inflater, container, false)

        /** Nav controller */
        navController = Navigation.findNavController(container!!)
        binding.btnBack.setOnClickListener { navController.navigate(R.id.nav_kids_to_home) }

        /** Initialize all variables*/
        init()

        /** Kids items Layout*/
        gettingKidsItems()

        /** Search view */
        if (binding.searchView.isNotEmpty()) {
            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    searchInItemsList(newText)
                    return true
                }
            })
        }

        return binding.root
    }

    private fun init() {
        /** FireBase */
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mStorage = FirebaseStorage.getInstance()
        mFireStore = FirebaseFirestore.getInstance()

        itemList = ArrayList()

        /** view model */
        fireStoreViewModel = ViewModelProvider(this)[FireStoreViewModel::class.java]

        /** getting Current User Data */
        gettingCurrentUserData()

        /** Getting User Cart List */
        gettingUserCartList()
    }

    private fun gettingCurrentUserData() {
        /** getting data from view model*/
        fireStoreViewModel.getCurrentUserDataLiveData()!!.observe(viewLifecycleOwner) {
            if (it != null) {
                this.user = it
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun gettingKidsItems() {
        val kidsAdapter = KidsItemsAdapter(requireContext(),object : AddToCartListener {
            override fun onAddItemsToCart(itemCard: CardModel) {
                addCartItem(itemCard)
                cartCounter++
                binding.tvCartCountNumber.text = cartCounter.toString()
                if (cartCounter == 1) {
                    binding.tvCartCountNumber.visibility = View.VISIBLE
                }
            }

            override fun onRemoveItemFromCart(itemCard: CardModel) {
                removeCartItem(itemCard)
                cartCounter--
                binding.tvCartCountNumber.text = cartCounter.toString()
                if (cartCounter <= 0) {
                    binding.tvCartCountNumber.visibility = View.INVISIBLE
                }

            }

        },this)
        binding.rvKidsToy.layoutManager = GridLayoutManager(requireContext(),2)

        binding.rvKidsToy.adapter = kidsAdapter
        binding.rvKidsToy.setHasFixedSize(true)

        /** getting data from view model*/
        fireStoreViewModel.getKidsItemsLiveData()!!.observe(viewLifecycleOwner) {
            if (it != null && it.size > 0) {
                itemList = it
                kidsAdapter.setKidsItemList(it)
                kidsAdapter.notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun searchInItemsList(newText: String) {
        val filterList = ArrayList<CardModel>()
        if(itemList.size>0){
            for (item in itemList) {
                if (item.itemName.lowercase(Locale.getDefault()).contains(newText.lowercase(Locale.getDefault()))) {
                    filterList.add(item)
                }
            }

            val newAdapter = KidsItemsAdapter(requireContext(),object : AddToCartListener {
                override fun onAddItemsToCart(itemCard: CardModel) {
                    addCartItem(itemCard)
                    cartCounter++
                    binding.tvCartCountNumber.text = cartCounter.toString()
                    if (cartCounter == 1) {
                        binding.tvCartCountNumber.visibility = View.VISIBLE
                    }
                }

                override fun onRemoveItemFromCart(itemCard: CardModel) {
                    removeCartItem(itemCard)
                    cartCounter--
                    binding.tvCartCountNumber.text = cartCounter.toString()
                    if (cartCounter <= 0) {
                        binding.tvCartCountNumber.visibility = View.INVISIBLE
                    }

                }

            },this)
            binding.rvKidsToy.adapter = newAdapter
            newAdapter.setKidsItemList(filterList)
            newAdapter.notifyDataSetChanged()
        }

    }

    override fun onItemClick(cardItem: CardModel) {
        val bundle = Bundle()
        bundle.putSerializable("cardItem", cardItem)
        navController.navigate(R.id.nav_bestOffer_to_item, bundle)
    }

    private fun gettingUserCartList() {
        fireStoreViewModel.getCartItemsLiveData()!!.observe(viewLifecycleOwner) {
            this.userCartList = it
        }
    }

    private fun addCartItem(item: CardModel) {
        val cartList = CartDataModel()
        if(userCartList!!.kidsList == null){
            userCartList!!.kidsList = ArrayList()
        }

        cartList.groceryList = ArrayList()
        cartList.menList = ArrayList()
        cartList.kidsList = ArrayList()
        cartList.womenList = ArrayList()
        cartList.bestOfferList = ArrayList()

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

    private fun removeCartItem(item: CardModel) {
        userCartList!!.kidsList!!.remove(item)
        mFireStore.collection("Cart").document(mAuth.currentUser!!.uid)
            .update("kidsList", userCartList!!.kidsList)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Removed from cart", Toast.LENGTH_SHORT)
                    .show()
            }

    }
}