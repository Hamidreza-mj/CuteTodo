package viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import controller.OpenAppIntentController
import controller.OpenAppIntentController.OpeningType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val intentController: OpenAppIntentController
) : ViewModel() {

    var openingType: OpeningType = OpeningType.NORMAL

    fun handleOpeningType(intent: Intent) {
        openingType = intentController.getOpeningTypeWithIntent(intent)
    }

}