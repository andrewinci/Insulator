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


def build_file(jar, url):
    if url is None:
        return None
    hash = adler(jar)
    size = os.path.getsize(jar)
    return f'<file uri="{url}" size="{size}" classpath="true" checksum="{hash}"/>'
