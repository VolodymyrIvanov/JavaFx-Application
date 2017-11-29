/*
 * Project        Traveler
 * (c) copyright  2020
 * Company        HARMAN Automotive Systems GmbH
 *        All rights reserved
 *
 * Secrecy Level  STRICTLY CONFIDENTIAL
 *
 * File           GridGeometry.java
 * Creation date  29.11.2017
 */
package com.harman.traveler.visualizer.geometry;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.LineSegment;

import javafx.scene.paint.Color;

/**
 * Implementation for grid.
 * 
 * @author VIvanov
 *
 */
public class GridGeometry extends MeshGeometry
{
    public GridGeometry(Envelope envelope, int lineWidth, Color color, int gridSize, float elevation)
    {
        super(color);
        buildGrid(envelope, lineWidth, gridSize, elevation);
    }
    
    protected void buildGrid(Envelope envelope, int lineWidth, int gridSize, float elevation)
    {
        LineSegment left = new LineSegment(envelope.getMinX(), envelope.getMinY(), envelope.getMinX(), envelope.getMaxY());
        super.addLineSegment(left, lineWidth, elevation);
        LineSegment top = new LineSegment(envelope.getMinX(), envelope.getMaxY(), envelope.getMaxX(), envelope.getMaxY());
        super.addLineSegment(top, lineWidth, elevation);
        LineSegment right = new LineSegment(envelope.getMaxX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY());
        super.addLineSegment(right, lineWidth, elevation);
        LineSegment bottom = new LineSegment(envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMinY());
        super.addLineSegment(bottom, lineWidth, elevation);
        
        int step = (int)Math.round(envelope.getWidth() / gridSize);
        for (int i = 1; i < gridSize; ++i)
        {
            //Vertical
            LineSegment gridVLine = new LineSegment(envelope.getMinX() + i * step, envelope.getMinY(), envelope.getMinX() + i * step, envelope.getMaxY());
            super.addLineSegment(gridVLine, lineWidth, elevation);
            //Horizontal
            LineSegment gridHLine = new LineSegment(envelope.getMinX(), envelope.getMinY() + i * step, envelope.getMaxX(), envelope.getMinY() + i * step);
            super.addLineSegment(gridHLine, lineWidth, elevation);
        }
        mesh.getTexCoords().addAll(0, 0);
    }
}
