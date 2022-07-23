package ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import com.yandex.metrica.push.YandexMetricaPush
import hlv.cute.todo.R
import hlv.cute.todo.databinding.ActivityMainBinding
import ui.fragment.AddEditTodoFragment
import ui.fragment.CategoriesFragment
import ui.fragment.HomeFragment
import utils.Constants

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val externalIntent = intent
        val action = externalIntent.action
        val type = externalIntent.type

        if (Intent.ACTION_SEND == action) { //share menu
            if (type == "text/plain") {
                configIntentTodo(externalIntent.getStringExtra(Intent.EXTRA_TEXT))
                return
            }

        } else if ("cute.todo.from.shortcut.add" == action) { //shortcut menu
            openAddEditTodo("")
            return

        } else if (Intent.ACTION_PROCESS_TEXT == action) { //pop up text selection
            if (type == "text/plain") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    configIntentTodo(
                        externalIntent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)
                    )

                    return
                }
            }
        } else if (YandexMetricaPush.OPEN_DEFAULT_ACTIVITY_ACTION == action) {
            val payload =
                externalIntent.getStringExtra(YandexMetricaPush.EXTRA_PAYLOAD) //TODO: yandex open with ntification

            Log.e(Constants.Tags.DEBUG, "onReceive: $payload")
        }

        initViews()
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