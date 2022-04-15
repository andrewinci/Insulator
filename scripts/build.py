import glob
import os
from helper import build_file
from constants import header, footer, insulator_jar, other_jars
import requests

"""
Must run first
./gradlew getDependencySources
./gradlew :app:mergeLocalLibs
python3 scripts/build.py
"""

jars_path = "./app/build/distributions/app/lib/"

# retrieve os specific jars
for k in other_jars:
    with open(jars_path + k, "wb") as jar:
        r = requests.get(other_jars[k]["uri"], allow_redirects=True)
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
dependency_map.update(other_jars)

# Remove prefix in local jars
jars = {j.replace(jars_path, "") for j in glob.glob(f"{jars_path}*.jar")}

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
