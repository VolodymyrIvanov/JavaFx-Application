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

import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Envelope;

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
    
    public GeometryContainer(T data, int lineWidth, Color color, CoordinateFilter coordTransformer)
    {
        super(color);
        this.data = data;
        buildGeometry(lineWidth, coordTransformer);
        super.meshView.setUserData(data);
    }
    
    /**
     * @return the envelope
     */
    public Envelope getEnvelope()
    {
        return envelope;
    }

    protected void buildGeometry(int lineWidth, CoordinateFilter coordTransformer)
    {
        this.envelope = injectGeometryIntoMesh(lineWidth, coordTransformer);
        mesh.getTexCoords().addAll(0, 0);
    }
    
    protected abstract Envelope injectGeometryIntoMesh(int lineWidth, CoordinateFilter coordTransformer);
}
