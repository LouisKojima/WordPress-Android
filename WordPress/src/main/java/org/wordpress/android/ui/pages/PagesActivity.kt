package org.wordpress.android.ui.pages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.pages_fragment.*
import org.wordpress.android.R
import org.wordpress.android.R.id
import org.wordpress.android.ui.posts.BasicFragmentDialog.BasicDialogNegativeClickInterface
import org.wordpress.android.ui.posts.BasicFragmentDialog.BasicDialogPositiveClickInterface

const val EXTRA_PAGE_REMOTE_ID_KEY = "extra_page_remote_id_key"
const val EXTRA_PAGE_PARENT_ID_KEY = "extra_page_parent_id_key"

class PagesActivity : AppCompatActivity(), BasicDialogPositiveClickInterface, BasicDialogNegativeClickInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.pages_activity)

        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPositiveClicked(instanceTag: String) {
        passDeleteConfirmation(instanceTag.toLong())
    }

    override fun onNegativeClicked(instanceTag: String) {
    }

    private fun passDeleteConfirmation(remoteId: Long) {
        val fragment = supportFragmentManager.findFragmentById(id.fragment_container)
        if (fragment is PagesFragment) {
            fragment.onPageDeleteConfirmed(remoteId)
        }
    }
}
