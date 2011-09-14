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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
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

    private final static Logger LOGGER = LoggerFactory.getLogger(ExternalGraphicsRepository.class);

    private final Map<String, GraphicSource> cache = new ConcurrentHashMap<String, GraphicSource>();

    //TODO -- verify the exception-handling scenario's
    //TODO -- replace the concurrentHashMap with ehcache (in order to control growth of the cache)

    public ExternalGraphicsRepository(String[] localGraphicsPackage) {
        for (String packageName :localGraphicsPackage){
            readGraphicsFromPackage(packageName);
        }
    }

    public GraphicSource get(String url) throws IOException {
        GraphicSource img = getFromCache(url);
        if (img == null) {
            img = retrieveImageFromUrl(url);
            storeInCache(url, img);
        }
        return img;
    }

    public void storeInCache(String url, GraphicSource source) {
        if (url == null || source == null) throw new IllegalArgumentException();
        this.cache.put(url, source);
    }

    public GraphicSource getFromCache(String url) {
        return this.cache.get(url);

    }

    private void readGraphicsFromPackage(String packageName) {
        InputStream graphicsIndex = getGraphicsIndex(packageName);
        if (graphicsIndex == null) {
            LOGGER.warn("Can't find package " + packageName +", or package doesn't have a graphics.index file.");
            return;
        }
        try {
            Properties props = readGraphicsIndex(graphicsIndex);
            readGraphicsFromPackage(packageName, props);
        } catch (IOException e){
            LOGGER.warn("Error reading from package " + packageName, e);
        }
    }

    private void readGraphicsFromPackage(String packageName, Properties props) throws IOException {
        Enumeration<String> enumeration = (Enumeration<String>)props.propertyNames();
        while (enumeration.hasMoreElements()) {
            String url = enumeration.nextElement();
            if (getFromCache(url) != null) continue;
            GraphicSource img = retrieveFromClassPath(packageName + "/" + props.getProperty(url).trim());
            this.cache.put(url, img);
        }
    }

    private Properties readGraphicsIndex(InputStream graphicsIndexFile) throws IOException {
        Properties props =  new Properties();
        props.loadFromXML(graphicsIndexFile);
        return props;
    }

    private GraphicSource retrieveFromClassPath(String path) throws IOException {
        InputStream in = getResourceAsStream(path);
        if (in == null) {
            LOGGER.warn(String.format("Graphics file %s not found on classpath.", path));
            return null;
        }
        //try to read it as an image (png, jpeg,..)
        RenderedImage img = ImageIO.read(in);
        if (img != null) return new RenderedImageGraphicSource(img);
        //if unsuccesfull, try to read it as an SVG
        //TODO -- implement svg parsing
        throw new IllegalArgumentException("File " + path + " is neither image nor svg.");
    }

    private GraphicSource retrieveImageFromUrl(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("Can't retrieve image from " + url);
        }

        HttpEntity entity = response.getEntity();
        if (contentTypeIsSVG(entity)) {
            //TODO --implement svg
            return null;
        } else {
            RenderedImage img = ImageIO.read(entity.getContent());
            if (img == null) throw new IOException("Response from " + url + " is not recognized as an image.");
            return new RenderedImageGraphicSource(img);
        }
    }

    private boolean contentTypeIsSVG(HttpEntity entity) {
        return "image/svg+xml".equalsIgnoreCase(entity.getContentType().getValue());
    }

    private InputStream getGraphicsIndex(String packageName) {
        return getResourceAsStream(packageName + "/graphics.index");
    }

    private InputStream getResourceAsStream(String resource) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
    }
}
