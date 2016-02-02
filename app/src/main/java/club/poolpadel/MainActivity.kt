package club.poolpadel

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.firebase.client.AuthData
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val TAG = MainActivity::class.java.simpleName

    /* TextView that is used to display information about the logged in user */
    var mLoggedInStatusTextView: TextView? = null

    /* A dialog that is presented until the Firebase authentication finished. */
    var mAuthProgressDialog: ProgressDialog? = null

    /* A reference to the Firebase */
    var mFirebaseRef: Firebase? = null

    /* Data from the authenticated user */
    var mAuthData: AuthData? = null

    /* Listener for Firebase session changes */
    var mAuthStateListener: Firebase.AuthStateListener? = null

    var mPasswordLoginButton: Button? = null

    var mAnonymousLoginButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            mAuthProgressDialog = ProgressDialog(this)
            mAuthProgressDialog?.setTitle("Loading")
            mAuthProgressDialog?.setMessage("Un-authenticating with Firebase...")
            mAuthProgressDialog?.setCancelable(false)
            mAuthProgressDialog?.show()

            mAuthStateListener = Firebase.AuthStateListener { authData ->
                mAuthProgressDialog?.hide()
                setAuthenticatedUser(authData)
            }

            mFirebaseRef?.unauth()
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        /* *************************************
         *               PASSWORD              *
         ***************************************/
        mPasswordLoginButton = findViewById(R.id.login_with_password) as Button
        mPasswordLoginButton?.setOnClickListener { loginWithPassword() }

        /* *************************************
         *              ANONYMOUSLY            *
         ***************************************/
        /* Load and setup the anonymous login button */
        mAnonymousLoginButton = findViewById(R.id.login_anonymously) as Button
        mAnonymousLoginButton?.setOnClickListener { loginAnonymously() }

        /* *************************************
         *               GENERAL               *
         ***************************************/
        mLoggedInStatusTextView = findViewById(R.id.login_status) as TextView

        /* Create the Firebase ref that is used for all authentication with Firebase */
        mFirebaseRef = Firebase(resources.getString(R.string.firebase_url))

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = ProgressDialog(this)
        mAuthProgressDialog?.setTitle("Loading")
        mAuthProgressDialog?.setMessage("Authenticating with Firebase...")
        mAuthProgressDialog?.setCancelable(false)
        mAuthProgressDialog?.show()

        mAuthStateListener = Firebase.AuthStateListener { authData ->
            mAuthProgressDialog?.hide()
            setAuthenticatedUser(authData)
        }
        /* Check if the user is authenticated with Firebase already. If this is the case we can set the authenticated
         * user and hide hide any login buttons */
        mFirebaseRef?.addAuthStateListener(mAuthStateListener)

    }

    /** Once a user is logged in, take the mAuthData provided from Firebase and "use" it. */
    private fun setAuthenticatedUser(authData: AuthData?) {
        if (authData != null) {
            /* Hide all the login buttons */
            mPasswordLoginButton?.visibility = View.GONE
            mAnonymousLoginButton?.visibility = View.GONE
            mLoggedInStatusTextView?.visibility = View.VISIBLE
            /* show a provider specific status text */
            var name: String? = null
            val provider = authData.provider
            when (provider) {
                in "facebook", "google", "twitter" -> {
                    name = authData.providerData["displayName"] as String
                }
                in "anonymous", "password" -> {
                    name = authData.uid
                }
                else -> {
                    Log.e(TAG, "Invalid provider: " + provider)
                }
            }
            if (name != null) {
                mLoggedInStatusTextView?.text = "Logged in as $name ($provider)"
            }
        } else {
            /* No authenticated user show all the login buttons */
            mPasswordLoginButton?.visibility = View.VISIBLE
            mAnonymousLoginButton?.visibility = View.VISIBLE
            mLoggedInStatusTextView?.visibility = View.GONE
        }
        this.mAuthData = authData
        /* invalidate options menu to hide/show the logout button */
        supportInvalidateOptionsMenu()
    }

    /** Show errors to users */
    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this).setTitle("Error").setMessage(message).setPositiveButton(android.R.string.ok, null).setIcon(android.R.drawable.ic_dialog_alert).show()
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            startActivity(Intent(this, SettingsActivity::class.java))
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun loginWithPassword() {
        mAuthProgressDialog?.show()
        mFirebaseRef?.authWithPassword("test@firebaseuser.com", "test1234", AuthResultHandler("password"))
    }

    fun loginAnonymously() {
        mAuthProgressDialog?.show()
        mFirebaseRef?.authAnonymously(AuthResultHandler("anonymous"))
    }

    /** Utility class for authentication results */
    inner class AuthResultHandler(private val provider: String) : Firebase.AuthResultHandler {

        override fun onAuthenticated(authData: AuthData) {
            mAuthProgressDialog?.hide()
            Log.i(TAG, provider + " auth successful")
            setAuthenticatedUser(authData)
        }

        override fun onAuthenticationError(firebaseError: FirebaseError) {
            mAuthProgressDialog?.hide()
            showErrorDialog(firebaseError.toString())
        }
    }
}
