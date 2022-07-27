package model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Search(
    var term: String? = null,
    var searchMode: SearchMode = SearchMode.TODO,
    var categoryId: Int = 0,

    ) : Parcelable {

    enum class SearchMode {
        TODO, CATEGORY, BOTH
    }
}