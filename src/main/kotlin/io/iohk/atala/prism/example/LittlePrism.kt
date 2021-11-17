package io.iohk.atala.prism.example

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



object LittlePrism {

    fun run() {
        println("""Generating DID""")

        val masterKeyPair = EC.generateKeyPair()
        val did = PrismDid.buildCanonicalFromMasterPublicKey(masterKeyPair.publicKey)
        println("""- DID:. $did""")


        val firstCredentialContent = CredentialContent(
            JsonObject(
                mapOf(
                    Pair("issuerDid", JsonPrimitive(did.value)),
                    Pair("issuanceKeyId", JsonPrimitive("ID-1")),
                    Pair("credentialSubject", JsonObject(
                        mapOf(
                            Pair("name", JsonPrimitive("Debbol Mammo")),
                            Pair("certificate", JsonPrimitive("Certificate of TOGAF certification completion")),
                            Pair("issue-date", JsonPrimitive("NOV-20-2020"))
                        )
                    )),
                )
            )
        )

        val secondCredentialContent = CredentialContent(
            JsonObject(
                mapOf(
                    Pair("issuerDid", JsonPrimitive(did.value)),
                    Pair("issuanceKeyId", JsonPrimitive("ID-2")),
                    Pair("credentialSubject", JsonObject(
                        mapOf(
                            Pair("name", JsonPrimitive("Debbol Mammo")),
                            Pair("certificate", JsonPrimitive("Certificate of PRISM certification completion")),
                            Pair("issue-date", JsonPrimitive("NOV-20-2020"))

                        )
                    )),
                )
            )
        )

        val firstCredential = JsonBasedCredential(firstCredentialContent)
        val secondCredential = JsonBasedCredential(secondCredentialContent)
        val signedCredential = listOf(firstCredential.sign(masterKeyPair.privateKey),secondCredential.sign(masterKeyPair.privateKey))
       // val signedCredential = firstCredential.sign(masterKeyPair.privateKey);secondCredential.sign(masterKeyPair.privateKey)
        val (merkleRoot, merkleProofs) = CredentialBatches.batch(signedCredential)
       // println("""- Credential:. $credential""")
        println("""- SignedCredential:. $signedCredential""")
        println("""- Merkel Root+Proof:. $merkleRoot,$merkleProofs""")


    }

}
