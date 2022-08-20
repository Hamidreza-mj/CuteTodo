package ui.fragment

import android.content.Context
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import controller.ShareController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ActivityContext
import hlv.cute.todo.R
import hlv.cute.todo.databinding.FragmentSearchBinding
import model.Category
import model.Search
import model.Todo
import ui.adapter.TodoAdapter
import ui.component.DimView
import ui.component.PopupMaker
import ui.component.bindingComponent.BaseViewBindingFragment
import ui.dialog.DeleteDialog
import ui.dialog.ShowMoreDialog
import ui.fragment.sheet.SearchModeBottomSheet
import utils.Constants
import utils.KeyboardUtil.focusAndShowKeyboard
import utils.TextHelper
import utils.collectLatestLifecycleFlow
import viewmodel.NotificationViewModel
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseViewBindingFragment<FragmentSearchBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSearchBinding
        get() = FragmentSearchBinding::inflate

    private val notificationViewModel by viewModels<NotificationViewModel>()

    private var adapter: TodoAdapter? = null

    private var afterTextChanged = false
    private var search: Search? = Search()

    @Inject
    lateinit var popupMaker: PopupMaker

    @Inject
    lateinit var shareController: ShareController

    @Inject
    @ActivityContext
    lateinit var iContext: Context

    @Inject
    lateinit var dimView: DimView

    private var moreDialog: ShowMoreDialog? = null

    companion object {
        @JvmStatic
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }

    override fun initiate() {
        initViews()
        handleActions()
        handleObserver()
    }

    private fun initViews() {
        binding.aImgBack.setOnClickListener { back() }

        Handler(Looper.getMainLooper()).postDelayed({
            binding.edtSearch.setText("")
            binding.edtSearch.requestFocus()
        }, 50)

        binding.edtSearch.focusAndShowKeyboard(iContext)

        handleShadowScroll()
    }

    private fun handleTabLayout(list: ArrayList<Category>?) {
        binding.tabLyt.removeAllTabs()

        val lp = (binding.tabLyt.layoutParams as ConstraintLayout.LayoutParams)

        if (list.isNullOrEmpty()) {
            binding.tabLyt.visibility = View.INVISIBLE
            lp.height = provideResource.getDimen(R.dimen.heigh_invisible_space).toInt()

            return
        }

        binding.tabLyt.visibility = View.VISIBLE
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT

        binding.tabLyt.layoutParams = lp
        binding.tabLyt.requestLayout()

        //use toMutableList() to clone it for avoid changing every time
        val allCategories = list.toMutableList()

        val categoryAllItem = Category().apply {
            id = 0
            name = "همه"
        }

        allCategories.add(0, categoryAllItem)
        allCategories.reverse()

        val maxLength = 18

        for (category in allCategories) {
            val categoryName = category.name

            if (categoryName!!.length > maxLength) {
                val categoryWithEllipsis =
                    categoryName.substring(
                        0,
                        maxLength
                    ) + provideResource.getString(R.string.ellipsis)

                binding.tabLyt.addTab(binding.tabLyt.newTab().setText(categoryWithEllipsis))
            } else {
                binding.tabLyt.addTab(binding.tabLyt.newTab().setText(categoryName))
            }
        }


        //val lastTabPos = binding.tabLyt.tabCount - 1

        Handler(Looper.getMainLooper()).postDelayed({
            search!!.categoryId = categoryAllItem.id
            binding.tabLyt.selectTab(binding.tabLyt.getTabAt(binding.tabLyt.tabCount - 1), true)
        }, 100)

        binding.tabLyt.clearOnTabSelectedListeners()
        binding.tabLyt.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab) {
                val tabCategoryId = allCategories[tab.position].id

                search!!.categoryId = tabCategoryId

                binding.nested.smoothScrollTo(0, 0, 500)

                if (tab.position == binding.tabLyt.tabCount - 1) { //all
                    searchViewModel.search()
                } else {
                    val searchText = binding.edtSearch.text.toString()

                    if (searchText.isEmpty()) {
                        searchViewModel.search(categoryId = tabCategoryId)
                    } else {
                        search!!.term = searchText
                        searchViewModel.search(search)
                    }
                }
            }

            override fun onTabUnselected(tab: Tab) {
            }

            override fun onTabReselected(tab: Tab) {
                binding.nested.smoothScrollTo(0, 0, 500)
            }
        })
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

    private fun handleActions() {
        binding.aImgFilter.setOnClickListener {
            if (search!!.term == null) {
                search!!.term = ""
                search!!.searchMode = Search.SearchMode.TODO
            }

            SearchModeBottomSheet.newInstance(search).apply {
                onCheckChanged = { search: Search? ->
                    disableViews()

                    search!!.term = searchViewModel.currentTerm

                    searchViewModel.search(search)

                    val lp = binding.tabLyt.layoutParams as ConstraintLayout.LayoutParams

                    if (search.searchMode === Search.SearchMode.CATEGORY || search.searchMode === Search.SearchMode.BOTH) {
                        binding.tabLyt.visibility = View.INVISIBLE
                        lp.height = provideResource.getDimen(R.dimen.heigh_invisible_space).toInt()
                    } else {
                        binding.tabLyt.visibility = View.VISIBLE
                        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    }

                    binding.tabLyt.layoutParams = lp
                    binding.tabLyt.requestLayout()

                    dismiss()
                }

            }.show(childFragmentManager, null)
        }

        binding.aImgClear.setOnClickListener {
            binding.edtSearch.setText("")
            binding.aImgClear.visibility = View.INVISIBLE
        }


        binding.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.isNotEmpty()) {
                    afterTextChanged = true
                    binding.txtResult.visibility = View.VISIBLE
                    binding.aImgClear.visibility = View.VISIBLE
                } else {
                    afterTextChanged = false
                    binding.txtResult.visibility = View.GONE
                    binding.aImgClear.visibility = View.INVISIBLE
                }
                search!!.term = editable.toString().trim()
                search!!.searchMode = searchViewModel.searchMode

                searchViewModel.search(search!!)
            }
        })

        handleRecyclerView()
    }

    private fun handleRecyclerView() {
        adapter = TodoAdapter(
            iContext,

            onCheckChangedListener = { todoID: Int ->
                doneTodo(todoID)
            },

            onClickMenuListener = { todoMenu: Todo, menuView: View, wholeItem: View, coordinatePoint: Point? ->
                showPopupMenuItem(todoMenu, menuView, wholeItem, coordinatePoint, true)
            },

            onClickMoreListener = { todoMore ->
                openMoreDialog(todoMore)
            }
        )

        val layoutManager = LinearLayoutManager(iContext)
        binding.rvSearch.apply {
            this.layoutManager = layoutManager
            adapter = this@SearchFragment.adapter
        }
    }

    private fun doneTodo(todoID: Int) {
        todoViewModel.setDoneTodo(todoID.toLong())
        searchViewModel.search()
    }

    private fun openAddEditFragment(todo: Todo? = null) {
        val fragment: Fragment =
            AddEditTodoFragment.newInstance(todo).apply {
                enterTransition = Slide(Gravity.BOTTOM)
            }

        parentFragmentManager.beginTransaction().apply {
            add(
                R.id.mainContainer,
                fragment,
                Constants.FragmentTag.ADD_EDIT_TODO
            )
            addToBackStack(Constants.FragmentTag.ADD_EDIT_TODO)
        }.commit()
    }

    private fun openDeleteDialog(todo: Todo) {
        DeleteDialog(iContext).apply {
            show()

            setTitle(provideResource.getString(R.string.delete_todo))

            var todoTitle = todo.title ?: ""
            if (todoTitle.trim().length > 30)
                todoTitle =
                    todoTitle.substring(0, 30)
                        .trim() + provideResource.getString(R.string.ellipsis)

            setMessage(
                provideResource.getString(
                    R.string.delete_todo_message,
                    todoTitle
                )
            )

            onClickDelete = {
                if (todo.arriveDate != 0L)
                    notificationViewModel.cancelAlarm(todo)

                todoViewModel.deleteTodo(todo)
                searchViewModel.search()

                moreDialog?.dismiss()
                dismiss()
            }
        }
    }

    private fun showPopupMenuItem(
        todoMenu: Todo,
        menuView: View,
        wholeItem: View,
        coordinatePoint: Point?,
        isFirstShow: Boolean
    ) {
        val popup = popupMaker.showMenu(
            anchor = menuView,

            coordinatePoint = coordinatePoint,

            viewToDim = binding.root,

            nonDimItem = wholeItem,

            menuRes = R.menu.popup_menu_todo_item,

            isFirstShow = isFirstShow,

            onMenuItemClick = itemClicked@{ menuItem ->
                when (menuItem.itemId) {
                    R.id.menuTickDone -> {
                        doneTodo(todoMenu.id)
                    }

                    R.id.menuEdit -> {
                        openAddEditFragment(todoMenu)
                    }

                    R.id.menuDetail -> {
                        openTodoDetailFragment(todoMenu)
                    }

                    R.id.menuFastShow -> {
                        openMoreDialog(todoMenu)
                    }

                    R.id.menuShare -> {
                        popupMaker.prepareForNestedMenu()

                        binding.root.postOnAnimation {
                            val sharePopup = popupMaker.showMenu(
                                anchor = menuView,

                                coordinatePoint = coordinatePoint,

                                viewToDim = binding.root,

                                nonDimItem = wholeItem,

                                menuRes = R.menu.popup_menu_share_detail,

                                isFirstShow = false,

                                onMenuItemClick = { shareMenuItem ->
                                    when (shareMenuItem.itemId) {
                                        R.id.menuNormalShare -> {
                                            shareController.apply {
                                                shareString(
                                                    activity,
                                                    prepareShareTodoContent(todoMenu)
                                                )
                                            }
                                        }

                                        R.id.menuAdvencedShare -> {

                                        }

                                        R.id.menuBack -> {
                                            popupMaker.prepareForNestedMenu()
                                            binding.root.postOnAnimation {
                                                showPopupMenuItem(
                                                    todoMenu,
                                                    menuView,
                                                    wholeItem,
                                                    coordinatePoint,
                                                    false
                                                )
                                            }
                                        }
                                    }
                                }
                            )

                            popupMaker.apply {
                                sharePopup?.changeTextColorOfItem(
                                    0,
                                    provideResource.getString(R.string.share_type),
                                    provideResource.getColor(R.color.gray_text)
                                )
                            }
                        }
                    }

                    R.id.menuDelete -> {
                        openDeleteDialog(todoMenu)
                    }
                }
            }
        )

        popupMaker.apply {
            popup?.changeTextColorOfItem(
                5,
                provideResource.getString(R.string.delete),
                provideResource.getColor(R.color.red)
            )

            //if (todoMenu.isDone)
            popup?.setVisibilityMenuItem(0, false)
        }
    }

    private fun openMoreDialog(todo: Todo) {
        moreDialog?.dismiss()

        moreDialog = ShowMoreDialog(iContext).apply {
            show()

            setMessage(todo.title)
            isDone(todo.isDone)

            onShowDialog = {
                binding.root.postOnAnimation {
                    dimView.applyBlurDim(binding.root)
                }
            }

            onDismissDialog = {
                popupMaker.releaseClick()

                binding.root.postOnAnimation {
                    dimView.clearDim(binding.root)
                }
            }

            //actions
            onClickDelete = {
                openDeleteDialog(todo)
            }

            onClickEdit = {
                dismiss()
                openAddEditFragment(todo)
            }

            onClickDone = {
                doneTodo(todo.id)
                showConfetti()
            }

            onClickShare = { view ->
                val sharePopup = popupMaker.showMenu(
                    anchor = view,

                    menuRes = R.menu.popup_menu_share_detail,

                    gravity = Gravity.END,

                    onMenuItemClick = itemClicked@{ menuItem ->
                        when (menuItem.itemId) {
                            R.id.menuNormalShare -> {
                                shareController.apply {
                                    shareString(activity, prepareShareTodoContent(todo))
                                }
                            }

                            R.id.menuAdvencedShare -> {

                            }
                        }
                    }
                )

                popupMaker.apply {
                    sharePopup?.setVisibilityMenuItem(0, false)
                }
            }

            onClickDetail = {
                dismiss()
                openTodoDetailFragment(todo)
            }
        }
    }

    private fun openTodoDetailFragment(todo: Todo) {
        val fragment: Fragment =
            TodoDetailFragment.newInstance(todo).apply {
                enterTransition = Slide(Gravity.BOTTOM)
            }

        parentFragmentManager.beginTransaction().apply {
            add(
                R.id.mainContainer,
                fragment,
                Constants.FragmentTag.TODO_DETAIL
            )

            addToBackStack(Constants.FragmentTag.TODO_DETAIL)
        }.commit()
    }

    private fun handleObserver() {
        searchViewModel.search()

        collectLatestLifecycleFlow(searchViewModel.searchedTodosFlow) { todos: List<Todo>? ->

            if (todos == null || todos.isEmpty()) {
                binding.txtResult.visibility = View.GONE
                binding.nested.visibility = View.GONE
                binding.rvSearch.visibility = View.GONE

                binding.txtNotes.visibility = View.VISIBLE


                if (todoViewModel.getTodosCount() == 0L) {
                    binding.txtNotes.text = provideResource.getString(R.string.todos_empty)
                } else {
                    var term = searchViewModel.currentTerm

                    if (term.isEmpty()) {
                        term = "موردی یافت نشد!"
                        binding.txtNotes.text = term
                    } else {
                        binding.txtNotes.text = TextHelper.fromHtml(
                            provideResource.getString(
                                R.string.todo_not_found,
                                searchViewModel.titleTerm,
                                term
                            )
                        )
                    }
                }

                binding.vector.visibility = View.VISIBLE

            } else {
                binding.txtNotes.visibility = View.GONE
                binding.vector.visibility = View.GONE

                binding.txtResult.text = TextHelper.fromHtml(
                    provideResource.getString(
                        R.string.search_result,
                        todos.size,
                        todoViewModel.getTodosCount(),
                        searchViewModel.titleTermResult
                    )
                )

                binding.txtResult.visibility = if (afterTextChanged) View.VISIBLE else View.GONE

                binding.nested.visibility = View.VISIBLE
                binding.rvSearch.visibility = View.VISIBLE

                binding.rvSearch.post { adapter?.differ?.submitList(todos) }
            }
        }

        collectLatestLifecycleFlow(categoryViewModel.categoriesFlow) { categories: List<Category>? ->
            val allCategories = categories as ArrayList<Category>?
            handleTabLayout(allCategories)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchViewModel.release()
    }

    override fun onResume() {
        super.onResume()
        searchViewModel.search()
    }
}