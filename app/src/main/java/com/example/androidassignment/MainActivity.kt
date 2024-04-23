package com.example.androidassignment

import android.content.ClipData.Item
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidassignment.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var contentList: ArrayList<ItemData> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater) // Inflate layout using view binding
        val view = binding.root
        setContentView(view)
        // calling a function to parse the data in the assets folder
        parseJsonData(this, "data.json", binding)
        // setting up of the recycler view
        val rv = binding.recyclerView
        val adapter = Adapter(contentList)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)

    }

    // This function reads the json data present in the file and displays the data in the app
    private fun parseJsonData(context: Context, fileName: String, binding: ActivityMainBinding) {
        try {
            val jsonString =
                context.assets.open(fileName).bufferedReader().use { it.readText() }
            val jsonData = parseJson(jsonString)
            DisplayApp(jsonData, binding)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON data: ${e.message}")
        }
    }

    // takes a string of json data and returns it in the form of our data class
    private fun parseJson(jsonString: String): DataClass {
        val jsonObject = JSONObject(jsonString)
        return parseJsonData(jsonObject)
    }

    // iterates over the json data, recursively calling itself by checking if the all the
    // children are iterated over, then stores and returns the data in the form of a data
    // class defined by us.
    private fun parseJsonData(jsonObject: JSONObject): DataClass {
        with(jsonObject) {
            val activityName = getString("ActivityName")
            val contentDescription = optString("ContentDescription", "")
            val elementName = getString("ElementName")
            val childrenArray = getJSONArray("children")
            val children = mutableListOf<DataClass>()
            for (i in 0 until childrenArray.length()) {
                children.add(parseJsonData(childrenArray.getJSONObject(i)))
            }
            return DataClass(activityName, contentDescription, elementName, children)
        }
    }

    // this takes the json data in the form of a data class, iterates over the data class,
    // by going into the child at each iteration and populates the application with the
    // respective view.
    private fun DisplayApp(jsonData: DataClass, binding: ActivityMainBinding) {
        Log.d("JSONDATA", jsonData.toString())
        val textViewMap = mapOf(
            "org.nio.topMenu.appName" to binding.appNameTv,
            "org.nio.topMenu.Username" to binding.userNameTv,
            "org.nio.topMenu.logout" to binding.logoutTv,
            "org.nio.newspiece" to binding.newsPieceTv,
        )
        textViewMap.forEach { (key, value) ->
            if (jsonData.elementName == key) {
                value.text = jsonData.contentDescription
            }
        }
        if (jsonData.elementName == "org.nio.newspiece.content") {
            contentList.add(ItemData(jsonData.contentDescription))
        }

        jsonData.children.forEach { DisplayApp(it, binding) }
    }

}



