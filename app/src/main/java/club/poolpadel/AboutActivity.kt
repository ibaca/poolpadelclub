package club.poolpadel

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

class AboutActivity : BaseDrawerActivity() {
    private val TAG = AboutActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
    }
}
