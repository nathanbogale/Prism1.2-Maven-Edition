package io.iohk.atala.prism.example

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.gson.annotations.SerializedName
import io.iohk.atala.prism.crypto.EC
import io.iohk.atala.prism.identity.PrismDid
import org.junit.Assert.assertEquals
import java.io.File


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
        val serialized = Gson().toJson(identities)

        File("result.json").writeText("$serialized")

        println("""- json:. $serialized""")


    }
}