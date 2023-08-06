package ui.activity

import android.content.Intent
import android.os.Build
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.yandex.metrica.push.YandexMetricaPush
import controller.OpenAppIntentController.OpeningType
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import hlv.cute.todo.R
import hlv.cute.todo.databinding.ActivityMainBinding
import ui.component.bindingComponent.BaseViewBindingActivity
import ui.fragment.AddEditTodoFragment
import ui.fragment.CategoriesFragment
import ui.fragment.HomeFragment
import ui.fragment.ShowNotificationFragment
import ui.util.AppThemeHandler
import utils.Constants
import viewmodel.MainViewModel

@AndroidEntryPoint
class MainActivity : BaseViewBindingActivity<ActivityMainBinding>() {

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    private val viewModel by viewModels<MainViewModel>()

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AppThemeHandlerFactory {
        fun getAppTheme(): AppThemeHandler
    }


    override fun beforeOnCreate() {
//        val factory = EntryPointAccessors.fromApplication(this, AppThemeHandlerFactory::class.java)
//        val themeHandler = factory.getAppTheme()


        /*lifecycleScope.launch {
            themeHandler.activity = this@MainActivity

            val currentTheme = themeHandler.getCurrentTheme(this@MainActivity)

            lifecycleScope.launch {
                themeHandler.applyNewTheme(currentTheme)
            }
        }*/
    }

    override fun initiate() {
        window.setBackgroundDrawable(null)
        viewModel.handleOpeningType(intent)

        when (viewModel.openingType) {
            OpeningType.SCHEDULED_NOTIF -> {
                openShowNotification()
            }

            OpeningType.SHARE_MENU -> {
                configIntentTodo(intent.getStringExtra(Intent.EXTRA_TEXT))
            }

            OpeningType.SHORTCUT_MENU -> {
                openAddEditTodo("")
            }

            OpeningType.POPUP_TEXT_SELECTION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    configIntentTodo(intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT))
            }

            OpeningType.PUSH_NOTIF -> {
                val payload =
                    intent.getStringExtra(YandexMetricaPush.EXTRA_PAYLOAD) //TODO: yandex open with ntification

                //Log.e(Constants.Tags.DEBUG, "onReceive: $payload")

                initViews()
            }

            OpeningType.NORMAL -> {
                initViews()
            }
        }
    }

    /* override fun onConfigurationChanged(newConfig: Configuration) {
         super.onConfigurationChanged(newConfig)
         updateViewsForNewTheme()


         val currentNightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
         when (currentNightMode) {
             Configuration.UI_MODE_NIGHT_NO -> {
                 //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                 themeHandler.applyNewTheme(AppThemeHandler.ThemeType.Light)
             }

             Configuration.UI_MODE_NIGHT_YES -> {
                 //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                 themeHandler.applyNewTheme(AppThemeHandler.ThemeType.Dark)
             }
         }
     }*/

    private fun updateViewsForNewTheme() {
        /*val fragment = supportFragmentManager.findFragmentById(R.id.mainContainer)
        fragment?.let {
            supportFragmentManager.beginTransaction().apply {
                detach(fragment)
            }.commit()

            supportFragmentManager.beginTransaction().apply {
                attach(fragment)
            }.commit()
        }*/
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