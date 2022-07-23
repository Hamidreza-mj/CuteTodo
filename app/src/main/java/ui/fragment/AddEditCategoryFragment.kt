package ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import hlv.cute.todo.databinding.FragmentAddEditCategoryBinding
import model.Category
import viewmodel.AddEditCategoryViewModel

class AddEditCategoryFragment : BaseFragment() {

    private lateinit var binding: FragmentAddEditCategoryBinding
    val viewModel by viewModels<AddEditCategoryViewModel>()

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

        if (arguments != null && arguments!!.isEmpty.not()) {
            val category = arguments!!.getSerializable(CATEGORY_ARGS) as Category?

            if (category != null) {
                viewModel.isEditMode = true
                viewModel.category = category
            } else {
                viewModel.isEditMode = false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLogic()
        handleAction()
    }

    private fun initLogic() {
        binding.aImgBack.setOnClickListener { back() }

        Handler(Looper.getMainLooper()).postDelayed({
            binding.inpEdtName.requestFocus()
        }, 500)

        Handler(Looper.getMainLooper()).postDelayed({
            showKeyboard()
        }, 600)

        binding.txtTitle.text = viewModel.titleFragment

        binding.mBtnSave.text = viewModel.buttonPrimaryText

        //for moving cursor to end of line editText
        binding.inpEdtName.setText("")
        binding.inpEdtName.append(viewModel.editTextTitle)
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
                    todoViewModel.fetch() //need to update todos if category was edited
                    searchViewModel.fetch()
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
}