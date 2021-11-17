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

object ContainLittlePrism {


    @ExperimentalUnsignedTypes
    fun run() {
        println("""Welcome to Little Prism""")
        println("-")
        println("Setting up environment and client")
        //since i'm not interacting with the Node and Connector, No need for this
        val environment = "localhost"
        val connector = ConnectorServiceCoroutine.Client(GrpcClient(GrpcOptions("http", environment, 50051)))
        val nodeAuthApi = NodeAuthApiImpl(GrpcOptions("http", environment, 50053))

        // Issuer claims an identity
        println("Generate Issuer DID - Unpublished")
        //First way
        //https://learnmeabitcoin.com/technical/mnemonic
        val mnemonic = KeyDerivation.randomMnemonicCode() //randomly using entropy and checksum
        val seed = KeyDerivation.binarySeed(mnemonic, "secret") // 64bi seed, passphrase is optional, modifying final seed
        val issuerMasterKeyPair = KeyGenerator.deriveKeyFromFullPath(seed, 0, PrismKeyType.MASTER_KEY, 0) //unknown references
        val issuerIssuingKeyPair = KeyGenerator.deriveKeyFromFullPath(seed, 0, PrismKeyType.ISSUING_KEY, 0)
        val issuerRevocationKeyPair = KeyGenerator.deriveKeyFromFullPath(seed, 0, PrismKeyType.REVOCATION_KEY, 0)
        val issuerUnpublishedDid = PrismDid.buildLongFormFromMasterPublicKey(issuerMasterKeyPair.publicKey) //needs prism API
        val issuerDid = issuerUnpublishedDid.asCanonical()

        //Second way
        //from elepic curve - Greate for SMS, major security flaws - https://en.wikipedia.org/wiki/Elliptic-curve_cryptography
        val masterKeyPair = EC.generateKeyPair() //java method, gen everytime called
        val did = DID.createUnpublishedDID(masterKeyPair.publicKey)


        /*
        val issuerRegisterDIDResponse = runBlocking {
            connector.RegisterDID(  //PublishAsABlock
                RegisterDIDRequest(
                    registerWith = RegisterDIDRequest.RegisterWith.CreateDidOperation(issuerCreatedDIDSignedOperation),
                    name = "Issuer"
                )
            )
        }
        val issuerDID = DID.fromString(issuerRegisterDIDResponse.did)
        val issuerUnpublishedDID = createDIDContext.unpublishedDID
*/





        println(
            """
            Issuer DID registered, the transaction can take up to 10 minutes to be confirmed by the Cardano network
            - DID: ${issuerRegisterDidResponse.did}
            - Operation identifier: ${issuerRegisterDidResponse.operationId}
            """.trimIndent()
        )
        println()

        // Issuer generates a token to connect with the credential holder
        val issuerGenerateConnectionTokenRequest = GenerateConnectionTokenRequest(count = 1)
        val issuerConnectionToken = runBlocking {
            connector.GenerateConnectionTokenAuth(
                issuerGenerateConnectionTokenRequest,
                RequestUtils.generateRequestMetadata(
                    issuerDid.value,
                    issuerMasterKeyPair.privateKey,
                    issuerGenerateConnectionTokenRequest
                )
            ).tokens.first()
        }
        println("Issuer: Token for connecting with Holder generated = $issuerConnectionToken")

        // Holder generates its identity to connect with issuer
        val holderMasterKeyPair = EC.generateKeyPair()
        val holderUnpublishedDid = PrismDid.buildLongFormFromMasterPublicKey(holderMasterKeyPair.publicKey)
        println("Holder: First DID generated to connect with Issuer = $holderUnpublishedDid")

        // Holder generates its identity to connect with verifier
        // in PRISM, you are supposed to use different identities for every connection
        // TODO: We'll need to allow accepting connections even if the acceptor's identity already exists
        val holderMasterKeyPair2 = EC.generateKeyPair()
        val holderUnpublishedDid2 = PrismDid.buildLongFormFromMasterPublicKey(holderMasterKeyPair2.publicKey)
        println("Holder: Second DID generated to connect with Verifier = $holderUnpublishedDid2")
        println()

        // Holder verifies the connection token details to make sure its connecting to the right entity
        val issuerConnectionTokenDetails = runBlocking {
            connector.GetConnectionTokenInfo(
                GetConnectionTokenInfoRequest(token = issuerConnectionToken)
            )
        }
        println(
            """
            Holder: Check Issuer's connection token details:
            - Issuer name = ${issuerConnectionTokenDetails.creatorName}
            - Issuer DID  = ${issuerConnectionTokenDetails.creatorDid}
            """.trimIndent()
        )

        // Holder accepts the connection token to connect to Issuer
        // TODO: remove the userId from the response, its totally unnecessary
        val holderAcceptsIssuerConnectionRequest = AddConnectionFromTokenRequest(token = issuerConnectionToken)
        val holderIssuerConnection = runBlocking {
            connector.AddConnectionFromTokenAuth(
                holderAcceptsIssuerConnectionRequest,
                RequestUtils.generateRequestMetadata(
                    holderUnpublishedDid.value,
                    holderMasterKeyPair.privateKey,
                    holderAcceptsIssuerConnectionRequest
                )
            ).connection!!
        }
        println("Holder (DID 1): Connected to Issuer, connectionId = ${holderIssuerConnection.connectionId}")
        println()

        // Issuer generates a credential to Holder and the credential in a batch
        val issuingKeyInfo =
            PrismKeyInformation(
                PrismDid.DEFAULT_ISSUING_KEY_ID,
                PrismKeyType.ISSUING_KEY,
                issuerIssuingKeyPair.publicKey
            )
        val addIssuingKeyDidInfo = issuerNodePayloadGenerator.updateDid(
            issuerCreateDidInfo.operationHash,
            PrismDid.DEFAULT_MASTER_KEY_ID,
            keysToAdd = arrayOf(issuingKeyInfo)
        )
        val addIssuingKeyOperationId = runBlocking {
            nodeAuthApi.updateDid(
                addIssuingKeyDidInfo.payload,
                issuerDid,
                PrismDid.DEFAULT_MASTER_KEY_ID,
                issuerCreateDidInfo.operationHash,
                keysToAdd = arrayOf(issuingKeyInfo),
                keysToRevoke = arrayOf()
            )
        }
        val issueCredentialsInfo = issuerNodePayloadGenerator.issueCredentials(
            PrismDid.DEFAULT_ISSUING_KEY_ID,
            arrayOf(
                CredentialClaim(
                    subjectDid = holderUnpublishedDid,
                    content =
                    JsonObject(
                        mapOf(
                            Pair("name", JsonPrimitive("Panos Mitronikas")),
                            Pair("certificate", JsonPrimitive("Certificate Of TOGAF Certification Complition"))
                        )
                    )
                )
            )
        )
        val issueOperationId = runBlocking {
            nodeAuthApi.issueCredentials(
                issueCredentialsInfo.payload,
                issuerDid,
                PrismDid.DEFAULT_ISSUING_KEY_ID,
                issueCredentialsInfo.merkleRoot
            )
        }

        val holderSignedCredential = issueCredentialsInfo.credentialsAndProofs.first().signedCredential
        val holderCredentialMerkleProof = issueCredentialsInfo.credentialsAndProofs.first().inclusionProof
        println(
            """
            Issuer: Credential issued to Holder, the transaction can take up to 10 minutes to be confirmed by the Cardano network
            - IssuerDID = $issuerDid
            - Add issuing key to DID operation identifier = $addIssuingKeyOperationId
            - Issuer credential batch operation identifier = $issueOperationId
            - Credential content = ${holderSignedCredential.content}
            - Signed credential = ${holderSignedCredential.canonicalForm}
            - Inclusion proof (encoded) = ${holderCredentialMerkleProof.encode()}
            - Batch id = ${issueCredentialsInfo.batchId}
            """.trimIndent()
        )

        // Issuer sends the credential to Holder through the connector
        val credentialFromIssuerMessage = AtalaMessage(
            message = AtalaMessage.Message.PlainCredential(
                PlainTextCredential(
                    encodedCredential = holderSignedCredential.canonicalForm,
                    encodedMerkleProof = holderCredentialMerkleProof.encode()
                )
            )
        )

        // Issuer needs the connection id to send a message to Holder, which can be retrieved
        // from the token generated before.
        val issuerGetConnectionRequest = GetConnectionByTokenRequest(issuerConnectionToken)
        val issuerHolderConnectionId = runBlocking {
            connector.GetConnectionByTokenAuth(
                issuerGetConnectionRequest,
                RequestUtils.generateRequestMetadata(
                    issuerUnpublishedDid.value,
                    issuerMasterKeyPair.privateKey,
                    issuerGetConnectionRequest
                )
            ).connection?.connectionId!!
        }

        // Connector allows any kind of message, this is just a way to send a credential but you can define your own
        val issuerSendMessageRequest = SendMessageRequest(
            issuerHolderConnectionId,
            pbandk.ByteArr(credentialFromIssuerMessage.encodeToByteArray())
        )
        runBlocking {
            connector.SendMessageAuth(
                issuerSendMessageRequest,
                RequestUtils.generateRequestMetadata(
                    issuerUnpublishedDid.value,
                    issuerMasterKeyPair.privateKey,
                    issuerSendMessageRequest
                )
            )
        }
        println("Issuer: Credential sent to Holder")
        println()

        // Holder receives the credential from Issuer
        val holderGetMessagesRequest = GetMessagesPaginatedRequest(limit = 1)
        val holderReceivedMessage = runBlocking {
            connector.GetMessagesPaginatedAuth(
                holderGetMessagesRequest,
                RequestUtils.generateRequestMetadata(
                    holderUnpublishedDid.value,
                    holderMasterKeyPair.privateKey,
                    holderGetMessagesRequest
                )
            ).messages.first()
        }

        val holderReceivedCredential = AtalaMessage
            .decodeFromByteArray(holderReceivedMessage.message.array)
            .plainCredential!!
        println(
            """
            Holder: Message received
            - Canonical credential = ${holderReceivedCredential.encodedCredential}
            - Inclusion proof = ${holderReceivedCredential.encodedMerkleProof}
            """.trimIndent()
        )
        println()

        // Verifier claims an identity, similar to the previous example done with Issuer
        println("Verifier: Generates and registers a DID")
        val verifierMnemonic = KeyDerivation.randomMnemonicCode()
        val verifierSeed = KeyDerivation.binarySeed(verifierMnemonic, "secret")
        val verifierMasterKeyPair = KeyGenerator.deriveKeyFromFullPath(verifierSeed, 0, PrismKeyType.MASTER_KEY, 0)
        val verifierUnpublishedDid = PrismDid.buildLongFormFromMasterPublicKey(verifierMasterKeyPair.publicKey)
        val verifierDid = verifierUnpublishedDid.asCanonical()

        val verifierNodePayloadGenerator = NodePayloadGenerator(
            verifierUnpublishedDid,
            mapOf(
                Pair(PrismDid.DEFAULT_MASTER_KEY_ID, verifierMasterKeyPair.privateKey)
            )
        )
        val verifierCreateDidInfo = verifierNodePayloadGenerator.createDid()

        val verifierCreateDidOperationId = runBlocking {
            nodeAuthApi.createDid(
                verifierCreateDidInfo.payload,
                verifierUnpublishedDid,
                PrismDid.DEFAULT_MASTER_KEY_ID
            )
        }

        // Wait until Node confirms the DID
        waitUntilConfirmed(nodeAuthApi, verifierCreateDidOperationId)

        val verifierRegisterDidResponse = runBlocking {
            connector.RegisterDID(
                RegisterDIDRequest(
                    registerWith = RegisterDIDRequest.RegisterWith.ExistingDid(verifierDid.value),
                    name = "Verifier"
                )
            )
        }
        println(
            """
            Verifier DID registered, the transaction can take up to 10 minutes to be confirmed by the Cardano network
            - DID: $verifierDid
            - Operation identifier: ${verifierRegisterDidResponse.operationId}
            """.trimIndent()
        )
        println()

        // Verifier generates a token to connect with the credential holder
        val verifierGenerateConnectionTokenRequest = GenerateConnectionTokenRequest(count = 1)
        val verifierConnectionToken = runBlocking {
            connector.GenerateConnectionTokenAuth(
                verifierGenerateConnectionTokenRequest,
                RequestUtils.generateRequestMetadata(
                    verifierUnpublishedDid.value,
                    verifierMasterKeyPair.privateKey,
                    verifierGenerateConnectionTokenRequest
                )
            ).tokens.first()
        }
        println("Verifier: Token for connecting with Holder generated = $verifierConnectionToken")
        println()

        // Holder accepts the connection token to connect to Verifier
        val holderAcceptsVerifierConnectionRequest = AddConnectionFromTokenRequest(token = verifierConnectionToken)
        val holderVerifierConnection = runBlocking {
            connector.AddConnectionFromTokenAuth(
                holderAcceptsVerifierConnectionRequest,
                RequestUtils.generateRequestMetadata(
                    holderUnpublishedDid2.value,
                    holderMasterKeyPair2.privateKey,
                    holderAcceptsVerifierConnectionRequest
                )
            )
                .connection!!
        }
        println("Holder (DID 2): Connected to Verifier, connectionId = ${holderVerifierConnection.connectionId}")

        // Holder shares a credential with Verifier
        val credentialFromHolderMessage = AtalaMessage(
            message = AtalaMessage.Message.PlainCredential(
                PlainTextCredential(
                    encodedCredential = holderReceivedCredential.encodedCredential,
                    encodedMerkleProof = holderReceivedCredential.encodedMerkleProof
                )
            )
        )

        val holderSendMessageRequest = SendMessageRequest(
            holderVerifierConnection.connectionId,
            pbandk.ByteArr(credentialFromHolderMessage.encodeToByteArray())
        )
        runBlocking {
            connector.SendMessageAuth(
                holderSendMessageRequest,
                RequestUtils.generateRequestMetadata(
                    holderUnpublishedDid2.value,
                    holderMasterKeyPair2.privateKey,
                    holderSendMessageRequest
                )
            )
        }
        println("Holder (DID 2): Credential sent to Verifier")
        println()

        // Verifier receives the credential shared from Holder
        val verifierGetMessagesRequest = GetMessagesPaginatedRequest(limit = 1)
        val verifierReceivedMessage = runBlocking {
            connector.GetMessagesPaginatedAuth(
                verifierGetMessagesRequest,
                RequestUtils.generateRequestMetadata(
                    verifierUnpublishedDid.value,
                    verifierMasterKeyPair.privateKey,
                    verifierGetMessagesRequest
                )
            )
                .messages.first()
        }
        val verifierReceivedCredential = AtalaMessage
            .decodeFromByteArray(verifierReceivedMessage.message.array)
            .plainCredential!!
        println(
            """
            Verifier: Message received
            - Canonical credential = ${verifierReceivedCredential.encodedCredential}
            - Inclusion proof = ${verifierReceivedCredential.encodedMerkleProof}
            """.trimIndent()
        )
        println()

        // decode the received credential
        val verifierReceivedJsonCredential =
            JsonBasedCredential.fromString(verifierReceivedCredential.encodedCredential)
        val verifierReceivedCredentialIssuerDid = verifierReceivedJsonCredential.content.getString("id")!!
        val verifierReceivedCredentialIssuanceKeyId =
            verifierReceivedJsonCredential.content.getString("keyId")!!
        val verifierReceivedCredentialMerkleProof =
            MerkleInclusionProof.decode(verifierReceivedCredential.encodedMerkleProof)
        val verifierReceivedCredentialBatchId = CredentialBatches.computeCredentialBatchId(
            PrismDid.fromString(verifierReceivedCredentialIssuerDid),
            verifierReceivedCredentialMerkleProof.derivedRoot()
        )
        println(
            """
            Verifier: Received credential decoded
            - Credential: ${verifierReceivedJsonCredential.content}
            - Issuer DID: $verifierReceivedCredentialIssuerDid
            - Issuer issuance key id: $verifierReceivedCredentialIssuanceKeyId
            - Merkle proof root: ${verifierReceivedCredentialMerkleProof.hash.hexValue}
            """.trimIndent()
        )
        println()

        // Verifier using convinience method (which return no errors)
        println("Verifier: Verifying received credential using single convenience method")
        Thread.sleep(1000) // give some time to the backend to apply the operation
        val credentialVerificationServiceResult = runBlocking {
            nodeAuthApi.verify(
                signedCredential = verifierReceivedJsonCredential,
                merkleInclusionProof = verifierReceivedCredentialMerkleProof
            )
        }
        require(credentialVerificationServiceResult.verificationErrors.isEmpty()) {
            "VerificationErrors should be empty"
        }

        // Issuer revokes the credential
        val revocationKeyInfo =
            PrismKeyInformation(
                PrismDid.DEFAULT_REVOCATION_KEY_ID,
                PrismKeyType.REVOCATION_KEY,
                issuerRevocationKeyPair.publicKey
            )
        val addRevocationKeyDidInfo = issuerNodePayloadGenerator.updateDid(
            addIssuingKeyDidInfo.operationHash,
            PrismDid.DEFAULT_MASTER_KEY_ID,
            keysToAdd = arrayOf(revocationKeyInfo)
        )
        val addRevocationKeyOperationId = runBlocking {
            nodeAuthApi.updateDid(
                addRevocationKeyDidInfo.payload,
                issuerDid,
                PrismDid.DEFAULT_MASTER_KEY_ID,
                addIssuingKeyDidInfo.operationHash,
                keysToAdd = arrayOf(revocationKeyInfo),
                arrayOf()
            )
        }
        val revokeCredentialsInfo = issuerNodePayloadGenerator.revokeCredentials(
            PrismDid.DEFAULT_REVOCATION_KEY_ID,
            issueCredentialsInfo.operationHash,
            issueCredentialsInfo.batchId.id,
            arrayOf(holderSignedCredential.hash())
        )
        val revokeCredentialsOperationId = runBlocking {
            nodeAuthApi.revokeCredentials(
                revokeCredentialsInfo.payload,
                issuerDid,
                PrismDid.DEFAULT_REVOCATION_KEY_ID,
                issueCredentialsInfo.operationHash,
                issueCredentialsInfo.batchId.id,
                arrayOf(holderSignedCredential.hash())
            )
        }
        println(
            """
            Issuer: Credential revoked, the transaction can take up to 10 minutes to be confirmed by the Cardano network
            - Add revocation key operation identifier: $addRevocationKeyOperationId
            - Revoke credentials operation identifier: $revokeCredentialsOperationId
            """.trimIndent()
        )
        println()

        // Verifier resolves the credential revocation time from the node
        println("Verifier: Checking the credential validity again, expect an error explaining that the credential is revoked")
        waitUntilConfirmed(nodeAuthApi, revokeCredentialsOperationId)
        val verifierReceivedCredentialRevocationTime2 = runBlocking {
            nodeAuthApi.getCredentialRevocationTime(
                batchId = verifierReceivedCredentialBatchId.id,
                credentialHash = verifierReceivedJsonCredential.hash()
            )
        }

        // Verifier checks the credential validity (which fails)
        val credentialVerificationServiceResult2 = runBlocking {
            nodeAuthApi.verify(
                signedCredential = verifierReceivedJsonCredential,
                merkleInclusionProof = verifierReceivedCredentialMerkleProof
            )
        }

        require(
            credentialVerificationServiceResult2.verificationErrors.contains(
                VerificationError.CredentialWasRevokedOn(
                    verifierReceivedCredentialRevocationTime2.ledgerData!!.timestampInfo
                )
            )
        ) { "CredentialWasRevokedOn error is expected" }
    }
}
