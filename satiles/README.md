# INSTALLATION

## You need gdal-devel for this project
```Bash
dnf install gdal-devel
```


## make virtualenv
```Bash
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

# Start project
```Bash
python satiles.py
```

# Usage
```json
POST /
{
	"url": "https://peps.cnes.fr/resto/collections/S2ST/07ddd0cf-defa-53e8-ac34-c8a1b3e8cb7c/download"
}

=> /path/to/img/dir/07ddd0cf-defa-53e8-ac34-c8a1b3e8cb7c

GET /check-img-status?img=07ddd0cf-defa-53e8-ac34-c8a1b3e8cb7c

=> true if finished
```
