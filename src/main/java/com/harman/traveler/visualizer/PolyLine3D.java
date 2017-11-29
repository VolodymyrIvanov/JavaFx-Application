package com.harman.traveler.visualizer;

import java.util.List;

import com.harman.learning.TraceFile;
import com.harman.learning.TraceFile.TracePath;
import com.harman.learning.Common.Position;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import com.vividsolutions.jts.geom.util.NoninvertibleTransformationException;
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
import javafx.geometry.Point3D;

public class PolyLine3D extends Group
{
    private static GeometryFactory factory = new GeometryFactory();
    public List<Point3D> points;
    public int width = 2;
    public Color color = Color.WHITE;
    private TriangleMesh mesh;
    public MeshView meshView;
    public PhongMaterial material;

    public PolyLine3D(List<Point3D> points, int width, Color color)
    {
        this(points, width, color, null);
    }

    public PolyLine3D(List<Point3D> points, int width, Color color, Object property)
    {
        this.points = points;
        this.width = width;
        this.color = color;
        setDepthTest(DepthTest.ENABLE);
        mesh = new TriangleMesh();
        for (int i = 0; i < points.size() - 1; ++i)
        {
            Point3D startPoint = points.get(i);
            Point3D endPoint = points.get(i + 1);
            LineSegment ls = new LineSegment(new Coordinate(startPoint.getX(), startPoint.getY(), startPoint.getZ()),
                    new Coordinate(endPoint.getX(), endPoint.getY(), endPoint.getZ()));
            Geometry poly = ls.toGeometry(factory).buffer(1.0 * width / 2.0, 4, BufferParameters.CAP_FLAT);
            // Must be 5 points - last point is equal to first ones
            for (int j = 0; j < poly.getCoordinates().length - 1; ++j)
            {
                Coordinate c = poly.getCoordinates()[j];
                mesh.getPoints().addAll((float) c.x, (float) c.y, (float) startPoint.getZ());
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
        if (property != null && property instanceof TracePath)
        {
            TracePath traceFile = (TracePath) property;
            for (int i = 0; i < traceFile.getRecordsList().size(); ++i)
            {
                Point3D point = points.get(i);
                float heading = traceFile.getRecordsList().get(i).getHeading();
                float apexX = (float) point.getX();
                float apexY = (float) point.getY() + width;
                float leftX = (float) point.getX() - width;
                float leftY = (float) point.getY() - width;
                float rightX = (float) point.getX() + width;
                float rightY = (float) point.getY() - width;
                Coordinate[] trianglePoints = new Coordinate[4];
                trianglePoints[0] = new Coordinate(apexX, apexY, point.getZ());
                trianglePoints[1] = new Coordinate(leftX, leftY, point.getZ());
                trianglePoints[2] = new Coordinate(rightX, rightY, point.getZ());
                trianglePoints[3] = new Coordinate(apexX, apexY, point.getZ());
                Polygon trianglePoly = factory.createPolygon(trianglePoints);
                AffineTransformation translation = AffineTransformation.translationInstance(-point.getX(), -point.getY());
                AffineTransformation rotation = AffineTransformation.rotationInstance(Math.toRadians(-heading));
                trianglePoly = (Polygon) translation.transform(trianglePoly);
                trianglePoly = (Polygon) rotation.transform(trianglePoly);
                try
                {
                    trianglePoly = (Polygon) translation.getInverse().transform(trianglePoly);
                }
                catch (NoninvertibleTransformationException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // Must be 4 points - last point is equal to first ones
                for (int j = 0; j < trianglePoly.getCoordinates().length - 1; ++j)
                {
                    Coordinate c = trianglePoly.getCoordinates()[j];
                    mesh.getPoints().addAll((float) c.x, (float) c.y, (float) point.getZ());
                }
                // Add 2 faces (front and back)
                int lastIndex = mesh.getPoints().size() / 3 - 1;
                // Front (CW)
                mesh.getFaces().addAll(lastIndex - 2, 0, lastIndex - 1, 0, lastIndex, 0);
                // Back (CCW)
                mesh.getFaces().addAll(lastIndex - 2, 0, lastIndex, 0, lastIndex - 1, 0);

            }

        }
        // add each point. For each point add another point shifted on Z axis by
        // width
        // This extra point allows us to build triangles later
        // for(Point3D point: points) {
        // mesh.getPoints().addAll((float)point.getX(),(float)point.getY(),(float)point.getZ());
        // mesh.getPoints().addAll((float)point.getX(),(float)point.getY(),(float)point.getZ()+width);
        // }
        // add dummy Texture Coordinate
        mesh.getTexCoords().addAll(0, 0);
        // Now generate trianglestrips for each line segment
        // for(int i=2;i<points.size()*2;i+=2) { //add each segment
        // //Vertices wound counter-clockwise which is the default front face of
        // any Triange
        // //These triangles live on the frontside of the line facing the camera
        // mesh.getFaces().addAll(i,0,i-2,0,i+1,0); //add primary face
        // mesh.getFaces().addAll(i+1,0,i-2,0,i-1,0); //add secondary Width face
        // //Add the same faces but wind them clockwise so that the color looks
        // correct when camera is rotated
        // //These triangles live on the backside of the line facing away from
        // initial the camera
        // mesh.getFaces().addAll(i+1,0,i-2,0,i,0); //add primary face
        // mesh.getFaces().addAll(i-1,0,i-2,0,i+1,0); //add secondary Width face
        // }
        // Need to add the mesh to a MeshView before adding to our 3D scene
        meshView = new MeshView(mesh);
        meshView.setDrawMode(DrawMode.FILL); // Fill so that the line shows
                                             // width
        material = new PhongMaterial(color);
        material.setDiffuseColor(color);
        material.setSpecularColor(color);
        meshView.setMaterial(material);
        // Make sure you Cull the Back so that no black shows through
        meshView.setCullFace(CullFace.BACK);
        if (property != null)
        {
            meshView.setUserData(property);
        }

        // Add some ambient light so folks can see it
        AmbientLight light = new AmbientLight(Color.WHITE);
        light.getScope().add(meshView);
        getChildren().add(light);
        getChildren().add(meshView);
    }
}
