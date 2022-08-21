package ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.Slide
import android.view.*
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ActivityContext
import hlv.cute.todo.R
import hlv.cute.todo.databinding.FragmentAddEditTodoBinding
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog
import ir.hamsaa.persiandatepicker.api.PersianPickerDate
import ir.hamsaa.persiandatepicker.api.PersianPickerListener
import kotlinx.coroutines.launch
import model.Category
import model.DateTime
import model.Priority
import model.Todo
import ui.component.bindingComponent.BaseViewBindingFragment
import ui.dialog.DropDownCategoriesDialog
import ui.dialog.ReminderGuideDialog
import ui.dialog.TimePickerSheetDialog
import ui.dialog.WarningDateDialog
import utils.Constants
import utils.ToastUtil
import utils.collectLatestLifecycleFlow
import utils.collectLifecycleFlow
import viewmodel.AddEditTodoViewModel
import viewmodel.NotificationViewModel
import java.text.MessageFormat
import javax.inject.Inject

@AndroidEntryPoint
class AddEditTodoFragment : BaseViewBindingFragment<FragmentAddEditTodoBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddEditTodoBinding
        get() = FragmentAddEditTodoBinding::inflate

    val viewModel by viewModels<AddEditTodoViewModel>()
    private val notificationViewModel by viewModels<NotificationViewModel>()

    @Inject
    lateinit var toastUtil: ToastUtil

    @Inject
    @ActivityContext
    lateinit var iContext: Context

    private var allCategories: ArrayList<Category>? = null

    companion object {
        private const val TODO_ARGS = "todo-args"
        private const val SHARE_MODE_ARGS = "share-mode-args"

        @JvmStatic
        fun newInstance(todo: Todo?): AddEditTodoFragment {
            val fragment = AddEditTodoFragment()

            val args = bundleOf(
                TODO_ARGS to todo
            )

            fragment.arguments = args

            return fragment
        }

        @JvmStatic
        fun newInstanceShare(title: String?): AddEditTodoFragment {
            val fragment = AddEditTodoFragment()

            val args = bundleOf(
                SHARE_MODE_ARGS to title
            )

            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null && arguments!!.isEmpty.not()) {

            val todo: Todo? = arguments!!.getParcelable(TODO_ARGS)

            if (todo != null) {
                //edit mode
                viewModel.isEditMode = true
                viewModel.isShareMode = false
                viewModel.todo = todo
            } else {
                //add mode (share or normal)
                viewModel.isEditMode = false
                val shareTitle = arguments!!.getString(SHARE_MODE_ARGS)

                if (shareTitle != null) {
                    viewModel.isShareMode = true
                    viewModel.shareTitle = shareTitle
                } else {
                    viewModel.isShareMode = false
                }

            }
        }
    }

    override fun initiate() {
        handleViews()
        initLogic()
        handleAction()
        handleObserver()
    }

    private fun handleViews() {
        binding.aImgBack.setOnClickListener {
            if (viewModel.isShareMode) {
                back()

                val homeFragment: Fragment = HomeFragment.newInstance()

                parentFragmentManager.beginTransaction().apply {
                    add(R.id.mainContainer, homeFragment, Constants.FragmentTag.HOME)
                }.commit()

                return@setOnClickListener
            }

            back()
        }

        binding.aImgReminderGuide.setOnClickListener {
            ReminderGuideDialog(iContext).apply {
                show()

                setTitle(provideResource.getString(R.string.set_reminder))

                setMessage(provideResource.getString(R.string.reminder_guide))
            }
        }

        handleScroll()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun handleScroll() {
        binding.inpEdtTitle.setOnTouchListener { v: View, event: MotionEvent ->
            // FIXME:
            if (binding.inpEdtTitle.hasFocus()) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_SCROLL) {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    return@setOnTouchListener true
                }
            }

            false
        }


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

                    //toolbar.setTranslationZ(0);
                } else if (scrollY > 0) {
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

    @SuppressLint("NonConstantResourceId")
    private fun initLogic() {
        binding.txtTitle.text = viewModel.titleFragment

        binding.inpEdtTitle.setText(viewModel.editTextTitle)

        binding.mBtnAdd.text = viewModel.buttonPrimaryText

        viewModel.firstInitDateTime()

        if (viewModel.isEditMode) {

            @IdRes val chipID: Int
            when (viewModel.todo?.priority) {
                Priority.LOW -> {
                    chipID = R.id.chipLow
                    viewModel.priority = Priority.LOW
                }

                Priority.NORMAL -> {
                    chipID = R.id.chipNormal
                    viewModel.priority = Priority.NORMAL
                }

                Priority.HIGH -> {
                    chipID = R.id.chipHigh
                    viewModel.priority = Priority.HIGH
                }

                else -> {
                    chipID = R.id.chipLow
                    viewModel.priority = Priority.LOW
                }
            }

            binding.chipGP.check(chipID)
            return
        }

        //set default priority
        binding.chipGP.check(R.id.chipLow)
        viewModel.priority = Priority.LOW
    }

    @SuppressLint("NonConstantResourceId")
    private fun handleAction() {
        binding.mCardCategory.setOnClickListener {
            DropDownCategoriesDialog(activity!!, allCategories).apply {
                show()

                onClickCategory = { category: Category? ->
                    dismiss()
                    viewModel.commitCategory(category)
                }

                onclickManage = {
                    viewModel.commitCategory(null)

                    val fragment = CategoriesFragment.newInstance().apply {
                        enterTransition = Slide(Gravity.BOTTOM)
                    }

                    parentFragmentManager.beginTransaction().apply {
                        add(R.id.mainContainer, fragment, Constants.FragmentTag.CATEGORY)

                        addToBackStack(Constants.FragmentTag.CATEGORY)
                    }.commit()

                    dismiss()
                }
            }
        }

        /*chipGP!!.setOnCheckedChangeListener { group: ChipGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.chipLow -> viewModel.setPriority(
                    Priority.LOW
                )
                R.id.chipNormal -> viewModel.setPriority(Priority.NORMAL)
                R.id.chipHigh -> viewModel.setPriority(Priority.HIGH)
                else -> viewModel.setPriority(Priority.LOW)
            }
        }*/

        binding.chipGP.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds[0]) {
                R.id.chipLow -> viewModel.priority = Priority.LOW

                R.id.chipNormal -> viewModel.priority = Priority.NORMAL

                R.id.chipHigh -> viewModel.priority = Priority.HIGH

                else -> viewModel.priority = Priority.LOW
            }
        }

        binding.inpEdtTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.isNotEmpty())
                    binding.inpTitle.error = null
            }
        })


        binding.mCardReminder.setOnClickListener {
            handlePickers(viewModel.oldDateTime?.date)
        }

        binding.aImgClear.setOnClickListener { viewModel.releaseAll() }

        binding.mBtnAdd.setOnClickListener {
            binding.inpTitle.error = null

            if (viewModel.isEditMode) {
                //edit mode
                val editedTodo = viewModel.editTodo(binding.inpEdtTitle.text.toString().trim())

                val res = todoViewModel.validateTodo(editedTodo)

                if (res == null) {
                    if (editedTodo.arriveDate != 0L)
                        notificationViewModel.setNotificationEditMode(editedTodo)
                    else  //if date not set or date cleared with imgClear
                        notificationViewModel.cancelAlarm(editedTodo)

                    todoViewModel.editTodo(editedTodo)

                    //searchViewModel.search()

                    updateDetail(editedTodo)

                    toastUtil.successToast(provideResource.getString(R.string.todo_edited_successfully_simple))

                    back()

                    return@setOnClickListener
                }

                binding.inpTitle.error = res

            } else {
                //add mode
                val newTodo = viewModel.addTodo(binding.inpEdtTitle.text.toString().trim())

                val res = todoViewModel.validateTodo(newTodo)

                if (res == null) {
                    todoViewModel.goToTop()

                    lifecycleScope.launch {
                        val insertedId = todoViewModel.addTodo(newTodo)

                        if (newTodo.arriveDate != 0L) {
                            newTodo.id = insertedId.toInt()
                            notificationViewModel.addNotification(newTodo)
                        }
                    }

                    toastUtil.successToast(provideResource.getString(R.string.todo_added_successfully_simple))

                    back()

                    if (viewModel.isShareMode) {
                        binding.mBtnAdd.isEnabled = false

                        val homeFragment: Fragment = HomeFragment.newInstance()

                        parentFragmentManager.beginTransaction().apply {
                            add(R.id.mainContainer, homeFragment, Constants.FragmentTag.HOME)
                        }.commit()
                    }


                    return@setOnClickListener
                }

                binding.inpTitle.error = res
            }
        }
    }

    private fun handlePickers(persianDate: PersianPickerDate?) {
        val picker = PersianDatePickerDialog(iContext)

        picker.setPositiveButtonString("مرحله بعد")
            .setNegativeButton("انصراف")
            .setTodayButton("تاریخ امروز")
            .setTodayButtonVisible(true)
            .setMinYear(1400)
            .setMaxYear(1440)
            .setTypeFace(Typeface.createFromAsset(iContext.assets, "font/vazir_rd_fd_medium.ttf"))
            .setTitleColor(provideResource.getColor(R.color.blue))
            .setAllButtonsTextSize(15)
            .setPickerBackgroundColor(provideResource.getColor(R.color.white))
            .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
            .setShowInBottomSheet(true)
            .setEnableBellView(false)
            .setNotificationNoteClick { }
            .setListener(object : PersianPickerListener {
                override fun onDateSelected(
                    persianPickerDate: PersianPickerDate,
                    isPassed: Boolean
                ) {
                    if (isPassed) {
                        WarningDateDialog(iContext).apply {
                            show()

                            setTitle(provideResource.getString(R.string.passedDateTitle))

                            setMessage(provideResource.getString(R.string.passedDateMessage))

                            setEditText(provideResource.getString(R.string.editDate))

                            continueClicked = {
                                dismiss()
                                picker.dismiss()
                                dateSelectedAction(persianPickerDate)
                            }
                        }

                        return
                    }

                    picker.dismiss()
                    dateSelectedAction(persianPickerDate)
                }

                override fun onDismissed() {}
            })

        //init default values
        val date = viewModel.setInitDateValue(persianDate)

        val year = date[0]
        val month = date[1]
        val day = date[2]

        picker.setInitDate(year, month, day)
        picker.show()
    }

    private fun dateSelectedAction(persianPickerDate: PersianPickerDate?) {
        if (persianPickerDate == null)
            return

        viewModel.configTempDateTime()

        viewModel.setDateTemp(persianPickerDate)

        TimePickerSheetDialog(iContext, true, viewModel.tempDateTime).apply timerPicker@{
            show()

            onClickApply = applyClick@{ pickedDateTime: DateTime? ->
                if (viewModel.todayTtimePassed(pickedDateTime)) {

                    WarningDateDialog(iContext).apply {
                        setTitle(provideResource.getString(R.string.passedDateTitle))

                        setMessage(provideResource.getString(R.string.passedTimeMessage))

                        setEditText(provideResource.getString(R.string.editTime))

                        continueClicked = {
                            dismiss()
                            this@timerPicker.dismiss()

                            viewModel.commitDateTime(pickedDateTime)
                            viewModel.commitOldDateTime(pickedDateTime)
                        }

                        show()
                    }

                    return@applyClick
                }

                viewModel.commitDateTime(pickedDateTime)
                viewModel.commitOldDateTime(pickedDateTime)

                dismiss()
            }

            onBackClick = { date: PersianPickerDate? ->
                handlePickers(date)
            }
        }
    }

    private fun handleObserver() {
        collectLatestLifecycleFlow(viewModel.dateTimeStateFlow) { changedDateTime: DateTime? ->

            if (changedDateTime?.date != null) {
                binding.txtDate.setTextColor(
                    provideResource.getColor(R.color.black)
                )

                binding.txtDate.text = MessageFormat.format(
                    "{0}\nساعت {1}",
                    changedDateTime.persianDate,
                    changedDateTime.clock
                )

                binding.aImgClear.visibility = View.VISIBLE
            } else {
                binding.txtDate.setTextColor(
                    provideResource.getColor(R.color.gray)
                )

                binding.txtDate.text = provideResource.getString(R.string.set_date_time)
                binding.aImgClear.visibility = View.GONE
            }
        }

        collectLatestLifecycleFlow(viewModel.categoryStateFlow) {
            binding.txtCategory.text = viewModel.categoryTitleText

            if (viewModel.categoryIsValid())
                binding.txtCategory.setTextColor(provideResource.getColor(R.color.black))
            else
                binding.txtCategory.setTextColor(provideResource.getColor(R.color.gray))
        }

        collectLifecycleFlow(categoryViewModel.categoriesFlow) { categories ->
            allCategories = categories as ArrayList<Category>?
        }
    }

    private fun updateDetail(todo: Todo) {
        val fragment =
            parentFragmentManager.findFragmentByTag(Constants.FragmentTag.TODO_DETAIL) as TodoDetailFragment?

        fragment?.let {
            it.viewModel.todo = todo
        }
    }

}