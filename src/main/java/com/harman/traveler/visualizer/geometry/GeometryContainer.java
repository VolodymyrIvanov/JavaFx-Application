/*
 * Project        Traveler
 * (c) copyright  2020
 * Company        HARMAN Automotive Systems GmbH
 *        All rights reserved
 *
 * Secrecy Level  STRICTLY CONFIDENTIAL
 *
 * File           GeometryContainer.java
 * Creation date  29.11.2017
 */
package com.harman.traveler.visualizer.geometry;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import com.vividsolutions.jts.geom.util.NoninvertibleTransformationException;

import javafx.scene.paint.Color;

/**
 * Basic container for user defined data.
 * 
 * @author VIvanov
 *
 */
public abstract class GeometryContainer<T> extends MeshGeometry
{
    protected T data;
    
    protected Envelope envelope;
    
    protected int lineWidth;
    
    protected Coordinate anchor;
    
    protected float scale;
    
    protected CoordinateFilter coordinateTransformer;
    
    public GeometryContainer(T data, int lineWidth, Color color, Coordinate anchor, float scale)
    {
        super(color);
        this.data = data;
        this.lineWidth = lineWidth;
        this.anchor = anchor;
        this.scale = scale;
        this.coordinateTransformer = new CoordinateFilter()
        {
            @Override
            public void filter(Coordinate coord)
            {
                if (anchor != null)
                {
                    coord.x -= anchor.x;
                    coord.y -= anchor.y;
                }
                coord.x *= scale;
                coord.y *= scale;
            }
        };
        super.meshView.setUserData(this);
    }
    
    /**
     * @return the envelope
     */
    public Envelope getEnvelope()
    {
        if (envelope == null)
        {
            buildGeometry();
        }
        return envelope;
    }

    /**
     * @return the data
     */
    public T getData()
    {
        return data;
    }

    protected void buildGeometry()
    {
        this.envelope = injectGeometryIntoMesh();
        mesh.getTexCoords().addAll(0, 0);
    }
    
    protected Polygon createTriangle(Point point, int width, float rotationDegrees)
    {
        double apexX = point.getX();
        double apexY = point.getY() + width;
        double leftX = point.getX() - width;
        double leftY = point.getY() - width;
        double rightX = point.getX() + width;
        double rightY = point.getY() - width;
        Coordinate[] trianglePoints = new Coordinate[4];
        trianglePoints[0] = new Coordinate(apexX, apexY, point.getCoordinate().z);
        trianglePoints[1] = new Coordinate(leftX, leftY, point.getCoordinate().z);
        trianglePoints[2] = new Coordinate(rightX, rightY, point.getCoordinate().z);
        trianglePoints[3] = new Coordinate(apexX, apexY, point.getCoordinate().z);
        Polygon trianglePoly = factory.createPolygon(trianglePoints);
        if (rotationDegrees != 0)
        {
            AffineTransformation translation = AffineTransformation.translationInstance(-point.getX(), -point.getY());
            AffineTransformation rotation = AffineTransformation.rotationInstance(Math.toRadians(rotationDegrees));
            trianglePoly = (Polygon) translation.transform(trianglePoly);
            trianglePoly = (Polygon) rotation.transform(trianglePoly);
            try
            {
                trianglePoly = (Polygon) translation.getInverse().transform(trianglePoly);
            }
            catch (NoninvertibleTransformationException e)
            {
            }
        }
        return trianglePoly;
    }
    
    public abstract String getInfo();
    
    protected abstract Envelope injectGeometryIntoMesh();
}
