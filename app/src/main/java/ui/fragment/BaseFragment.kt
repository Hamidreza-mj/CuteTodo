package ui.fragment

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import utils.KeyboardUtil
import utils.ResourceProvider
import viewmodel.CategoryViewModel
import viewmodel.SearchViewModel
import viewmodel.TodoViewModel
import javax.inject.Inject

@AndroidEntryPoint
open class BaseFragment : Fragment() {

    protected val todoViewModel by activityViewModels<TodoViewModel>()
    protected val categoryViewModel by activityViewModels<CategoryViewModel>()
    protected val searchViewModel by activityViewModels<SearchViewModel>()

    @Inject
    protected lateinit var provideResource: ResourceProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideKeyboard()
    }

    private fun hideKeyboard() {
        activity?.let {
            KeyboardUtil.hideKeyboard(it)
        }
    }

    protected fun back() {
        hideKeyboard()
        parentFragmentManager.popBackStack()
    }

    protected fun back(fragmentTagName: String?) {
        hideKeyboard()
        parentFragmentManager.popBackStack(
            fragmentTagName,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    protected fun back(@IdRes fragmentID: Int) {
        hideKeyboard()
        parentFragmentManager.popBackStack(fragmentID, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    protected fun transit(
        transaction: FragmentTransaction,
        transitionType: Int
    ): FragmentTransaction {
        return transaction.setTransition(transitionType)
    }

    protected fun transit(transaction: FragmentTransaction): FragmentTransaction {
        return transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
    }
}