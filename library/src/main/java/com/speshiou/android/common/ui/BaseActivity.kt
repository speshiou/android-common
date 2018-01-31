import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

/**
 * Created by joey on 2018/1/11.
 */
class BaseActivity: AppCompatActivity() {
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}