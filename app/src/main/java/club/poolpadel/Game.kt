package club.poolpadel

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import android.widget.TextView
import java.text.DateFormat
import java.util.*

data class Game(
        var id: String? = null,
        var owner: String = "",
        var dateTime: Int = timestamp(),
        var court: String = "...",
        var player1: String = "...",
        var player2: String = "...",
        var player3: String = "...",
        var player4: String = "..."
) {
    private val calendar: Calendar by lazy { dateTime.calendar }
    fun dateTime(field: Int) = calendar.get(field)

    companion object {
        val path = "game"
        val contentUri: Uri.Builder get() = BASE_CONTENT_URI.buildUpon().appendPath(path)
        fun gameUri(gameId: String) = contentUri.appendEncodedPath(gameId).build()
    }

    class GameSummaryVh(v: View) : RecyclerView.ViewHolder (v) {
        lateinit var item: Game
        var onEditListener: () -> Unit = {}

        val game_date: TextView by bindView(R.id.match_date)
        val game_time: TextView by bindView(R.id.match_time)
        val game_court: TextView by bindView(R.id.match_court)
        val game_player1: TextView by bindView(R.id.match_player1)
        val game_player2: TextView by bindView(R.id.match_player2)
        val game_player3: TextView by bindView(R.id.match_player3)
        val game_player4: TextView by bindView(R.id.match_player4)

        val binder: Unit by lazy {
            itemView.findViewById(R.id.match_date_row).setOnClickListener {
                DatePickerDialog(itemView.context,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            item.dateTime = item.dateTime.calendar
                                    .apply {
                                        set(Calendar.YEAR, year)
                                        set(Calendar.MONTH, monthOfYear)
                                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                    }.timestamp
                            sync()
                        },
                        item.dateTime(Calendar.YEAR),
                        item.dateTime(Calendar.MONTH),
                        item.dateTime(Calendar.DAY_OF_MONTH)
                ).show()
            }
            itemView.findViewById(R.id.match_time_row).setOnClickListener {
                TimePickerDialog(itemView.context,
                        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                            item.dateTime = item.dateTime.calendar
                                    .apply {
                                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                                        set(Calendar.MINUTE, minute)
                                    }.timestamp
                            sync()
                        },
                        item.dateTime(Calendar.HOUR_OF_DAY),
                        item.dateTime(Calendar.MINUTE),
                        false
                ).show()
            }
            itemView.findViewById(R.id.match_court_row).setOnClickListener {
                textDialog("Court", item.court, { item.court = it;sync() })
            }
            itemView.findViewById(R.id.match_player1_row).setOnClickListener {
                textDialog("Player 1", item.player1, { item.player1 = it; sync() })
            }
            itemView.findViewById(R.id.match_player2_row).setOnClickListener {
                textDialog("Player 2", item.player2, { item.player2 = it; sync() })
            }
            itemView.findViewById(R.id.match_player3_row).setOnClickListener {
                textDialog("Player 3", item.player3, { item.player3 = it; sync() })
            }
            itemView.findViewById(R.id.match_player4_row).setOnClickListener {
                textDialog("Player 4", item.player4, { item.player4 = it; sync() })
            }
        }

        private fun sync() {
            update(item);
            onEditListener()
        }

        fun update(game: Game) {
            this.item = game
            binder // bind after arsing item

            game_date.text = DateFormat.getDateInstance().format(game.dateTime.date)
            game_time.text = DateFormat.getTimeInstance().format(game.dateTime.date)
            game_court.text = game.court
            game_player1.text = game.player1
            game_player2.text = game.player2
            game_player3.text = game.player3
            game_player4.text = game.player4
        }

        private fun textDialog(fieldName: String, fieldValue: String, callback: (value: String) -> Unit): Unit {
            val userInput = EditText(itemView.context).apply { setText(fieldValue) }
            return AlertDialog.Builder(itemView.context).apply {
                setTitle(fieldName)
                setView(userInput)
                setPositiveButton(itemView.resources.getString(android.R.string.ok), { dialog, which -> callback.invoke(userInput.text.toString()) })
                setNegativeButton(itemView.resources.getString(android.R.string.cancel), { dialog, which -> dialog.dismiss() })
            }.create().show()
        }
    }
}