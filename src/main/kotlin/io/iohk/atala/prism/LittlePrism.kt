package io.iohk.atala.prism

import io.iohk.atala.prism.kotlin.credentials.CredentialBatches
import io.iohk.atala.prism.kotlin.credentials.content.CredentialContent
import io.iohk.atala.prism.kotlin.credentials.json.JsonBasedCredential
import io.iohk.atala.prism.kotlin.crypto.EC
import io.iohk.atala.prism.kotlin.crypto.derivation.KeyDerivation
import io.iohk.atala.prism.kotlin.crypto.derivation.KeyDerivation.randomMnemonicCode
import io.iohk.atala.prism.kotlin.identity.DID
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.security.Security
import javax.crypto.KeyGenerator

import io.iohk.atala.prism.crypto.MerkleInclusionProof
import io.iohk.atala.prism.crypto.derivation.KeyDerivation //used for genrating the mnemonics and DID
import io.iohk.atala.prism.kotlin.extras.ProtoUtils

object LittlePrism {


    @ExperimentalUnsignedTypes
    fun run() {


        println("""Little Prism""")
        val nodeApi = NodeAuthApiImpl(GrpcOptions("http", "localhost", 50053))


        // Issuer's DID Generation
        //https://learnmeabitcoin.com/technical/mnemonic
        val issuerMnemonic = KeyDerivation.randomMnemonicCode()//randomly using entropy and checksum
        val issuerSeed = KeyDerivation.binarySeed(issuerMnemonic, "secret") // 64bit seed, passphrase salt is optional
        val issuerMasterKeyPair = KeyGenerator.deriveKeyFromFullPath(issuerSeed, 0, PrismKeyType.MASTER_KEY, 0)
        val issuerDid = PrismDid.buildLongFormFromMasterPublicKey(issuerMasterKeyPair.publicKey)
        val nodePayloadGenerator = NodePayloadGenerator(issuerDid, mapOf(Pair(PrismDid.DEFAULT_MASTER_KEY_ID, issuerMasterKeyPair.privateKey)))
        val issuerCreateDidPayload = nodePayloadGenerator.createDid().payload
        val issuerCreateDidOperationId = nodeApi.createDid(issuerCreateDidPayload, issuerDid, PrismDid.DEFAULT_MASTER_KEY_ID)
        println("Issuer DID: $issuerDid")

        // Holder's DID Generation
        // Not published to the net
        val holderMasterKeyPair = EC.generateKeyPair()
        val holderUnpublishedDid = PrismDid.buildLongFormFromMasterPublicKey(holderMasterKeyPair.publicKey)
        println("Holder's Unpublished DID generated: $holderUnpublishedDid")

        // Generating credentialto holder
        // and formatting it to JSON
        // wil be changed to Binary encrypted format at the end
        val holderCredentialContent = CredentialContent(
            JsonObject(
                mapOf(
                    Pair("issuerDid", JsonPrimitive(issuerDid.value)),
                    Pair("issuanceKeyId", JsonPrimitive(PrismDid.DEFAULT_ISSUING_KEY_ID)),
                    Pair(
                        "credentialSubject",
                        JsonObject(
                            mapOf(
                                Pair("did", JsonPrimitive(holderUnpublishedDid.value)),
                                Pair("name", JsonPrimitive("Panos Mitronikas")),
                                Pair("certificate", JsonPrimitive("Certificate Of TOGAF Modules Complition"))
                            )
                        )
                    )
                )
            )
        )

        val holderUnsignedCredential = JsonBasedCredential(holderCredentialContent) // converting the credential to a JSON format
        val holderSignedCredential = holderUnsignedCredential.sign(issuerMasterKeyPair.privateKey) // signing it with issuer's masterkey as a private key

        // BATCHING THE CREDS
        // to better handle loads of credential issues
        // and with a singletrx fee, and not waiting for other trxs ahead of this
        val (holderCredentialMerkleRoot, holderCredentialMerkleProofs) = CredentialBatches.batch(
            listOf(
                holderSignedCredential
            )
        )
        val credentialBatchData = CredentialBatchData(
            issuerDid = issuerDid.suffix, // This requires the suffix only, as the node stores only suffixes
            merkleRoot = ByteArr(holderCredentialMerkleRoot.hash.value)
        )

        //PUBLISHING TO THE CARDANO NET
        val signedIssueCredentialOperation = ProtoUtils.signedAtalaOperation(
            issuerMasterKeyPair.privateKey,
            PrismDid.DEFAULT_ISSUING_KEY_ID,
            ProtoUtils.issueCredentialBatchOperation(credentialBatchData)
        )

        val issuedCredentialResult = nodePayloadGenerator.issueCredentials(
            PrismDid.DEFAULT_ISSUING_KEY_ID,
            arrayOf(
                CredentialClaim(
                    issuerDid,
                    JsonObject(
                        mapOf(
                            Pair("issuerDid", JsonPrimitive(issuerDid.value)),
                            Pair("issuanceKeyId", JsonPrimitive(PrismDid.DEFAULT_ISSUING_KEY_ID)),
                            Pair(
                                "credentialSubject",
                                JsonObject(
                                    mapOf(
                                        Pair("did", JsonPrimitive(holderUnpublishedDid.value)),
                                        Pair("name", JsonPrimitive("Panos Mitronikas")),
                                        Pair("certificate", JsonPrimitive("Certificate Of TOGAF Modules Complition"))
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
        val issuedCredentialOperationId = nodeApi.issueCredentials(
            issuedCredentialResult.payload,
            issuerDid.asCanonical(),
            PrismDid.DEFAULT_ISSUING_KEY_ID,
            issuedCredentialResult.merkleRoot
        )

        println(
            """Report of proccess:
                    - IssuerDID = $issuerDid
                    - Operation identifier = $issuedCredentialOperationId
                    - Credential content = $holderUnsignedCredential
                    - Signed credential = ${holderSignedCredential.canonicalForm}
                    - Inclusion proof (encoded) = ${holderCredentialMerkleProofs.first().encode()}
                    - Batch id = ${issuedCredentialResult.batchId}
                    """.trimIndent()
        )

        // check the credential validity (which succeeds)
        nodeApi.verify(
            signedCredential = holderSignedCredential,
            merkleInclusionProof = holderCredentialMerkleProofs.first()
        )
        println("Successfully Verified The Credentials")
    }
}