/*
 * Project        Traveler
 * (c) copyright  2020
 * Company        HARMAN Automotive Systems GmbH
 *        All rights reserved
 *
 * Secrecy Level  STRICTLY CONFIDENTIAL
 *
 * File           TraceFileRecordContainer.java
 * Creation date  30.11.2017
 */
package com.harman.traveler.visualizer.geometry;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.harman.learning.Common.Position;
import com.harman.learning.TraceFile.TraceFileRecord;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import javafx.scene.paint.Color;

/**
 * Container for TraceFileRecord point.
 * 
 * @author VIvanov
 *
 */
public class TraceFileRecordContainer extends GeometryContainer<TraceFileRecord>
{
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    
    public TraceFileRecordContainer(TraceFileRecord data, int lineWidth, Color color, Coordinate anchor, float scale,
            float elevation)
    {
        super(data, lineWidth, color, new CoordinateFilter()
        {
            @Override
            public void filter(Coordinate coord)
            {
                coord.x -= anchor.x;
                coord.y -= anchor.y;
                coord.z = elevation;
                coord.x *= scale;
                coord.y *= scale;
            }
        });
    }

    @Override
    protected Envelope injectGeometryIntoMesh(int lineWidth, CoordinateFilter coordTransformer)
    {
        Position pos = data.getPosition();
        float heading = data.getHeading();
        Point point = factory.createPoint(new Coordinate(pos.getLongitude(), pos.getLatitude(), 0.0));
        if (coordTransformer != null)
        {
            point.apply(coordTransformer);
            point.geometryChanged();
        }
        Polygon trianglePoly = super.createTriangle(point, lineWidth, -heading);
        super.addTriangle(trianglePoly, (float)point.getCoordinate().z);
        return trianglePoly.getEnvelopeInternal();
    }

    @Override
    public String getInfo()
    {
        return data.getRecordType().name() + "\nat " + 
               String.format("Lat: %.5f, Lon: %.5f, Alt: %.3f", data.getPosition().getLatitude(), 
                       data.getPosition().getLongitude(), data.getPosition().getAltitude()) +
               "\nTimestamp: " + dateFormat.format(new Date(data.getTimestamp())) +
               "\nHeading: " + String.format("%.2f", data.getHeading());
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
