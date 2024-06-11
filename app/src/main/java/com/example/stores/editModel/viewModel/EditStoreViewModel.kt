package com.example.stores.editModel.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stores.common.entities.StoreEntity
import com.example.stores.editModel.model.EditStoreInteractor

class EditStoreViewModel : ViewModel() {
    private val storeSelected = MutableLiveData<StoreEntity>()
    private val showFab = MutableLiveData<Boolean>()
    private val result = MutableLiveData<Any>()

    private val interactor: EditStoreInteractor

    init{
        interactor = EditStoreInteractor()
    }

    fun setStoreSelected(storeEntity: StoreEntity){
        storeSelected.postValue(storeEntity)
    }

    fun getStoreSelected(): LiveData<StoreEntity>{
        return storeSelected
    }

    fun setShowFab(isVisible: Boolean){
        showFab.postValue(isVisible)
    }

    fun getShowFab(): LiveData<Boolean>{
        return showFab
    }

    fun setResult(value: Any){
        result.postValue(value)
    }

    fun getResult(): LiveData<Any>{
        return result
    }

    fun saveStore(storeEntity: StoreEntity){
        interactor.saveStore(storeEntity) { newId ->
            result.postValue(newId)
        }
    }

    fun updateStore(storeEntity: StoreEntity){
        interactor.updateStore(storeEntity) { storeUpdated ->
            result.postValue(storeUpdated)
        }
    }


}