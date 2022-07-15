package model

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class Filter : Serializable {

    var isDone = false
    var isUndone = false
    var isScheduled = false
    var isToday = false

    val priorities: MutableList<Priority> = ArrayList()
    var categoryIds: List<Int>? = ArrayList()

    var isLow = false
        set(value) {
            if (value)
                priorities.add(Priority.LOW)

            field = value
        }

    var isNormal = false
        set(value) {
            if (value)
                priorities.add(Priority.NORMAL)

            field = value
        }

    var isHigh = false
        set(value) {
            if (value)
                priorities.add(Priority.HIGH)

            field = value
        }


    /**
     * add all priority when no priority selected
     *
     * @return list of priorities
     */
    fun addAllPriorities(): List<Priority> {
        priorities.add(Priority.LOW)
        priorities.add(Priority.NORMAL)
        priorities.add(Priority.HIGH)
        return priorities
    }

    fun needToFilterByCategory(): Boolean {
        return categoryIds != null && categoryIds!!.isNotEmpty()
    }

    fun filterIsEmpty(): Boolean {
        val emptyConditions = !isDone && !isUndone && !isScheduled && !isToday &&
                !isLow && !isNormal && !isHigh && categoryIds!!.isEmpty()

        val emptyWithNonPrioritiesSelected = emptyConditions && priorities.isEmpty()

        val emptyWithAllPrioritiesSelected = emptyConditions && priorities.size == 3

        return emptyWithNonPrioritiesSelected || emptyWithAllPrioritiesSelected
    }
}