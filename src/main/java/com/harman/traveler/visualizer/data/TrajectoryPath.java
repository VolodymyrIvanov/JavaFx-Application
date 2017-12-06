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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.harman.learning.Common.Position;
import com.harman.learning.ObservationFile.NewEntry;
import com.harman.learning.ObservationFile.ObservationPath;
import com.harman.learning.TraceFile.RecordType;
import com.harman.learning.TraceFile.TraceFileRecord;
import com.harman.learning.TraceFile.TracePath;
import com.harman.traveler.visualizer.Tuple2;
import com.harman.traveler.visualizer.geometry.ObservationPathContainer;
import com.harman.traveler.visualizer.geometry.TraceFileRecordContainer;
import com.harman.traveler.visualizer.geometry.TracePathContainer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import javafx.scene.paint.Color;

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
    
    private Coordinate anchor;
    
    private Envelope envelope;
    
    public TrajectoryPath(File traceFile, List<File> observationFiles, Coordinate anchor)
    {
        super();
        this.anchor = anchor;
        this.envelope = new Envelope();
        loadData(traceFile, observationFiles);
    }
    
    public void loadData(File traceFile, List<File> observationFiles)
    {
        try
        {
            TracePath tracePath = TracePath.parseFrom(new FileInputStream(traceFile)); 
            tracePathLine = new TracePathContainer(tracePath, 6, Color.BLUE, anchor, 1000000);
            envelope.expandToInclude(tracePathLine.getEnvelope());
            List<Tuple2<TraceFileRecordContainer, ObservationPathContainer>> rawObservations = new ArrayList<>();
            for (File observationFile : observationFiles)
            {
                ObservationPath observationPath = ObservationPath.parseFrom(new FileInputStream(observationFile));
                for (NewEntry entry : observationPath.getObservations().getNewEntriesList())
                {
                    TraceFileRecord observationPoint = findObservationPoint(tracePath, entry);
                    if (observationPoint != null)
                    {
                        TraceFileRecordContainer pointContainer = new TraceFileRecordContainer(observationPoint, 6, Color.DARKRED, anchor, 1000000, 1);
                        envelope.expandToInclude(pointContainer.getEnvelope());
                        ObservationPathContainer observationContainer = new ObservationPathContainer(entry, 6, Color.RED, observationPoint.getPosition(),
                                anchor, 1000000);
                        envelope.expandToInclude(observationContainer.getEnvelope());
                        rawObservations.add(new Tuple2<>(pointContainer, observationContainer));
                    }
                }
            }
            //Group all observations by their observation point
            observations = rawObservations.stream()
                .collect(Collectors.groupingBy(tuple -> tuple._1.getData().getTimestamp()))
                .entrySet()
                .stream()
                .map(entry -> 
                {
                    List<ObservationPathContainer> obsList = entry.getValue().stream()
                            .map(tuple -> tuple._2).collect(Collectors.toList());
                    return new Tuple2<>(entry.getValue().get(0)._1, obsList);
                })
                .collect(Collectors.toList());
        }
        catch (IOException e)
        {
        }
    }
    
    /**
     * @return the tracePathLine
     */
    public TracePathContainer getTracePathLine()
    {
        return tracePathLine;
    }

    /**
     * @return the observations
     */
    public List<Tuple2<TraceFileRecordContainer, List<ObservationPathContainer>>> getObservations()
    {
        return observations;
    }

    /**
     * @return the envelope
     */
    public Envelope getEnvelope()
    {
        return envelope;
    }

    private TraceFileRecord findObservationPoint(TracePath tracePath, NewEntry entry)
    {
        for (TraceFileRecord rec : tracePath.getRecordsList())
        {
            if (/*RecordType.OBSERVATION.equals(rec.getRecordType()) &&*/ rec.getTimestamp() == entry.getTimestamp())
            {
                return rec;
            }
        }
        return null;
    }
}
