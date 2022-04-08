/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufsc.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vanes
 */
public class MultipleAspectTrajectory {

    private String description;
    private int id;
    private List<Point> pointList;

    public MultipleAspectTrajectory(String description, int id) {
        this.description = description;
        this.id = id;
        pointList = new ArrayList<Point>();
    }

    public MultipleAspectTrajectory(int id) {
        this.id = id;
        pointList = new ArrayList<Point>();
    }
    
     public MultipleAspectTrajectory(String description) {
        this.description = description;
        pointList = new ArrayList<Point>();
    }
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addPoint(Point p) {
        pointList.add(p);
        p.setTrajectory(this);
    }

    public void removePoint(Point p) {
        pointList.remove(p);
        p.setTrajectory(null);
    }
    
    public Point getLastPoint(){
        return pointList.get(pointList.size()-1);
    }

    @Override
    public String toString() {
        String aux = "ID: " + id;
        aux += "\nDescription: "+description;
        if (!pointList.isEmpty()) {
            aux += "\nPoint List: \n";

            for (Point p : pointList) {
                aux += p+"\n";
            }
        }
        return aux;
    }

    public List<Point> getPointList() {
        return pointList;
    }

    
}
