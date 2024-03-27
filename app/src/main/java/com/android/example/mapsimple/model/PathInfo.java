package com.android.example.mapsimple.model;

import android.graphics.PointF;

import java.util.List;

public class PathInfo {
    private String documentId; // Firestore document ID
    private String startLocation;
    private String endLocation;
    private List<PointF> nodeCoordinates;


    // Constructor
    public PathInfo(String documentId, String startLocation, String endLocation, List<PointF> nodeCoordinates) {
        this.documentId = documentId;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.nodeCoordinates = nodeCoordinates;

    }

    // Getters

    // Getter for documentId
    public String getDocumentId() {
        return documentId;
    }

    // Getters and setters for other fields
    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public List<PointF> getNodeCoordinates() {
        return nodeCoordinates;
    }

    public void setNodeCoordinates(List<PointF> nodeCoordinates) {
        this.nodeCoordinates = nodeCoordinates;
    }


}