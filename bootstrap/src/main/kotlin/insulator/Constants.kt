package insulator

import java.nio.file.Path

const val APP_NAME = "Insulator"
val LOCAL_CONFIG_PATH: String by lazy {
    with(System.getProperty("os.name")) {
        when {
            contains("nux") -> "${System.getProperty("user.home")!!}/.config/$APP_NAME/"
            contains("win") -> "${System.getenv("LOCALAPPDATA")}/$APP_NAME"
            else -> "${System.getProperty("user.home")!!}/Library/Application Support/$APP_NAME/"
        }
    }
}

val LOCAL_CONFIG_FILE_PATH = "${LOCAL_CONFIG_PATH}insulator-update.xml"
val TMP_UPDATE_PATH: Path = Path.of(System.getProperty("java.io.tmpdir") ?: "", "update.zip")
val DEV_FILE_PATH = "${LOCAL_CONFIG_PATH}DEV_MODE"
const val LATEST_RELEASE_URL = "https://github.com/andrewinci/Insulator/releases/latest/download/insulator-update.xml"
