package ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import hlv.cute.todo.App
import utils.AppResourcesProvider

abstract class BaseViewBindingActivity<VB : ViewBinding> : BaseActivity() {

    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater) -> VB

    protected val provideResource = AppResourcesProvider(App.get()?.appContext!!)

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater.invoke(layoutInflater)
        setContentView(requireNotNull(_binding).root)
        initiate()
    }

    abstract fun initiate()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}