package ui.fragment.sheet

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import hlv.cute.todo.App
import utils.AppResourcesProvider

open class BaseBottomSheet : BottomSheetDialogFragment() {

    protected val provideResource = AppResourcesProvider(App.get()?.appContext!!)

}