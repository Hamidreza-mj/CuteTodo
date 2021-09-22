package ui.fragment;

import android.transition.Slide;
import android.view.Gravity;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import utils.KeyboardInputHelper;


public class BaseFragment extends Fragment {

    protected void hideKeyboard() {
        if (getContext() == null)
            return;
        KeyboardInputHelper.getKeyboardInput().hideKeyboard(getContext());
    }

    protected void showKeyboard() {
        if (getContext() == null)
            return;
        KeyboardInputHelper.getKeyboardInput().showKeyboard(getContext());
    }

    protected void back() {
        hideKeyboard();
        getParentFragmentManager().popBackStack();
    }

    protected void back(String fragmentTagName) {
        hideKeyboard();
        getParentFragmentManager().popBackStack(fragmentTagName, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    protected void back(@IdRes int fragmentID) {
        hideKeyboard();
        getParentFragmentManager().popBackStack(fragmentID, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    protected FragmentTransaction transit(FragmentTransaction transaction, int transitionType) {
        return transaction.setTransition(transitionType);
    }

    protected FragmentTransaction transit(FragmentTransaction transaction) {
        return transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    }

}
