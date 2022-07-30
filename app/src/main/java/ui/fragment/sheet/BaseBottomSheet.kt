package ui.fragment.sheet

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import utils.ResourceProvider
import javax.inject.Inject

@AndroidEntryPoint
open class BaseBottomSheet : BottomSheetDialogFragment() {

    @Inject
    protected lateinit var provideResource: ResourceProvider

}