package com.example.stores.mainModule.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stores.R
import com.example.stores.common.entities.StoreEntity

class StoreAdapter(private var stores: MutableList<StoreEntity>, private var listener: OnClickListener) :
    RecyclerView.Adapter<StoreViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_store,parent,false)

        return StoreViewHolder(view,parent.context,listener)
    }

    override fun getItemCount(): Int = stores.size

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val item = stores[position]
        holder.render(item)

    }

    fun setStores(stores: List<StoreEntity>) {
        this.stores = stores as MutableList<StoreEntity>
        notifyDataSetChanged()
    }

    fun update(store: StoreEntity) {

        val index = stores.indexOf(store)
        if(index != -1){
            stores.set(index,store)
            notifyItemChanged(index)
        }

    }

    fun delete(store: StoreEntity) {

        val index = stores.indexOf(store)
        if(index != -1){
            stores.removeAt(index)
            notifyItemRemoved(index)
        }

    }

    fun add(store: StoreEntity) {
        if (store.id != 0L) {
            if(!stores.contains(store)) {
                stores.add(store)
                notifyItemInserted(stores.size-1)
            } else {
                update(store)
            }
        }

    }


}