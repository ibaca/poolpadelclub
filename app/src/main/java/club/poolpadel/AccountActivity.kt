package club.poolpadel

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.firebase.client.DataSnapshot
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener

class AccountActivity : BaseDrawerActivity() {
    private val TAG = AccountActivity::class.java.simpleName

    val account_pic: ImageView by bindView(R.id.account_pic)
    val account_email: TextView by bindView(R.id.account_email)
    val account_provider: TextView by bindView(R.id.account_provider)
    val account_display_name: TextView by bindView(R.id.account_display_name)
    var listener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val userRef = app.firebase.child("people").child(firebaseRef.auth.uid)
        listener = userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                if (s.exists()) {
                    val user = s.getValue(User::class.java)
                    account_pic.apply { picasso.load(user.pic).into(this) }
                    account_email.text = user.email
                    account_provider.text = firebaseRef.auth.provider
                    account_display_name.text = user.displayName
                } else {
                    Toast.makeText(this@AccountActivity, "Ups, user not found or just removed!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(e: FirebaseError?) {
                Log.i(GameDetailFragment.TAG, "Games listener cancelled: $e")
            }
        })

        findViewById(R.id.account_display_name_row).setOnClickListener {
            val userInput = EditText(this).apply { setText(account_display_name.text) }
            AlertDialog.Builder(this).apply {
                setTitle("Nombre de usuario")
                setView(userInput)
                setNegativeButton(getString(android.R.string.cancel), { dialog, which -> dialog.dismiss() })
                setPositiveButton(getString(android.R.string.ok), { dialog, which ->
                    firebaseRef.child("people").child(auth.uid).child("displayName").setValue(userInput.text.toString())
                })
            }.create().show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listener?.apply { firebaseRef.removeEventListener(this) }
    }
}
