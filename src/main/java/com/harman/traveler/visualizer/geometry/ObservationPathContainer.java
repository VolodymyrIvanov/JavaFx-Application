/*
 * Project        Traveler
 * (c) copyright  2020
 * Company        HARMAN Automotive Systems GmbH
 *        All rights reserved
 *
 * Secrecy Level  STRICTLY CONFIDENTIAL
 *
 * File           ObservationPathContainer.java
 * Creation date  05.12.2017
 */
package com.harman.traveler.visualizer.geometry;

import com.harman.learning.Common.Position;
import com.harman.learning.ObservationFile.NewEntry;
import com.harman.traveler.rawdata.harman.HarmanRawDataUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

import javafx.scene.paint.Color;

/**
 * Container for NewEntry observation.
 * 
 * @author VIvanov
 *
 */
public class ObservationPathContainer extends GeometryContainer<NewEntry>
{
    private Position observationPoint;
    
    public ObservationPathContainer(NewEntry data, int lineWidth, Color color, Position observationPoint,
            Coordinate anchor, float scale)
    {
        super(data, lineWidth, color, anchor, scale);
        this.observationPoint = observationPoint;
    }

    @Override
    protected Envelope injectGeometryIntoMesh()
    {
        switch (data.getObservationTypeCase())
        {
            case LINEOBSERVATION:
                LineString line = HarmanRawDataUtils.generateObservationLine(observationPoint, data.getLineObservation().getLine());
                if (coordinateTransformer != null)
                {
                    line.apply(coordinateTransformer);
                    line.geometryChanged();
                }
                super.addLine(line, lineWidth, .0f);
                return line.getEnvelopeInternal();
                
            case POINTOBSERVATION:
                Point point = HarmanRawDataUtils.generateObservationPoint(observationPoint, data.getPointObservation().getOffset());
                if (coordinateTransformer != null)
                {
                    point.apply(coordinateTransformer);
                    point.geometryChanged();
                }
                //TODO: Add primitives for points
                return point.getEnvelopeInternal();
                
            default:
                //Nothing to do
        }
        return null;
    }

    @Override
    public String getInfo()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
