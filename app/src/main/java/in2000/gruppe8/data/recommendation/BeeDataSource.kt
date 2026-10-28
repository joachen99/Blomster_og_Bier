package in2000.gruppe8.data.recommendation

import android.content.Context
import in2000.gruppe8.R

class BeeDataSource(private val context: Context) {
    fun fetchBees(): String {
        val inputStream = context.resources.openRawResource(R.raw.beefile)
        return inputStream.bufferedReader().use { it.readText() }
    }

}