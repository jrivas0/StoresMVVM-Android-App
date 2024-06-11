package com.example.stores.mainModule.adapter

import com.example.stores.common.entities.StoreEntity

interface OnClickListener {

    fun onClick(store: StoreEntity)
    fun onFavoriteStore(store: StoreEntity)
    fun onDeleteStore(storeEntity: StoreEntity)
}