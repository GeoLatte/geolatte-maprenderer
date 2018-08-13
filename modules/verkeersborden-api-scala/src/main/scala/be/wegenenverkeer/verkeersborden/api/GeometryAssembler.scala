package be.wegenenverkeer.verkeersborden.api

import be.wegenenverkeer.api.verkeersborden.model.geojson
import org.geolatte.geom
import org.geolatte.geom.builder.DSL
import org.geolatte.geom.crs.{CrsRegistry, ProjectedCoordinateReferenceSystem}
import org.geolatte.geom.{C2D, Geometry}

object GeometryAssembler {

  def toTransferObject(geometry: geom.Geometry[C2D]): geojson.Geometry = {
    geometry match {
      case point: geom.Point[C2D]                              => toTransferObject(point)
      case lineString: geom.LineString[C2D]                    => toTransferObject(lineString)
      case multiPoint: geom.MultiPoint[C2D]                    => toTransferObject(multiPoint)
      case multiLineString: geom.MultiLineString[C2D]          => toTransferObject(multiLineString)
      case polygon: geom.Polygon[C2D]                          => toTransferObject(polygon)
      case multiPolygon: geom.MultiPolygon[C2D]                => toTransferObject(multiPolygon)
      case geometryCollection: geom.GeometryCollection[C2D, _] => toTransferObject(geometryCollection)
    }
  }

  def toTransferObject(point: geom.Point[C2D]): geojson.Point = {
    geojson.Point(
      coordinates = getPoints(point).head,
      crs         = Some(geojson.Crs(type$ = "name", properties = geojson.NamedCrsProperty(name = s"EPSG:${point.getSRID}")))
    )
  }

  def toTransferObject(lineString: geom.LineString[C2D]): geojson.LineString = {
    geojson.LineString(
      coordinates = getPoints(lineString),
      crs         = Some(geojson.Crs(type$ = "name", properties = geojson.NamedCrsProperty(name = s"EPSG:${lineString.getSRID}")))
    )
  }

  def toTransferObject(multiPoint: geom.MultiPoint[C2D]): geojson.MultiPoint = {
    geojson.MultiPoint(
      coordinates = getPoints(multiPoint),
      crs         = Some(geojson.Crs(type$ = "name", properties = geojson.NamedCrsProperty(name = s"EPSG:${multiPoint.getSRID}")))
    )
  }

  def toTransferObject(multiLineString: geom.MultiLineString[C2D]): geojson.MultiLineString = {
    geojson.MultiLineString(
      coordinates = (0 until multiLineString.getNumGeometries).toList.map(multiLineString.getGeometryN).map(getPoints),
      crs         = Some(geojson.Crs(type$ = "name", properties = geojson.NamedCrsProperty(name = s"EPSG:${multiLineString.getSRID}")))
    )
  }

  def toTransferObject(polygon: geom.Polygon[C2D]): geojson.Polygon = {

    val exteriorRing  = getPoints(polygon.getExteriorRing)
    val interiorRings = (0 until polygon.getNumInteriorRing).toList.map(polygon.getInteriorRingN).map(getPoints)

    geojson.Polygon(
      coordinates = exteriorRing :: interiorRings,
      crs         = Some(geojson.Crs(type$ = "name", properties = geojson.NamedCrsProperty(name = s"EPSG:${polygon.getSRID}")))
    )
  }

  def toTransferObject(multiPolygon: geom.MultiPolygon[C2D]): geojson.MultiPolygon = {
    geojson.MultiPolygon(
      coordinates = (0 until multiPolygon.getNumGeometries).toList.map(multiPolygon.getGeometryN).map(toTransferObject).map(_.coordinates),
      crs         = Some(geojson.Crs(type$ = "name", properties = geojson.NamedCrsProperty(name = s"EPSG:${multiPolygon.getSRID}")))
    )
  }

  def toTransferObject(geometryCollection: geom.GeometryCollection[C2D, Geometry[C2D]]): geojson.GeometryCollection = {
    geojson.GeometryCollection(
      geometries = (0 until geometryCollection.getNumGeometries).toList.map(geometryCollection.getGeometryN).map(toTransferObject),
      crs        = Some(geojson.Crs(type$ = "name", properties = geojson.NamedCrsProperty(name = s"EPSG:${geometryCollection.getSRID}")))
    )
  }

  /**
    *
    * @param geometry De om te zetten geometry TO.
    * @param crsId De crs id voor de doel geometry (indien None wordt de crs id van de input geometry gebruikt).
    * @return De omgezette geometry.
    */
  def fromTransferObject(geometry: geojson.Geometry, crsId: Option[geom.crs.CrsId]): geom.Geometry[C2D] = {
    geometry match {
      case point: geojson.Point                           => fromTransferObject(point, crsId)
      case multiPoint: geojson.MultiPoint                 => fromTransferObject(multiPoint, crsId)
      case lineString: geojson.LineString                 => fromTransferObject(lineString, crsId)
      case multiLineString: geojson.MultiLineString       => fromTransferObject(multiLineString, crsId)
      case polygon: geojson.Polygon                       => fromTransferObject(polygon, crsId)
      case multiPolygon: geojson.MultiPolygon             => fromTransferObject(multiPolygon, crsId)
      case geometryCollection: geojson.GeometryCollection => fromTransferObject(geometryCollection, crsId)
    }
  }

