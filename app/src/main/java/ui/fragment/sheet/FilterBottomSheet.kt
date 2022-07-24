package ui.fragment.sheet

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import hlv.cute.todo.R
import hlv.cute.todo.databinding.SheetFilterBinding
import model.Category
import model.Filter
import ui.adapter.FilterCategoryAdapter
import utils.KeyboardUtil

class FilterBottomSheet : BaseViewBindingBottomSheet<SheetFilterBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> SheetFilterBinding
        get() = SheetFilterBinding::inflate


    private var filter: Filter? = null

    var onApplyClick: (() -> Unit)? = null
    var onClearClick: (() -> Unit)? = null

    private var categories: ArrayList<Category>? = null

    private var adapter: FilterCategoryAdapter? = null

    companion object {
        private const val FILTER_ARGS = "filter-args"
        private const val CATEGORY_ARGS = "categories-args"

        @JvmStatic
        fun newInstance(filter: Filter?, categories: ArrayList<Category?>?): FilterBottomSheet {
            val filterBottomSheet = FilterBottomSheet()

            val args = bundleOf(
                FILTER_ARGS to filter,
                CATEGORY_ARGS to categories
            )

            filterBottomSheet.arguments = args
            return filterBottomSheet
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null && !arguments!!.isEmpty) {
            filter = arguments!!.getSerializable(FILTER_ARGS) as Filter?

            @Suppress("UNCHECKED_CAST")
            categories = arguments!!.getSerializable(CATEGORY_ARGS) as ArrayList<Category>?
        }


        KeyboardUtil.hideKeyboard(context!!)
    }

    override fun initiate() {
        initData()
        handleAction()
    }

    private fun initData() {
        adapter = FilterCategoryAdapter(context!!)

        if (filter != null) {
            binding.aChkBoxDone.isChecked = filter!!.isDone
            binding.aChkBoxUndone.isChecked = filter!!.isUndone
            binding.aChkBoxScheduled.isChecked = filter!!.isScheduled
            binding.aChkBoxToday.isChecked = filter!!.isToday
            binding.aChkBoxLow.isChecked = filter!!.isLow
            binding.aChkBoxNormal.isChecked = filter!!.isNormal
            binding.aChkBoxHigh.isChecked = filter!!.isHigh

            //select item adapter
            //+ categories
            //+ must be select ids
            val selectedIds = filter!!.categoryIds
            for (i in categories!!.indices) {
                val tempCat = categories!![i]

                if (selectedIds!!.contains(tempCat.id))
                    tempCat.isSelectedForFilter = true
            }
        }
    }

    private fun handleAction() {
        binding.aImgClose.setOnClickListener { dismiss() }

        binding.mBtnApplyFilter.setOnClickListener {
            if (onApplyClick == null) {
                dismiss()
                return@setOnClickListener
            }

            onApplyClick!!()
        }

        binding.mBtnClearFilter.setOnClickListener {
            if (onClearClick == null) {
                dismiss()
                return@setOnClickListener
            }

            onClearClick!!()
        }

        val layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW_REVERSE
            justifyContent = JustifyContent.FLEX_START
            flexWrap = FlexWrap.WRAP
        }

        binding.rvCategory.layoutManager = layoutManager
        binding.rvCategory.adapter = adapter
        adapter!!.differ.submitList(categories)

        binding.nested.setOnScrollChangeListener(object :
            NestedScrollView.OnScrollChangeListener {
            val dpShadow = resources.getDimension(R.dimen.toolbar_sheet_shadow)
            override fun onScrollChange(
                v: NestedScrollView,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                if (scrollY == 0) {
                    binding.toolbar.animate()
                        .translationZ(0f)
                        .setStartDelay(0)
                        .setDuration(200)
                        .start()

                } else if (scrollY > 50) {
                    binding.toolbar.translationZ = dpShadow

                    binding.toolbar.animate()
                        .translationZ(dpShadow)
                        .setStartDelay(0)
                        .setDuration(90)
                        .start()
                }
            }
        })
    }

    fun getFilter(): Filter {
        val filter = Filter().apply {
            isDone = binding.aChkBoxDone.isChecked
            isUndone = binding.aChkBoxUndone.isChecked
            isScheduled = binding.aChkBoxScheduled.isChecked
            isToday = binding.aChkBoxToday.isChecked
            isLow = binding.aChkBoxLow.isChecked
            isNormal = binding.aChkBoxNormal.isChecked
            isHigh = binding.aChkBoxHigh.isChecked
            categoryIds = selectedIdsInAdapter
        }

        return filter
    }

    private val selectedIdsInAdapter: ArrayList<Int>
        get() {
            val categoryIds = ArrayList<Int>()

            for (i in adapter!!.selectedItems.indices) {
                val category = adapter!!.selectedItems[i]!!
                categoryIds.add(category.id)
            }

            return categoryIds
        }

    fun clearFilterViews() {
        //clear checked
        binding.aChkBoxDone.isChecked = false
        binding.aChkBoxUndone.isChecked = false
        binding.aChkBoxScheduled.isChecked = false
        binding.aChkBoxToday.isChecked = false
        binding.aChkBoxLow.isChecked = false
        binding.aChkBoxNormal.isChecked = false
        binding.aChkBoxHigh.isChecked = false

        clearSelectRecyclerView()
        disableViews()
    }

    private fun clearSelectRecyclerView() {
        for (i in categories!!.indices)
            categories!![i].isSelectedForFilter = false

        adapter!!.differ.submitList(categories)
    }

    fun disableViews() {
        binding.aChkBoxDone.isEnabled = false
        binding.aChkBoxUndone.isEnabled = false
        binding.aChkBoxScheduled.isEnabled = false
        binding.aChkBoxToday.isEnabled = false
        binding.aChkBoxLow.isEnabled = false
        binding.aChkBoxNormal.isEnabled = false
        binding.aChkBoxHigh.isEnabled = false
        binding.mBtnApplyFilter.isEnabled = false
        binding.mBtnClearFilter.isEnabled = false
        binding.rvCategory.isEnabled = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return try {
            val dialog = super.onCreateDialog(savedInstanceState)

            dialog.setOnShowListener { dialogInterface: DialogInterface ->
                val bottomSheetDialog = dialogInterface as BottomSheetDialog
                setupFullHeight(bottomSheetDialog)
            }

            dialog
        } catch (e: Exception) {
            requireDialog()
        }
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            ?: return

        val layoutParams: ViewGroup.LayoutParams? = bottomSheet.layoutParams

        layoutParams?.height = windowHeight

        bottomSheet.layoutParams = layoutParams

        BottomSheetBehavior.from(bottomSheet).apply {
            setPeekHeight(windowHeight, true)

            skipCollapsed = true //use it for ignore setting snap to new state

            addBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    /*if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }*/
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }
    }

    private val windowHeight: Int
        get() = try {
            context?.let {
                context!!.resources.displayMetrics.heightPixels
            } ?: run {
                1500
            }
        } catch (e: Exception) {
            1500
        }
}