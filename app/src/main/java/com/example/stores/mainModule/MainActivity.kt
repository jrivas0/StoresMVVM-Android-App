package com.example.stores.mainModule


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.URLUtil.isValidUrl
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.stores.editModel.EditStoreFragment
import com.example.stores.R
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.TypeError
import com.example.stores.databinding.ActivityMainBinding
import com.example.stores.editModel.viewModel.EditStoreViewModel
import com.example.stores.mainModule.adapter.OnClickListener
import com.example.stores.mainModule.adapter.StoreListAdapter
import com.example.stores.mainModule.viewModel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mAdapter: StoreListAdapter

    private lateinit var mGridLayout: GridLayoutManager

    //MVVM
    private lateinit var mMainViewModel: MainViewModel
    private lateinit var mEditStoreViewModel: EditStoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        /*mBinding.btnSave.setOnClickListener {
            val store = StoreEntity(name = mBinding.etUserName.text.toString().trim())

            Thread{
                StoreApplication.database.storeDao().addStore(store)
            }.start()

            mAdapter.add(store)
        }*/


        mBinding.fab.setOnClickListener{launchEditFragment()}
        setUpViewModel()
        initUi()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mEditStoreViewModel.setShowFab(true)
                // You can also call finish() if you want to close the activity
                if (isEnabled) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun setUpViewModel() {
        mMainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mMainViewModel.getStores().observe(this) { stores ->
            mBinding.progressBar.visibility = View.GONE
            mAdapter.submitList(stores)
        }
        mMainViewModel.isShowProgress().observe(this) { isShowProgress ->
            mBinding.progressBar.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }

        mMainViewModel.getTypeError().observe(this) { typeError ->
            val msgRes = when (typeError) {
                TypeError.GET -> getString(R.string.get_error_string)
                TypeError.INSERT -> getString(R.string.insert_error_string)
                TypeError.UPDATE-> getString(R.string.update_error_string)
                TypeError.DELETE -> getString(R.string.delete_error_string)
                else -> getString(R.string.unknown_error_string)
            }
            Snackbar.make(mBinding.root, msgRes, Snackbar.LENGTH_SHORT).show()

        }

        mEditStoreViewModel = ViewModelProvider(this).get(EditStoreViewModel::class.java)
        mEditStoreViewModel.getShowFab().observe(this) { isVisible ->
            if (isVisible) mBinding.fab.show() else mBinding.fab.hide()
        }

    }

    private fun launchEditFragment(storeEntity: StoreEntity = StoreEntity()) {
        mEditStoreViewModel.setShowFab(false)
        mEditStoreViewModel.setStoreSelected(storeEntity)

        val fragment = EditStoreFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.main,fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

//    private fun getStores(){
//        val queue = LinkedBlockingQueue<MutableList<StoreEntity>>()
//        Thread{
//            val stores = StoreApplication.database.storeDao().getAllStores()
//            queue.add(stores)
//        }.start()
//
//        mAdapter.setStores(queue.take())
//    }


    private fun initUi(){
        mAdapter = StoreListAdapter(this)
        mGridLayout = GridLayoutManager(this,2)
        //getStores()

        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mGridLayout
            adapter = mAdapter
        }
    }

    //OnClick
    override fun onClick(store: StoreEntity) {
        launchEditFragment(store)
    }

    override fun onFavoriteStore(store: StoreEntity) {
        mMainViewModel.updateStore(store)
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        val items = resources.getStringArray(R.array.array_options_items)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_options_title)
            .setItems(items) { _, i ->
                when (i) {
                    0 -> confirmDelete(storeEntity)
                    1 -> dial(storeEntity.phone)
                    2 -> goWebsite(storeEntity.website)
                }
            }
            .show()

    }

    private fun confirmDelete(storeEntity: StoreEntity){
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm) { _, _ ->
                mMainViewModel.deleteStore(storeEntity)
            }
            .setNegativeButton(R.string.dialog_delete_cancel,null)
            .show()
    }

    private fun dial(phone:String){
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }

        startIntent(callIntent)
    }

    private fun goWebsite(website: String) {
        if (website.isEmpty()) {
            Toast.makeText(this, R.string.main_error_no_website, Toast.LENGTH_LONG).show()
        } else if (isValidUrl(website)) {
            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }
            startIntent(websiteIntent)
        } else {
            Toast.makeText(this, R.string.main_error_invalid_website, Toast.LENGTH_LONG).show()
        }
    }

    private fun startIntent(intent: Intent){
        if (intent.resolveActivity(packageManager) != null) startActivity(intent)
        else Toast.makeText(this, R.string.main_error_no_resolve,Toast.LENGTH_LONG).show()
    }


}