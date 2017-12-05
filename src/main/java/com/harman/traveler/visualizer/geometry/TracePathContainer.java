/*
 * Project        Traveler
 * (c) copyright  2020
 * Company        HARMAN Automotive Systems GmbH
 *        All rights reserved
 *
 * Secrecy Level  STRICTLY CONFIDENTIAL
 *
 * File           TracePathContainer.java
 * Creation date  29.11.2017
 */
package com.harman.traveler.visualizer.geometry;

import com.harman.learning.Common.Position;
import com.harman.learning.TraceFile.TraceFileRecord;
import com.harman.learning.TraceFile.TracePath;
import com.harman.learning.TraceFile.RecordType;
import com.harman.traveler.core.transform.CoordinateSystemTransformer;
import com.harman.traveler.rawdata.harman.HarmanRawDataUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import javafx.scene.paint.Color;

/**
 * Container for TracePath line.
 * 
 * @author VIvanov
 *
 */
public class TracePathContainer extends GeometryContainer<TracePath>
{
    private double pathLength;
    
    public TracePathContainer(TracePath data, int lineWidth, Color color, Coordinate anchor, float scale)
    {
        super(data, lineWidth, color, anchor, scale);
        evaluatePathLength();
    }

    @Override
    protected Envelope injectGeometryIntoMesh()
    {
        LineString line = HarmanRawDataUtils.generateGpsTrackLine(((TracePath)data).getRecordsList());
        if (coordinateTransformer != null)
        {
            line.apply(coordinateTransformer);
            line.geometryChanged();
        }
        
        super.addLine(line, lineWidth, .0f);
        //First point is observation (temporary)
        for (int i = 1; i < data.getRecordsCount(); ++i)
        {
            TraceFileRecord rec = data.getRecordsList().get(i);
            //Skip all observations
            if (!RecordType.OBSERVATION.equals(rec.getRecordType()))
            {
                Position pos = rec.getPosition();
                float heading = rec.getHeading();
                Point point = factory.createPoint(new Coordinate(pos.getLongitude(), pos.getLatitude(), 0.0));
                if (coordinateTransformer != null)
                {
                    point.apply(coordinateTransformer);
                    point.geometryChanged();
                }
                Polygon trianglePoly = super.createTriangle(point, lineWidth, -heading);
                super.addTriangle(trianglePoly, .0f);
            }
        }
        return line.getEnvelopeInternal();
    }
    
    private void evaluatePathLength()
    {
        this.pathLength = 0;
        Position prevPosition = null;
        for (TraceFileRecord rec : data.getRecordsList())
        {
            if (prevPosition == null)
            {
                prevPosition = rec.getPosition();
            }
            else
            {
                double wgsDistance = new Coordinate(prevPosition.getLongitude(), 
                                                    prevPosition.getLatitude(), 
                                                    prevPosition.getAltitude()).distance(
                                     new Coordinate(rec.getPosition().getLongitude(),
                                                    rec.getPosition().getLatitude(),
                                                    rec.getPosition().getAltitude()));
                double metersDistance = CoordinateSystemTransformer.wgsToMetres(wgsDistance);
                pathLength += metersDistance;
                prevPosition = rec.getPosition();
            }
        }
    }
    
    @Override
    public String getInfo()
    {
        return "Path has size " + data.getSerializedSize() + " bytes\nlength " + String.format("%.3f meters\n", pathLength) +
               data.getRecordsCount() + " points";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getInfo();
    }
}
