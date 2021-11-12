package io.iohk.atala.prism.example


import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
//import com.google.gson.stream.JsonReader
import io.iohk.atala.prism.api.CredentialBatches
import io.iohk.atala.prism.credentials.content.CredentialContent
import io.iohk.atala.prism.credentials.json.JsonBasedCredential
import io.iohk.atala.prism.crypto.EC
import io.iohk.atala.prism.identity.PrismDid
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import java.io.IOException
import java.lang.reflect.Type

object PrismRunner2 {

    data class Credential(
        var cred_title: String,
        var Issuer_authority: String,
        var category: String,
        var Issued_date: String
    ) {
    }

    data class Identity(
        var user_name: String,
        var generated_did: String,
        @SerializedName("identity_credentials")
        var credentials: List<Credential>? = null,
    ) {
    }



    fun initiateRun(
        full_name: String,
        cred_title: String,
        Issuer_authority: String,
        Category: String,
        Issued_Date: String
    ) {

        println("""--------------------------------------------------------""")
        println("""- Generating DID""")
        println("""--------------------------------------------------------""")
        val masterKeyPair = EC.generateKeyPair()
        val did = PrismDid.buildCanonicalFromMasterPublicKey(masterKeyPair.publicKey)

        val identities = listOf(
            Identity(
                "$full_name", "$did",
                listOf(Credential("$cred_title", "$Issuer_authority", "$Category", "$Issued_Date"))
            )
        )
        println("""- Generated DID: $did""")

        val gson = Gson()
        val verResult = null
        println("""--------------------------------------------------------""")
        println("""- Attaching VCS""")
        val joined = Json.encodeToJsonElement("$identities")
        val ActualCredentialContent = CredentialContent(
            JsonObject(mapOf(Pair("Rsponse", joined)))
        )

        val serialized = Gson().toJson("$ActualCredentialContent")

        val CredentialContent = JsonBasedCredential(ActualCredentialContent)
        val signedCredential = listOf(CredentialContent.sign(masterKeyPair.privateKey))
        println("""--------------------------------------------------------""")

        val (merkleRoot, merkleProofs) = CredentialBatches.batch(signedCredential)


        File("result.json").writeText("$signedCredential")
        println("""- Here Are signedCredential:. $signedCredential""")
        println("""-------------------------------------------------------""")

        println("""- merkleRoot:. $merkleRoot""")
        println("""--------------------------- COMPLETED SUCCESSFULLY ----------------------------""")

    }


    fun readJSONFromAsset(): String? {
        var json: String? = null
        try {
            val inputStream: InputStream = File("result.json").inputStream()
            json = inputStream.bufferedReader().use{it.readText()}
          //  println(json[Credential])


        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    class JsonExport(
                val credentials: List<Credential>
        ) {
        override fun toString(): String {
            return "Category [credentials: ${this.credentials}]"
                }
    }

    fun VerifyCred(DID: String) {

        println("""--------------------------------------------------------""")
        println("""Verifying Please Wait""")
        println("""--------------------------------------------------------""")
        /*
        data class ResponseData(
            var Response: String? = null,
            var Identity: String? = null){}



        class StudentSerializer : JsonSerializer<ResponseData> {

            override fun serialize(src: ResponseData?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {

                val jsonObject = JsonObject()

                jsonObject.add("Response", context?.serialize("src.Response"))
                jsonObject.add("Identity", JsonParser().parse("src.Response[Identity]"))


                return jsonObject
            }
        }

        val gSon = GsonBuilder().registerTypeAdapter(ResponseData::class.java, StudentSerializer()).create()
        val json = gSon.toJson(ResponseData())
        println("""$jsonObject""")
*/

        val inputStream: InputStream = File("result.json").inputStream()
        val inputString = inputStream.bufferedReader().use { it.readText() }
        val decentralized = Json.encodeToJsonElement(inputString)
        val new = Gson().toJson(decentralized)
         println("""$decentralized""")
        println("""--------------------------------------------------------""")
        println("""Verification Result: Successfully Completed""")
        println("""--------------------------------------------------------""")

    }


}