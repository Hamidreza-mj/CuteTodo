package ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ActivityContext
import hlv.cute.todo.databinding.FragmentAddEditCategoryBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.Category
import ui.component.bindingComponent.BaseViewBindingFragment
import utils.Constants
import utils.KeyboardUtil.focusAndShowKeyboard
import viewmodel.AddEditCategoryViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AddEditCategoryFragment : BaseViewBindingFragment<FragmentAddEditCategoryBinding>() {

    val viewModel by viewModels<AddEditCategoryViewModel>()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddEditCategoryBinding
        get() = FragmentAddEditCategoryBinding::inflate

    @Inject
    @ActivityContext
    lateinit var iContext: Context

    companion object {
        private const val CATEGORY_ARGS = "category-args"

        @JvmStatic
        fun newInstance(category: Category?): AddEditCategoryFragment {
            val fragment = AddEditCategoryFragment()

            val args = bundleOf(CATEGORY_ARGS to category)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null && requireArguments().isEmpty.not()) {
            val category: Category? = requireArguments().getParcelable(CATEGORY_ARGS)

            if (category != null) {
                viewModel.isEditMode = true
                viewModel.category = category
            } else {
                viewModel.isEditMode = false
            }
        }
    }

    override fun initiate() {
        initLogic()
        handleAction()
    }

    private fun initLogic() {
        binding.aImgBack.setOnClickListener { back() }

        binding.inpEdtName.focusAndShowKeyboard(iContext)

        binding.txtTitle.text = viewModel.getTitleFragment()

        binding.mBtnSave.text = viewModel.getButtonPrimaryText()

        //for moving cursor to end of line editText
        binding.inpEdtName.setText("")
        binding.inpEdtName.append(viewModel.getEditTextTitle())
    }

    private fun handleAction() {
        binding.inpEdtName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.isNotEmpty())
                    binding.inpName.error = null
            }
        })

        binding.mBtnSave.setOnClickListener {
            binding.inpName.error = null

            if (viewModel.isEditMode) {
                val editedCategory =
                    viewModel.editCategory(binding.inpEdtName.text.toString().trim())

                val res = categoryViewModel.validateCategory(editedCategory)

                if (res == null) {
                    categoryViewModel.editCategory(editedCategory)
                    updateTodoDetail()
                    //todoViewModel.fetch() //need to update todos if category was edited
                    //searchViewModel.search()
                    back()

                    return@setOnClickListener
                }

                binding.inpName.error = res

            } else {
                val category = viewModel.addCategory(binding.inpEdtName.text.toString().trim())

                val res = categoryViewModel.validateCategory(category)

                if (res == null) {
                    categoryViewModel.goToTop()
                    categoryViewModel.addCategory(category)
                    back()

                    return@setOnClickListener
                }

                binding.inpName.error = res
            }
        }
    }

    private fun updateTodoDetail() {
        lifecycleScope.launch {
            delay(200L) //need some delay to get new todo from db

            val fragment =
                parentFragmentManager.findFragmentByTag(Constants.FragmentTag.TODO_DETAIL) as TodoDetailFragment?

            fragment?.viewModel?.refreshTodo()
        }
    }

}