package utils

class Constants {

    object Names {
        const val DB_NAME = "TodoDB"
    }

    object Keys {
        const val NOTIF_ID_KEY = "notif-id"
        const val NOTIF_CONTENT_KEY = "notif-content"
        const val NOTIF_ID_DETAIL = "notif-id-detail"
    }

    object Tags {
        const val ALARM_TAG = "=== AlarmReceived ==="
        const val SUCCESS_NOTIF_ALARM = "=== Notif Successed ==="
        const val DEBUG = "=== DEBUG CUTE ==="
        const val FCM = "FCM-SERVICE"
    }

    object FragmentTag {
        const val HOME = "home-fragment"
        const val CATEGORY = "category-fragment"
        const val ADD_EDIT_TODO = "add-edit-todo-fragment"
        const val ADD_EDIT_CATEGORY = "add-edit-category-fragment"
        const val SEARCH = "search-fragment"
        const val TODO_DETAIL = "todo-detail-fragment"
        const val SHOW_NOTIF = "show-notif-fragment"
    }
}