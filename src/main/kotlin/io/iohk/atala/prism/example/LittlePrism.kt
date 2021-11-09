package io.iohk.atala.prism.example

import java.io.File
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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

val format = Json { prettyPrint = true }

object LittlePrism {

    fun initiateRun() {
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
        // val signedCredential = listOf(firstCredential.sign(masterKeyPair.privateKey),secondCredential.sign(masterKeyPair.privateKey))
        // val signedCredential = firstCredential.sign(masterKeyPair.privateKey);secondCredential.sign(masterKeyPair.privateKey)
        val (merkleRoot, merkleProofs) = CredentialBatches.batch(signedCredential)
        // println("""- Credential:. $credential""")
        println("""- SignedCredential:. $signedCredential""")
        println("""- Merkel Root+Proof:. $merkleRoot,$merkleProofs""")


        val outputData = (
                JsonObject(
                    mapOf(
                        Pair("Issue DID", JsonPrimitive("$did")),
                        Pair("credentialSubject", JsonObject(
                            mapOf(
                                Pair("Issued & Signed Credentials", JsonPrimitive("$signedCredential")),
                                Pair("Merket Root Generated", JsonPrimitive("$merkleRoot")),
                            )
                        )),
                    )
                )
                )

    } // END INITIATE RUN


    fun runMajor() {
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
       // val signedCredential = listOf(firstCredential.sign(masterKeyPair.privateKey),secondCredential.sign(masterKeyPair.privateKey))
       // val signedCredential = firstCredential.sign(masterKeyPair.privateKey);secondCredential.sign(masterKeyPair.privateKey)
        val (merkleRoot, merkleProofs) = CredentialBatches.batch(signedCredential)
       // println("""- Credential:. $credential""")
        println("""- SignedCredential:. $signedCredential""")
        println("""- Merkel Root+Proof:. $merkleRoot,$merkleProofs""")


        val outputData = (
            JsonObject(
                mapOf(
                    Pair("Issue DID", JsonPrimitive("$did")),
                    Pair("credentialSubject", JsonObject(
                        mapOf(
                            Pair("Issued & Signed Credentials", JsonPrimitive("$signedCredential")),
                            Pair("Merket Root Generated", JsonPrimitive("$merkleRoot")),
                          )
                    )),
                )
            )
        )

        /*
       // File("out.JSON").writeText("$outputData")
        File("out.JSON").writeText("$outputData")

        val tutsList: List<Tutorial> = listOf(
            Tutorial("Tut #1", listOf("cat1", "cat2")),
            Tutorial("Tut #2", listOf("cat3", "cat4"))
        );

        val gson = Gson()

        val jsonTutsList: String = gson.toJson(tutsList)
        File("bezkoder1.json").writeText(jsonTutsList)

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonTutsListPretty: String = gsonPretty.toJson(tutsList)
        File("bezkoder2.json").writeText(jsonTutsListPretty)

*/
        File("out.JSON").writeText("$outputData")

        val tutsList: List<Tutorial> = listOf(
            Tutorial("$did", listOf("Certificate of TOGAF certification completion", "NOV-20-2020")),
            Tutorial("Tut #2", listOf("Certificate of PRISM certification completion", "NOV-20-2021"))
        );

        val gson = Gson()

        val jsonTutsList: String = gson.toJson(tutsList)
        File("export-unstructured.json").writeText(jsonTutsList)

        val gsonPretty = GsonBuilder().setPrettyPrinting().create()
        val jsonTutsListPretty: String = gsonPretty.toJson(tutsList)
//        File("bezkoder2.json").writeText(jsonTutsListPretty)

        val element = buildJsonObject {

            putJsonArray("forks") {
                addJsonObject {
                    put("Credentials", "Certificate of TOGAF certification completion")
                }
                addJsonObject {
                    put("Credentials", "Certificate of PRISM certification completion")
                }
            }
        }
        val elemented: String = gsonPretty.toJson(element)
     //  File("bezkoder2.json").writeText("$elemented")

        val NewData = (
                JsonObject(
                    mapOf(
                                Pair("$did", JsonObject(
                                    mapOf(
                                        Pair("Signed Credential", JsonPrimitive("Certificate of TOGAF certification completion")),
                                        Pair("Signed Credential 2", JsonPrimitive("$signedCredential")),
                                    )
                                )),
                            )
                )
                )

        val NewDatad: String = gsonPretty.toJson(NewData)
        File("export-structured.json").writeText("$NewDatad")

    } // END RUN MAJOR




    class DIDVC(
        val DID: String,
        val Credentials: String
    )


    class Tutorial(
        val DID: String,
        val Credentials: List<String>
    ) {
        override fun toString(): String {
            return "Category [title: ${this.DID}, categories: ${this.Credentials}]"
        }
    }




}
