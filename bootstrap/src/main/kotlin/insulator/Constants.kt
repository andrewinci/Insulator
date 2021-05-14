package insulator

import java.nio.file.Path

const val appName = "Insulator"
val localPath: String by lazy {
    with(System.getProperty("os.name")) {
        when {
            contains("nux") -> "${System.getProperty("user.home")!!}/.config/$appName/"
            contains("win") -> "${System.getenv("LOCALAPPDATA")}/$appName"
            else -> "${System.getProperty("user.home")!!}/Library/Application Support/$appName/"
        }
    }
}

val localConfigFile = "${localPath}insulator-update.xml"
val updatePath: Path = Path.of(System.getProperty("java.io.tmpdir") ?: "", "update.zip")
const val configPath = "https://github.com/andrewinci/Insulator/releases/latest/download/insulator-update.xml"
