package ui.activity

import android.animation.Animator
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.NestedScrollView
import hlv.cute.todo.R
import hlv.cute.todo.databinding.ActivityShowNotificationBinding
import model.Notification
import model.Priority
import utils.ToastHelper
import viewmodel.ShowNotificationViewModel

class ShowNotificationActivity : BaseViewBindingActivity<ActivityShowNotificationBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityShowNotificationBinding
        get() = ActivityShowNotificationBinding::inflate

    private val viewModel by viewModels<ShowNotificationViewModel>()

    override fun initiate() {
        initViewModel()
        initViews()
        handleObserver()
    }

    private fun initViewModel() {
        viewModel.setIntent(intent)
    }

    private fun initViews() {
        binding.aImgBack.setOnClickListener { close() }

        binding.mBtnClose.setOnClickListener { close() }

        binding.mBtnDone.setOnClickListener {
            binding.mBtnDone.isEnabled = false

            viewModel.done()

            ToastHelper.get()
                .successToast(provideResource.getString(R.string.todo_done_successfully_simple))

            binding.confetti.visibility = View.VISIBLE
            binding.confetti.playAnimation()
            binding.confetti.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    close()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }

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
                        .translationZ(dpShadow)
                        .setStartDelay(0)
                        .setDuration(90).start()
                }
            }
        })
    }

    private fun handleObserver() {
        viewModel.closeLive.observe(this) { mustClose: Boolean ->
            if (mustClose) close()
        }

        viewModel.runMainLive.observe(this) { runMain: Boolean ->
            if (runMain)
                runActivity(MainActivity::class.java, true)
        }

        viewModel.notificationLive.observe(this) { notif: Notification? ->
            if (notif == null) { //when manual close and open with home
                runActivity(MainActivity::class.java, true)
                return@observe
            }

            //set shown true in startup get all is shown and delete it
            viewModel.setShown()
            binding.txtTodoTitle.text = notif.content

            when (notif.priority) {
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

            binding.lytDate.visibility = viewModel.lytDateVisibility
            binding.lytCategory.visibility = viewModel.lytCategoryVisibility

            if (viewModel.hasCategory())
                binding.txtCategory.text = notif.category

            if (viewModel.hasArriveDate()) {
                binding.txtDate.text = viewModel.dateReminder
                binding.txtClock.text = viewModel.clockReminder
            }
        }
    }

    private fun close() {
        viewModel.deleteNotification()
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        close()
    }

    override fun onDestroy() {
        super.onDestroy()
        close()
    }
}