package club.poolpadel

import android.app.Application
import com.firebase.client.Firebase

class PoolPadel : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.setAndroidContext(this)
    }
}
