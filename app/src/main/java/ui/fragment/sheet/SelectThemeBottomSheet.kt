package ui.fragment.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.os.bundleOf
import hlv.cute.todo.R
import hlv.cute.todo.databinding.SheetSelectThemeBinding
import ui.component.bindingComponent.BaseViewBindingBottomSheet
import ui.util.AppThemeHandler
import utils.KeyboardUtil

class SelectThemeBottomSheet : BaseViewBindingBottomSheet<SheetSelectThemeBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> SheetSelectThemeBinding
        get() = SheetSelectThemeBinding::inflate

    private var themeType: AppThemeHandler.ThemeType = AppThemeHandler.ThemeType.SystemDefault
    lateinit var onCheckChanged: (themeType: AppThemeHandler.ThemeType) -> Unit

    companion object {
        private const val THEME_TYPE_ARGS = "theme-type-args"

        @JvmStatic
        fun newInstance(themeType: AppThemeHandler.ThemeType): SelectThemeBottomSheet {
            val bottomSheet = SelectThemeBottomSheet()

            val args = bundleOf(THEME_TYPE_ARGS to themeType)
            bottomSheet.arguments = args

            return bottomSheet
        }
    }


    override fun initiate() {
        initData()
        handleAction()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null && requireArguments().isEmpty.not())
            themeType = requireArguments().getParcelable(THEME_TYPE_ARGS)
                ?: AppThemeHandler.ThemeType.SystemDefault

        activity?.let {
            KeyboardUtil.hideKeyboard(it)
        }
    }

    private fun initData() {
        when (themeType) {
            is AppThemeHandler.ThemeType.SystemDefault -> binding.radioBtnSysDef.isChecked = true

            is AppThemeHandler.ThemeType.Light -> binding.radioBtnDay.isChecked = true

            is AppThemeHandler.ThemeType.Dark -> binding.radioBtnNight.isChecked = true
        }
    }

    private fun handleAction() {
        binding.aImgClose.setOnClickListener { dismiss() }

        binding.radioGP.setOnCheckedChangeListener { _: RadioGroup?, id: Int ->
            themeType = when (id) {
                R.id.radioBtnSysDef -> AppThemeHandler.ThemeType.SystemDefault
                R.id.radioBtnDay -> AppThemeHandler.ThemeType.Light
                R.id.radioBtnNight -> AppThemeHandler.ThemeType.Dark
                else -> AppThemeHandler.ThemeType.SystemDefault
            }

            onCheckChanged(themeType)
        }
    }

}