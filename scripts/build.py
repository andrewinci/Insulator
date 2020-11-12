import glob
import os
from constants import header, footer, manual_dependencies
from helper import build_file

"""
Must run first
./gradlew getDependencySources
./gradlew :app:mergeLocalLibs
python3.8 scripts/build.py
"""

RELEASE = os.environ.get("RELEASE_VERSION")
insulator_jar = {
    "insulator.jar": f"https://github.com/andrea-vinci/Insulator/releases/download/{RELEASE}/insulator.jar"
}
jars_path = "./app/build/distributions/app/lib/"

dependency_files = set(
    glob.glob("./**/**/**/dependencies.txt") + glob.glob("./**/**/dependencies.txt")
)
dependencies = [
    l.replace("\n", "").split("\t")
    for x in [open(f).readlines() for f in dependency_files]
    for l in x
]
dependency_map = {j: url for [j, url] in dependencies}
dependency_map.update(insulator_jar)
jars = [j.replace(jars_path, "") for j in glob.glob(f"{jars_path}*.jar")]

files = {j: build_file(jars_path + j, dependency_map.get(j)) for j in jars}
files.update(manual_dependencies)

print("Writing new update4j config file")
with open("insulator-update.xml", "w") as config:
    config.write(header + "\n")
    for jar, xml in files.items():
        if xml is None:
            raise Exception(f"Missing jar {jar}")
        config.write(xml + "\n")
    config.write(footer)
