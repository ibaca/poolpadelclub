package club.poolpadel

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import club.poolpadel.Game.GameSummaryVh
import com.firebase.client.Firebase.CompletionListener

class GameCreateActivity : BaseDetailActivity() {
    val game_view: GameSummaryVh by lazy { GameSummaryVh(findViewById(R.id.game_summary_card)) }

    lateinit var game: Game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_create)

        game = Game()
        sync()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return menuInflater.inflate(R.menu.create, menu).let { true }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_save -> {
            if (anonymous) {
                Snackbar.make(game_view.itemView, "Ups, debes estar logeado.", Snackbar.LENGTH_LONG)
                        .setAction("Login", { showFirebaseLoginPrompt() })
                        .show()
            } else {
                val gameRef = firebaseRef.child("games").push()
                val gameId = gameRef.key
                game.owner = auth.uid
                val progress = ProgressDialog(this).apply {
                    setMessage("Guardando...")
                    show()
                }
                gameRef.setValue(game, CompletionListener { error, firebase ->
                    if (error != null) {
                        Toast.makeText(this@GameCreateActivity,
                                "Error al guardar: ${error.message}", Toast.LENGTH_LONG).show()
                    } else {
                        startActivity(Intent(this@GameCreateActivity, GameDetailActivity::class.java).apply {
                            putExtra(GameDetailFragment.ARG_ITEM_ID, gameId)
                        })
                    }
                    progress.hide()
                })
                firebaseRef.child("users").child(auth.uid).child("games").child(gameId).setValue(true)
                firebaseRef.child("gameActivities").child(gameId).push().setValue(
                        GameActivity(action = "create", description = "Nueva partida creada")
                )
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    fun sync() {
        (findViewById(R.id.toolbar_layout) as CollapsingToolbarLayout).setTitle(game.court)
        game_view.update(game)
    }

    companion object {
        private val TAG = GameCreateActivity::class.java.simpleName
    }
}
