import glob
import os
from helper import build_file
import requests

"""
Must run first
./gradlew getDependencySources
./gradlew :app:mergeLocalLibs
python3 scripts/build.py
"""

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
    "insulator.jar": {"uri": f"https://github.com/andrewinci/Insulator/releases/download/{RELEASE}/insulator.jar"}
}

os_specific_jar = {
    "kotlinx-serialization-runtime-jvm-1.0-M1-1.4.0-rc.jar": { "uri": "https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-serialization-runtime-jvm/1.0-M1-1.4.0-rc/kotlinx-serialization-runtime-jvm-1.0-M1-1.4.0-rc.jar"},
    # javafx for mac
    "javafx-swing-16-mac.jar": {"uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-swing/16/javafx-swing-16-mac.jar",  "os":"mac"},
    "javafx-graphics-16-mac.jar": {"uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/16/javafx-graphics-16-mac.jar",  "os":"mac"},
    "javafx-base-16-mac.jar": {"uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-base/16/javafx-base-16-mac.jar", "os":"mac"},
    "javafx-controls-16-mac.jar": {"uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/16/javafx-controls-16-mac.jar", "os":"mac"},
    "javafx-fxml-16-mac.jar": {"uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/16/javafx-fxml-16-mac.jar", "os":"mac"},
    # javafx for linux
    "javafx-swing-16-linux.jar": {"uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-swing/16/javafx-swing-16-linux.jar", "os": "linux"},
    "javafx-graphics-16-linux.jar": {"uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/16/javafx-graphics-16-linux.jar", "os": "linux"},
    "javafx-base-16-linux.jar": {"uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-base/16/javafx-base-16-linux.jar", "os": "linux"},
    "javafx-controls-16-linux.jar": {"uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/16/javafx-controls-16-linux.jar", "os": "linux"},
    "javafx-fxml-16-linux.jar": {"uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/16/javafx-fxml-16-linux.jar", "os": "linux"},
    # javafx for windows
    "javafx-swing-16-win.jar": {"uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-swing/16/javafx-swing-16-win.jar", "os": "win"},
    "javafx-graphics-16-win.jar": {"uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/16/javafx-graphics-16-win.jar", "os": "win"},
    "javafx-base-16-win.jar": {"uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-base/16/javafx-base-16-win.jar", "os": "win"},
    "javafx-controls-16-win.jar": {"uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-controls/16/javafx-controls-16-win.jar", "os": "win"},
    "javafx-fxml-16-win.jar": {"uri": "https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/16/javafx-fxml-16-win.jar", "os": "win"},
}

jars_path = "./app/build/distributions/app/lib/"

# retrieve os specific jars
for k in os_specific_jar:
    with open(jars_path + k, 'wb') as jar:
        r = requests.get(os_specific_jar[k]["uri"], allow_redirects=True)
        jar.write(r.content)

# retrieve jars dependencies from the gradle output
dependency_files = set(
    glob.glob("./**/**/**/dependencies.txt") + glob.glob("./**/**/dependencies.txt")
)
dependencies = [
    l.replace("\n", "").split("\t")
    for x in [open(f).readlines() for f in dependency_files]
    for l in x
]
dependency_map = {j: {"uri": url} for [j, url] in dependencies}
dependency_map.update(insulator_jar)
dependency_map.update(os_specific_jar)

# Remove prefix in local jars
jars = [j.replace(jars_path, "") for j in glob.glob(f"{jars_path}*.jar")]

# Build xml line for each dependency
files = {j: build_file(jars_path + j, dependency_map.get(j)) for j in jars}

# Put all together
print("Writing new update4j config file")
with open("insulator-update.xml", "w") as config:
    config.write(header + "\n")
    for jar, xml in files.items():
        if xml is None:
            raise Exception(f"Missing jar {jar}")
        config.write(xml + "\n")
    config.write(footer)
