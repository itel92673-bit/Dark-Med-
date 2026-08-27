from pathlib import Path
import hashlib
import io
import zipfile
from PIL import Image

supplied = Path('/home/ubuntu/upload/1000045205.png')
source = Path('/home/ubuntu/DarkMed/app/src/main/res/drawable-nodpi/dark_med_icon.png')
apk = Path('/home/ubuntu/DarkMed/deliverables/Dark Med f.apk')
with zipfile.ZipFile(apk) as archive:
    packaged_bytes = archive.read('res/bj.png')

def pixels(path_or_bytes):
    if isinstance(path_or_bytes, bytes):
        image = Image.open(io.BytesIO(path_or_bytes))
    else:
        image = Image.open(path_or_bytes)
    image.load()
    return image.convert('RGBA')

supplied_image = pixels(supplied)
source_image = pixels(source)
packaged_image = pixels(packaged_bytes)
print(f'supplied_sha256={hashlib.sha256(supplied.read_bytes()).hexdigest()}')
print(f'source_sha256={hashlib.sha256(source.read_bytes()).hexdigest()}')
print(f'packaged_png_sha256={hashlib.sha256(packaged_bytes).hexdigest()}')
print(f'supplied_size={supplied_image.size}')
print(f'source_size={source_image.size}')
print(f'packaged_size={packaged_image.size}')
print(f'supplied_source_pixels_equal={supplied_image.tobytes() == source_image.tobytes()}')
print(f'supplied_packaged_pixels_equal={supplied_image.tobytes() == packaged_image.tobytes()}')
print(f'source_packaged_pixels_equal={source_image.tobytes() == packaged_image.tobytes()}')
