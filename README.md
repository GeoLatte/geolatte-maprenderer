# Mosaic

https://drive.google.com/file/d/1KqRnKhuCO_8MxiXwb0ElCmtxnSd9_Qo1/view

## Git subtree

### Voeg remote repo's toe
    git remote add geolatte-geom git@github.com:GeoLatte/geolatte-geom.git
    git remote add geolatte-maprenderer git@github.com:GeoLatte/geolatte-maprenderer.git
    git remote add geolatte-mapserver git@github.com:GeoLatte/geolatte-mapserver.git

### Initiëel toevoegen van subtree aan project
    git subtree add --prefix=modules/geolatte-geom geolatte-geom transform --squash
    git subtree add --prefix=modules/geolatte-maprenderer geolatte-maprenderer master --squash
    git subtree add --prefix=modules/geolatte-mapserver geolatte-mapserver master --squash

### Wijzigingen binnentrekken van subtrees
    git fetch geolatte-geom transform
    git fetch geolatte-maprenderer master
    git fetch geolatte-mapserver master

    git subtree pull --prefix=modules/geolatte-geom geolatte-geom transform --squash
    git subtree pull --prefix=modules/geolatte-maprenderer geolatte-maprenderer master --squash
    git subtree pull --prefix=modules/geolatte-mapserver geolatte-mapserver master --squash
