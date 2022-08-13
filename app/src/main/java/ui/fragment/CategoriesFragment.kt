package ui.fragment

import android.content.Context
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ActivityContext
import hlv.cute.todo.R
import hlv.cute.todo.databinding.FragmentCategoriesBinding
import model.Category
import ui.adapter.CategoryAdapter
import ui.component.PopupMaker
import ui.component.bindingComponent.BaseViewBindingFragment
import ui.dialog.DeleteDialog
import ui.fragment.AddEditCategoryFragment.Companion.newInstance
import utils.Constants
import utils.TextHelper
import utils.ToastUtil
import javax.inject.Inject

@AndroidEntryPoint
class CategoriesFragment : BaseViewBindingFragment<FragmentCategoriesBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCategoriesBinding
        get() = FragmentCategoriesBinding::inflate


    private var scrollBehavior: HideBottomViewOnScrollBehavior<FrameLayout?>? = null

    private var adapter: CategoryAdapter? = null

    var scrollYPos = 0
        private set

    @Inject
    lateinit var toastUtil: ToastUtil

    @Inject
    lateinit var popupMaker: PopupMaker

    @Inject
    @ActivityContext
    lateinit var iContext: Context

    companion object {
        @JvmStatic
        fun newInstance(): CategoriesFragment {
            return CategoriesFragment()
        }
    }

    override fun initiate() {
        initViews()
        handleActions()
        handleObserver()
    }

    private fun initViews() {
        binding.aImgBack.setOnClickListener { back() }

        binding.txtNotesEmpty.text =
            TextHelper.fromHtml(provideResource.getString(R.string.categories_empty_notes))

        setScrollBehavior()
        handleShadowScroll()
    }

    private fun setScrollBehavior() {
        scrollBehavior = HideBottomViewOnScrollBehavior()
        val lp = binding.frameLytButton.layoutParams as CoordinatorLayout.LayoutParams
        lp.behavior = scrollBehavior
    }

    private fun handleShadowScroll() {
        binding.nested.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
            val dpShadow = provideResource.getDimen(R.dimen.toolbar_shadow)
            override fun onScrollChange(
                v: NestedScrollView,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                scrollYPos = scrollY
                if (scrollY == 0) {
                    binding.toolbar.animate()
                        .translationZ(0f)
                        .setStartDelay(0)
                        .setDuration(200)
                        .start()

                    //toolbar.setTranslationZ(0);

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

    private fun handleActions() {
        binding.aImgDeleteAll.setOnClickListener {
            if (categoryViewModel.categoriesIsEmpty()) {
                toastUtil.toast(provideResource.getString(R.string.categories_is_empty))
                return@setOnClickListener
            }

            DeleteDialog(iContext).apply {
                show()

                setTitle(provideResource.getString(R.string.delete_all_categories))

                setMessage(
                    provideResource.getString(
                        R.string.delete_all_categories_message,
                        categoryViewModel.getCategoriesCount()
                    )
                )

                onClickDelete = {
                    categoryViewModel.deleteAllCategories()
                    todoViewModel.fetch() //need to update todos if categories was deleted
                    searchViewModel.fetch()
                    scrollBehavior!!.slideUp(binding.frameLytButton)
                    dismiss()
                }
            }
        }

        binding.mBtnAdd.setOnClickListener {
            val fragment: Fragment = newInstance(null).apply {
                enterTransition = Slide(Gravity.BOTTOM)
            }

            parentFragmentManager.beginTransaction().apply {
                add(R.id.mainContainer, fragment, Constants.FragmentTag.ADD_EDIT_CATEGORY)
                addToBackStack(Constants.FragmentTag.ADD_EDIT_CATEGORY)
            }.commit()
        }

        handleRecyclerView()
    }

    private fun handleRecyclerView() {
        adapter = CategoryAdapter(
            iContext,

            onClickMenuListener = { category: Category, anchor: View, wholeItem: View, coordinatePoint: Point? ->

                val popup = popupMaker.showMenu(
                    anchor = anchor,

                    coordinatePoint = coordinatePoint,

                    viewToDim = binding.root,

                    nonDimItem = wholeItem,

                    menuRes = R.menu.popup_menu_categories,

                    onMenuItemClick = itemClicked@{ menuItem ->
                        when (menuItem.itemId) {
                            R.id.menuEdit -> {
                                val fragment: Fragment = newInstance(category).apply {
                                    enterTransition = Slide(Gravity.BOTTOM)
                                }

                                parentFragmentManager.beginTransaction().apply {
                                    add(
                                        R.id.mainContainer,
                                        fragment,
                                        Constants.FragmentTag.ADD_EDIT_CATEGORY
                                    )

                                    addToBackStack(Constants.FragmentTag.ADD_EDIT_CATEGORY)
                                }.commit()
                            }

                            R.id.menuDelete -> {
                                DeleteDialog(iContext).apply {
                                    show()

                                    setTitle(provideResource.getString(R.string.delete_category))

                                    val categoryName = category.name ?: ""

                                    /*if (categoryName != null && categoryName.trim().length > 40)
                                        categoryName = categoryName.substring(0, 40).trim()*/

                                    setMessage(
                                        provideResource.getString(
                                            R.string.delete_category_message,
                                            categoryName
                                        )
                                    )

                                    onClickDelete = {
                                        categoryViewModel.deleteCategory(category)
                                        todoViewModel.fetch() //need to update todos if category was deleted
                                        scrollBehavior!!.slideUp(binding.frameLytButton)
                                        dismiss()
                                    }
                                }
                            }
                        }
                    }
                )

                popupMaker.apply {
                    popup?.changeTextColorOfItem(
                        1,
                        provideResource.getString(R.string.delete),
                        provideResource.getColor(R.color.red)
                    )
                }
            }
        )

        val layoutManager = LinearLayoutManager(iContext)

        binding.rvCategory.apply {
            this.layoutManager = layoutManager
            adapter = this@CategoriesFragment.adapter
        }
    }

    private fun handleObserver() {
        categoryViewModel.fetch()

        categoryViewModel.categoriesLiveDate.observe(viewLifecycleOwner) { categories: List<Category>? ->
            if (categories == null || categories.isEmpty()) {
                binding.rvCategory.visibility = View.GONE
                binding.cLytEmpty.visibility = View.VISIBLE
            } else {
                binding.cLytEmpty.visibility = View.GONE
                binding.rvCategory.visibility = View.VISIBLE
                binding.rvCategory.post { adapter?.differ?.submitList(categories) }
            }
        }

        categoryViewModel.goToTopLiveData.observe(viewLifecycleOwner) {
            goToTop(1000)
        }
    }

    fun goToTop(duration: Int) {
        if (scrollBehavior == null)
            return

        binding.nested.smoothScrollTo(0, 0, duration)

        Handler(Looper.getMainLooper()).postDelayed({
            scrollBehavior!!.slideUp(binding.frameLytButton)
        }, 500)
    }

    override fun onResume() {
        super.onResume()
        categoryViewModel.fetch()
    }
}