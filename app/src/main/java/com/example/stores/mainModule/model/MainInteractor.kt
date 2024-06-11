package com.example.stores.mainModule.model

import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import com.google.gson.Gson
import java.util.concurrent.LinkedBlockingQueue

class MainInteractor {


    fun getStores(callback: (MutableList<StoreEntity>) -> Unit){
        val url = "https://stores.free.beeceptor.com"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, {response ->
            Log.i("response", response.toString())
        },{
            it.printStackTrace()
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