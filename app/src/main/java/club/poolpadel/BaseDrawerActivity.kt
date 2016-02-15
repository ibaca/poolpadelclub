package club.poolpadel

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.firebase.client.AuthData
import com.squareup.picasso.Picasso

abstract class BaseDrawerActivity : BaseActivity() {
    private val TAG = BaseDrawerActivity::class.java.simpleName
    val picasso by lazy { Picasso.with(this) }
    val drawer: DrawerLayout by bindView(R.id.drawer_layout)
    val nav_header: View by bindView(R.id.nav_header)
    val nav_header_image: ImageView by bindView(R.id.nav_header_image)
    val nav_header_primary: TextView by bindView(R.id.nav_header_primary)
    val nav_header_secondary: TextView by bindView(R.id.nav_header_secondary)

    override fun setContentView(layoutResId: Int) {
        super.setContentView(R.layout.layout_base)
        val layout = findViewById(R.id.content_layout) as CoordinatorLayout
        layoutInflater.inflate(layoutResId, layout, true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        toolbar.title = title

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_games -> startActivity(Intent(this, GameListActivity::class.java))
                R.id.nav_history -> {
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.menu_history))
                        setMessage("Esta actividad debería mostrar la partidas ya juagadas del usuario.")
                        setPositiveButton(getString(android.R.string.ok), null)
                    }.create().show()
                }
                R.id.nav_ranking -> {
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.menu_ranking))
                        setMessage("Esta actividad debería mostrar la clasificación del usuario. Un usuario " +
                                "puede estar en varias clasificaciones, como la de su zona, " +
                                "de su quinta, de sus amigos, etc. Además la clasificación puede " +
                                "estar limitada en tiempo, por ejemplo la clasificación entre amigos puede " +
                                "ademas ser mensual. Las clasifiaciones son gestionadas por la aplicación.")
                        setPositiveButton(getString(android.R.string.ok), null)
                    }.create().show()
                }
                R.id.nav_tournament -> {
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.menu_tournament))
                        setMessage("Esta actividad debería mostrar los torneos. Los torneos son similares" +
                                "a las clasificaciones respecto a que son una competición con otros jugadores, la " +
                                "diferencia es que los toreneos el usuario se suscribe y las clasifiaciones " +
                                "el usuario está implicitamente suscrito.")
                        setPositiveButton(getString(android.R.string.ok), null)
                    }.create().show()
                }
                R.id.nav_algorithm -> {
                    AlertDialog.Builder(this).apply {
                        setTitle(getString(R.string.menu_algorithm))
                        setMessage("Algoritmos.. dices eh!? Pues tanto las clasifiaciones como los torneos " +
                                "usan un criterio mágico para decidir tu posición, pues esta mágia es " +
                                "lo que se llama 'algoritmo'... y los usuarios podría proponer sus criterios, " +
                                "es decir, sus 'algoritmos'.")
                        setPositiveButton(getString(android.R.string.ok), null)
                    }.create().show()
                }
                R.id.nav_about -> startActivity(Intent(this, AboutActivity::class.java))
                R.id.nav_manage -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.nav_login -> showFirebaseLoginPrompt()
                R.id.nav_logout -> firebaseRef.unauth()
            }

            drawer.closeDrawer(GravityCompat.START).let { true }
        }

        nav_header.setOnClickListener {
            if (!anonymous) startActivity(Intent(this, AccountActivity::class.java))
        }
    }

    override fun onFirebaseLoggedIn(authData: AuthData) {
        super.onFirebaseLoggedIn(authData)
        updateAuth(authData)
    }

    override fun onFirebaseLoggedOut() {
        super.onFirebaseLoggedOut()
        updateAuth(null)
    }

    private fun updateAuth(authData: AuthData?) {
        if (authData != null) {
            nav_header_image.apply { picasso.load(authData.pic).into(this) }
            nav_header_primary.text = authData.displayName
            nav_header_secondary.text = authData.email
        } else {
            nav_header_image.setImageResource(R.mipmap.ic_launcher)
            nav_header_primary.text = "Buenas"
            nav_header_secondary.text = "No estas logeado, pero puedes curiosear..."
        }

        (findViewById(R.id.nav_view) as NavigationView).apply {
            menu.findItem(R.id.nav_login).isVisible = authData == null
            menu.findItem(R.id.nav_logout).isVisible = authData != null
        }
    }

    override fun onBackPressed() {
        val isDrawerOpen = drawer.isDrawerOpen(GravityCompat.START)
        if (isDrawerOpen) drawer.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }
}
