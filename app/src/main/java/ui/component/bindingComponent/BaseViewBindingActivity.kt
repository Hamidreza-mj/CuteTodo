package ui.component.bindingComponent

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import ui.activity.BaseActivity

abstract class BaseViewBindingActivity<VB : ViewBinding> : BaseActivity() {

    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    override fun onCreate(savedInstanceState: Bundle?) {
        beforeOnCreate()
        super.onCreate(savedInstanceState)
        _binding = bindingInflater.invoke(layoutInflater)
        setContentView(requireNotNull(_binding).root)
        initiate()
    }

    abstract fun initiate()

    open fun beforeOnCreate() {}

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}