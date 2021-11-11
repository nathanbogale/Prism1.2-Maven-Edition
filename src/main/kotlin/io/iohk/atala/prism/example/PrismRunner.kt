package io.iohk.atala.prism.example

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.gson.annotations.SerializedName
import org.junit.Assert.assertEquals
import java.io.File


object PrismRunner {
    val gsonPretty = GsonBuilder().setPrettyPrinting().create()

    data class Credential(
        var title: String,
        var category: String,
        var views: Int
    ) {
    }

    data class Identity(
        var name: String,
        var type: String? = null,
        @SerializedName("identity_credentials")
        var credentials: List<Credential>? = null,
    ) {
    }

    fun initiateRun() {

        val authors = listOf(
            Identity(
                "John",
                "Technical Identity",
                listOf(Credential("Streams in Java", "Java", 3), Credential("Lambda Expressions", "Java", 5))
            ),
            Identity("Jane", "Technical Identity", listOf(Credential("Functional Interfaces", "Java", 2))),
            Identity("William", "Technical Editor")
        )
        val serialized = Gson().toJson(authors)

        val json =
            """[{"name":"John","type":"Technical Identity","identity_credentials":[{"title":"Streams in Java","category":"Java","views":3},{"title":"Lambda Expressions","category":"Java","views":5}]},{"name":"Jane","type":"Technical Identity","identity_credentials":[{"title":"Functional Interfaces","category":"Java","views":2}]},{"name":"William","type":"Technical Editor"}]"""
        assertEquals(serialized, json)

        File("result.json").writeText("$json")

        println("""- json:. $json""")


    }
}