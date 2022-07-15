package model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class Search : Serializable {
    var term: String? = null
    var searchMode: SearchMode = SearchMode.TODO
    var categoryId = 0

    enum class SearchMode {
        TODO, CATEGORY, BOTH
    }
}