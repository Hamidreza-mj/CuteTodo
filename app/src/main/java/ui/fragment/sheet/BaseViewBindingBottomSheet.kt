package ui.fragment.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hlv.cute.todo.App
import utils.AppResourcesProvider

abstract class BaseViewBindingBottomSheet<VB : ViewBinding> : BottomSheetDialogFragment() {

    private var _binding: ViewBinding? = null

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    protected val provideResource = AppResourcesProvider(App.get()?.appContext!!)

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initiate()
    }

    abstract fun initiate()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}