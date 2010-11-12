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


import org.geolatte.core.Feature;
import org.geolatte.maprenderer.map.Labeler;
import org.geolatte.maprenderer.map.MapGraphics;
import org.geolatte.maprenderer.reference.Projector;
import org.geolatte.maprenderer.sld.FeatureTypeStyle;
import org.hibernatespatial.readers.FeatureReader;

public class BasicLabeler implements Labeler {


    private Iterable<Feature> reader;
    private FeatureTypeStyle style;
    private Projector projector;
    private final LabelPlacer labelPlacer = new BasicLabelPlacer();

    public BasicLabeler(Iterable<Feature> reader, FeatureTypeStyle style, Projector trf) {
        this.reader = reader;
        this.style = style;
        this.projector = trf;
    }

    public void label(MapGraphics g2, FeatureReader reader) {
//		ReferencedEnvelope renv = this.projector.inverse().project(aoi);
//
//		for (Rule rule : style.getRules()){
//			Filter filter = rule.getFilter();
//			for (AbstractSymbolizer symbolizer: rule.getSymbolizers()){
//				if (symbolizer instanceof TextSymbolizer){
//					TextSymbolizer symb = (TextSymbolizer)symbolizer;
//					FontRenderContext frc = g2.getFontRenderContext();
//					Font font = null;
//					try {
//						font = symb.getFont().deriveFont(frc.getTransform().createInverse());
//					} catch (NoninvertibleTransformException e) {
//						throw new RuntimeException(e);
//					}
//
//					System.out.println(font);
//					g2.setColor(Color.BLACK);
//					g2.setFont(font);
//					int co = 0;
//					for (FeatureReader fi = this.reader.createFeatureReader(renv); fi.hasNext();){
//						Feature f = fi.next();
//						if ( filter != null && !filter.evaluate(f)){
//							continue;
//						}
//						String lbl = f.getAttribute(symb.getLabelProperty()).toString();
//						Geometry geom = this.projector.project(f.getGeometry());
//						//TODO avoid duplicate call to getStringBounds
//						LabelRect labelRect = this.labelPlacer.createLabelRect(geom,lbl, font, frc);
//						//labelRect.draw(g2);
//						//g2.draw(labelRect.getBounds());
//						//TextLayout layout = new TextLayout(lbl, font,frc);
//						//g2.drawString(lbl, (float)anchor.getX(), (float)anchor.getY());
//						context2DJAI.addLabelRect(labelRect);
//						co++;
//
//					}
//					System.out.println("Labeled: " + co);
//				}
//		}
//
//	}

    }

}
