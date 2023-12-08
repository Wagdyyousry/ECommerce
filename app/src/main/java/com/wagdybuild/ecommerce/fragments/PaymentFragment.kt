package com.wagdybuild.ecommerce.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wagdybuild.ecommerce.R
import com.wagdybuild.ecommerce.databinding.FragmentHomeBinding
import com.wagdybuild.ecommerce.databinding.FragmentPaymentBinding


class PaymentFragment : Fragment() {
    private lateinit var binding: FragmentPaymentBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: FirebaseDatabase
    private lateinit var mStorage: FirebaseStorage
    private lateinit var mFireStore: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentPaymentBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }
    private fun init() {
        //Firebase references
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        mStorage = FirebaseStorage.getInstance()
        mFireStore = FirebaseFirestore.getInstance()


    }
}