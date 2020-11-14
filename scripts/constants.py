header = """<configuration timestamp="2020-11-12T00:00:00.000000000Z">
    <base uri="" path="${app.lib}"/>
    <properties>
        <property key="app.name" value="Insulator"/>
        <property key="app.dir" value="${user.dir}/${app.name}"/>
        <property key="app.lib" value="${LOCALAPPDATA}/${app.name}/lib" os="win"/>
        <property key="app.lib" value="${app.dir}/lib"/>
        <property key="default.launcher.main.class" value="insulator.AppKt"/>
        <property key="default.launcher.main.classpath" value="insulator.jar"/>
    </properties>
    <files>"""

footer = """</files>
</configuration>"""

manual_dependencies = {
    "kotlinx-serialization-runtime-jvm-1.0-M1-1.4.0-rc-218.jar": '<file uri="https://jcenter.bintray.com/org/jetbrains/kotlinx/kotlinx-serialization-runtime-jvm/1.0-M1-1.4.0-rc-218/kotlinx-serialization-runtime-jvm-1.0-M1-1.4.0-rc-218.jar" size="531216" classpath="true" checksum="222f5a14"/>',
    # javafx for mac
    "javafx-swing-15.0.1-mac.jar": '<file os="mac" uri="https://repo1.maven.org/maven2/org/openjfx/javafx-swing/15.0.1/javafx-swing-15.0.1-mac.jar"  size="88725" classpath="true" checksum="f761e2bf"/>',
    "javafx-graphics-15.0.1-mac.jar": '<file os="mac" uri="https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/15.0.1/javafx-graphics-15.0.1-mac.jar"  size="4962652" classpath="true" checksum="6511c8ad"/>',
    "javafx-base-15.0.1-mac.jar": '<file os="mac" uri="https://repo1.maven.org/maven2/org/openjfx/javafx-base/15.0.1/javafx-base-15.0.1-mac.jar"  size="745527" classpath="true" checksum="cb1b6659"/>',
    "javafx-controls-15.0.1-mac.jar": '<file os="mac" uri="https://repo1.maven.org/maven2/org/openjfx/javafx-controls/15.0.1/javafx-controls-15.0.1-mac.jar"  size="2531050" classpath="true" checksum="98ed0adf"/>',
    "javafx-fxml-15.0.1-mac.jar": '<file os="mac" uri="https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/15.0.1/javafx-fxml-15.0.1-mac.jar"  size="128790" classpath="true" checksum="b15b8785"/>',
    # javafx for linux
    "javafx-swing-15.0.1-linux.jar": '<file os="linux" uri="https://repo1.maven.org/maven2/org/openjfx/javafx-swing/15.0.1/javafx-swing-15.0.1-linux.jar"  size="88725" classpath="true" checksum="62b59890"/>',
    "javafx-graphics-15.0.1-linux.jar": '<file os="linux" uri="https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/15.0.1/javafx-graphics-15.0.1-linux.jar"  size="5063475" classpath="true" checksum="46774dc6"/>',
    "javafx-base-15.0.1-linux.jar": '<file os="linux" uri="https://repo1.maven.org/maven2/org/openjfx/javafx-base/15.0.1/javafx-base-15.0.1-linux.jar"  size="745527" classpath="true" checksum="d6bedd4d"/>',
    "javafx-controls-15.0.1-linux.jar": '<file os="linux" uri="https://repo1.maven.org/maven2/org/openjfx/javafx-controls/15.0.1/javafx-controls-15.0.1-linux.jar"  size="2531099" classpath="true" checksum="8e5467f3"/>',
    "javafx-fxml-15.0.1-linux.jar": '<file os="linux" uri="https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/15.0.1/javafx-fxml-15.0.1-linux.jar"  size="128790" classpath="true" checksum="ecb5193e"/>',
    # javafx for windows
    "javafx-swing-15.0.1-win.jar": '<file os="win" uri="https://repo1.maven.org/maven2/org/openjfx/javafx-swing/15.0.1/javafx-swing-15.0.1-win.jar"  size="88725" classpath="true" checksum="34bf6673"/>',
    "javafx-graphics-15.0.1-win.jar": '<file os="win" uri="https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/15.0.1/javafx-graphics-15.0.1-win.jar"  size="5898578" classpath="true" checksum="4eef746f"/>',
    "javafx-base-15.0.1-win.jar": '<file os="win" uri="https://repo1.maven.org/maven2/org/openjfx/javafx-base/15.0.1/javafx-base-15.0.1-win.jar"  size="745526" classpath="true" checksum="95ac37b3"/>',
    "javafx-controls-15.0.1-win.jar": '<file os="win" uri="https://repo1.maven.org/maven2/org/openjfx/javafx-controls/15.0.1/javafx-controls-15.0.1-win.jar"  size="2531028" classpath="true" checksum="94c5eb71"/>',
    "javafx-fxml-15.0.1-win.jar": '<file os="win" uri="https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/15.0.1/javafx-fxml-15.0.1-win.jar"  size="128790" classpath="true" checksum="6a818590"/>',
}
