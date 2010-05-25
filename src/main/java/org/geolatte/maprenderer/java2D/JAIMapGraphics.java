/*
 * This file is part of the GeoLatte project. This code is licenced under
 * the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * Copyright (C) 2010 - 2010 and Ownership of code is shared by:
 * Qmino bvba - Romeinsestraat 18 - 3001 Heverlee (http://www.Qmino.com)
 * Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
 */

package org.geolatte.maprenderer.java2D;

import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.map.SpatialExtent;
import org.geolatte.maprenderer.reference.SpatialReference;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public class JAIMapGraphics extends MapGraphics {

    private static final int IMAGE_TYPE = BufferedImage.TYPE_INT_ARGB;
    private static final boolean OPTIMIZE_FOR_QUALITY = true;

    private final static Color DEFAULT_BACKGROUND_COLOR = new Color(1.0f, 1.0f, 1.0f, 0.0f);

    private float scale;
    private final int width;
    private final int height;
    private Graphics2D g2;
    private final SpatialReference spatialReference;
    private final BufferedImage image;


    public JAIMapGraphics(Dimension dimension, SpatialReference reference, ColorModel colorModel) {
        this.spatialReference = reference;
        this.width = (int) dimension.getWidth();
        this.height = (int) dimension.getHeight();
        WritableRaster raster = colorModel.createCompatibleWritableRaster(this.width, this.height);
        this.image = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
        initGraphics();
    }

    public JAIMapGraphics(Dimension dimension, SpatialReference reference, boolean transparency) {
        ColorModel colorModel = null;

        if (transparency) {
            ColorSpace space = ColorSpace.getInstance(ColorSpace.CS_sRGB);
            colorModel = new DirectColorModel(space, 32, 0x00ff0000, 0x0000ff00, 0x000000ff, 0xff000000, true, DataBuffer.TYPE_INT);
        } else {
            ColorSpace space = ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB);
            colorModel = new DirectColorModel(space, 24, 0xff0000, 0x00ff00, 0x000ff, 0, false, DataBuffer.TYPE_INT);
        }

        this.spatialReference = reference;
        this.width = (int) dimension.getWidth();
        this.height = (int) dimension.getHeight();
        WritableRaster raster = colorModel.createCompatibleWritableRaster(this.width, this.height);
        this.image = new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
        initGraphics();
    }

    public JAIMapGraphics(Dimension dimension, SpatialReference reference) {
        this(dimension, reference, true);
    }

    public void initGraphics() {
        this.g2 = (Graphics2D) this.image.getGraphics();
        setBackground(DEFAULT_BACKGROUND_COLOR);
        setComposite(AlphaComposite.SrcOver);
        this.clearRect(0, 0, this.width, this.height);
        RenderingHints hints = getDefaultHints();
        setRenderingHints(hints);
    }


    protected RenderingHints getDefaultHints() {
        RenderingHints hints = new RenderingHints(null);
        if (OPTIMIZE_FOR_QUALITY) {
            getDefaultHintsForQuality(hints);
        } else {
            getDefaultHintsForSpeed(hints);
        }

        return hints;
    }

    private RenderingHints getDefaultHintsForSpeed(RenderingHints hints) {
        hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        return hints;
    }

    private RenderingHints getDefaultHintsForQuality(RenderingHints hints) {
        hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);

        //hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return hints;
    }

    @Override
    public void setToExtent(SpatialExtent extent) {
        if (!extent.getSpatialReference().equals(getSpatialReference()))
            throw new IllegalArgumentException("Spatial Reference of extent object must be EPSG: " + getSpatialReference().getEPSGCode());
        AffineTransform atf = new AffineTransform();
        double sx = width / extent.getWidth();
        double sy = height / extent.getHeight();
        this.scale = (float) Math.min(sx, sy);
        atf.scale(sx, -sy);
        atf.translate(-extent.getMinX(), -extent.getMaxY());
//        AffineTransform tr = (AffineTransform) projection.clone();
//        tr.concatenate(modelView);
        setTransform(atf);
    }

    @Override
    public Dimension getDimension() {
        return new Dimension(width, height);
    }

    @Override
    public SpatialReference getSpatialReference() {
        return this.spatialReference;
    }

    @Override
    public float getScale() {
        return this.scale;
    }

    @Override
    public RenderedImage createRendering() {
        return this.image;
    }

    public Graphics create() {
        return g2.create();
    }

    public Color getColor() {
        return g2.getColor();
    }

    public void setColor(Color c) {
        g2.setColor(c);
    }

    public void setPaintMode() {
        g2.setPaintMode();
    }

    public void setXORMode(Color c1) {
        g2.setXORMode(c1);
    }

    public Font getFont() {
        return g2.getFont();
    }

    public void setFont(Font font) {
        g2.setFont(font);
    }

    public FontMetrics getFontMetrics(Font f) {
        return g2.getFontMetrics(f);
    }

    public Rectangle getClipBounds() {
        return g2.getClipBounds();
    }

    public void clipRect(int x, int y, int width, int height) {
        g2.clipRect(x, y, width, height);
    }

    public void setClip(int x, int y, int width, int height) {
        g2.setClip(x, y, width, height);
    }

    public Shape getClip() {
        return g2.getClip();
    }

    public void setClip(Shape clip) {
        g2.setClip(clip);
    }

    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        g2.copyArea(x, y, width, height, dx, dy);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        g2.drawLine(x1, y1, x2, y2);
    }

    public void fillRect(int x, int y, int width, int height) {
        g2.fillRect(x, y, width, height);
    }

    public void clearRect(int x, int y, int width, int height) {
        g2.clearRect(x, y, width, height);
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g2.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        g2.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public void draw3DRect(int x, int y, int width, int height, boolean raised) {
        g2.draw3DRect(x, y, width, height, raised);
    }

    public void fill3DRect(int x, int y, int width, int height, boolean raised) {
        g2.fill3DRect(x, y, width, height, raised);
    }

    public void drawOval(int x, int y, int width, int height) {
        g2.drawOval(x, y, width, height);
    }

    public void fillOval(int x, int y, int width, int height) {
        g2.fillOval(x, y, width, height);
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        g2.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        g2.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        g2.drawPolyline(xPoints, yPoints, nPoints);
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        g2.drawPolygon(xPoints, yPoints, nPoints);
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        g2.fillPolygon(xPoints, yPoints, nPoints);
    }

    public void drawString(String str, int x, int y) {
        g2.drawString(str, x, y);
    }

    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return g2.drawImage(img, x, y, observer);
    }

    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        return g2.drawImage(img, x, y, width, height, observer);
    }

    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        return g2.drawImage(img, x, y, bgcolor, observer);
    }

    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        return g2.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return g2.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        return g2.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
    }

    public void dispose() {
        g2.dispose();
    }

    public void draw(Shape s) {
        g2.draw(s);
    }

    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        return g2.drawImage(img, xform, obs);
    }

    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        g2.drawRenderedImage(img, xform);
    }

    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        g2.drawRenderableImage(img, xform);
    }

    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        g2.drawImage(img, op, x, y);
    }

    public void drawString(String s, float x, float y) {
        g2.drawString(s, x, y);
    }

    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        g2.drawString(iterator, x, y);
    }

    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        g2.drawString(iterator, x, y);
    }

    public void drawGlyphVector(GlyphVector v, float x, float y) {
        g2.drawGlyphVector(v, x, y);
    }

    public void fill(Shape s) {
        g2.fill(s);
    }

    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return g2.hit(rect, s, onStroke);
    }

    public GraphicsConfiguration getDeviceConfiguration() {
        return g2.getDeviceConfiguration();
    }

    public FontRenderContext getFontRenderContext() {
        return g2.getFontRenderContext();
    }

    public void setComposite(Composite comp) {
        g2.setComposite(comp);
    }

    public void setPaint(Paint paint) {
        g2.setPaint(paint);
    }

    public void setStroke(Stroke s) {
        g2.setStroke(s);
    }

    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        g2.setRenderingHint(hintKey, hintValue);
    }

    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return g2.getRenderingHint(hintKey);
    }

    public RenderingHints getRenderingHints() {
        return g2.getRenderingHints();
    }

    public void translate(int x, int y) {
        g2.translate(x, y);
    }

    public void translate(double x, double v) {
        g2.translate(x, v);
    }

    public void rotate(double theta) {
        g2.rotate(theta);
    }

    public void rotate(double theta, double v, double x) {
        g2.rotate(theta, v, x);
    }

    public void scale(double sx, double v) {
        g2.scale(sx, v);
    }

    public void shear(double shx, double v) {
        g2.shear(shx, v);
    }

    public void transform(AffineTransform Tx) {
        g2.transform(Tx);
    }

    public void setTransform(AffineTransform Tx) {
        g2.setTransform(Tx);
    }

    public AffineTransform getTransform() {
        return g2.getTransform();
    }

    public Paint getPaint() {
        return g2.getPaint();
    }

    public Composite getComposite() {
        return g2.getComposite();
    }

    public void setBackground(Color color) {
        g2.setBackground(color);
    }

    public Color getBackground() {
        return g2.getBackground();
    }

    public Stroke getStroke() {
        return g2.getStroke();
    }

    public void clip(Shape s) {
        g2.clip(s);
    }


    public void setRenderingHints(Map<?, ?> hints) {
        g2.setRenderingHints(hints);
    }

    public void addRenderingHints(Map<?, ?> hints) {
        g2.addRenderingHints(hints);
    }

    public Graphics create(int x, int y, int width, int height) {
        return g2.create(x, y, width, height);
    }

    public FontMetrics getFontMetrics() {
        return g2.getFontMetrics();
    }

    public void drawRect(int x, int y, int width, int height) {
        g2.drawRect(x, y, width, height);
    }

    public void drawPolygon(Polygon p) {
        g2.drawPolygon(p);
    }

    public void fillPolygon(Polygon p) {
        g2.fillPolygon(p);
    }

    public void drawChars(char[] data, int offset, int length, int x, int y) {
        g2.drawChars(data, offset, length, x, y);
    }

    public void drawBytes(byte[] data, int offset, int length, int x, int y) {
        g2.drawBytes(data, offset, length, x, y);
    }

    public void finalize() {
        g2.finalize();
    }

    public String toString() {
        return g2.toString();
    }

    @Deprecated
    public Rectangle getClipRect() {
        return g2.getClipRect();
    }

    public boolean hitClip(int x, int y, int width, int height) {
        return g2.hitClip(x, y, width, height);
    }

    public Rectangle getClipBounds(Rectangle r) {
        return g2.getClipBounds(r);
    }
}
