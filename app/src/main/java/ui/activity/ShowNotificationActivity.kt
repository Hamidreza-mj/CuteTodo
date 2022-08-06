package ui.activity

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.NestedScrollView
import com.aghajari.rlottie.AXrLottie
import com.aghajari.rlottie.AXrLottieDrawable
import dagger.hilt.android.AndroidEntryPoint
import hlv.cute.todo.R
import hlv.cute.todo.databinding.ActivityShowNotificationBinding
import model.Notification
import model.Priority
import ui.component.UiToolkit
import ui.component.bindingComponent.BaseViewBindingActivity
import utils.ToastUtil
import viewmodel.ShowNotificationViewModel
import javax.inject.Inject


@AndroidEntryPoint
class ShowNotificationActivity : BaseViewBindingActivity<ActivityShowNotificationBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityShowNotificationBinding
        get() = ActivityShowNotificationBinding::inflate

    private val viewModel by viewModels<ShowNotificationViewModel>()

    @Inject
    lateinit var toastUtil: ToastUtil

    @Inject
    lateinit var uiToolkit: UiToolkit

    override fun initiate() {
        initViewModel()
        initViews()
        handleObserver()
    }

    private fun initViewModel() {
        viewModel.setIntent(intent)
    }

    private fun initViews() {
        AXrLottie.init(this)

        binding.aImgBack.setOnClickListener { close() }

        binding.mBtnClose.setOnClickListener { close() }

        binding.mBtnDone.setOnClickListener {
            binding.mBtnDone.isEnabled = false

            viewModel.done()

            toastUtil.successToast(provideResource.getString(R.string.todo_done_successfully_simple))

            binding.confetti.visibility = View.VISIBLE

            val width = uiToolkit.displayWidth
            val height = uiToolkit.displayHeight

            binding.confetti.lottieDrawable =
                AXrLottieDrawable
                    .fromAssets(this, "congratulations_confetti_lottie.json") //from assets
                    .setSize(width, height)
                    .setOnLottieLoaderListener(object : AXrLottieDrawable.OnLottieLoaderListener {
                        override fun onLoaded(drawable: AXrLottieDrawable?) {
                            Handler(Looper.myLooper()!!).postDelayed({ close() }, 3_000L)
                        }

                        override fun onError(drawable: AXrLottieDrawable?, error: Throwable?) {
                        }
                    })
                    .build()

            binding.confetti.playAnimation()
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

        viewModel.notificationLiveData.observe(this) { notif: Notification? ->
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

            binding.lytDate.visibility = viewModel.getLytDateVisibility()
            binding.lytCategory.visibility = viewModel.getLytCategoryVisibility()

            if (viewModel.hasCategory())
                binding.txtCategory.text = notif.category

            if (viewModel.hasArriveDate()) {
                binding.txtDate.text = viewModel.getDateReminder()
                binding.txtClock.text = viewModel.getClockReminder()
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