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

import com.harman.learning.TraceFile.TracePath;
import com.harman.traveler.rawdata.harman.HarmanRawDataUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineString;

import javafx.scene.paint.Color;

/**
 * Container for TracePath line.
 * 
 * @author VIvanov
 *
 */
public class TracePathContainer extends GeometryContainer<TracePath>
{
    public TracePathContainer(TracePath data, int lineWidth, Color color, Coordinate anchor, float scale)
    {
        super(data, lineWidth, color, new CoordinateFilter()
        {
            @Override
            public void filter(Coordinate coord)
            {
                coord.x -= anchor.x;
                coord.y -= anchor.y;
                coord.z -= anchor.z;
                coord.x *= scale;
                coord.y *= scale;
            }
        });
    }

    @Override
    protected Envelope injectGeometryIntoMesh(int lineWidth, CoordinateFilter coordTransformer)
    {
        LineString line = HarmanRawDataUtils.generateGpsTrackLine(((TracePath)data).getRecordsList());
        if (coordTransformer != null)
        {
            line.apply(coordTransformer);
            line.geometryChanged();
        }
        
        super.addLine(line, lineWidth, .0f);
        return line.getEnvelopeInternal();
    }
}
