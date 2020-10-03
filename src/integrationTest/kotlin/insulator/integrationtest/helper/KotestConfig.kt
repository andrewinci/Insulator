package insulator.integrationtest.helper

import insulator.di.CONFIG_FILE_NAME
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.test.TestCaseOrder
import java.net.URLDecoder
import java.nio.file.Paths

object ProjectConfig : AbstractProjectConfig() {

    override val parallelism = 1
    override val testCaseOrder = TestCaseOrder.Random

    override fun beforeAll() {
        mockCurrentAppVersion("999.999.999")
        super.beforeAll()
    }

    fun mockCurrentAppVersion(version: String) {
        val jarPath = Paths.get(this::class.java.protectionDomain.codeSource.location.toURI()).toString()
        val jarFolder = Paths.get(URLDecoder.decode(jarPath, "UTF-8")).parent.toAbsolutePath().toString()
        val configPath =
            with(Paths.get(jarFolder, CONFIG_FILE_NAME).toAbsolutePath().toFile()) {
                createNewFile()
                writeText("app.version=$version")
            }
    }
}