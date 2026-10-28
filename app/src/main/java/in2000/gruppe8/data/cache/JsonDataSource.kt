package in2000.gruppe8.data.cache

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.reflect.Type

interface JsonDataSource<T>{
    suspend fun readJsonFile(): List<T>
    suspend fun writeToJsonFile(items: List<T>)
    suspend fun deleteCache(): Boolean
}

class JsonDataSourceImplementation<T>(
    private val file: File,
    private val type: Type,
    private val gson: Gson
) : JsonDataSource<T>{
    //Reads Jsonfile and returns it as a list of Observations-objects
    override suspend fun readJsonFile(): List<T> = withContext(Dispatchers.IO){
        if(!file.exists()) return@withContext emptyList()

        val jsonString = file.readText()
        if(jsonString.isBlank()) return@withContext emptyList()

        return@withContext gson.fromJson(jsonString, type)
    }

    //Writes list of Observations-objects to Jsonfile
    override suspend fun writeToJsonFile(items: List<T>) = withContext(Dispatchers.IO){
        val jsonString = gson.toJson(items, type)
        file.writeText(jsonString)
    }

    override suspend fun deleteCache(): Boolean = withContext(Dispatchers.IO) {
        file.delete() || !file.exists()
    }
}