package in2000.gruppe8.data.recommendation
import android.content.Context
import in2000.gruppe8.R

class ActionsDataSource(private val context: Context) {
    fun fetchActions(): String {
        val inputStream = context.resources.openRawResource(R.raw.actionsfile)
        return inputStream.bufferedReader().use { it.readText() }
    }
}
