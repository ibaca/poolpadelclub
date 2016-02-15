package club.poolpadel

import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import club.poolpadel.Game.GameSummaryVh
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener
import com.firebase.ui.FirebaseListAdapter
import java.text.DateFormat

/**
 * This fragment is either contained in a [GameListActivity] in two-pane mode (on tablets) or a
 * [GameDetailActivity] on handsets.
 */
class GameDetailFragment : Fragment() {
    val app: PoolPadel  by lazy { activity.application as PoolPadel }
    val game_activity: ListView by bindView(R.id.game_activity)
    val game_view: GameSummaryVh by lazy {
        GameSummaryVh(view!!.findViewById(R.id.game_summary_card)).apply {
            onEditListener = { gameRef.setValue(game) }
        }
    }

    lateinit var game: Game
    lateinit var gameId: String
    lateinit var gameRef: Firebase
    var listener: ValueEventListener? = null

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, state: Bundle?): View? {
        return i.inflate(R.layout.content_game_detail, c, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (arguments.containsKey(ARG_ITEM_ID)) {
            gameId = arguments.getString(ARG_ITEM_ID)!!
            gameRef = app.firebase.child("games").child(gameId)
            listener = gameRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(s: DataSnapshot) {
                    if (s.exists()) {
                        game = s.getValue(Game::class.java)
                        sync()
                    } else {
                        Toast.makeText(activity, "Ups, game not found or just removed!", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onCancelled(e: FirebaseError?) {
                    Log.i(TAG, "Games listener cancelled: $e")
                }
            })

            game_activity.adapter = object : FirebaseListAdapter<GameActivity>(
                    activity, GameActivity::class.java, R.layout.li_game,
                    app.firebase.child("gameActivities").child(gameId)
            ) {
                override fun populateView(v: View, g: GameActivity, pos: Int) {
                    (v.findViewById(android.R.id.text1) as TextView).text = g.description
                    (v.findViewById(android.R.id.text2) as TextView).text = DateFormat.getDateInstance().format(g.dateTime.date)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listener?.apply { app.firebase.removeEventListener(this) }
        (game_activity.adapter as? FirebaseListAdapter<*>)?.cleanup()
    }

    fun sync() {
        (activity?.findViewById(R.id.toolbar_layout) as? CollapsingToolbarLayout)?.setTitle(game.court)
        game_view.update(game)
        view?.refreshDrawableState()
    }

    companion object {
        val TAG = GameDetailFragment::class.java.simpleName
        /** The fragment argument representing the item ID that this fragment represents. */
        val ARG_ITEM_ID = "item_id"

        fun create(gameId: String): Fragment = GameDetailFragment().apply {
            arguments = Bundle().apply { putString(GameDetailFragment.ARG_ITEM_ID, gameId) }
        }
    }
}
