import os
from zlib import adler32

BLOCKSIZE = 256 * 1024 * 1024


def adler(fname):
    with open(fname, "rb") as f:
        asum = 1
        while True:
            data = f.read(BLOCKSIZE)
            if not data:
                break
            asum = adler32(data, asum)
            if asum < 0:
                asum += 2 ** 32
        return hex(asum)[2:10].zfill(8).lower()


def build_file(jar, info):
    if info["uri"] is None:
        return None
    hash = adler(jar)
    size = os.path.getsize(jar)
    os_attr = ("os=\""+info["os"]+"\" ") if "os" in info else ""
    return f'<file {os_attr}uri="{info["uri"]}" size="{size}" classpath="true" checksum="{hash}"/>'
