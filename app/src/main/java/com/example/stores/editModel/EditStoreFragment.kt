package com.example.stores.editModel

import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.R
import com.example.stores.common.entities.StoreEntity
import com.example.stores.common.utils.TypeError
import com.example.stores.databinding.FragmentEditStoreBinding
import com.example.stores.editModel.viewModel.EditStoreViewModel
import com.example.stores.mainModule.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class EditStoreFragment : Fragment() {

    private lateinit var mBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private lateinit var mStoreEntity: StoreEntity

    //MVVM
    private lateinit var mEditStoreViewModel: EditStoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEditStoreViewModel = ViewModelProvider(requireActivity()).get(EditStoreViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEditStoreBinding.inflate(inflater,container,false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //MVVM
        setUpViewModel()


        setUpTextFields()

    }

    private fun setUpViewModel() {
        mEditStoreViewModel.getStoreSelected().observe(viewLifecycleOwner) {
            mStoreEntity = it ?: StoreEntity()
            if (it != null){
                mIsEditMode = true
                setUiStore(it)
            }else {
                mIsEditMode = false
            }

            setUpActionBar()

        }

        mEditStoreViewModel.getResult().observe(viewLifecycleOwner){result ->
            hideKeyboard()

            when(result){
                is StoreEntity -> {
                    val msgRes = if (result.id == 0L) R.string.edit_store_message_save_success else
                        R.string.edit_store_message_update_success
                    mEditStoreViewModel.setStoreSelected(mStoreEntity)
                    Snackbar.make(mBinding.root, msgRes, Snackbar.LENGTH_SHORT).show()
                    mActivity?.onBackPressedDispatcher?.onBackPressed()
                }
            }

            mEditStoreViewModel.getTypeError().observe(viewLifecycleOwner) { typeError ->
                if (typeError != TypeError.NONE) {
                    val msgRes = when (typeError) {
                        TypeError.GET -> getString(R.string.get_error_string)
                        TypeError.INSERT -> getString(R.string.insert_error_string)
                        TypeError.UPDATE -> getString(R.string.update_error_string)
                        TypeError.DELETE -> getString(R.string.delete_error_string)
                        else -> getString(R.string.unknown_error_string)
                    }
                    Snackbar.make(mBinding.root, msgRes, Snackbar.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun setUpActionBar() {
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = if(mIsEditMode) getString(R.string.edit_store_title_edit)
        else getString(R.string.edit_store_title_add)

        setHasOptionsMenu(true)
    }

    private fun setUpTextFields() {
        with(mBinding){
            etPhotoUrl.addTextChangedListener { validateFields(tilPhotoUrl) }
            etPhone.addTextChangedListener { validateFields(tilPhone) }
            etUserName.addTextChangedListener { validateFields(tilName)
                loadImage(it.toString().trim())}
        }
    }

    private fun loadImage(url: String){
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(mBinding.imgPhoto)
    }


    private fun setUiStore(it: StoreEntity) {
        with(mBinding){
            etUserName.text = it.name.editable()
            etPhone.text = it.phone.editable()
            etWeb.text = it.website.editable()
            etPhotoUrl.text = it.photoUrl.editable()
            Glide.with(requireActivity())
                .load(it.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(imgPhoto)
        }
    }

    private fun String.editable() : Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                mActivity?.onBackPressedDispatcher?.onBackPressed()
                true
            }
            R.id.action_save -> {
                if (validateFields(mBinding.tilPhotoUrl, mBinding.tilPhone, mBinding.tilName)){

                    with(mStoreEntity){
                        name = mBinding.etUserName.text.toString().trim()
                        phone = mBinding.etPhone.text.toString().trim()
                        website = mBinding.etWeb.text.toString().trim()
                        photoUrl = mBinding.etPhotoUrl.text.toString().trim()
                    }

                    if (mIsEditMode) mEditStoreViewModel.updateStore(mStoreEntity)
                    else mEditStoreViewModel.saveStore(mStoreEntity)

                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
        //return super.onOptionsItemSelected(item)
    }

    private fun validateFields(vararg textFields: TextInputLayout): Boolean{
        var isValid = true

        for (textField in textFields){
            if (textField.editText?.text.toString().trim().isEmpty()){
                textField.error = getString(R.string.helper_required)
                textField.editText?.requestFocus()
                isValid = false
            } else textField.error = null
        }

        return isValid
    }


    private fun hideKeyboard(){
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        if (view != null){
            imm?.hideSoftInputFromWindow(requireView().windowToken,0)
        }
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mEditStoreViewModel.setResult(Any())
        mEditStoreViewModel.setTypeError(TypeError.NONE)
        setHasOptionsMenu(false)
        super.onDestroy()
    }

}
