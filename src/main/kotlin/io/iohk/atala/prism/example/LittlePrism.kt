package io.iohk.atala.prism.example

import java.sql.DriverManager.println
import io.iohk.atala.prism.api.*
import io.iohk.atala.prism.common.PrismSdkInternal
import io.iohk.atala.prism.crypto.*
import io.iohk.atala.prism.identity.*
import io.iohk.atala.prism.credentials.*
import io.iohk.atala.prism.credentials.content.*
import io.iohk.atala.prism.credentials.json.*
import kotlinx.datetime.*


/*
import io.iohk.protos.CredentialBatchData
import io.iohk.protos.GrpcOptions
import io.iohk.atala.protos.CredentialBatchData
import io.iohk.atala.protos.GrpcOptions
import io.iohk.atala.prism.protos.CredentialBatchData
import io.iohk.atala.prism.protos.GrpcOptions
import pbandk.ByteArr
*/

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive



object LittlePrism {


    fun run() {
        println("""Hello im Little Prism""")
        println("""-This is where we start-""")

//DID Creation
        val masterKeyPair = EC.generateKeyPair()
        //val did = DID.createUnpublishedDID(masterKeyPair.publicKey)
        val did = PrismDid.buildCanonicalFromMasterPublicKey(masterKeyPair.publicKey)
        println("""THIS IS THE DID GENERATED: $did""" )

/*
        val credentialContent = CredentialContent(
            JsonObject(
                mapOf(
                    Pair("issuerDid", JsonPrimitive(did.value)),
                    Pair("issuanceKeyId", JsonPrimitive("Issuance-0")),
                    Pair("credentialSubject", JsonObject(
                        mapOf(
                            Pair("name", JsonPrimitive("José López Portillo")),
                            Pair("certificate", JsonPrimitive("Certificate of PRISM SDK tutorial completion"))
                        )
                    )),
                )
            )
        )

*/
/*
        val credential = JsonBasedCredential(credentialContent)
        val signedCredential = credential.sign(masterKeyPair.privateKey)
        val (merkleRoot, merkleProofs) = CredentialBatches.batch(listOf(signedCredential))
*/

    }

}