package ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import hlv.cute.todo.R
import hlv.cute.todo.databinding.DialogDropdownCategoriesBinding
import model.Category
import ui.adapter.DropDownCategoryAdapter
import utils.TextHelper

class DropDownCategoriesDialog(
    context: Context,
    var categories: MutableList<Category>?,
    cancelable: Boolean = true
) {

    private val binding: DialogDropdownCategoriesBinding

    private var dialog: AlertDialog? = null

    var onClickCategory: ((Category?) -> Unit)? = null
    var onclickManage: (() -> Unit?)? = null

    init {
        binding = DialogDropdownCategoriesBinding.inflate(LayoutInflater.from(context), null, false)

        val builder = AlertDialog.Builder(context, R.style.TranslucentDialog).apply {
            setCancelable(cancelable)
            setView(binding.root)
        }

        categories = categories?.toMutableList()

        binding.txtNotesEmpty.text =
            TextHelper.fromHtml(context.getString(R.string.manage_categories_notes))

        handleRecyclerView(context)

        binding.txtClose.setOnClickListener { dismiss() }

        binding.txtManageCategories.setOnClickListener {
            if (onclickManage == null) {
                dismiss()
                return@setOnClickListener
            }

            onclickManage!!()
        }

        if (dialog == null)
            dialog = builder.create()
    }

    private fun handleRecyclerView(context: Context) {
        if (categories.isNullOrEmpty()) {
            binding.rvCategory.visibility = View.GONE
            binding.cLytEmpty.visibility = View.VISIBLE

            return
        }

        val adapter = DropDownCategoryAdapter(context) clickEvent@{ category ->
            if (onClickCategory == null) {
                dismiss()
                return@clickEvent
            }

            onClickCategory!!(category)
        }

        val layoutManager = LinearLayoutManager(context)

        binding.rvCategory.layoutManager = layoutManager
        binding.rvCategory.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.rvCategory.adapter = adapter
        binding.cLytEmpty.visibility = View.GONE
        binding.rvCategory.visibility = View.VISIBLE

        //add first item for without category
        val firstCat = Category().apply {
            id = 0
            name = null
        }

        categories?.add(0, firstCat)

        adapter.differ.submitList(categories)
    }

    fun show() = dialog?.show()
    fun dismiss() = dialog?.dismiss()

}