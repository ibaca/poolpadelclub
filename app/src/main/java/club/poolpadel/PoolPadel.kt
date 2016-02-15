package club.poolpadel

import android.app.Application
import com.fasterxml.jackson.databind.ObjectMapper
import com.firebase.client.Config
import com.firebase.client.Firebase

class PoolPadel : Application() {
    val firebase: Firebase by lazy { Firebase(resources.getString(R.string.firebase_url)) }

    override fun onCreate() {
        super.onCreate()
        Firebase.setAndroidContext(this)
        Firebase.getDefaultConfig().isPersistenceEnabled = true
    }
}
