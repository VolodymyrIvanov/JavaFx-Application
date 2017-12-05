/*
 * Project        Traveler
 * (c) copyright  2020
 * Company        HARMAN Automotive Systems GmbH
 *        All rights reserved
 *
 * Secrecy Level  STRICTLY CONFIDENTIAL
 *
 * File           TrajectoryPath.java
 * Creation date  30.11.2017
 */
package com.harman.traveler.visualizer.data;

import java.io.File;
import java.util.List;

import com.harman.traveler.visualizer.Tuple2;
import com.harman.traveler.visualizer.geometry.ObservationPathContainer;
import com.harman.traveler.visualizer.geometry.TraceFileRecordContainer;
import com.harman.traveler.visualizer.geometry.TracePathContainer;

/**
 * Representation of trajectory path including trajectory line geometry,
 * trajectory points and observation.
 * 
 * @author VIvanov
 *
 */
public class TrajectoryPath
{
    private TracePathContainer tracePathLine;
    
    private List<Tuple2<TraceFileRecordContainer, List<ObservationPathContainer>>> observations;
    
    public TrajectoryPath(File traceFile, List<File> observationFiles)
    {
        super();
        loadData(traceFile, observationFiles);
    }
    
    public void loadData(File traceFile, List<File> observationFiles)
    {
        
    }
}
