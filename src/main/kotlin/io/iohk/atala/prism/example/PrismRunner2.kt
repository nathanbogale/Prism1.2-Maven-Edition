package io.iohk.atala.prism.example


import java.io.File
import io.iohk.atala.prism.api.CredentialBatches
import io.iohk.atala.prism.credentials.*
import io.iohk.atala.prism.credentials.content.CredentialContent
import io.iohk.atala.prism.credentials.json.JsonBasedCredential
import io.iohk.atala.prism.crypto.EC
import io.iohk.atala.prism.crypto.MerkleInclusionProof
import io.iohk.atala.prism.crypto.derivation.KeyDerivation
import io.iohk.atala.prism.identity.PrismDid
import io.iohk.atala.prism.protos.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import pbandk.decodeFromByteArray
import pbandk.encodeToByteArray
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import org.junit.Assert.assertEquals



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



    fun initiateRun(full_name: String, cred_title: String, Issuer_authority: String, Category: String, Issued_Date: String) {

        println("""Generating DID""")

        val masterKeyPair = EC.generateKeyPair()
        val did = PrismDid.buildCanonicalFromMasterPublicKey(masterKeyPair.publicKey)

        val identities = listOf(
            Identity(
                "$full_name","$did",
                listOf(Credential("$cred_title", "$Issuer_authority","$Category", "$Issued_Date"))
            )
        )



        val gson = Gson()


        val joined = Json.encodeToJsonElement("$identities")

        val ActualCredentialContent = CredentialContent(
            JsonObject(mapOf(Pair("Rsponse", joined)))
        )

        val serialized = Gson().toJson("$ActualCredentialContent")


        val CredentialContent = JsonBasedCredential(ActualCredentialContent)
        val signedCredential = listOf(CredentialContent.sign(masterKeyPair.privateKey))

        val (merkleRoot, merkleProofs) = CredentialBatches.batch(signedCredential)


        File("result.json").writeText("$signedCredential")
        println("""- signedCredential:. $signedCredential""")
        println("""-----------------------------------------------""")
        println("""- merkleRoot:. $merkleRoot""")









    }
}