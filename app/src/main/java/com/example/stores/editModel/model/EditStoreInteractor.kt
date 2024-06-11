package com.example.stores.editModel.model

import android.os.Handler
import android.os.Looper
import com.example.stores.StoreApplication
import com.example.stores.common.entities.StoreEntity
import java.util.concurrent.LinkedBlockingQueue

class EditStoreInteractor {

    fun saveStore (storeEntity: StoreEntity, callback: (Long) -> Unit){
        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread {
            val newId = StoreApplication.database.storeDao().addStore(storeEntity)
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            queue.add(storeEntity)
            Handler(Looper.getMainLooper()).post {
                callback(newId)
            }
        }.start()

    }

    fun updateStore (storeEntity: StoreEntity, callback: (StoreEntity) -> Unit){
        val queue = LinkedBlockingQueue<StoreEntity>()
        Thread {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            queue.add(storeEntity)
            Handler(Looper.getMainLooper()).post {
                callback(storeEntity)
            }
        }.start()

    }
}