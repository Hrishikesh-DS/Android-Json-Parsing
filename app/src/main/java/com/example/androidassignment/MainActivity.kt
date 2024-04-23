package com.example.androidassignment

import android.content.ClipData.Item
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.androidassignment.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var contentList: ArrayList<ItemData> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater) // Inflate layout using view binding
        val view = binding.root
        setContentView(view)
        JsonParser.parseJsonData(this, "data.json", binding)
    }

    object JsonParser {
        fun parseJsonData(context: Context, fileName: String, binding: ActivityMainBinding) {
            try {
                val jsonString =
                    context.assets.open(fileName).bufferedReader().use { it.readText() }
                val jsonData = parseJson(jsonString)
                DisplayApp(jsonData, binding)
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing JSON data: ${e.message}")
            }
        }

        private fun parseJson(jsonString: String): DataClass {
            val jsonObject = JSONObject(jsonString)
            return parseJsonData(jsonObject)
        }

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
                if (jsonData.elementName == "org.nio.newspiece.content") {

                }
            }


            Log.d(TAG, "${jsonData.activityName}: ${jsonData.contentDescription}")
            jsonData.children.forEach { DisplayApp(it, binding) }
        }

    }
}


