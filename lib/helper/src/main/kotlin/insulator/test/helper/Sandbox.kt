package insulator.test.helper

import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

fun getTestSandboxFolder(): Path = Paths.get("test-sandbox", UUID.randomUUID().toString()).also { it.toFile().mkdirs() }
fun deleteTestSandboxFolder() = Paths.get("test-sandbox").toFile().deleteRecursively()
