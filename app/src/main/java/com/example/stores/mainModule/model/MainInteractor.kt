package com.example.stores.mainModule.model

import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.LinkedBlockingQueue

class MainInteractor {


    fun getStores(callback: (MutableList<StoreEntity>) -> Unit){
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
    }

    fun deleteStore (storeEntity: StoreEntity, callback: (StoreEntity) -> Unit){
        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread {
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            queue.add(storeEntity)
        }.start()
        callback(storeEntity)
        //mAdapter.delete(queue.take())
    }

    fun updateStore (storeEntity: StoreEntity, callback: (StoreEntity) -> Unit){
        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread{
            StoreApplication.database.storeDao().updateStore(storeEntity)
            queue.add(storeEntity)
        }.start()
        callback(storeEntity)
        //updateStore(queue.take())
    }


}