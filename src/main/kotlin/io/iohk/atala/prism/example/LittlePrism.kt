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

        val credential = JsonBasedCredential(credentialContent)
        val signedCredential = credential.sign(masterKeyPair.privateKey)
        val (merkleRoot, merkleProofs) = CredentialBatches.batch(listOf(signedCredential))
        println("""- Credential:. $credential""")
        println("""- SignedCredential:. $signedCredential""")


    }

}
