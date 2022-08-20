package ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import com.aghajari.rlottie.AXrLottie
import com.aghajari.rlottie.AXrLottieDrawable
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ActivityContext
import hlv.cute.todo.R
import hlv.cute.todo.databinding.FragmentShowNotificationBinding
import model.Notification
import model.Priority
import ui.activity.MainActivity
import ui.component.UiToolkit
import ui.component.bindingComponent.BaseViewBindingFragment
import utils.ToastUtil
import utils.collectLatestLifecycleFlow
import viewmodel.ShowNotificationViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ShowNotificationFragment : BaseViewBindingFragment<FragmentShowNotificationBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentShowNotificationBinding
        get() = FragmentShowNotificationBinding::inflate

    private val viewModel by viewModels<ShowNotificationViewModel>()

    @Inject
    lateinit var toastUtil: ToastUtil

    @Inject
    lateinit var uiToolkit: UiToolkit

    @Inject
    @ActivityContext
    lateinit var iContext: Context

    companion object {
        private const val INTENT_ARGS = "intent-args"

        @JvmStatic
        fun newInstance(intent: Intent): ShowNotificationFragment {
            val fragment = ShowNotificationFragment()

            val args = bundleOf(
                INTENT_ARGS to intent
            )

            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null && arguments!!.isEmpty.not()) {
            val intent: Intent? = arguments!!.getParcelable(INTENT_ARGS)

            intent?.let {
                viewModel.setIntent(it)
            } ?: run {
                close()
            }
        }
    }

    override fun initiate() {
        initViews()
        handleObserver()

        doOnBackPressedFragment {
            close()
        }
    }

    private fun initViews() {
        AXrLottie.init(iContext)

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
                    .fromAssets(iContext, "congratulations_confetti_lottie.json") //from assets
                    .setSize(width, height)
                    .setOnLottieLoaderListener(object : AXrLottieDrawable.OnLottieLoaderListener {
                        override fun onLoaded(drawable: AXrLottieDrawable?) {
                            binding.root.postOnAnimationDelayed({ close() }, 3_000L)
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
        collectLatestLifecycleFlow(viewModel.closeFlow) {
            close()
        }

        collectLatestLifecycleFlow(viewModel.runMainFlow) {
            (activity as MainActivity).runActivity(MainActivity::class.java, true)
        }

        collectLatestLifecycleFlow(viewModel.notificationStateFlow) { notif: Notification? ->
            if (notif == null) { //when manual close and open with home
                (activity as MainActivity).runActivity(MainActivity::class.java, true)
                return@collectLatestLifecycleFlow
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
        activity?.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        close()
    }
}