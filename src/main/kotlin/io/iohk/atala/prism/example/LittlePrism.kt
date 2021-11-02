package io.iohk.atala.prism.example

import io.iohk.atala.prism.credentials.*
import io.iohk.atala.prism.credentials.content.CredentialContent
import io.iohk.atala.prism.credentials.json.JsonBasedCredential
import io.iohk.atala.prism.crypto.EC
import io.iohk.atala.prism.crypto.MerkleInclusionProof
import io.iohk.atala.prism.crypto.derivation.KeyDerivation
import io.iohk.atala.prism.crypto.Hash
import io.iohk.atala.prism.crypto.derivation.KeyType
import io.iohk.atala.prism.extras.ProtoClientUtils
import io.iohk.atala.prism.extras.ProtoUtils
import io.iohk.atala.prism.extras.RequestUtils
import io.iohk.atala.prism.identity.DID
import io.iohk.atala.prism.identity.DID.Companion.issuingKeyId
import io.iohk.atala.prism.identity.DID.Companion.masterKeyId
import io.iohk.atala.prism.identity.KeyInformation
import io.iohk.atala.prism.identity.util.ECProtoOps
import io.iohk.atala.prism.protos.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import pbandk.decodeFromByteArray
import pbandk.encodeToByteArray


object LittlePrism {

    fun run() {
        println("""Hello im Little Prism""")
        println("""-This is where we start-""")

        val masterKeyPair = EC.generateKeyPair()
        val did = DID.createUnpublishedDID(masterKeyPair.publicKey)
/*
// DID CREATION 1
        val masterKeyPair = EC.generateKeyPair()
        val did = DID.createUnpublishedDID(masterKeyPair.publicKey)

// DID CREATION 2
        val mnemonic = KeyDerivation.randomMnemonicCode()
        val seed = KeyDerivation.binarySeed(mnemonic, "secret")
        // Create KeyPair out of mnemonic seed phrase, did index, type of key, key index
        val issuerMasterKeyPair = DID.deriveKeyFromFullPath(seed, 0, KeyType.MASTER_KEY, 0)
        val createDIDContext = DID.createDIDFromMnemonic(mnemonic, 0, "secret")
        val issuerCreatedDIDSignedOperation = createDIDContext.createDIDSignedOperation

*/
    }

}
