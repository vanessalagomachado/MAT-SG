/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufsc.model;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author vanes
 */
public class Point {

    private MultipleAspectTrajectory trajectory;
    private int rId;
    protected double x;
    protected double y;
    protected Date time;
//    private String cellReference;
    protected List<AttributeValue> listAttrValues;
    
    public static SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm");
    
    

    public Point(MultipleAspectTrajectory t, int rid, double x, double y, Date time) {
        this.trajectory = t;
        this.rId = rid;
        this.x = x;
        this.y = y;
        this.time = time;
        listAttrValues = new ArrayList<>();
    }

    public Point(int rid, double x, double y, Date time) {
        this.rId = rid;
        this.x = x;
        this.y = y;
        this.time = time;
        listAttrValues = new ArrayList<>();
    }

    public Point(double x, double y, Date time) {
        this.x = x;
        this.y = y;
        this.time = time;
        listAttrValues = new ArrayList<>();
    }

    public Point(double x, double y, int time) {
        this.x = x;
        this.y = y;
        this.time = Util.convertMinutesToDate(time);
        listAttrValues = new ArrayList<>();
    }

    public Point(int rid, double x, double y, int time) {
        this.rId = rid;
        this.x = x;
        this.y = y;
        this.time = Util.convertMinutesToDate(time);
        listAttrValues = new ArrayList<>();
    }

    public Point(int rid, double x, double y, int time, ArrayList semantics) {
        this.rId = rid;
        this.x = x;
        this.y = y;
        this.time = Util.convertMinutesToDate(time);
        listAttrValues = semantics;
    }
    
    public Point(int rid, double x, double y, Date time, ArrayList semantics) {
        this.rId = rid;
        this.x = x;
        this.y = y;
        this.time = time;
        listAttrValues = semantics;
    }
    
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
        listAttrValues = new ArrayList<>();
    }
    
    public Point(){
        listAttrValues = new ArrayList<>();
    }
    
    /*

public Date convertMinutesToDate(int min) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, ((int) min / 60));
        c.set(Calendar.MINUTE, min % 60);
        
        //System.out.println(c.getTime());
        Date d = c.getTime();
        
//        System.out.println("Infos-- ID: "+rId+" - min: "+min+" - Em date: "+formatDate.format(d));
//        System.out.println("Dados: Hora: "+((int) min / 60));
//        System.out.println("Dados em Calendar: "+formatDate.format(c.getTime()));
        
//        d.setTime(min*60000);
        return d;
    }
*/

    public double euclideanDistance(Point other) {
        return Math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y));
    }

    @Override
    public String toString() {
        Locale.setDefault(Locale.US);
        DecimalFormat formatNumber = new DecimalFormat("##.##");
        String txt = ((rId != 0) ? ("RId: " + rId + "\n") : ("rt"));

        txt += "\n(x,y)= (" + formatNumber.format(x) + "," + formatNumber.format(y) + "), ";
        if (time != null) {
            txt += "\nTIME: " + formatDate.format(time) + ", ";
        }
        if (!listAttrValues.isEmpty() ) {
            txt += showAttrValues();
        }
//                );
        return txt;

    }

    public MultipleAspectTrajectory getTrajectory() {
        return trajectory;
    }

    public void setTrajectory(MultipleAspectTrajectory trajectory) {
        this.trajectory = trajectory;
    }

    public int getrId() {
        return rId;
    }

    public void setrId(int rId) {
        this.rId = rId;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.trajectory);
        hash = 79 * hash + this.rId;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Point other = (Point) obj;
        if (this.rId != other.rId) {
            return false;
        }
        if (!Objects.equals(this.trajectory, other.trajectory)) {
            return false;
        }
        return true;
    }

//    public String getCellReference() {
//        return cellReference;
//    }
//
//    public void setCellReference(String cellReference) {
//        this.cellReference = cellReference;
//    }
    public void addAttrValue(Object value, Attribute attr) {
        listAttrValues.add(new AttributeValue(value, attr));
    }

    public String showAttrValues() {
        String txt = "(";
        for (AttributeValue atv : listAttrValues) {
                 
            txt += atv.getAttibute().getName() + ": " + atv.getValue() + ", ";
        }
        txt += ")";

        return txt;
    }
    
    

    public List<AttributeValue> getListAttrValues() {
        return listAttrValues;
    }

    public void setListAttrValues(List<AttributeValue> listAttrValues) {
        this.listAttrValues = listAttrValues;
    }

    public int getTimeInMinutes(){
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        return (c.get(Calendar.HOUR_OF_DAY) * 60) + c.get(Calendar.MINUTE);
    }
    
    /**
     * Return the value of the attribute a
     * @param Attribute a
     * @return value of the attribute a
     */
    public AttributeValue getAttributeValue(Attribute a){
        for(AttributeValue atv: listAttrValues){
            if(atv.getAttibute().equals(a))
                return atv;
        }
        return null;
    }
}
