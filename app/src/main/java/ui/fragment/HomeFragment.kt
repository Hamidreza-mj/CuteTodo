package ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import hlv.cute.todo.R
import hlv.cute.todo.databinding.FragmentHomeBinding
import model.Filter
import model.Todo
import ui.adapter.TodoAdapter
import ui.dialog.DeleteDialog
import ui.dialog.GlobalMenuDialog
import ui.dialog.MoreDialog
import ui.fragment.sheet.FilterBottomSheet
import utils.Constants
import utils.TextHelper
import utils.ToastHelper
import viewmodel.NotificationViewModel

class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding

    private val notificationViewModel by viewModels<NotificationViewModel>()

    private var scrollBehavior: HideBottomViewOnScrollBehavior<FrameLayout?>? = null

    private var adapter: TodoAdapter? = null

    var scrollYPos = 0
        private set

    companion object {
        @JvmStatic
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            val dpShadow = resources.getDimension(R.dimen.toolbar_shadow)
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
            GlobalMenuDialog(context).apply {
                show()

                onClickCategories = {
                    val fragment: Fragment = CategoriesFragment.newInstance().apply {
                        enterTransition = Slide(Gravity.BOTTOM)
                    }

                    parentFragmentManager.beginTransaction().apply {
                        add(R.id.mainContainer, fragment, Constants.FragmentTag.CATEGORY)
                        addToBackStack(Constants.FragmentTag.CATEGORY)
                    }.commit()

                    dismiss()
                }

                onClickDeleteAll = deleteAll@{
                    dismiss()

                    if (todoViewModel.todosIsEmpty()) {
                        ToastHelper.get().toast(getString(R.string.todos_is_empty))
                        return@deleteAll //empty return
                    }

                    DeleteDialog(context).apply {
                        show()

                        setTitle(getString(R.string.delete_all_todos))

                        setMessage(
                            getString(
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

                onClickDeleteAllDone = deleteAllDone@{
                    dismiss()

                    if (todoViewModel.todosIsEmpty()) {
                        ToastHelper.get().toast(getString(R.string.todos_is_empty))
                        return@deleteAllDone //empty return
                    }

                    if (todoViewModel.todosDoneIsEmpty()) {
                        ToastHelper.get().toast(getString(R.string.todos_done_is_empty))
                        return@deleteAllDone //empty return
                    }

                    DeleteDialog(context).apply {
                        show()

                        setTitle(getString(R.string.delete_all_done_todos))

                        setMessage(
                            getString(
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


        binding.aImgFilter.setOnClickListener {
            FilterBottomSheet.newInstance(
                todoViewModel.currentFilter,
                ArrayList(categoryViewModel.allCategories)
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

            { todoMenu: Todo, _: View? ->
                MoreDialog(context).apply {
                    show()

                    setWithDetail(true)

                    onClickEdit = {
                        dismiss()
                        val fragment: Fragment = AddEditTodoFragment.newInstance(todoMenu)
                        fragment.enterTransition = Slide(Gravity.BOTTOM)
                        val transaction = parentFragmentManager.beginTransaction()
                        transaction.add(
                            R.id.mainContainer,
                            fragment,
                            Constants.FragmentTag.ADD_EDIT_TODO
                        )
                        transaction.addToBackStack(Constants.FragmentTag.ADD_EDIT_TODO)
                        transaction.commit()
                    }

                    onClickDetail = {
                        dismiss()
                        val fragment: Fragment = TodoDetailFragment.newInstance(todoMenu).apply {
                            enterTransition = Slide(Gravity.BOTTOM)
                        }

                        /*MaterialContainerTransform t = new MaterialContainerTransform();
                        t.setDuration(400);
                        t.setScrimColor(Color.TRANSPARENT);
                        t.setPathMotion(new MaterialArcMotion());
                        t.setStartElevation(0);
                        t.setEndElevation(0);

                        fragment.setSharedElementEnterTransition(t);
                        fragment.setSharedElementReturnTransition(t);

                        fragment.setAllowEnterTransitionOverlap(true);*/
                        parentFragmentManager.beginTransaction().apply {
                            add(R.id.mainContainer, fragment, Constants.FragmentTag.TODO_DETAIL)

                            addToBackStack(Constants.FragmentTag.TODO_DETAIL)
                        }.commit()

                        /*transaction.setReorderingAllowed(true);
                         scheduleStartPostponedTransition(sharedElement);
                         transaction.addSharedElement(sharedElement, sharedElement.getTransitionName());
                         transaction.hide(this);*/

                    }

                    onClickDelete = {
                        dismiss()

                        DeleteDialog(context).apply {
                            show()

                            setTitle(getString(R.string.delete_todo))

                            var todoTitle = todoMenu.title
                            if (todoTitle != null && todoTitle.trim().length > 30)
                                todoTitle =
                                    todoTitle.substring(0, 30).trim() + getString(R.string.ellipsis)

                            setMessage(getString(R.string.delete_todo_message, todoTitle))

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

                    binding.txtEmpty.text = getString(R.string.todos_empty)
                    binding.txtNotesEmpty.text =
                        TextHelper.fromHtml(getString(R.string.todos_empty_notes))

                    params.verticalBias = 0.2f
                } else {
                    binding.filterIndicator.visibility = View.VISIBLE
                    binding.cLytGuide.visibility = View.GONE

                    binding.txtEmpty.text = getString(R.string.empty_todos_with_filter)
                    binding.txtNotesEmpty.text = getString(R.string.empty_todos_with_filter_notes)

                    params.verticalBias = 0.35f
                }

                box.layoutParams = params
            } else {
                binding.filterIndicator.visibility =
                    if (todoViewModel.currentFilter != null && !todoViewModel.currentFilter.filterIsEmpty())
                        View.VISIBLE
                    else
                        View.GONE

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