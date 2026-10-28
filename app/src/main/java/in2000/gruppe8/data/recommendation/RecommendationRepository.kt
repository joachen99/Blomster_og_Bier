package in2000.gruppe8.data.recommendation

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import in2000.gruppe8.model.actions.ActionInfo
import in2000.gruppe8.model.bees.BeeInfo

class RecommendationRepository(
    private val actionsDataSource: ActionsDataSource,
    private val beeDataSource: BeeDataSource,
    private val gson: Gson = Gson()
){


    //actions against heavy rain
    fun getActions(): List<ActionInfo> {
        val json = actionsDataSource.fetchActions()
        val listType = object : TypeToken<List<ActionInfo>>() {}.type
        return gson.fromJson(json, listType)
    }

    //actions for bees
    fun getBees(): List<BeeInfo> {
        val json = beeDataSource.fetchBees()
        val listType = object : TypeToken<List<BeeInfo>>() {}.type
        return gson.fromJson(json, listType)
    }
}