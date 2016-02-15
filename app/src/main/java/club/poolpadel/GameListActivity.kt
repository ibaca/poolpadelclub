package club.poolpadel

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.firebase.ui.FirebaseRecyclerAdapter

/**
 * This activity has different presentations for handset and tablet-size devices. On handsets, the
 * activity presents a list of items, which when touched, lead to a [GameDetailActivity]
 * representing item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class GameListActivity : BaseDrawerActivity() {

    val twoPane: Boolean by lazy { findViewById(R.id.game_detail_container) != null }
    val list: RecyclerView by bindView(R.id.game_list)
    val fab: FloatingActionButton by bindView(R.id.fab)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)


        list.adapter = object : FirebaseRecyclerAdapter<Game, GameVh>(
                Game::class.java, R.layout.li_game,
                GameVh::class.java, firebaseRef.child("games")) {
            override fun populateViewHolder(vh: GameVh, g: Game, pos: Int) {
                vh.item = g.apply { id = getRef(pos).key }
                vh.li_where.text = g.court
                vh.li_when.text = relativeTime(g.dateTime)

                vh.v.setOnClickListener { v ->
                    if (twoPane) {
                        supportFragmentManager.tx {
                            replace(R.id.game_detail_container, GameDetailFragment.create(vh.item.id!!))
                        }
                    } else {
                        v.context.startActivity(Intent(v.context, GameDetailActivity::class.java).apply {
                            putExtra(GameDetailFragment.ARG_ITEM_ID, vh.item.id!!)
                        })
                    }
                }
            }
        }

        fab.setOnClickListener { startActivity(Intent(this, GameCreateActivity::class.java)) }
    }

    override fun onDestroy() {
        super.onDestroy()
        (list.adapter as? FirebaseRecyclerAdapter<*, *>)?.cleanup()
    }

    class GameVh(val v: View) : RecyclerView.ViewHolder(v) {
        lateinit var item: Game
        val li_where: TextView by bindView(android.R.id.text1)
        val li_when: TextView by bindView(android.R.id.text2)

        override fun toString(): String {
            return super.toString() + " '" + li_where.text + "'"
        }
    }
}
