package com.wagdybuild.ecommerce.repository

import androidx.compose.material3.CardDefaults
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.wagdybuild.ecommerce.models.CartDataModel
import com.wagdybuild.ecommerce.models.CardModel
import com.wagdybuild.ecommerce.models.User

class FireStoreRepo(onGettingAllDataInterface: OnGettingAllDataComplete) {
    private var onGettingAllDataInterface: OnGettingAllDataComplete? = null
    private var mAuth: FirebaseAuth? = null
    private var db: FirebaseDatabase? = null
    private var mFireStore: FirebaseFirestore? = null

    init {
        this.onGettingAllDataInterface = onGettingAllDataInterface
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        mFireStore = FirebaseFirestore.getInstance()
    }

    fun gettingCurrentUserData() {

        mFireStore!!.collection("Users").document(mAuth!!.currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val userData = it.toObject<User>()!!
                    onGettingAllDataInterface!!.onSuccessGettingCurrentUserData(userData)
                }
            }
    }

    fun gettingBestOffersItems() {
        val bestOfferItems = ArrayList<CardModel>()
        mFireStore!!.collection("Best Offer").get().addOnCompleteListener {
            if (it.isSuccessful) {
                bestOfferItems.clear()
                for (doc in it.result) {
                    val bestOfferItem = doc.toObject<CardModel>()
                    bestOfferItems.add(bestOfferItem)

                }
                onGettingAllDataInterface!!.onSuccessGettingBestOffersItems(bestOfferItems)

            }
        }

    }

    fun gettingGroceryItems() {
        val groceryItems = ArrayList<CardModel>()
        mFireStore!!.collection("Grocery").get().addOnCompleteListener {
            if (it.isSuccessful) {
                groceryItems.clear()
                for (doc in it.result) {
                    val groceryItem = doc.toObject<CardModel>()
                    groceryItems.add(groceryItem)
                }
                onGettingAllDataInterface!!.onSuccessGettingGroceryItems(groceryItems)
            }

        }
    }

    fun gettingKidsItems() {
        val kidsItems = ArrayList<CardModel>()
        mFireStore!!.collection("Kids").get().addOnCompleteListener {
            if (it.isSuccessful) {
                kidsItems.clear()
                for (doc in it.result) {
                    val kidsItem = doc.toObject<CardModel>()
                    kidsItems.add(kidsItem)
                }
                onGettingAllDataInterface!!.onSuccessGettingKidsItems(kidsItems)

            }


        }
    }

    fun gettingMenItems() {
        val menItems = ArrayList<CardModel>()
        mFireStore!!.collection("Men").get().addOnCompleteListener {
            if (it.isSuccessful) {
                menItems.clear()
                for (doc in it.result) {
                    val menItem = doc.toObject<CardModel>()
                    menItems.add(menItem)
                }
                onGettingAllDataInterface!!.onSuccessGettingMenItems(menItems)

            }


        }
    }

    fun gettingWomenItems() {
        val womenItems = ArrayList<CardModel>()
        mFireStore!!.collection("Women").get().addOnCompleteListener {
            if (it.isSuccessful) {
                womenItems.clear()
                for (doc in it.result) {
                    val womenItem = doc.toObject<CardModel>()
                    womenItems.add(womenItem)
                }
                onGettingAllDataInterface!!.onSuccessGettingWomenItems(womenItems)

            }


        }

    }

    fun gettingCartItems() {
        mFireStore!!.collection("Cart").document(mAuth!!.currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val obj = it.toObject<CartDataModel>()!!

                    onGettingAllDataInterface!!.onSuccessGettingCartItems(obj)

                }
            }

        /*val cartItems = ArrayList<CardModel>()
        mFireStore!!.collection("Cart").document(mAuth!!.currentUser!!.uid).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val obj = it.toObject<CartDataModel>()!!
                    if (obj.groceryList!=null &&obj.groceryList!!.size > 0) {
                        for (item in obj.groceryList!!) {
                            cartItems.add(item)
                        }
                    }
                    if (obj.kidsList!=null && obj.kidsList!!.size > 0) {
                        for (item in obj.kidsList!!) {
                            cartItems.add(item)
                        }
                    }
                    if (obj.menList!=null && obj.menList!!.size > 0) {
                        for (item in obj.menList!!) {
                            cartItems.add(item)
                        }
                    }
                    if (obj.womenList!=null && obj.womenList!!.size > 0) {
                        for (item in obj.womenList!!) {
                            cartItems.add(item)
                        }
                    }
                    onGettingAllDataInterface!!.onSuccessGettingCartItems(cartItems)

                }
            }*/
    }

    interface OnGettingAllDataComplete {
        fun onSuccessGettingCurrentUserData(user: User)
        fun onSuccessGettingGroceryItems(groceryList: ArrayList<CardModel>)
        fun onSuccessGettingKidsItems(kidsItemList: ArrayList<CardModel>)
        fun onSuccessGettingMenItems(menItemList: ArrayList<CardModel>)
        fun onSuccessGettingWomenItems(womenItemList: ArrayList<CardModel>)
        fun onSuccessGettingBestOffersItems(bestOfferItemList: ArrayList<CardModel>)
        //fun onSuccessGettingCartItems(cartItemList: ArrayList<CardModel>)
        fun onSuccessGettingCartItems(cartItemList: CartDataModel)

    }
}