package ui.fragment

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import ui.activity.MainActivity
import utils.KeyboardInputHelper
import viewmodel.CategoryViewModel
import viewmodel.SearchViewModel
import viewmodel.TodoViewModel

open class BaseFragment : Fragment() {

    protected val todoViewModel by activityViewModels<TodoViewModel>()
    protected val categoryViewModel by activityViewModels<CategoryViewModel>()
    protected val searchViewModel by activityViewModels<SearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideKeyboard()
    }

    private fun hideKeyboard() {
        context?.let {
            KeyboardInputHelper.getKeyboardInput().hideKeyboard(it)
        }
    }

    protected fun showKeyboard() {
        context?.let {
            KeyboardInputHelper.getKeyboardInput().showKeyboard(it)
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