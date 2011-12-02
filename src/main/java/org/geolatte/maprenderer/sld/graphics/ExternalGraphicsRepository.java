/*
 * This file is part of the GeoLatte project.
 *
 *     GeoLatte is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     GeoLatte is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with GeoLatte.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright (C) 2010 - 2011 and Ownership of code is shared by:
 *  Qmino bvba - Esperantolaan 4 - 3001 Heverlee  (http://www.qmino.com)
 *  Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
 */

package org.geolatte.maprenderer.sld.graphics;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.geolatte.maprenderer.util.SVGDocumentIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A Repository for ExternalGraphic images.
 *
 * <p>Clients can get {@link ExternalGraphic}s from this repository by URL. The ExternalGraphic is first search in the internal cache,
 * then it is search in local symbol packages (see below), finally it is searched on the internet.</p>
 *
 * <p>An <code>ExternalGraphicsRepository</code> can be provided with a set of local symbol packages. These packages (jars)
 * must reside on the class path and contain a properties file 'graphics.index' listing the URL's and image filenames.</p>
 *
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 9/14/11
 */
public class ExternalGraphicsRepository {

    public final static float DEFAULT_SIZE = 16f;
    public final static float DEFAULT_ROTATION = 0f;

    private final static Logger LOGGER = LoggerFactory.getLogger(ExternalGraphicsRepository.class);

    private final Map<ImageKey, BufferedImage> cache = new ConcurrentHashMap<ImageKey, BufferedImage>();
    private final Map<String, SVGDocument> svgCache = new ConcurrentHashMap<String, SVGDocument>();


    //TODO -- verify the exception-handling scenario's
    //TODO -- replace the concurrentHashMaps with ehcache (in order to control growth of the cache). Note that ehcache is already a dependency

    public ExternalGraphicsRepository(String[] localGraphicsPackage) {
        for (String packageName :localGraphicsPackage){
            readGraphicsFromPackage(packageName);
        }
    }

    public BufferedImage get(String url) throws IOException {
        return get(url, DEFAULT_SIZE, DEFAULT_ROTATION, false);
    }

    public BufferedImage get(String url, float size, float rotation, boolean sizeSet) throws IOException {
        ImageKey key = new ImageKey(url, size, rotation);
        BufferedImage image = getFromCache(key);
        if (image == null) {
            image = retrieve(url, size, sizeSet);
            if (rotation != 0) {
                image = rotate(image, rotation);
            }
            storeInCache(key, image);
        }
        return image;
    }

    public void storeInCache(ImageKey url, BufferedImage source) {
        if (url == null || source == null) throw new IllegalArgumentException();
        this.cache.put(url, source);
    }

    public void storeInSvgCache(String url, SVGDocument svgDoc) {
        if (url == null || svgDoc == null) throw new IllegalArgumentException();
        this.svgCache.put(url, svgDoc);
    }

    public BufferedImage getFromCache(ImageKey url) {
        return this.cache.get(url);
    }

    public SVGDocument getSVGFromCache(String url) {
        return this.svgCache.get(url);
    }

    /**
     * Reads all the graphics from a package and stores the images in the image cache and svg's
     * in the svg cache.
     *
     * @param packageName
     */
    private void readGraphicsFromPackage(String packageName) {
        InputStream graphicsIndex = getGraphicsIndex(packageName);
        if (graphicsIndex == null) {
            LOGGER.warn("Can't find package " + packageName +", or package doesn't have a graphics.index file.");
            return;
        }
        try {
            Properties index = readGraphicsIndex(graphicsIndex);
            readGraphicsFromPackage(packageName, index);
        } catch (IOException e){
            LOGGER.warn("Error reading from package " + packageName, e);
        }
    }

    private void readGraphicsFromPackage(String packageName, Properties index) throws IOException {
        Enumeration<String> enumeration = (Enumeration<String>)index.propertyNames();
        while (enumeration.hasMoreElements()) {
            String url = enumeration.nextElement();
            String path = packageName + "/" + index.getProperty(url).trim();
            retrieveAndStore(url, path);
        }
    }

    private Properties readGraphicsIndex(InputStream graphicsIndexFile) throws IOException {
            Properties index =  new Properties();
            index.loadFromXML(graphicsIndexFile);
            return index;
    }

    /**
     * Retrieves image or SVG from path and stores it in the cache (svg or image cache)
     * @param uri
     * @param path
     * @throws IOException
     */
    private void retrieveAndStore(String uri, String path) throws IOException {
        InputStream inputStream = getResourceAsInputStream(path);
        if (inputStream == null) {
            throw new IOException(String.format("Graphics file %s not found on classpath.", path));
        }

        //try to read it as an image (png, jpeg,..)
        BufferedImage img = ImageIO.read(inputStream);
        if (img != null) {
            storeInCache(new ImageKey(uri),img);
            return;
        }
        //ImageIO.read() removes the first 8 characters from the input stream, so we need to reset it.
        inputStream = getResourceAsInputStream(path);

        //if unsuccesfull, try to read it as an SVG
        SVGDocument svg = SVGDocumentIO.read(uri, inputStream);
        if (svg != null) {
            storeInSvgCache(uri, svg);
            return;
        }
        throw new IOException("File " + path + " is neither image nor svg.");
    }

