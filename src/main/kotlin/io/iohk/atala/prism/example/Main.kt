package io.iohk.atala.prism.example

import java.sql.DriverManager.println
import io.iohk.atala.prism.api.*
import io.iohk.atala.prism.crypto.*
import io.iohk.atala.prism.identity.*
import io.iohk.atala.prism.credentials.*
import io.iohk.atala.prism.credentials.content.*
import io.iohk.atala.prism.credentials.json.*
import kotlinx.datetime.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

fun main() {
    LittlePrism.run()
}
