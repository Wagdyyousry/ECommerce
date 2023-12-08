package com.wagdybuild.ecommerce.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wagdybuild.ecommerce.R
import com.wagdybuild.ecommerce.databinding.FragmentAddItemsBinding
import com.wagdybuild.ecommerce.models.CardModel
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.Date


class AddItemsFragment : Fragment() {
    private lateinit var binding: FragmentAddItemsBinding
    private lateinit var currentTime: String
    private var itemCategory: String ="Categories"
    private lateinit var navController: NavController
    /** FireBase */
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mStorage: FirebaseStorage
    private lateinit var mFireStore: FirebaseFirestore

    /** picking Image  */
    private var result_uri: Uri? = null
    private lateinit var cActivityResultLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddItemsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        /** Select spinner item */
        binding.spinnerSelectCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                    if(i>0){
                        itemCategory = adapterView!!.selectedItem.toString()
                    }
                    if (itemCategory == "Best Offer") {
                        binding.etItemDiscount.visibility = View.VISIBLE
                        binding.tvItemDiscount.visibility = View.VISIBLE
                    }
                    else {
                        binding.etItemDiscount.visibility = View.GONE
                        binding.tvItemDiscount.visibility = View.INVISIBLE
                    }

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}

            }

        /** Send data to DataBase */
        binding.btnAddItem.setOnClickListener {
            val itemId = mAuth.currentUser!!.uid
            val itemName = binding.etItemName.text.toString().trim()
            val itemDesc = binding.etItemDescription.text.toString().trim()
            val itemPrice = binding.etItemPrice.text.toString().trim()
            val itemQuantity = binding.etItemQuantity.text.toString().trim()
            val itemDiscount = binding.etItemDiscount.text.toString().trim()

            if (itemCategory == "Categories") {
                Toast.makeText(requireContext(), "You have to choose Category", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            if (itemName.isEmpty()) {
                binding.etItemName.error = "Write item name"
                return@setOnClickListener
            }
            if (itemDesc.isEmpty()) {
                binding.etItemDescription.error = "Write item description"
                return@setOnClickListener
            }
            if (itemPrice.isEmpty()) {
                binding.etItemPrice.error = "Write item price"
                return@setOnClickListener
            }
            if (itemQuantity.isEmpty()) {
                binding.etItemQuantity.error = "Write item Quantity"
                return@setOnClickListener
            }
            if (itemCategory == "Best Offer") {
                if (itemDiscount.isEmpty()) {
                    binding.etItemDiscount.error = "Write item Discount"
                    return@setOnClickListener
                }
            }
            if (result_uri == null) {
                Toast.makeText(requireContext(), "You have to choose item photo", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            } else {
                val newItem = CardModel(
                    "",
                    itemCategory,
                    itemId,
                    itemName,
                    itemDesc,
                    itemPrice.toDouble(),
                    itemQuantity,
                    itemDiscount,
                    0
                )
                addNewItem(newItem)
            }
        }

        /** Nav controller */
        navController = Navigation.findNavController(view)
        binding.btnBack.setOnClickListener { navController.navigate(R.id.nav_addItems_to_home) }

    }

    private fun init() {
        /** FireBase */
        mAuth = FirebaseAuth.getInstance()
        mStorage = FirebaseStorage.getInstance()
        mFireStore = FirebaseFirestore.getInstance()

        /** Selected image from gallery */
        binding.btnAddItemImage.setOnClickListener { cActivityResultLauncher.launch("image/*") }

        cActivityResultLauncher = registerForActivityResult<String, Uri>(
            ActivityResultContracts.GetContent()
        ) {
            if (it != null) {
                /*result_uri = it
                Glide.with(requireContext()).load(it.toString())
                    .placeholder(R.drawable.ic_image).into(binding.civItemImage)*/
                startCrop(it)
            }
        }

        /** spinner category selection */
        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.Categories,
            R.layout.spinner_for_gategories
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSelectCategory.adapter = spinnerAdapter
    }

    private fun addNewItem(newItem: CardModel) {
        currentTime = Date().time.toString()
        newItem.itemTime = currentTime.toLong()
        mFireStore.collection(newItem.itemCategory).document(currentTime)
            .set(newItem).addOnCompleteListener {
                if (it.isSuccessful) {

                    binding.etItemPrice.setText("")
                    binding.etItemQuantity.setText("")
                    binding.etItemDiscount.setText("")
                    binding.etItemName.setText("")
                    binding.etItemDescription.setText("")
                    binding.spinnerSelectCategory.setSelection(0)
                    binding.civItemImage.setImageResource(R.drawable.ic_image)

                    Toast.makeText(requireContext(), "Item Added Successfully", Toast.LENGTH_LONG)
                        .show()
                    storeItemImage(result_uri!!)
                }
                else {
                    Toast.makeText(
                        requireContext(),
                        "Error ,${it.exception!!.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

    }

    private fun startCrop(crop_uri: Uri) {
        val time = Date().time.toString()
        val uCrop = UCrop.of(crop_uri, Uri.fromFile(File(requireContext().cacheDir, "$time.jpg")))
        uCrop.withAspectRatio(5f, 5f)
        uCrop.withMaxResultSize(700, 700)
        uCrop.withOptions(getCropOption())
        uCrop.start(requireContext(),this)

    }

    @SuppressLint("ResourceAsColor")
    private fun getCropOption(): UCrop.Options {
        val options = UCrop.Options()
        //options.setCompressionQuality(70)
        options.setHideBottomControls(false)
        options.setFreeStyleCropEnabled(true)
        options.setStatusBarColor(R.color.black)
        options.setStatusBarColor(R.color.black)
        options.setToolbarTitle("Move Photo to crop")
        return options
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            var result_uri_crop: Uri? = null
            if (data != null) {
                result_uri_crop = UCrop.getOutput(data)
            }
            if (result_uri_crop != null) {
                this.result_uri = result_uri_crop
                Glide.with(requireContext().applicationContext).load(result_uri!!.toString())
                    .placeholder(R.drawable.ic_person).into(binding.civItemImage)

            }
        }
    }

    private fun storeItemImage(imageUri: Uri) {

        val reference =
            mStorage.reference.child("Items_pics").child(itemCategory).child(currentTime)

        reference.putFile(imageUri).addOnSuccessListener {
            reference.downloadUrl.addOnSuccessListener { it1 ->
                mFireStore.collection(itemCategory).document(currentTime).update("itemImage",it1.toString())
            }

        }
    }

}