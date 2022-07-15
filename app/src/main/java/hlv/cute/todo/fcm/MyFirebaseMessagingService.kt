package hlv.cute.todo.fcm

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import utils.Constants

class MyFirebaseMessagingService : FirebaseMessagingService() {
    @SuppressLint("LongLogTag")
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Log.d(Constants.Tags.FCM, "Refreshed token: $newToken")


        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        //sendRegistrationToServer(token);
    }
}