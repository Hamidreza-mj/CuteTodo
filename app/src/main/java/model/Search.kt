package model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Search(
    var term: String? = null,
    var searchMode: SearchMode = SearchMode.TODO,
    var categoryId: Int? = null,

    ) : Parcelable {

    @Parcelize
    enum class SearchMode : Parcelable {
        TODO, CATEGORY, BOTH
    }
}