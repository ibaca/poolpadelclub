package club.poolpadel

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.firebase.client.*
import com.firebase.ui.auth.core.AuthProviderType
import com.firebase.ui.auth.core.FirebaseLoginBaseActivity
import com.firebase.ui.auth.core.FirebaseLoginError
import io.fabric.sdk.android.Fabric

abstract class BaseActivity : FirebaseLoginBaseActivity() {
    private val TAG = BaseActivity::class.java.simpleName
    val app: PoolPadel get() = application as PoolPadel
    val toolbar: Toolbar by bindView(R.id.toolbar)
    val anonymous: Boolean get() = auth == null

    override fun getFirebaseRef(): Firebase = app.firebase

    override fun onFirebaseLoginUserError(p0: FirebaseLoginError?) {
        Toast.makeText(this, "Login user error: $p0", Toast.LENGTH_LONG).show()
    }

    override fun onFirebaseLoginProviderError(p0: FirebaseLoginError?) {
        Toast.makeText(this, "Login provider error: $p0", Toast.LENGTH_LONG).show()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics());
    }

    override fun onStart() {
        super.onStart()
        // setEnabledAuthProvider(AuthProviderType.PASSWORD)
        setEnabledAuthProvider(AuthProviderType.GOOGLE)
    }

    override fun onFirebaseLoggedIn(authData: AuthData) {
        super.onFirebaseLoggedIn(authData)

        Crashlytics.setUserEmail(authData.email)
        Crashlytics.setUserIdentifier(authData.uid)
        Crashlytics.setUserName(authData.displayName)

        val userRef = firebaseRef.child("users").child(authData.uid)
        val peopleRef = firebaseRef.child("people").child(authData.uid)
        peopleRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(peopleSnap: DataSnapshot) {
                if (!peopleSnap.exists()) {
                    val user = User(
                            email = authData.email,
                            displayName = authData.displayName,
                            pic = authData.pic)
                    Log.i(TAG, "This is the first login, uploading user details: $user")
                    peopleRef.setValue(user)
                }
                peopleRef.child("presence").setValue("online")
            }

            override fun onCancelled(p0: FirebaseError) {
                throw UnsupportedOperationException()
            }
        })
    }

}
