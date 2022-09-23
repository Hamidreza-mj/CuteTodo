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
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import controller.ShareController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ActivityContext
import hlv.cute.todo.R
import hlv.cute.todo.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import model.Category
import model.Todo
import ui.adapter.TodoAdapter
import ui.component.DimView
import ui.component.PopupMaker
import ui.component.bindingComponent.BaseViewBindingFragment
import ui.dialog.DeleteDialog
import ui.dialog.ShowMoreDialog
import ui.fragment.sheet.FilterBottomSheet
import utils.Constants
import utils.TextHelper
import utils.ToastUtil
import utils.collectLatestLifecycleFlow
import viewmodel.NotificationViewModel
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : BaseViewBindingFragment<FragmentHomeBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    private val notificationViewModel by viewModels<NotificationViewModel>()

    private var scrollBehavior: HideBottomViewOnScrollBehavior<FrameLayout?>? = null

    private var adapter: TodoAdapter? = null

    var scrollYPos = 0
        private set

    private var allCategories: ArrayList<Category?>? = null

    @Inject
    lateinit var toastUtil: ToastUtil

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

    private var isFirstCollectTodo = true

    companion object {
        @JvmStatic
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun initiate() {
        initViews()
        handleActions()
        handleObserver()
    }

    private fun initViews() {
        setScrollBehavior()
        handleShadowScroll()
    }

    private fun setScrollBehavior() {
        scrollBehavior = HideBottomViewOnScrollBehavior()
        val lp = binding.frameLytButton.layoutParams as CoordinatorLayout.LayoutParams
        lp.behavior = scrollBehavior
    }

    private fun handleShadowScroll() {
        /*rvTodo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            final float dpShadow = DisplayUtils.getDisplay().dpToPx(rvTodo.getContext(), 12);

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                scrollYPos += dy;

                if (scrollYPos == 0) {
                    toolbar.animate().translationZ(0).setStartDelay(0).setDuration(200).start();
                    //toolbar.setTranslationZ(0);
                } else if (scrollYPos > 50) {
                    toolbar.setTranslationZ(dpShadow);
                    toolbar.animate().translationZ(dpShadow).setStartDelay(0).setDuration(90).start();
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });*/

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
        binding.aImgGlobalMenu.setOnClickListener {
            val popup: PopupMenu? =
                popupMaker.showMenu(
                    anchor = it,

                    viewToDim = binding.root,

                    nonDimItem = it,

                    menuRes = R.menu.popup_menu_home,

                    onMenuItemClick = itemClicked@{ menuItem ->
                        when (menuItem.itemId) {
                            R.id.menuCategories -> {
                                val fragment: Fragment = CategoriesFragment.newInstance().apply {
                                    enterTransition = Slide(Gravity.BOTTOM)
                                }

                                parentFragmentManager.beginTransaction().apply {
                                    add(
                                        R.id.mainContainer,
                                        fragment,
                                        Constants.FragmentTag.CATEGORY
                                    )
                                    addToBackStack(Constants.FragmentTag.CATEGORY)
                                }.commit()
                            }

                            R.id.menuDeleteAll -> {
                                lifecycleScope.launch {
                                    if (todoViewModel.todosCountIsEmpty()) {
                                        toastUtil.toast(provideResource.getString(R.string.todos_is_empty))
                                        return@launch //empty return
                                    }

                                    DeleteDialog(iContext).apply {
                                        show()

                                        setTitle(provideResource.getString(R.string.delete_all_todos))

                                        setMessage(
                                            provideResource.getString(
                                                R.string.delete_all_todos_message,
                                                todoViewModel.getTodosCount()
                                            )
                                        )

                                        onClickDelete = {
                                            notificationViewModel.cancelAllAlarm()
                                            todoViewModel.deleteAllTodos()
                                            scrollBehavior!!.slideUp(binding.frameLytButton)

                                            dismiss()
                                        }
                                    }
                                }
                            }

                            R.id.menuDeleteAllDone -> {
                                lifecycleScope.launch {
                                    if (todoViewModel.todosCountIsEmpty()) {
                                        toastUtil.toast(provideResource.getString(R.string.todos_is_empty))
                                        return@launch //empty return
                                    }

                                    if (todoViewModel.getDoneTodosCount() == 0L) {
                                        toastUtil.toast(provideResource.getString(R.string.todos_done_is_empty))
                                        return@launch //empty return
                                    }

                                    DeleteDialog(iContext).apply {
                                        show()

                                        setTitle(provideResource.getString(R.string.delete_all_done_todos))

                                        setMessage(
                                            provideResource.getString(
                                                R.string.delete_all_done_todos_message,
                                                todoViewModel.getDoneTodosCount()
                                            )
                                        )

                                        onClickDelete = {
                                            notificationViewModel.cancelAllDoneAlarm()
                                            todoViewModel.deleteAllDoneTodos()
                                            scrollBehavior!!.slideUp(binding.frameLytButton)
                                            dismiss()
                                        }

                                    }
                                }
                            }
                        }
                    }
                )

            popupMaker.apply {
                popup?.changeTextColorOfItem(
                    1,
                    provideResource.getString(R.string.delete_all_todos),
                    provideResource.getColor(R.color.red)
                )

                popup?.changeTextColorOfItem(
                    2,
                    provideResource.getString(R.string.delete_all_done_todos),
                    provideResource.getColor(R.color.red)
                )
            }
        }


        binding.aImgFilter.setOnClickListener {
            FilterBottomSheet.newInstance(
                todoViewModel.currentFilter,
                allCategories
            ).apply {
                onApplyClick = {
                    disableViews()
                    val filter = getFilter()

//              if (!filter.filterIsEmpty() || getTodoViewModel().getTodosCount() != 0) //if all filter is empty do nothing
                    todoViewModel.applyFilter(filter)
                    //goToTop(800);
                    scrollBehavior!!.slideUp(binding.frameLytButton)
                    dismiss()
                }

                onClearClick = {
                    clearFilterViews()
                    todoViewModel.applyFilter(null)
                    //goToTop(800);
                    scrollBehavior!!.slideUp(binding.frameLytButton)
                    dismiss()
                }
            }.show(childFragmentManager, null)
        }


        binding.aImgSearch.setOnClickListener {
            val fragment: Fragment = SearchFragment.newInstance().apply {
                enterTransition = Slide(Gravity.BOTTOM)
            }

            parentFragmentManager.beginTransaction().apply {
                add(R.id.mainContainer, fragment, Constants.FragmentTag.SEARCH)

                addToBackStack(Constants.FragmentTag.SEARCH)
            }.commit()
        }

        binding.mBtnAdd.setOnClickListener {
            openAddEditFragment()
        }


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
        binding.rvTodo.apply {
            this.layoutManager = layoutManager
            adapter = this@HomeFragment.adapter
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

    private fun doneTodo(todoID: Int) {
        todoViewModel.setDoneTodo(todoID.toLong())
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
                scrollBehavior!!.slideUp(binding.frameLytButton)

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
        val box = binding.box
        val params = box.layoutParams as ConstraintLayout.LayoutParams

        //todoViewModel.fetch() init fetch run in this,
        //because MutableStateFlow emit with null default value at first
        collectLatestLifecycleFlow(todoViewModel.filterStateFlow) {
            todoViewModel.fetch()
        }

        collectLatestLifecycleFlow(
            todoViewModel.todosFlow,

            map = { mappedList ->
                //apply filter when collectiong datas
                //has filter
                todoViewModel.currentFilter?.let { currentFilter -> //for avoid remove filter after update todos
                    todoViewModel.fetchWithFilter(currentFilter)
                } ?: run {
                    //without filter
                    mappedList
                }
            },

            collect = { todos: List<Todo>? ->
                if (todos == null || todos.isEmpty()) {
                    binding.rvTodo.visibility = View.GONE
                    binding.cLytEmpty.visibility = View.VISIBLE
                    if (todoViewModel.currentFilter == null /*|| getTodoViewModel().getCurrentFilter().filterIsEmpty()*/) {
                        binding.filterIndicator.visibility = View.GONE
                        binding.cLytGuide.visibility = View.VISIBLE

                        binding.txtEmpty.text = provideResource.getString(R.string.todos_empty)
                        binding.txtNotesEmpty.text =
                            TextHelper.fromHtml(provideResource.getString(R.string.todos_empty_notes))

                        params.verticalBias = 0.2f
                    } else {
                        binding.filterIndicator.visibility = View.VISIBLE
                        binding.cLytGuide.visibility = View.GONE

                        binding.txtEmpty.text =
                            provideResource.getString(R.string.empty_todos_with_filter)

                        binding.txtNotesEmpty.text =
                            provideResource.getString(R.string.empty_todos_with_filter_notes)

                        params.verticalBias = 0.35f
                    }

                    box.layoutParams = params
                } else {
                    binding.filterIndicator.visibility = todoViewModel.filterIndicatorVisibility
                    binding.cLytEmpty.visibility = View.GONE
                    binding.rvTodo.visibility = View.VISIBLE

                    binding.rvTodo.post { adapter?.differ?.submitList(todos) }
                }

                isFirstCollectTodo = false
            })

        collectLatestLifecycleFlow(todoViewModel.goToTopFlow) {
            goToTop(1000)
        }

        collectLatestLifecycleFlow(categoryViewModel.categoriesFlow) { categories: List<Category?>? ->
            allCategories = categories as ArrayList<Category?>?
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

}