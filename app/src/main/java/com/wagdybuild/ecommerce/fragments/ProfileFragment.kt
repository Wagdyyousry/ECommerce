package com.wagdybuild.ecommerce.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wagdybuild.ecommerce.R
import com.wagdybuild.ecommerce.databinding.FragmentProfileBinding
import com.wagdybuild.ecommerce.models.User
import com.wagdybuild.ecommerce.viewModel.FireStoreViewModel
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.Date

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var fireStoreViewModel: FireStoreViewModel
    private lateinit var navController: NavController

    /** Firebase */
    private var mAuth: FirebaseAuth? = null
    private var mFireStore: FirebaseFirestore? = null
    private var dbStorage: FirebaseStorage? = null

    private var current_name: String? = null
    private var result_uri: Uri? = null
    private lateinit var user: User
    private var cActivityResultLauncher: ActivityResultLauncher<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        /** initialize all variables */
        initialize()

        /** Getting Current User Data */
        gettingCurrentUserData()

        closeFields()

        binding.profileBtnEdit.setOnClickListener { openFields() }

        /** Nav controller */
        navController = Navigation.findNavController(container!!)
        binding.profileBtnBack.setOnClickListener { navController.navigate(R.id.nav_profile_to_home) }

        return binding.root
    }

    private fun initialize() {
        /** FireBase */
        mAuth = FirebaseAuth.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
        dbStorage = FirebaseStorage.getInstance()

        /** Picking image from gallery */
        cActivityResultLauncher = registerForActivityResult<String, Uri>(
            ActivityResultContracts.GetContent()
        ) {
            if (it != null) {
                startCrop(it)
            }
        }

        /** FireStore View Model */
        fireStoreViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().application)
        )[FireStoreViewModel()::class.java]
    }

    private fun gettingCurrentUserData() {
        fireStoreViewModel.getCurrentUserDataLiveData()!!.observe(viewLifecycleOwner) {
            if (it != null) {
                user = it

                binding.profileEtName.setText(it.name)
                this.current_name = it.name

                Glide.with(requireContext()).load(it.profileImageUri)
                    .placeholder(R.drawable.ic_person_black).into(binding.profileCiv)
            }
        }

    }

    private fun closeFields() {
        binding.profileEtName.isEnabled = false
        binding.btnAddImage.isEnabled = false
        binding.profileBtnUpdate.visibility = View.INVISIBLE
    }

    private fun openFields() {
        binding.profileEtName.isEnabled = true
        binding.btnAddImage.isEnabled = true
        binding.profileBtnUpdate.visibility = View.VISIBLE


        binding.btnAddImage.setOnClickListener { cActivityResultLauncher!!.launch("image/*") }

        binding.profileBtnUpdate.setOnClickListener {
            val name: String = binding.profileEtName.text.toString()

            if (name.isEmpty()) {
                binding.profileEtName.error = "You have to write your name"
                return@setOnClickListener
            } else {
                updateData(name)
            }
        }
    }

    private fun updateData(name: String) {
        if (current_name != name) {
            mFireStore!!.collection("Users").document(mAuth!!.currentUser!!.uid)
                .update("name", name).addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Name changed Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }

        if (result_uri != null) {
            storeProfileImage(result_uri!!)
        }

        navController.navigate(R.id.nav_profile_to_home)

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
                Glide.with(requireContext().applicationContext).load(result_uri_crop.toString())
                    .placeholder(R.drawable.ic_person).into(binding.profileCiv)

            }
        }
    }

    private fun storeProfileImage(imageUri: Uri) {
        val reference =
            dbStorage!!.reference.child("Profile_images").child(mAuth!!.currentUser!!.uid)

        reference.putFile(imageUri).addOnSuccessListener {
            reference.downloadUrl.addOnSuccessListener {
                mFireStore!!.collection("Users").document(mAuth!!.currentUser!!.uid)
                    .update("profileImageUri", it.toString()).addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Profile Image Changed Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }
}