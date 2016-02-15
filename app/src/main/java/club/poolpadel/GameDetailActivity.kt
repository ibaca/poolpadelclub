package club.poolpadel

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.NavUtils.navigateUpTo
import android.view.MenuItem

class GameDetailActivity : BaseDetailActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detail)

        (findViewById(R.id.fab) as FloatingActionButton).setOnClickListener { view ->
            Snackbar.make(view, "Aquí deberías poder asignar jugadores y cancelar o terminar el " +
                    "partido indicando la puntiación final.", Snackbar.LENGTH_LONG)
                    .setAction(getString(android.R.string.ok), null).show()
        }

        if (savedInstanceState == null) supportFragmentManager.tx {
            add(R.id.game_detail_container, GameDetailFragment().apply {
                this.arguments = Bundle().apply {
                    putString(GameDetailFragment.ARG_ITEM_ID, intent.getStringExtra(GameDetailFragment.ARG_ITEM_ID)!!)
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> navigateUpTo(this, Intent(this, GameListActivity::class.java)).let { true }
        else -> super.onOptionsItemSelected(item)
    }
}
