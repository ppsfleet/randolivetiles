from aiohttp import web
from aiojobs.aiohttp import setup, spawn
from concurrent.futures import ProcessPoolExecutor
from zipfile import ZipFile
from gdal2tiles import generate_tiles
import asyncio
import aiohttp
import asyncio
import shutil
import os
import re


async def downloadfile(url, filename):
    auth = aiohttp.BasicAuth("elio.maisonneuve@eisti.fr", "MDPRandolivetiles1")

    async with aiohttp.ClientSession(auth=auth) as session:
        async with session.get(url, timeout=None) as satimg_service_response:
            with open(filename + ".zip", "wb") as zipfile:
                while True:
                    chunk = await satimg_service_response.content.read(16144)
                    if not chunk:
                        break
                    zipfile.write(chunk)


async def extract_img(filename):
    with ZipFile(filename + ".zip", 'r') as zfile:
        for member in zfile.infolist():
            if re.match(r'(.*)(TCI)(.)(?!(20m|30m))(.*)', member.filename):
                filepath = os.path.join(filename, os.path.basename(member.filename))
                member.filename = os.path.basename(member.filename)
                zfile.extract(member)
                yield member.filename


def gen_tiles_wrapper(sat_img):
    generate_tiles(sat_img, sat_img.split(".")[0], zoom="6-11")


async def run_tile_generation(file_extractor):
    async for sat_img in file_extractor:
        print("starting generation on {}".format(sat_img))
        with ProcessPoolExecutor() as process_pool:
            loop = asyncio.get_event_loop()
            await loop.run_in_executor(
                process_pool,
                gen_tiles_wrapper,
                sat_img,
            )
        yield sat_img


async def process_url(url, filename):
    await downloadfile(url, filename)
    images = run_tile_generation(extract_img(filename))

    async for sat_img in images:
        if not os.path.exists(filename):
            os.mkdir(filename)
        shutil.move(sat_img, os.path.join(filename, sat_img))
        sat_tiles_dir = sat_img.split(".")[0]
        shutil.move(sat_tiles_dir, os.path.join(filename, sat_tiles_dir))


async def sat_img_downloader(request):
    data = await request.json()
    img_url = data["url"]
    filename = img_url.split("/")[-2]
    print(img_url)

    await spawn(request, process_url(img_url, filename))

    response = web.Response(text=os.path.join(os.getcwd(), filename), status=202)
    await response.prepare(request)
    await response.write_eof()

    return response


async def check_img_done(request):
    final_folder_created = os.path.exists(request.query["img"])
    return web.json_response(final_folder_created)


app = web.Application()
app.add_routes([web.post('/', sat_img_downloader),
                web.get('/check-img-status', check_img_done)])
setup(app)
web.run_app(app)
