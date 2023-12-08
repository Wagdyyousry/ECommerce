package com.wagdybuild.ecommerce.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wagdybuild.ecommerce.R
import com.wagdybuild.ecommerce.adapters.CategoryAdapter
import com.wagdybuild.ecommerce.adapters.GroceryItemsAdapter
import com.wagdybuild.ecommerce.adapters.KidsItemsAdapter
import com.wagdybuild.ecommerce.adapters.MenItemsAdapter
import com.wagdybuild.ecommerce.adapters.SliderAdapter
import com.wagdybuild.ecommerce.adapters.WomenItemsAdapter
import com.wagdybuild.ecommerce.databinding.FragmentHomeBinding
import com.wagdybuild.ecommerce.databinding.NavBarHeaderBinding
import com.wagdybuild.ecommerce.models.CartDataModel
import com.wagdybuild.ecommerce.models.CardModel
import com.wagdybuild.ecommerce.models.Category
import com.wagdybuild.ecommerce.models.User
import com.wagdybuild.ecommerce.viewModel.FireStoreViewModel
import com.wagdybuild.ecommerce.views.MainActivity
import com.wagdybuild.ecommerce.views.SignInActivity
import kotlin.math.abs

class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener,
    OnItemClickListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var fireStoreViewModel: FireStoreViewModel
    private var user: User? = null
    private var cartCounter: Int = 0
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
    /** nav Controller */
    private lateinit var navController: NavController
    /** Slider Layout*/
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var sliderHandler: Handler
    private lateinit var sliderRun: Runnable

    /** Recycle Adapters  */
    private lateinit var groceryAdapter: GroceryItemsAdapter

    private lateinit var kidsAdapter: KidsItemsAdapter

    private lateinit var womenAdapter: WomenItemsAdapter

    private lateinit var menAdapter: MenItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        /** Nav controller */
        navController = Navigation.findNavController(container!!)

        /** Initialize all variables*/
        init()

        /** Slider Layout*/
        gettingBestOfferItems()

        /** Grocery items Layout*/
        gettingGroceryItems()

        /** Kids items Layout*/
        gettingKidsItems()

        /** Women items Layout*/
        gettingWomenItems()

        /** Men items Layout*/
        gettingMenItems()

        binding.swipeRefresh.setOnRefreshListener {
            requireActivity().finish()
            requireActivity().overridePendingTransition(0, 0)
            requireContext().startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().overridePendingTransition(0, 0)

            //requireActivity().recreate()
            binding.swipeRefresh.isRefreshing = false
        }

        binding.btnCart.setOnClickListener {
            navController.navigate(R.id.nav_home_to_cart)
        }

        return binding.root
    }

    private fun init() {
        /** FireBase */
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mStorage = FirebaseStorage.getInstance()
        mFireStore = FirebaseFirestore.getInstance()

        /** Cart Lists */
        groceryList = ArrayList()
        kidsList = ArrayList()
        womenList = ArrayList()
        menList = ArrayList()

        /** view model */
        fireStoreViewModel = ViewModelProvider(this)[FireStoreViewModel::class.java]

        /** on btn see all clicked */
        seeAllButtons()

        /** Getting Current User Data */
        gettingCurrentUserData()

        /** Getting User Cart List */
        gettingUserCartList()

        /** toolbar Layout && Navigation bar  */
        binding.civMainUserImage.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.navView.itemIconTintList = null
        binding.navView.setNavigationItemSelectedListener(this)

        /** Category Layout*/
        val catList = ArrayList<Category>()
        gettingCategories(catList)

        val catAdapter = CategoryAdapter(requireContext(), catList)
        binding.allScreen.rvAllCategory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.allScreen.rvAllCategory.adapter = catAdapter

    }

    private fun seeAllButtons() {

        binding.allScreen.tvSeeAllBestOffer.paintFlags =
            binding.allScreen.tvSeeAllBestOffer.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.allScreen.tvSeeAllGrocery.paintFlags =
            binding.allScreen.tvSeeAllGrocery.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.allScreen.tvSeeAllKidsToy.paintFlags =
            binding.allScreen.tvSeeAllKidsToy.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.allScreen.tvSeeAllMenClothes.paintFlags =
            binding.allScreen.tvSeeAllMenClothes.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.allScreen.tvSeeAllWomenClothes.paintFlags =
            binding.allScreen.tvSeeAllWomenClothes.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.allScreen.tvSeeAllBestOffer.setOnClickListener { navController.navigate(R.id.nav_home_to_bestOffers) }
        binding.allScreen.tvSeeAllGrocery.setOnClickListener { navController.navigate(R.id.nav_home_to_groceries) }
        binding.allScreen.tvSeeAllKidsToy.setOnClickListener { navController.navigate(R.id.nav_home_to_kids) }
        binding.allScreen.tvSeeAllMenClothes.setOnClickListener { navController.navigate(R.id.nav_home_to_men) }
        binding.allScreen.tvSeeAllWomenClothes.setOnClickListener { navController.navigate(R.id.nav_home_to_women) }


    }

    private fun gettingCurrentUserData() {
        val navBinding = NavBarHeaderBinding.bind(binding.navView.getHeaderView(0))

        /** getting data from view model*/
        fireStoreViewModel.getCurrentUserDataLiveData()!!.observe(viewLifecycleOwner) {
            if (it != null) {
                user = it

                /**Putting data in navbar & toolbar*/
                navBinding.navPhoneNumber.text = it.phone_number
                navBinding.navName.text = it.name

                Glide.with(requireContext()).load(it.profileImageUri)
                    .placeholder(R.drawable.ic_person).into(binding.civMainUserImage)
                Glide.with(requireContext()).load(it.profileImageUri)
                    .placeholder(R.drawable.ic_person).into(navBinding.navImage)

            }
        }
    }

    private fun gettingUserCartList() {
        fireStoreViewModel.getCartItemsLiveData()!!.observe(viewLifecycleOwner) {
            this.userCartList = it
        }
    }

    private fun gettingCategories(catList: ArrayList<Category>) {
        catList.add(Category(R.drawable.ic_woman, "Women"))
        catList.add(Category(R.drawable.ic_manswera, "Men"))
        catList.add(Category(R.drawable.ic_grocery, "Grocery"))
        catList.add(Category(R.drawable.ic_kids, "Kids"))

        catList.add(Category(R.drawable.ic_folwa, "Ice Cream"))
        catList.add(Category(R.drawable.ic_mobile, "Tech"))
        catList.add(Category(R.drawable.ic_cleansing, "Cleaning"))
        catList.add(Category(R.drawable.ic_fastion, "Fashion"))

        catList.add(Category(R.drawable.ic_rice, "Rice"))
        catList.add(Category(R.drawable.ic_vegetable, "Vegetable"))
        catList.add(Category(R.drawable.ic_fruits, "Fruits"))
        catList.add(Category(R.drawable.ic_oils, "Oils"))
        catList.add(Category(R.drawable.ic_lentils, "Lentils"))
        catList.add(Category(R.drawable.ic_teas, "Tea"))
        catList.add(Category(R.drawable.ic_ingerspic, "innerspring"))
        catList.add(Category(R.drawable.ic_juice, "Juice"))
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun gettingBestOfferItems() {
        sliderAdapter =
            SliderAdapter(requireContext(), binding.allScreen.viewPagerImageSlider, this)

        binding.allScreen.viewPagerImageSlider.adapter = sliderAdapter
        binding.allScreen.viewPagerImageSlider.clipToPadding = false
        binding.allScreen.viewPagerImageSlider.clipChildren = false
        binding.allScreen.viewPagerImageSlider.offscreenPageLimit = 3
        binding.allScreen.viewPagerImageSlider.getChildAt(0).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER

        val cpt = CompositePageTransformer()
        cpt.addTransformer(MarginPageTransformer(30))
        cpt.addTransformer { page, position ->
            val r: Float = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        binding.allScreen.viewPagerImageSlider.setPageTransformer(cpt)

        sliderHandler = Handler()
        sliderRun = Runnable {
            binding.allScreen.viewPagerImageSlider.currentItem =
                binding.allScreen.viewPagerImageSlider.currentItem + 1
        }

        binding.allScreen.viewPagerImageSlider.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRun)
                sliderHandler.postDelayed(sliderRun, 2500)
            }
        })

        /** getting data from view model*/
        fireStoreViewModel.getBestOfferItemsLiveData()!!.observe(viewLifecycleOwner) {
            if (it != null && it.size > 0) {
                sliderAdapter.setBestOfferItemList(it)
                sliderAdapter.notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun gettingGroceryItems() {
        groceryAdapter = GroceryItemsAdapter(requireContext(), object : AddToCartListener {
            override fun onAddItemsToCart(itemCard: CardModel) {
                addCartItem(itemCard)
                groceryList.add(itemCard)
                cartCounter = groceryList.size + kidsList.size + womenList.size + menList.size

                if (cartCounter > 0) {
                    binding.tvCartCountNumber.visibility = View.VISIBLE
                    binding.tvCartCountNumber.text = cartCounter.toString()
                }
            }

            override fun onRemoveItemFromCart(itemCard: CardModel) {
                removeCartItem(itemCard)
                groceryList.remove(itemCard)

                cartCounter = groceryList.size + kidsList.size + womenList.size + menList.size

                if (cartCounter <= 0) {
                    binding.tvCartCountNumber.text = "0"
                    binding.tvCartCountNumber.visibility = View.INVISIBLE
                }
            }
        }, this)

        binding.allScreen.rvGrocery.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.allScreen.rvGrocery.setHasFixedSize(true)
        binding.allScreen.rvGrocery.adapter = groceryAdapter

        /** getting data from view model*/
        fireStoreViewModel.getGroceryItemsLiveData()!!.observe(viewLifecycleOwner) {
            if (it != null && it.size > 0) {
                groceryAdapter.setGroceryItemList(it)
                groceryAdapter.notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun gettingKidsItems() {
        kidsAdapter = KidsItemsAdapter(requireContext(), object : AddToCartListener {
            override fun onAddItemsToCart(itemCard: CardModel) {
                addCartItem(itemCard)
                kidsList.add(itemCard)
                cartCounter = groceryList.size + kidsList.size + womenList.size + menList.size

                if (cartCounter > 0) {
                    binding.tvCartCountNumber.visibility = View.VISIBLE
                    binding.tvCartCountNumber.text = cartCounter.toString()
                }
            }

            override fun onRemoveItemFromCart(itemCard: CardModel) {
                removeCartItem(itemCard)
                kidsList.remove(itemCard)

                cartCounter = groceryList.size + kidsList.size + womenList.size + menList.size

                if (cartCounter <= 0) {
                    binding.tvCartCountNumber.text = "0"
                    binding.tvCartCountNumber.visibility = View.INVISIBLE
                }
            }
        }, this)
        binding.allScreen.rvKidsToy.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.allScreen.rvKidsToy.adapter = kidsAdapter
        binding.allScreen.rvKidsToy.setHasFixedSize(true)
        /** getting data from view model*/
        fireStoreViewModel.getKidsItemsLiveData()!!.observe(viewLifecycleOwner) {
            if (it != null && it.size > 0) {
                kidsAdapter.setKidsItemList(it)
                kidsAdapter.notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun gettingWomenItems() {
        womenAdapter = WomenItemsAdapter(requireContext(), object : AddToCartListener {
            override fun onAddItemsToCart(itemCard: CardModel) {
                addCartItem(itemCard)
                womenList.add(itemCard)

                cartCounter = groceryList.size + kidsList.size + womenList.size + menList.size

                if (cartCounter > 0) {
                    binding.tvCartCountNumber.visibility = View.VISIBLE
                    binding.tvCartCountNumber.text = cartCounter.toString()
                }
            }

            override fun onRemoveItemFromCart(itemCard: CardModel) {
                removeCartItem(itemCard)
                womenList.remove(itemCard)

                cartCounter = groceryList.size + kidsList.size + womenList.size + menList.size

                if (cartCounter <= 0) {
                    binding.tvCartCountNumber.text = "0"
                    binding.tvCartCountNumber.visibility = View.INVISIBLE
                }
            }
        }, this)
        binding.allScreen.rvWomenClothes.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.allScreen.rvWomenClothes.adapter = womenAdapter
        binding.allScreen.rvWomenClothes.setHasFixedSize(true)
        /** getting data from view model*/
        fireStoreViewModel.getWomenItemsLiveData()!!.observe(viewLifecycleOwner) {
            if (it != null && it.size > 0) {
                womenAdapter.setWomenItemsList(it)
                womenAdapter.notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun gettingMenItems() {
        menAdapter = MenItemsAdapter(requireContext(), object : AddToCartListener {
            override fun onAddItemsToCart(itemCard: CardModel) {
                addCartItem(itemCard)
                menList.add(itemCard)
                cartCounter = groceryList.size + kidsList.size + womenList.size + menList.size

                if (cartCounter > 0) {
                    binding.tvCartCountNumber.visibility = View.VISIBLE
                    binding.tvCartCountNumber.text = cartCounter.toString()
                }
            }

            override fun onRemoveItemFromCart(itemCard: CardModel) {
                removeCartItem(itemCard)
                menList.remove(itemCard)

                cartCounter = groceryList.size + kidsList.size + womenList.size + menList.size

                if (cartCounter <= 0) {
                    binding.tvCartCountNumber.text = "0"
                    binding.tvCartCountNumber.visibility = View.INVISIBLE
                }
            }
        }, this)

        binding.allScreen.rvMenClothes.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.allScreen.rvMenClothes.adapter = menAdapter
        binding.allScreen.rvMenClothes.setHasFixedSize(true)
        /** getting data from view model*/
        fireStoreViewModel.getMenItemsLiveData()!!.observe(viewLifecycleOwner) {
            if (it != null && it.size > 0) {
                menAdapter.setMenItemList(it)
                menAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun addCartItem(item: CardModel) {
        val cartList = CartDataModel()
        if(userCartList!!.menList == null){
            userCartList!!.menList = ArrayList()
        }
        if(userCartList!!.womenList == null){
            userCartList!!.womenList = ArrayList()
        }
        if(userCartList!!.kidsList == null){
            userCartList!!.kidsList = ArrayList()
        }
        if(userCartList!!.groceryList == null){
            userCartList!!.groceryList = ArrayList()
        }
        if(userCartList!!.bestOfferList == null){
            userCartList!!.bestOfferList = ArrayList()
        }
        cartList.groceryList = ArrayList()
        cartList.menList = ArrayList()
        cartList.kidsList = ArrayList()
        cartList.womenList = ArrayList()

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
                    .update("menList", userCartList!!.menList!!)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Removed from cart", Toast.LENGTH_SHORT)
                            .show()
                    }
            }

        }

    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRun)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRun, 2500)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_btn_add_items -> {
                if (user!!.user_type == "admin") {
                    navController.navigate(R.id.nav_home_to_addItems)
                } else {
                    Toast.makeText(requireContext(), "You are not an admin", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            R.id.nav_btn_logOut -> {
                FirebaseAuth.getInstance().signOut()
                requireContext().startActivity(Intent(requireContext(), SignInActivity::class.java))
                requireActivity().finish()
            }

            R.id.nav_btn_myProfile -> {
                navController.navigate(R.id.nav_home_to_profile)
            }

            /*R.id.nav_btn_myFavorites -> {
                navController.navigate(R.id.nav_home_to_myFavorites)
            }

            R.id.nav_btn_contact_with_us -> {
                navController.navigate(R.id.nav_home_to_contactWithUs)
            }*/

        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onItemClick(cardItem: CardModel) {
        val bundle = Bundle()
        bundle.putSerializable("cardItem", cardItem)
        navController.navigate(R.id.nav_home_to_item, bundle)

    }


}