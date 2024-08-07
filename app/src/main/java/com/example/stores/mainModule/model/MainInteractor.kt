package com.example.stores.mainModule.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.Constants
import com.example.stores.common.utils.StoresException
import com.example.stores.common.utils.TypeError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.LinkedBlockingQueue

class MainInteractor {

   /*   fun getStores(callback: (MutableList<StoreEntity>) -> Unit){
        val isLocal = true
        if (isLocal){
            getStoresApi{storeEntities -> callback(storeEntities) }
        } else {
            getStoresRoom { storeEntities -> callback(storeEntities)  }
        }
    }


    fun getStoresApi(callback: (MutableList<StoreEntity>) -> Unit){
        val url = Constants.STORES_URL + Constants.GET_ALL_PATH
        var storeList = mutableListOf<StoreEntity>()

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, {response ->
            Log.i("response", response.toString())

            //val status = response.getInt(Constants.STATUS_PROPERTY)
            val status = response.optInt(Constants.STATUS_PROPERTY, Constants.ERROR)

            if (status == Constants.SUCCESS){
                Log.i("status",status.toString())

                val jsonList = response.optJSONArray(Constants.STORES_PROPERTY)?.toString()
                if (jsonList != null){
                    val mutableListType = object : TypeToken<MutableList<StoreEntity>>(){}.type
                    storeList = Gson().fromJson(jsonList, mutableListType)

                    callback(storeList)
                }

            }
            callback(storeList)
        },{
            it.printStackTrace()
            callback(storeList)
        })

        StoreApplication.storeAPI.addToRequestQueue(jsonObjectRequest)

    }

    fun getStoresRoom(callback: (MutableList<StoreEntity>) -> Unit){
        Thread{
            val storesList = StoreApplication.database.storeDao().getAllStores()
            val json = Gson().toJson(storesList)
            Log.i("JSON", json)
            callback(storesList)
            //queue.add(storesList)
        }.start()
    } */

    val stores: LiveData<MutableList<StoreEntity>> = liveData {
        val storesLiveData = StoreApplication.database.storeDao().getAllStores()
        emitSource(storesLiveData.map { stores ->
            stores.sortedBy { it.name }.toMutableList()  //ordenar alfabeticamente
        })

    }

    suspend fun deleteStore (storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        val result = StoreApplication.database.storeDao().deleteStore(storeEntity)
        if (result == 0) throw StoresException(TypeError.DELETE)
    }

    suspend fun updateStore (storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        val result = StoreApplication.database.storeDao().updateStore(storeEntity)
        if (result == 0) throw StoresException(TypeError.UPDATE)
    }


}