    /**
     * Retrieves the image from specified URL.
     *
     * If the url points to an SVG, the SVG is rendered at the specified size. If the
     * url points to an bitmap image, then the image is scaled to size only if sizeSet is true.
     *
     * @param url URL to the graphic source.
     * @param size size at which to render the image (for SVG) or to which to scale (if sizeSet)
     * @param sizeSet whether or not a specific size is explicitly requested for the image
     * @return
     * @throws IOException
     */
    private BufferedImage retrieve(String url, float size, boolean sizeSet) throws IOException {
        //check if we have the image in default_size of
        BufferedImage unscaledImage = getFromCache(new ImageKey(url));
        if (unscaledImage != null) {
            return sizeSet ? scale(unscaledImage, size) : unscaledImage;
        }
        //check if we have it in the SVG Cache
        SVGDocument cachedSVG = this.svgCache.get(url);
        if (cachedSVG != null){
            return transCodeSVG(cachedSVG, size);
        }
        //if not, retrieve from URL.
        HttpEntity entity = retrieveGraphicFromUrl(url);
        if (contentTypeIsSVG(entity)) {
            return SVGFromURLResponse(url, size, entity);
        } else {
            return scale(ImageFromURLResponse(url, entity), size);
        }
    }

    private BufferedImage ImageFromURLResponse(String url, HttpEntity entity) throws IOException {
        BufferedImage img = ImageIO.read(entity.getContent());
        if (img == null) throw new IOException("Response from " + url + " is not recognized as an image.");
        //remember the default image (before rotating and scaling)
        storeInCache(new ImageKey(url), img);
        return img;
    }

    private BufferedImage SVGFromURLResponse(String url, float size, HttpEntity entity) throws IOException {
        SVGDocument svg = SVGDocumentIO.read(url, entity.getContent());
        if (svg == null) throw new IOException("Response from " + url + " is not recognized as SVG document.");
        storeInSvgCache(url, svg);
        return transCodeSVG(svg, size);
    }

    private HttpEntity retrieveGraphicFromUrl(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("Can't retrieve image from " + url);
        }

        return response.getEntity();
    }

    private BufferedImage rotate(BufferedImage img, float rotation) {
        AffineTransform tx = new AffineTransform();
        //determine center point of image
        int sx = img.getMinX() + img.getWidth()/2;
        int sy = img.getMinY() + img.getHeight()/2;
        double theta = Math.toRadians(rotation);
        tx.rotate(theta, sx, sy);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(img, null);
    }

    private BufferedImage transCodeSVG(SVGDocument svg, float size){
        if (size < 0) {
            size = DEFAULT_SIZE;
        }
        SVGTranscoder transcoder = new SVGTranscoder();
        SVGSVGElement svgRootElement = svg.getRootElement();
        float svgWidth = svgRootElement.getWidth().getBaseVal().getValue();
        float svgHeight = svgRootElement.getHeight().getBaseVal().getValue();
//        float aspectRatio = svgWidth/svgHeight;
//        int height = Math.round(size);
//        int width = (int)(aspectRatio * height);
        Dimension dim = getWidthAndHeight(svgWidth, svgHeight, size);
        return transcoder.transcode(svg, dim.width, dim.height);
    }

    private BufferedImage scale(BufferedImage unscaledImage, float size) {
        Dimension dim = getWidthAndHeight(unscaledImage.getWidth(), unscaledImage.getHeight(), size);
        AffineTransform tx = new AffineTransform();
        tx.scale(((double)dim.width / unscaledImage.getWidth()), ((double)dim.height) / unscaledImage.getHeight());
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BICUBIC);
        return op.filter(unscaledImage, null);
    }

    private Dimension getWidthAndHeight(float originalWidth, float originalHeight, float newSize){
        float aspectRatio = originalWidth/originalHeight;
        Dimension dim;
        int height = Math.round(newSize);
        int width = (int)(aspectRatio * height);
        return new Dimension(width, height);
    }

    private boolean contentTypeIsSVG(HttpEntity entity) {
        return "image/svg+xml".equalsIgnoreCase(entity.getContentType().getValue());
    }

    private InputStream getGraphicsIndex(String packageName)  {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(packageName + "/graphics.index");
    }

    private InputStream getResourceAsInputStream(String resource)  {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
    }


    public static class ImageKey {
        private final String url;
        private final float size;
        private final float rotation;

        private ImageKey(String url, float size, float rotation) {
            this.url = url;
            this.size = size;
            this.rotation = rotation;
        }

        public ImageKey(String url) {
            this(url, DEFAULT_SIZE, DEFAULT_ROTATION);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ImageKey imageKey = (ImageKey) o;

            if (Float.compare(imageKey.rotation, rotation) != 0) return false;
            if (Float.compare(imageKey.size, size) != 0) return false;
            if (url != null ? !url.equals(imageKey.url) : imageKey.url != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = url != null ? url.hashCode() : 0;
            result = 31 * result + (size != +0.0f ? Float.floatToIntBits(size) : 0);
            result = 31 * result + (rotation != +0.0f ? Float.floatToIntBits(rotation) : 0);
            return result;
        }
    }
}
