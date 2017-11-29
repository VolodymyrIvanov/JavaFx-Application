/*
 * Project        Traveler
 * (c) copyright  2020
 * Company        HARMAN Automotive Systems GmbH
 *        All rights reserved
 *
 * Secrecy Level  STRICTLY CONFIDENTIAL
 *
 * File           MeshGeometry.java
 * Creation date  29.11.2017
 */
package com.harman.traveler.visualizer.geometry;

import java.util.List;

import com.harman.traveler.core.geometry.GeometryHelper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.operation.buffer.BufferParameters;

import javafx.scene.AmbientLight;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 * @author VIvanov
 *
 */
public class MeshGeometry extends Group
{
    protected final static Color DEFAULT_COLOR = Color.WHITE;
    
    protected GeometryFactory factory = new GeometryFactory();
    
    protected TriangleMesh mesh;
    
    protected MeshView meshView;
    
    protected PhongMaterial material;
    
    public MeshGeometry()
    {
        this(DEFAULT_COLOR);
    }
    
    public MeshGeometry(Color color)
    {
        super();
        super.setDepthTest(DepthTest.ENABLE);
        this.mesh = new TriangleMesh();
        this.meshView = new MeshView(mesh);
        meshView.setDrawMode(DrawMode.FILL);
        
        this.material = new PhongMaterial(color);
        material.setDiffuseColor(color);
        material.setSpecularColor(color);
        meshView.setMaterial(material);
        meshView.setCullFace(CullFace.BACK);

        AmbientLight light = new AmbientLight(Color.WHITE);
        light.getScope().add(meshView);
        super.getChildren().add(light);
        super.getChildren().add(meshView);
    }
    
    protected void addLine(LineString line, int lineWidth, float height)
    {
        List<LineSegment> segments = GeometryHelper.segments(line);
        for (LineSegment segment : segments)
        {
            addLineSegment(segment, lineWidth, height);
        }
    }
    
    protected void addLineSegment(LineSegment segment, int lineWidth, float height)
    {
        Geometry poly = segment.toGeometry(factory).buffer(1.0 * lineWidth / 2.0, 4, BufferParameters.CAP_FLAT);
        // Must be 5 points - last point is equal to first ones
        for (int j = 0; j < poly.getCoordinates().length - 1; ++j)
        {
            Coordinate c = poly.getCoordinates()[j];
            mesh.getPoints().addAll((float)c.x, (float) c.y, height);
        }
        // Add 4 faces (front and back)
        int lastIndex = mesh.getPoints().size() / 3 - 1;
        // Front (CW)
        mesh.getFaces().addAll(lastIndex - 3, 0, lastIndex - 2, 0, lastIndex - 1, 0);
        mesh.getFaces().addAll(lastIndex - 3, 0, lastIndex - 1, 0, lastIndex, 0);
        // Back (CCW)
        mesh.getFaces().addAll(lastIndex - 3, 0, lastIndex - 1, 0, lastIndex - 2, 0);
        mesh.getFaces().addAll(lastIndex - 3, 0, lastIndex, 0, lastIndex - 1, 0);
    }
}
