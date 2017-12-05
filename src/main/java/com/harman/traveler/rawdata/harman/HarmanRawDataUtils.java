/*
 * Project        Traveler
 * (c) copyright  2020
 * Company        HARMAN Automotive Systems GmbH
 *        All rights reserved
 *
 * Secrecy Level  STRICTLY CONFIDENTIAL
 *
 * File           HarmanRawDataUtils.java
 * Creation date  24.10.2017
 */
package com.harman.traveler.rawdata.harman;

import java.util.List;

import com.harman.learning.Common.LinearFeature;
import com.harman.learning.Common.Offset;
import com.harman.learning.Common.OffsetDiff;
import com.harman.learning.Common.Position;
import com.harman.learning.TraceFile.TraceFileRecord;
import com.harman.traveler.core.transform.CoordinateSystemTransformer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.linearref.LinearGeometryBuilder;

/**
 * Utility class for Harman raw data (also fleet data). 
 * 
 * @author VIvanov
 *
 */
public class HarmanRawDataUtils
{
    private static GeometryFactory factory = new GeometryFactory();
    
    public static LineString generateGpsTrackLine(List<TraceFileRecord> records)
    {
        LinearGeometryBuilder builder = new LinearGeometryBuilder(factory);
        for (TraceFileRecord record : records)
        {
            Position pos = record.getPosition();
            Coordinate c = new Coordinate(pos.getLongitude(), pos.getLatitude(), pos.getAltitude());
            builder.add(c);
        }
        return (LineString)builder.getGeometry();
    }
    
    public static LineString generateObservationLine(Position observationPoint, LinearFeature line)
    {
        LinearGeometryBuilder builder = new LinearGeometryBuilder(factory);
        Offset startPoint = line.getStartPoint();
        Coordinate startCoord = new Coordinate(
                observationPoint.getLongitude() + CoordinateSystemTransformer.metresToWgs(startPoint.getX()),
                observationPoint.getLatitude() + CoordinateSystemTransformer.metresToWgs(startPoint.getY()),
                observationPoint.getAltitude() + startPoint.getZ());
        builder.add(startCoord);
        Coordinate prevCoord = startCoord;
        for (OffsetDiff diff : line.getPointsList())
        {
            Coordinate c = new Coordinate(
                    prevCoord.x + CoordinateSystemTransformer.metresToWgs(diff.getDx()), 
                    prevCoord.y + CoordinateSystemTransformer.metresToWgs(diff.getDy()), 
                    prevCoord.z + diff.getDz());
            builder.add(c);
            prevCoord = c;
        }
        return (LineString)builder.getGeometry();
    }
    
    public static Point generateObservationPoint(Position observationPoint, Offset offset)
    {
        Coordinate coord = new Coordinate(
                observationPoint.getLongitude() + CoordinateSystemTransformer.metresToWgs(offset.getX()),
                observationPoint.getLatitude() + CoordinateSystemTransformer.metresToWgs(offset.getY()),
                observationPoint.getAltitude() + offset.getZ());
        return factory.createPoint(coord);
    }
}