  def fromTransferObject(point: geojson.Point, crsId: Option[geom.crs.CrsId]): geom.Point[C2D] = {
    createPoint(point.coordinates, getCrs(point, crsId))
  }

  def fromTransferObject(multiPoint: geojson.MultiPoint, crsId: Option[geom.crs.CrsId]): geom.MultiPoint[C2D] = {
    val points = multiPoint.coordinates.map(createPoint(_, getCrs(multiPoint, crsId)))
    new geom.MultiPoint[C2D](points: _*)
  }

  def fromTransferObject(lineString: geojson.LineString, crsId: Option[geom.crs.CrsId]): geom.LineString[C2D] = {
    new geom.LineString[C2D](createPositionSequence(lineString.coordinates), getCrs(lineString, crsId))
  }

  def fromTransferObject(multiLineString: geojson.MultiLineString, crsId: Option[geom.crs.CrsId]): geom.MultiLineString[C2D] = {
    val lineStrings = multiLineString.coordinates
      .map(createPositionSequence)
      .map(new geom.LineString[C2D](_, getCrs(multiLineString, crsId)))
      .toArray
    new geom.MultiLineString[C2D](lineStrings: _*)
  }

  def fromTransferObject(polygon: geojson.Polygon, crsId: Option[geom.crs.CrsId]): geom.Polygon[C2D] = {
    createPolygon(polygon.coordinates, getCrs(polygon, crsId))
  }

  def fromTransferObject(multyPolygon: geojson.MultiPolygon, crsId: Option[geom.crs.CrsId]): geom.MultiPolygon[C2D] = {
    val polygons = multyPolygon.coordinates.map(sequence => createPolygon(sequence, getCrs(multyPolygon, crsId))).toArray
    new geom.MultiPolygon[C2D](polygons: _*)
  }

  def fromTransferObject(geometryCollection: geojson.GeometryCollection,
                         crsId: Option[geom.crs.CrsId]): geom.GeometryCollection[C2D, Geometry[C2D]] = {
    val geometries: List[Geometry[C2D]] = geometryCollection.geometries.map(geom => fromTransferObject(geom, crsId))
    new geom.GeometryCollection[C2D, Geometry[C2D]](geometries: _*)
  }

  private def getPoints(input: geom.Geometry[C2D]): List[List[Double]] = {
    for {
      i <- (0 until input.getNumPositions).toList
      point = input.getPositionN(i)
    } yield {
      List(point.getX, point.getY)
    }
  }

  private def getCrs(geometry: geojson.Geometry, crsId: Option[geom.crs.CrsId]): ProjectedCoordinateReferenceSystem = {
    CrsRegistry.getProjectedCoordinateReferenceSystemForEPSG(crsId.getOrElse(getCrsId(geometry)).getCode)
  }

  protected def getCrsId(to: geojson.Geometry): geom.crs.CrsId = {

    to.crs map { crs =>
      if (crs.type$ != "name") throw new IllegalArgumentException("Geometry zonder 'name' CRS")
      val sridString: String = crs.properties.name
      if (sridString.startsWith("EPSG:")) {
        val srid = sridString.substring(5).toInt
        new geom.crs.CrsId("EPSG", srid)
      } else if (sridString.startsWith("urn:ogc:def:crs:EPSG:")) {
        val srid = sridString.substring(21).toInt
        new geom.crs.CrsId("EPSG", srid)
      } else throw new IllegalArgumentException(s"Geometry met onbekende CRS: $sridString")
    } getOrElse geom.crs.CrsId.UNDEFINED

  }

  /**
    * Helpermethod that creates a point starting from its geojsonto coordinate array
    *
    * @param input      the coordinate array to convert to a point
    * @param crs the crs in which the point is defined
    * @return an instance of a geolatte point corresponding to the given to or null if the given array is null
    */
  private def createPoint(input: List[Double], crs: ProjectedCoordinateReferenceSystem): geom.Point[C2D] = {
    val pos: C2D = DSL.c(input(0), input(1))
    new geom.Point[C2D](pos, crs)
  }

  /**
    * Helpermethod that creates a geolatte pointsequence starting from an array containing coordinate arrays
    *
    * @param coordinates an array containing coordinate arrays
    * @return a geolatte pointsequence or null if the coordinatesequence was null
    */
  private def createPositionSequence(coordinates: List[List[Double]]): geom.PositionSequence[C2D] = {
    if (coordinates.isEmpty) {
      geom.PositionSequenceBuilders.fixedSized(0, classOf[C2D]).toPositionSequence
    } else {
      val psb: geom.PositionSequenceBuilder[C2D] = geom.PositionSequenceBuilders.variableSized(classOf[C2D])
      for (point <- coordinates) {
        psb.add(point.toArray: _*)
      }
      psb.toPositionSequence
    }
  }

  private def createPolygon(coordinates: List[List[List[Double]]], crs: ProjectedCoordinateReferenceSystem): geom.Polygon[C2D] = {
    new geom.Polygon[C2D](coordinates.map(sequence => new geom.LinearRing[C2D](createPositionSequence(sequence), crs)).toArray: _*)
  }

}
