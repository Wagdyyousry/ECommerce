package com.wagdybuild.ecommerce.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wagdybuild.ecommerce.models.CardModel
import com.wagdybuild.ecommerce.models.CartDataModel
import com.wagdybuild.ecommerce.models.User
import com.wagdybuild.ecommerce.repository.FireStoreRepo

class FireStoreViewModel : ViewModel(), FireStoreRepo.OnGettingAllDataComplete {
    private var currentUserDataLiveData: MutableLiveData<User>? = null
    private var bestOfferItemsLiveData: MutableLiveData<ArrayList<CardModel>>? = null
    private var groceryItemsLiveData: MutableLiveData<ArrayList<CardModel>>? = null
    private var kidsItemsLiveData: MutableLiveData<ArrayList<CardModel>>? = null
    private var menItemsLiveData: MutableLiveData<ArrayList<CardModel>>? = null
    private var womenItemsLiveData: MutableLiveData<ArrayList<CardModel>>? = null
    //private var cartItemsLiveData: MutableLiveData<ArrayList<CardModel>>? = null
    private var cartItemsLiveData: MutableLiveData<CartDataModel>? = null
    private var repo: FireStoreRepo? = null

    init {
        repo = FireStoreRepo(this)
        repo!!.gettingCurrentUserData()
        repo!!.gettingBestOffersItems()
        repo!!.gettingKidsItems()
        repo!!.gettingGroceryItems()
        repo!!.gettingMenItems()
        repo!!.gettingWomenItems()
        repo!!.gettingCartItems()

        currentUserDataLiveData = MutableLiveData<User>()
        bestOfferItemsLiveData = MutableLiveData<ArrayList<CardModel>>()
        groceryItemsLiveData = MutableLiveData<ArrayList<CardModel>>()
        kidsItemsLiveData = MutableLiveData<ArrayList<CardModel>>()
        menItemsLiveData = MutableLiveData<ArrayList<CardModel>>()
        womenItemsLiveData = MutableLiveData<ArrayList<CardModel>>()
        //cartItemsLiveData = MutableLiveData<ArrayList<CardModel>>()
        cartItemsLiveData = MutableLiveData<CartDataModel>()
    }

    /** Getting all data as Live Data type */
    fun getCurrentUserDataLiveData(): MutableLiveData<User>? {
        return currentUserDataLiveData
    }

    fun getBestOfferItemsLiveData(): MutableLiveData<ArrayList<CardModel>>? {
        return bestOfferItemsLiveData
    }

    fun getGroceryItemsLiveData(): MutableLiveData<ArrayList<CardModel>>? {
        return groceryItemsLiveData
    }

    fun getKidsItemsLiveData(): MutableLiveData<ArrayList<CardModel>>? {
        return kidsItemsLiveData
    }

    fun getMenItemsLiveData(): MutableLiveData<ArrayList<CardModel>>? {
        return menItemsLiveData
    }

    fun getWomenItemsLiveData(): MutableLiveData<ArrayList<CardModel>>? {
        return womenItemsLiveData
    }

    /*fun getCartItemsLiveData(): MutableLiveData<ArrayList<CardModel>>? {
        return cartItemsLiveData
    }*/
    fun getCartItemsLiveData(): MutableLiveData<CartDataModel>? {
        return cartItemsLiveData
    }

    /** Getting all data from the interface */
    override fun onSuccessGettingCurrentUserData(user: User) {
        currentUserDataLiveData!!.value = user
    }

    override fun onSuccessGettingGroceryItems(groceryList: ArrayList<CardModel>) {
        groceryItemsLiveData!!.value = groceryList

    }

    override fun onSuccessGettingKidsItems(kidsItemList: ArrayList<CardModel>) {
        kidsItemsLiveData!!.value = kidsItemList
    }

    override fun onSuccessGettingMenItems(menItemList: ArrayList<CardModel>) {
        menItemsLiveData!!.value = menItemList

    }

    override fun onSuccessGettingWomenItems(womenItemList: ArrayList<CardModel>) {
        womenItemsLiveData!!.value = womenItemList

    }

    override fun onSuccessGettingBestOffersItems(bestOfferItemList: ArrayList<CardModel>) {
        bestOfferItemsLiveData!!.value = bestOfferItemList

    }

    override fun onSuccessGettingCartItems(cartItemList: CartDataModel) {
        cartItemsLiveData!!.value = cartItemList
    }

    /*override fun onSuccessGettingCartItems(cartItemList: ArrayList<CardModel>) {
        cartItemsLiveData!!.value = cartItemList
    }*/


}