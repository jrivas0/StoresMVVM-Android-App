package com.example.stores.mainModule.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.common.entities.StoreEntity
import com.example.stores.databinding.ItemStoreBinding

class StoreViewHolder(view: View, private val context: Context,private var listener: OnClickListener): RecyclerView.ViewHolder(view) {

    private val binding = ItemStoreBinding.bind(view)


    fun setListener(store: StoreEntity){
        binding.root.setOnClickListener{listener.onClick(store)}
        binding.cbFavorite.setOnClickListener{listener.onFavoriteStore(store)}
        binding.root.setOnLongClickListener{listener.onDeleteStore(store)
            true}
    }

    fun render(store: StoreEntity){
        setListener(store)
        binding.tvName.text = store.name
        binding.cbFavorite.isChecked = store.isFavorite
        Glide.with(context)
            .load(store.photoUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(binding.ivPhoto)
    }

}