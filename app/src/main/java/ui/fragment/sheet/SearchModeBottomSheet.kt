package ui.fragment.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.os.bundleOf
import hlv.cute.todo.R
import hlv.cute.todo.databinding.SheetSearchModeBinding
import model.Search
import model.Search.SearchMode
import utils.KeyboardUtil

class SearchModeBottomSheet : BaseViewBindingBottomSheet<SheetSearchModeBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> SheetSearchModeBinding
        get() = SheetSearchModeBinding::inflate

    private var search: Search? = null
    var onCheckChanged: ((search: Search?) -> Unit)? = null

    companion object {
        private const val SEARCH_MODE_ARGS = "search-mode-args"

        @JvmStatic
        fun newInstance(search: Search?): SearchModeBottomSheet {
            val bottomSheet = SearchModeBottomSheet()

            val args = bundleOf(SEARCH_MODE_ARGS to search)
            bottomSheet.arguments = args

            return bottomSheet
        }
    }


    override fun initiate() {
        initData()
        handleAction()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null && arguments!!.isEmpty.not())
            search = arguments!!.getParcelable(SEARCH_MODE_ARGS)

        KeyboardUtil.hideKeyboard(context!!)
    }

    private fun initData() {
        when (search?.searchMode) {
            SearchMode.TODO -> binding.radioBtnTodo.isChecked = true
            SearchMode.CATEGORY -> binding.radioBtnCategory.isChecked = true
            SearchMode.BOTH -> binding.radioBtnBoth.isChecked = true
            else -> binding.radioBtnTodo.isChecked = true
        }
    }

    private fun handleAction() {
        binding.aImgClose.setOnClickListener { dismiss() }
        binding.mBtnClose.setOnClickListener { dismiss() }

        binding.radioGP.setOnCheckedChangeListener { _: RadioGroup?, id: Int ->
            if (onCheckChanged == null) {
                dismiss()
                return@setOnCheckedChangeListener
            }

            when (id) {
                R.id.radioBtnTodo -> search!!.searchMode = SearchMode.TODO
                R.id.radioBtnCategory -> search!!.searchMode = SearchMode.CATEGORY
                R.id.radioBtnBoth -> search!!.searchMode = SearchMode.BOTH
                else -> search!!.searchMode = SearchMode.TODO
            }

            onCheckChanged!!(search)
        }
    }

    fun disableViews() {
        binding.radioGP.isEnabled = false
        binding.radioBtnTodo.isEnabled = false
        binding.radioBtnCategory.isEnabled = false
        binding.radioBtnBoth.isEnabled = false
    }
}