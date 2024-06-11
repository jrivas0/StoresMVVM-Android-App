package com.example.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.mainModule.model.MainInteractor
import java.util.concurrent.LinkedBlockingQueue

class MainViewModel: ViewModel() {
    private var storeList: MutableList<StoreEntity>
    private var interactor: MainInteractor


    init {
        storeList = mutableListOf()
        interactor = MainInteractor()
    }

    private val stores: MutableLiveData<List<StoreEntity>> by lazy {
        MutableLiveData<List<StoreEntity>>()/*.also { // descomentar con corrutinas
            loadStores()
        } */
    }

    fun getStores(): LiveData<List<StoreEntity>>{
        return stores.also {
            loadStores()
        }
    }

    private fun loadStores(){
/*        interactor.getStoresCallback(object : MainInteractor.StoresCallback{
            override fun getStoresCallbackAux(storesList: MutableList<StoreEntity>) {
                stores.postValue(storesList)
                //this@MainViewModel.stores.value = stores
            }
        })*/
        interactor.getStores {
            stores.postValue(it)
            storeList = it
        }

    }

    fun deleteStore(storeEntity: StoreEntity){
        interactor.deleteStore(storeEntity) {
            val index = storeList.indexOf(storeEntity)
            if (index != -1) {
                storeList.removeAt(index)
                stores.value = storeList
            }
        }
    }

    fun updateStore(storeEntity: StoreEntity){
        storeEntity.isFavorite = !storeEntity.isFavorite
        interactor.updateStore(storeEntity) {
            val index = storeList.indexOf(storeEntity)
            if (index != -1) {
                storeList.set(index, storeEntity)
                stores.value = storeList
            }
        }
    }
}