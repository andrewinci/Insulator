import os

header = """<configuration timestamp="2022-02-12T00:00:00.000000000Z">
    <base uri="" path="${app.lib}"/>
    <properties>
        <property key="app.name" value="Insulator"/>
        <property key="app.lib" value="${LOCALAPPDATA}/${app.name}/" os="win"/>
        <property key="app.lib" value="${user.home}/Library/Application Support/${app.name}/" os="mac"/>
        <property key="app.lib" value="${user.home}/.config/${app.name}/" os="linux"/>
        <property key="default.launcher.main.class" value="insulator.AppKt"/>
        <property key="default.launcher.main.classpath" value="insulator.jar"/>
    </properties>
    <files>"""

footer = """</files>
</configuration>"""

RELEASE = os.environ.get("RELEASE_VERSION")

insulator_jar = {
    "insulator.jar": {
        "uri": f"https://github.com/andrewinci/Insulator/releases/download/{RELEASE}/insulator.jar"
    }
}

other_jars = {
    "kotlinx-serialization-runtime-jvm-1.0-M1-1.4.0-rc.jar": {
        "uri": "https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-serialization-runtime-jvm/1.0-M1-1.4.0-rc/kotlinx-serialization-runtime-jvm-1.0-M1-1.4.0-rc.jar"
    },
    # javafx for mac
    "javafx-swing-16-mac.jar": {
        "uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-swing/16/javafx-swing-16-mac.jar",
        "os": "mac",
    },
    "javafx-graphics-16-mac.jar": {
        "uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/16/javafx-graphics-16-mac.jar",
        "os": "mac",
    },
    "javafx-base-16-mac.jar": {
        "uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-base/16/javafx-base-16-mac.jar",
        "os": "mac",
    },
    "javafx-controls-16-mac.jar": {
        "uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/16/javafx-controls-16-mac.jar",
        "os": "mac",
    },
    "javafx-fxml-16-mac.jar": {
        "uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/16/javafx-fxml-16-mac.jar",
        "os": "mac",
    },
    # javafx for linux
    "javafx-swing-16-linux.jar": {
        "uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-swing/16/javafx-swing-16-linux.jar",
        "os": "linux",
    },
    "javafx-graphics-16-linux.jar": {
        "uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/16/javafx-graphics-16-linux.jar",
        "os": "linux",
    },
    "javafx-base-16-linux.jar": {
        "uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-base/16/javafx-base-16-linux.jar",
        "os": "linux",
    },
    "javafx-controls-16-linux.jar": {
        "uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/16/javafx-controls-16-linux.jar",
        "os": "linux",
    },
    "javafx-fxml-16-linux.jar": {
        "uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/16/javafx-fxml-16-linux.jar",
        "os": "linux",
    },
    # javafx for windows
    "javafx-swing-16-win.jar": {
        "uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-swing/16/javafx-swing-16-win.jar",
        "os": "win",
    },
    "javafx-graphics-16-win.jar": {
        "uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/16/javafx-graphics-16-win.jar",
        "os": "win",
    },
    "javafx-base-16-win.jar": {
        "uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-base/16/javafx-base-16-win.jar",
        "os": "win",
    },
    "javafx-controls-16-win.jar": {
        "uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/16/javafx-controls-16-win.jar",
        "os": "win",
    },
    "javafx-fxml-16-win.jar": {
        "uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/16/javafx-fxml-16-win.jar",
        "os": "win",
    },
    # missing transitive dependencies
    "telemetry-events-api-7.0.1-ce.jar": {
        "uri": "https://packages.confluent.io/maven/io/confluent/telemetry-events/7.0.1-ce/telemetry-events-7.0.1-ce.jar"
    },
    "jackson-core-2.12.5.jar": {
        "uri": "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.12.5/jackson-core-2.12.5.jar"
    },
    "snappy-java-1.1.8.1.jar": {
        "uri": "https://repo1.maven.org/maven2/org/xerial/snappy/snappy-java/1.1.8.1/snappy-java-1.1.8.1.jar"
    },
    "zstd-jni-1.5.0-2.jar": {
        "uri": "https://repo1.maven.org/maven2/com/github/luben/zstd-jni/1.5.0-2/zstd-jni-1.5.0-2.jar"
    },
    "snakeyaml-1.27.jar": {
        "uri": "https://repo1.maven.org/maven2/org/yaml/snakeyaml/1.27/snakeyaml-1.27.jar"
    },
    "lz4-java-1.7.1.jar": {
        "uri": "https://repo1.maven.org/maven2/org/lz4/lz4-java/1.7.1/lz4-java-1.7.1.jar"
    },
    "jackson-dataformat-yaml-2.12.5.jar": {
        "uri": "https://repo1.maven.org/maven2/com/fasterxml/jackson/dataformat/jackson-dataformat-yaml/2.12.5/jackson-dataformat-yaml-2.12.5.jar"
    },
    "jackson-annotations-2.12.5.jar": {
        "uri": "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.12.5/jackson-annotations-2.12.5.jar"
    },
}
