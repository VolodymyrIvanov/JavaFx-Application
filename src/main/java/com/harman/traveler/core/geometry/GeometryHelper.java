package com.harman.traveler.core.geometry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vividsolutions.jts.algorithm.LineIntersector;
import com.vividsolutions.jts.algorithm.RobustLineIntersector;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * Helper functions to proceed with geometries.
 * 
 * @author OKashyrin
 */
public class GeometryHelper
{
    /**
     * Splits geometry into line segments
     * 
     * @param g     Geometry
     * @return      List of line segments
     */
    public static List<LineSegment> segments(Geometry g)
    {
        List<LineSegment> result = new ArrayList<>();

        Coordinate[] points = g.getCoordinates();
        Arrays.asList(points).stream()
            .map(new Function<Coordinate, LineSegment>()
            {
                private Coordinate prev;

                @Override
                public LineSegment apply(Coordinate next)
                {
                    LineSegment ls = null;

                    if(prev != null)
                    {
                        ls = new LineSegment(prev, next);
                    }
                    prev = next;

                    return ls;
                }
            })
            .filter(ls -> ls != null)
            .forEach(ls -> result.add(ls));

        return result;
    }

    /**
     * Find nearest line segment of geometry to given point.
     * 
     * @param geom geometry
     * @param p point
     * @return nearest line segment of geometry
     */
    public static LineSegment getNearestSegment(Geometry geom, Coordinate p)
    {
        List<LineSegment> segments = segments(geom);
        
        Optional<LineSegment> min = segments.stream()
            .min(new Comparator<LineSegment>()
            {
                @Override
                public int compare(LineSegment s1, LineSegment s2)
                {
                    double d1 = s1.distance(p);
                    double d2 = s2.distance(p);
                    return Double.compare(d1, d2);
                }
            });
        
        return min.isPresent() ? min.get() : null; 
    }
    
    /**
     * Find intersection between line segment and envelope border.
     * 
     * @param line  Line segment
     * @param env   Envelope
     * @return      Intersection point (if exists) or null (if not exists)
     */
    public static Coordinate intersection(LineSegment line, Envelope env)
    {
        LineIntersector li = new RobustLineIntersector();

        Coordinate sw = new Coordinate(env.getMinX(), env.getMinY());
        Coordinate nw = new Coordinate(env.getMinX(), env.getMaxY());
        li.computeIntersection(line.p0, line.p1, sw, nw);
        if (li.hasIntersection())
        {
            return li.getIntersection(0);
        }

        Coordinate ne = new Coordinate(env.getMaxX(), env.getMaxY());
        li.computeIntersection(line.p0, line.p1, nw, ne);
        if (li.hasIntersection())
        {
            return li.getIntersection(0);
        }

        Coordinate se = new Coordinate(env.getMaxX(), env.getMinY());
        li.computeIntersection(line.p0, line.p1, ne, se);
        if (li.hasIntersection())
        {
            return li.getIntersection(0);
        }

        li.computeIntersection(line.p0, line.p1, se, sw);
        if (li.hasIntersection())
        {
            return li.getIntersection(0);
        }

        return null;
    }

    /**
     * Find lowest elevation of geometry points.
     * 
     * @param geom  Geometry
     * @return      lowest elevation of geometry points
     */
    public static double getLowestElevation(Geometry geom)
    {
        double lowestElevation = Double.MAX_VALUE;

        Coordinate[] coordinates = geom.getCoordinates();

        for(Coordinate c : coordinates)
        {
            if (!Double.isNaN(c.z) && c.z < lowestElevation)
            {
                lowestElevation = c.z;
            }
        }

        return lowestElevation;
    }
    
    /**
     * Output WKT string with 3-rd dimensional coordinates.
     * 
     * @param geom geometry
     * @return WKT string
     */
    public static String toWktText(Geometry geom)
    {
        WKTWriter writer = new WKTWriter(3);
        return writer.write(geom);
    }
}
