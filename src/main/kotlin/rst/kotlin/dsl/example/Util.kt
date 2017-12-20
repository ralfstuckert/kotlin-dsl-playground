package rst.kotlin.dsl.example

import com.google.gson.Gson
import com.google.gson.GsonBuilder

val GSON = GsonBuilder().setPrettyPrinting().create()

fun prettyJson(obj:Any):String = GSON.toJson(obj)

fun printPrettyJson(obj:Any) {
    println(prettyJson(obj))
}