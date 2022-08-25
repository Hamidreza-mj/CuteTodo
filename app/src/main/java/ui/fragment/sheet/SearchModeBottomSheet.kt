package ui.fragment.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.os.bundleOf
import hlv.cute.todo.R
import hlv.cute.todo.databinding.SheetSearchModeBinding
import model.Search.SearchMode
import ui.component.bindingComponent.BaseViewBindingBottomSheet
import utils.KeyboardUtil

class SearchModeBottomSheet : BaseViewBindingBottomSheet<SheetSearchModeBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> SheetSearchModeBinding
        get() = SheetSearchModeBinding::inflate

    private var searchMode: SearchMode = SearchMode.TODO
    lateinit var onCheckChanged: (searchMode: SearchMode) -> Unit

    companion object {
        private const val SEARCH_MODE_ARGS = "search-mode-args"

        @JvmStatic
        fun newInstance(searchMode: SearchMode): SearchModeBottomSheet {
            val bottomSheet = SearchModeBottomSheet()

            val args = bundleOf(SEARCH_MODE_ARGS to searchMode)
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

        if (arguments != null && requireArguments().isEmpty.not())
            searchMode = requireArguments().getParcelable(SEARCH_MODE_ARGS) ?: SearchMode.TODO

        activity?.let {
            KeyboardUtil.hideKeyboard(it)
        }
    }

    private fun initData() {
        when (searchMode) {
            SearchMode.TODO -> binding.radioBtnTodo.isChecked = true
            SearchMode.CATEGORY -> binding.radioBtnCategory.isChecked = true
            SearchMode.BOTH -> binding.radioBtnBoth.isChecked = true
        }
    }

    private fun handleAction() {
        binding.aImgClose.setOnClickListener { dismiss() }
        binding.mBtnClose.setOnClickListener { dismiss() }

        binding.radioGP.setOnCheckedChangeListener { _: RadioGroup?, id: Int ->
            searchMode = when (id) {
                R.id.radioBtnTodo -> SearchMode.TODO
                R.id.radioBtnCategory -> SearchMode.CATEGORY
                R.id.radioBtnBoth -> SearchMode.BOTH
                else -> SearchMode.TODO
            }

            onCheckChanged(searchMode)
        }
    }

    fun disableViews() {
        binding.radioGP.isEnabled = false
        binding.radioBtnTodo.isEnabled = false
        binding.radioBtnCategory.isEnabled = false
        binding.radioBtnBoth.isEnabled = false
    }
}