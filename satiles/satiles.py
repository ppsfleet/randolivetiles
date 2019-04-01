from aiohttp import web
from aiojobs.aiohttp import setup, spawn
import aiohttp
import asyncio
from zipfile import ZipFile
import os
from gdal2tiles import generate_tiles


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

            extract_img(filename)
            await gen_tiles(filename)


async def gen_tiles(filename):
            generate_tiles(filename + ".jp2", filename, np_processes=8, zoom="6-11")


def extract_img(filename):
    with ZipFile(filename + ".zip", 'r') as zfile:
        for member in zfile.infolist():
            if "TCI_10m" in member.filename:
                member.filename = filename + ".jp2"
                zfile.extract(member)


async def sat_img_downloader(request):
    data = await request.json()
    img_url = data["url"]
    filename = img_url.split("/")[-2]
    print(img_url)

    await spawn(request, downloadfile(img_url, filename))

    response = web.Response(text="bite", status=202)
    await response.prepare(request)
    await response.write_eof()

    return response


app = web.Application()
app.router.add_post('/', sat_img_downloader)
setup(app)
web.run_app(app)
