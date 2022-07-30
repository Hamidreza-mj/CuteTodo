package ui.fragment

import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import hlv.cute.todo.R
import hlv.cute.todo.databinding.FragmentTodoDetailBinding
import model.Priority
import model.Todo
import ui.component.bindingComponent.BaseViewBindingFragment
import ui.dialog.DeleteDialog
import utils.Constants
import viewmodel.NotificationViewModel
import viewmodel.TodoDetailViewModel
import java.text.MessageFormat

@AndroidEntryPoint
class TodoDetailFragment : BaseViewBindingFragment<FragmentTodoDetailBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTodoDetailBinding
        get() = FragmentTodoDetailBinding::inflate

    val viewModel by viewModels<TodoDetailViewModel>()
    private val notificationViewModel by viewModels<NotificationViewModel>()

    companion object {

        private const val TODO_DETAIL_ARGS = "todo-detail-args"

        @JvmStatic
        fun newInstance(todo: Todo?): TodoDetailFragment {
            val fragment = TodoDetailFragment()

            val bundle = bundleOf(
                TODO_DETAIL_ARGS to todo
            )

            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null && arguments!!.isEmpty.not()) {
            val todo: Todo? = arguments!!.getParcelable(TODO_DETAIL_ARGS)
            viewModel.todo = todo
        } else {
            back()
        }
    }

    override fun initiate() {
        handleViews()
        handleActions()
        handleObserver()
    }

    private fun handleViews() {
        binding.aImgBack.setOnClickListener { back() }

        handleShadowScroll()
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
                        .translationZ(dpShadow).setStartDelay(0)
                        .setDuration(90)
                        .start()
                }
            }
        })
    }

    private fun handleActions() {
        binding.aImgDelete.setOnClickListener {
            DeleteDialog(activity).apply {
                show()

                setTitle(provideResource.getString(R.string.delete_todo))


                var todoTitle = viewModel.todo?.title ?: ""

                if (todoTitle.trim().length > 30)
                    todoTitle = todoTitle.substring(0, 30)
                        .trim() + provideResource.getString(R.string.ellipsis)

                setMessage(provideResource.getString(R.string.delete_todo_message, todoTitle))

                onClickDelete = {
                    if (viewModel.hasArriveDate())
                        notificationViewModel.cancelAlarm(viewModel.todo)

                    todoViewModel.deleteTodo(viewModel.todo)
                    todoViewModel.fetch() //need to update todos if categories was deleted
                    searchViewModel.fetch()

                    if (todoViewModel.todosIsEmpty())
                        todoViewModel.goToTop()

                    dismiss()
                    back()
                }
            }

        }

        binding.aImgEdit.setOnClickListener {
            val fragment: Fragment = AddEditTodoFragment.newInstance(viewModel.todo).apply {
                enterTransition = Slide(Gravity.BOTTOM)
            }

            parentFragmentManager.beginTransaction().apply {
                add(R.id.mainContainer, fragment, Constants.FragmentTag.ADD_EDIT_TODO)

                addToBackStack(Constants.FragmentTag.ADD_EDIT_TODO)
            }.commit()

        }

        binding.aImgShare.setOnClickListener {
            if (activity == null)
                return@setOnClickListener

            val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, viewModel.shareContent())
            }

            activity?.startActivity(
                Intent.createChooser(sharingIntent, provideResource.getString(R.string.share_using))
            )
        }

        binding.mBtnClose.setOnClickListener { back() }
    }

    private fun handleObserver() {
        viewModel.todoLiveDate.observe(viewLifecycleOwner) { todo: Todo? ->
            if (todo != null) {
                //binding.getRoot().setTransitionName("t-" + todo.getId());
                binding.txtTodoTitle.text = todo.title

                when (todo.priority) {
                    Priority.LOW -> {
                        binding.txtLowPriority.visibility = View.VISIBLE
                        binding.txtNormalPriority.visibility = View.GONE
                        binding.txtHighPriority.visibility = View.GONE
                    }

                    Priority.NORMAL -> {
                        binding.txtLowPriority.visibility = View.GONE
                        binding.txtNormalPriority.visibility = View.VISIBLE
                        binding.txtHighPriority.visibility = View.GONE
                    }

                    Priority.HIGH -> {
                        binding.txtLowPriority.visibility = View.GONE
                        binding.txtNormalPriority.visibility = View.GONE
                        binding.txtHighPriority.visibility = View.VISIBLE
                    }

                    else -> {
                        binding.txtLowPriority.visibility = View.VISIBLE
                        binding.txtNormalPriority.visibility = View.GONE
                        binding.txtHighPriority.visibility = View.GONE
                    }
                }

                binding.txtDone.text = viewModel.doneText
                binding.imgDone.setImageResource(viewModel.imgDoneResource)

                binding.lytDate.visibility = viewModel.getLytDateVisibility()
                binding.lytCategory.visibility = viewModel.getLytCategoryVisibility()
                binding.txtCreatedAt.visibility = viewModel.getCreatedAtVisibility()
                binding.txtUpdatedAt.visibility = viewModel.getUpdatedAtVisibility()

                if (viewModel.hasCategory())
                    binding.txtCategory.text = todo.category

                if (viewModel.hasArriveDate()) {
                    binding.txtDate.text = viewModel.getDateReminder()
                    binding.txtClock.text = viewModel.getClockReminder()
                }

                if (viewModel.hasCreatedAt()) {
                    binding.txtCreatedAt.text = MessageFormat.format(
                        "{0} {1}",
                        provideResource.getString(R.string.todo_created_at),
                        viewModel.completeCreatedAt
                    )
                }

                if (viewModel.hasUpdatedAt()) {
                    binding.txtUpdatedAt.text = MessageFormat.format(
                        "{0} {1}",
                        provideResource.getString(R.string.todo_updated_at),
                        viewModel.completeUpdatedAt
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchOnResume()
    }
}