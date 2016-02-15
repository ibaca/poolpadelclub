package club.poolpadel

import android.os.Bundle
import android.support.design.widget.CoordinatorLayout

abstract class BaseDetailActivity : BaseActivity() {
    private val TAG = BaseDetailActivity::class.java.simpleName

    override fun setContentView(layoutResId: Int) {
        super.setContentView(R.layout.layout_detail)
        val layout = findViewById(R.id.content_layout) as CoordinatorLayout
        layoutInflater.inflate(layoutResId, layout, true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        toolbar.title = title

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}
