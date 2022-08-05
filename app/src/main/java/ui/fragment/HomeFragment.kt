package ui.fragment

import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import dagger.hilt.android.AndroidEntryPoint
import hlv.cute.todo.R
import hlv.cute.todo.databinding.FragmentHomeBinding
import model.Filter
import model.Todo
import ui.adapter.TodoAdapter
import ui.component.PopupMaker
import ui.component.bindingComponent.BaseViewBindingFragment
import ui.dialog.DeleteDialog
import ui.fragment.sheet.FilterBottomSheet
import utils.Constants
import utils.TextHelper
import utils.ToastUtil
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

    @Inject
    lateinit var toastUtil: ToastUtil

    @Inject
    lateinit var popupMaker: PopupMaker

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
                                if (todoViewModel.todosIsEmpty()) {
                                    toastUtil.toast(provideResource.getString(R.string.todos_is_empty))
                                    return@itemClicked //empty return
                                }

                                DeleteDialog(context).apply {
                                    show()

                                    setTitle(provideResource.getString(R.string.delete_all_todos))

                                    setMessage(
                                        provideResource.getString(
                                            R.string.delete_all_todos_message,
                                            todoViewModel.todosCount
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

                            R.id.menuDeleteAllDone -> {
                                if (todoViewModel.todosIsEmpty()) {
                                    toastUtil.toast(provideResource.getString(R.string.todos_is_empty))
                                    return@itemClicked //empty return
                                }

                                if (todoViewModel.todosDoneIsEmpty()) {
                                    toastUtil.toast(provideResource.getString(R.string.todos_done_is_empty))
                                    return@itemClicked //empty return
                                }

                                DeleteDialog(context).apply {
                                    show()

                                    setTitle(provideResource.getString(R.string.delete_all_done_todos))

                                    setMessage(
                                        provideResource.getString(
                                            R.string.delete_all_done_todos_message,
                                            todoViewModel.doneTodosCount
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
                categoryViewModel.getAllCategories()?.let { list -> ArrayList(list) }
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
            val fragment: Fragment = AddEditTodoFragment.newInstance(null).apply {
                enterTransition = Slide(Gravity.BOTTOM)
            }

            parentFragmentManager.beginTransaction().apply {
                add(R.id.mainContainer, fragment, Constants.FragmentTag.ADD_EDIT_TODO)

                addToBackStack(Constants.FragmentTag.ADD_EDIT_TODO)
            }.commit()
        }


        handleRecyclerView()
    }

    private fun handleRecyclerView() {
        adapter = TodoAdapter(
            context!!,

            { todoID: Int ->
                todoViewModel.setDoneTodo(todoID.toLong())
            },

            { todoMenu: Todo, menuView: View, wholeItem: View ->
                val popup = popupMaker.showMenu(
                    anchor = menuView,

                    viewToDim = binding.root,

                    nonDimItem = wholeItem,

                    menuRes = R.menu.popup_menu_todo_item,

                    onMenuItemClick = itemClicked@{ menuItem ->
                        when (menuItem.itemId) {
                            R.id.menuEdit -> {
                                val fragment: Fragment =
                                    AddEditTodoFragment.newInstance(todoMenu).apply {
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

                            R.id.menuDetail -> {
                                val fragment: Fragment =
                                    TodoDetailFragment.newInstance(todoMenu).apply {
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

                            R.id.menuShare -> {
                                activity?.let {
                                    val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            todoViewModel.shareContent(todoMenu)
                                        )
                                    }

                                    it.startActivity(
                                        Intent.createChooser(
                                            sharingIntent,
                                            provideResource.getString(R.string.share_using)
                                        )
                                    )
                                }
                            }

                            R.id.menuShare -> {}

                            R.id.menuDelete -> {
                                DeleteDialog(context).apply {
                                    show()

                                    setTitle(provideResource.getString(R.string.delete_todo))

                                    var todoTitle = todoMenu.title ?: ""
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
                                        if (todoMenu.arriveDate != 0L)
                                            notificationViewModel.cancelAlarm(todoMenu)

                                        todoViewModel.deleteTodo(todoMenu)
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
                        4,
                        provideResource.getString(R.string.delete),
                        provideResource.getColor(R.color.red)
                    )
                }

                /*MoreDialog(context).apply {
                    show()

                    setWithDetail(true)

                    onClickEdit = {
                        dismiss()
                    }

                    onClickDetail = {
                        dismiss()
                    }

                    onClickDelete = {
                        dismiss()
                    }
                }*/
            }
        )

        val layoutManager = LinearLayoutManager(context)
        binding.rvTodo.apply {
            this.layoutManager = layoutManager
            adapter = this@HomeFragment.adapter
        }
    }

    private fun handleObserver() {
        val box = binding.box
        val params = box.layoutParams as ConstraintLayout.LayoutParams

        todoViewModel.fetch()

        todoViewModel.todosLiveData.observe(viewLifecycleOwner) { todos: List<Todo>? ->

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
        }

        todoViewModel.filterLiveData.observe(viewLifecycleOwner) { filter: Filter? ->
            todoViewModel.fetch(filter)
        }

        todoViewModel.goToTopLiveData.observe(viewLifecycleOwner) {
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
        todoViewModel.fetch()
    }
}