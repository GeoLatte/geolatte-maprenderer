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
 * Copyright (C) 2010 - 2017 and Ownership of code is shared by:
 * Qmino bvba - Romeinsestraat 18 - 3001 Heverlee  (http://www.qmino.com)
 * Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
 */
package org.geolatte.geom.codec;

/**
 * The HANA EWKT decoder is a variant of the Postgis EWKT decoder. The differences are that it uses a different
 * tokenizer and a different set of keywords.
 * 
 * @author Jonathan Bregler, SAP
 */
class HANAWktDecoder extends PostgisWktDecoder {

	private final static HANAWktVariant WKT_GEOM_TOKENS = new HANAWktVariant();

	public HANAWktDecoder() {
		super( WKT_GEOM_TOKENS );
	}

	@Override
	protected void setTokenizer(AbstractWktTokenizer tokenizer) {
		if ( tokenizer instanceof WktTokenizer ) {
			WktTokenizer wktTokenizer = (WktTokenizer) tokenizer;
			super.setTokenizer( new HANAWktTokenizer( wktTokenizer.wkt, WKT_GEOM_TOKENS, wktTokenizer.baseCRS, wktTokenizer.forceToCRS ) );
		}
		else {
			throw new IllegalArgumentException( "The tokenizer must be an instance of " + WktTokenizer.class.getName() );
		}

	}
}
