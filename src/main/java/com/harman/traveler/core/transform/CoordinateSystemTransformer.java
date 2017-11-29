/*
 * Project        Traveler
 * (c) copyright  2020
 * Company        HARMAN Automotive Systems GmbH
 *        All rights reserved
 *
 * Secrecy Level  STRICTLY CONFIDENTIAL
 *
 * File           CoordinateSystemTransformer.java
 * Creation date  14.03.2016
 */
package com.harman.traveler.core.transform;

import com.vividsolutions.jts.geom.Geometry;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Utility class for coordinates transformation from different geographic
 * coordinate systems to Cartesian coordinates and backwards.
 * 
 * @author VIvanov
 *
 */
public class CoordinateSystemTransformer
{
    public static final int WGS_SRID = 4326;

    /**
     * Earth radiuses
     */
    public static final double EARTH_RADIUS_LAT = 6356752.3142;

    public static final double EARTH_RADIUS_LON = 6378137.0;

    /**
     * Coefficients for different earth radiuses
     */
    public static final double METRES_TO_NDS_LON = (1 / EARTH_RADIUS_LON) * (1L << 32) / (2 * Math.PI);

    public static final double METRES_TO_NDS_LAT = (1 / EARTH_RADIUS_LAT) * (1L << 32) / (2 * Math.PI);

    public static final double METRES_TO_WGS_LAT = 180 / (Math.PI * EARTH_RADIUS_LAT);
    
    /**
     * WGS to RDF coefficient
     */
    public static final double WGS_TO_RDF = 100000.0;

    public static MathTransform getTransformFromWGStoCustom(CoordinateReferenceSystem crs)
    {
        try
        {
            return CRS.findMathTransform(DefaultGeographicCRS.WGS84, crs);
        } 
        catch (FactoryException e)
        {
            throw new RuntimeException("Error initialization of coordinate systems: ", e);
        }
    }
    
    public static MathTransform getTransformFromCustomtoWGS(CoordinateReferenceSystem crs)
    {
        try
        {
            return CRS.findMathTransform(crs, DefaultGeographicCRS.WGS84);
        } 
        catch (FactoryException e)
        {
            throw new RuntimeException("Error initialization of coordinate systems: ", e);
        }
    }
    
    public static Coordinate transfromCustom(Coordinate source, MathTransform transformer)
    {
        try
        {
            return JTS.transform(source, null, transformer);
        } 
        catch (TransformException e)
        {
            throw new RuntimeException("Error transform coordinate: ", e);
        }
    }

    public static Geometry transfromCustom(Geometry source, MathTransform transformer, int finalSRID)
    {
        try
        {
            Geometry geom = JTS.transform(source, transformer);
            geom.setSRID(finalSRID);
            return geom;
        } 
        catch (TransformException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts meters to NDS coordinate (latitude) system. Warning: Precision
     * for latitude is 10^10 Uses different radiuses of earth, separate radius
     * of equator and meridian.
     *
     * @param metres length in meters
     * @return length in NDS coordinate system
     */
    public static long metresToNdsLat(double metres)
    {
        return Math.round(metres * METRES_TO_NDS_LAT);
    }

    /**
     * Converts meters to WGS coordinate system.
     *
     * @param metres length in meters
     * @return length in WGS coordinate system
     */
    public static double metresToWgs(double metres)
    {
        return metres * METRES_TO_WGS_LAT;
    }

    /**
     * Converts WGS coordinate to meters.
     *
     * @param wgs length in WGS coordinate system
     * @return length in meters
     */
    public static double wgsToMetres(double wgs)
    {
        return wgs / METRES_TO_WGS_LAT;
    }

    /**
     * Conversion table for latitude
     *
     * @param latitude latitude
     * @return the longitude coefficient
     * @see <a href="www.uwgb.edu/dutchs/UsefulData/UTMFormulas.htm">www.uwgb.edu/dutchs/UsefulData/UTMFormulas.htm</a>
     * <br />
     * A degree of longitude at the equator is 111.2 kilometers. A minute
     * is 1853 meters. A second is 30.9 meters. <br />
     * <B>For other latitudes multiply by cos(lat).</B>
     */
    public static double getLongitudeCoefficient(double latitude)
    {
        double absLatitude = Math.abs(latitude);

        return 1.0 * Math.cos(Math.toRadians(absLatitude));
    }
    
    /**
     * Converts meters to centimeters.
     *
     * @param meters in meters
     * @return length in centimeters
     */
    public static double metresToCentimeters(double meters)
    {
        return meters * 100.0;
    }
    
    /**
     * Converts centimeters to meters.
     *
     * @param centimeters in centimeters
     * @return length in meters
     */
    public static double centimetresToMeters(double centimeters)
    {
        return centimeters / 100.0;
    }
    
    /**
     * Converts WGS coordinate to RDF coordinate system.
     * @param wgs coordinate in WGS coordinate system.
     * @return coordinate in RDF coordinate system.
     */
    public static double wgsToRdf(double wgs)
    {
        return wgs * WGS_TO_RDF;
    }
    
    /**
     * Converts RDF coordinate to WGS coordinate system.
     * @param rdf coordinate in RDF coordinate system.
     * @return coordinate in WGS coordinate system.
     */
    public static double rdfToWgs(double rdf)
    {
        return rdf / WGS_TO_RDF;
    }
}
