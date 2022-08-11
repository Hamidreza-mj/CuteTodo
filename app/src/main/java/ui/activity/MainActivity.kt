package ui.activity

import android.content.Intent
import android.os.Build
import android.transition.Fade
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.yandex.metrica.push.YandexMetricaPush
import hlv.cute.todo.R
import hlv.cute.todo.databinding.ActivityMainBinding
import ui.component.bindingComponent.BaseViewBindingActivity
import ui.fragment.AddEditTodoFragment
import ui.fragment.CategoriesFragment
import ui.fragment.HomeFragment
import ui.fragment.ShowNotificationFragment
import utils.Constants
import viewmodel.MainViewModel

class MainActivity : BaseViewBindingActivity<ActivityMainBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    private val viewModel by viewModels<MainViewModel>()

    override fun initiate() {
        viewModel.handleOpeningType(intent)

        when (viewModel.openingType) {
            MainViewModel.OpeningType.SCHEDULED_NOTIF -> {
                openShowNotification()
            }

            MainViewModel.OpeningType.SHARE_MENU -> {
                configIntentTodo(intent.getStringExtra(Intent.EXTRA_TEXT))
            }

            MainViewModel.OpeningType.SHORTCUT_MENU -> {
                openAddEditTodo("")
            }

            MainViewModel.OpeningType.POPUP_TEXT_SELECTION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    configIntentTodo(intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT))
            }

            MainViewModel.OpeningType.PUSH_NOTIF -> {
                val payload =
                    intent.getStringExtra(YandexMetricaPush.EXTRA_PAYLOAD) //TODO: yandex open with ntification

                Log.e(Constants.Tags.DEBUG, "onReceive: $payload")

                initViews()
            }

            MainViewModel.OpeningType.NORMAL -> {
                initViews()
            }
        }
    }

    private fun openShowNotification() {
        val fragment: Fragment = ShowNotificationFragment.newInstance(intent)
        fragment.enterTransition = Fade()

        supportFragmentManager.beginTransaction().apply {
            add(R.id.mainContainer, fragment, Constants.FragmentTag.SHOW_NOTIF)
            addToBackStack(Constants.FragmentTag.SHOW_NOTIF)
        }.commit()
    }

    private fun configIntentTodo(intentString: String?) {
        if (intentString != null) {
            openAddEditTodo(intentString)
            return
        }

        finish()
    }

    private fun openAddEditTodo(todoTitle: String) {
        val fragment: Fragment = AddEditTodoFragment.newInstanceShare(todoTitle)
        fragment.enterTransition = Slide(Gravity.BOTTOM)

        supportFragmentManager.beginTransaction().apply {
            add(R.id.mainContainer, fragment, Constants.FragmentTag.ADD_EDIT_TODO)
            addToBackStack(Constants.FragmentTag.ADD_EDIT_TODO)
        }.commit()
    }

    private fun initViews() {
        val homeFragment: Fragment = HomeFragment.newInstance()

        supportFragmentManager.beginTransaction().apply {
            add(R.id.mainContainer, homeFragment, Constants.FragmentTag.HOME)
        }.commit()
    }

    override fun onBackPressed() {
        val backstackCount = supportFragmentManager.backStackEntryCount
        val fragment = supportFragmentManager.findFragmentById(R.id.mainContainer)

        if (fragment is HomeFragment) {
            val scrollY = fragment.scrollYPos

            if (fragment.isVisible() && scrollY > 0 && backstackCount == 0) {
                fragment.goToTop(800)
                return
            }

        } else if (fragment is CategoriesFragment) {
            val scrollY = fragment.scrollYPos
            if (fragment.isVisible() && scrollY > 0) {
                fragment.goToTop(800)
                return
            }

        } else if (fragment is AddEditTodoFragment) {
            val addEditTodoViewModel = fragment.viewModel

            if (addEditTodoViewModel.isShareMode) {
                supportFragmentManager.popBackStack()
                val homeFragment: Fragment = HomeFragment.newInstance()

                supportFragmentManager.beginTransaction().apply {
                    add(R.id.mainContainer, homeFragment, Constants.FragmentTag.HOME)
                }.commit()

                return
            }

        }

        super.onBackPressed()
    }
}