/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufsc.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author vanes
 */
public class Centroid extends Point{
    
//    private Date timeRangeLower;
//    private Date timeRangeUpper;
    
    private List<Point> pointListSource;
//    Locale.setDefault(Locale.US);
//    DecimalFormat formatNumber = new DecimalFormat("0.###");
    
    
    public Centroid() {
        super();
        pointListSource = new ArrayList<>();
    }
    public Centroid(double x, double y) {
        super(x, y);
        pointListSource = new ArrayList<>();
    }
    
    public Centroid(Point p){
        super(p.getX(), p.getY());
        pointListSource = new ArrayList<>();
    }

    
    //methods
    public void setSpatialDimension(double x, double y){
        this.x = x;
        this.y = y;
    }
    
//    public Date getTimeRangeLower() {
////        return timeRangeLower;
//    }

//    public void setTimeRangeLower(Date timeRangeLower) {
//        this.timeRangeLower = timeRangeLower;
//    }
    
    

//    public Date getTimeRangeUpper() {
//        return timeRangeUpper;
//    }
//
//    public void setTimeRangeUpper(Date timeRangeUpper) {
//        this.timeRangeUpper = timeRangeUpper;
//    }
    
//    public void setTimeRangeUpper(int timeInMinutes) {
//        this.timeRangeUpper = Util.convertMinutesToDate(timeInMinutes);
//    }
//    
//    public void setTimeRangeLower(int timeInMinutes) {
//        this.timeRangeLower = Util.convertMinutesToDate(timeInMinutes);
//    }

    public void addPoint(Point p) {
        pointListSource.add(p);
    }

    public void removePoint(Point p) {
        pointListSource.remove(p);
    }
    
//    @Override
    public String toString() {
        String aux = super.toString() + 
                "\nLocal mapped: ";
                for(Point p: pointListSource){
                    aux += p.getTrajectory().getId() + " - "+p.getrId()+", ";
                }
                aux += "";
                return aux;
    }

    public List<Point> getPointListSource() {
        return pointListSource;
    }
    
    @Override
        public String showAttrValues() {
            
        Locale.setDefault(Locale.US);
        DecimalFormat formatNumber = new DecimalFormat("0.###");
        
        
        String txt = "";
        for (AttributeValue atv : super.listAttrValues) {
            txt+="\n";

            if(atv.getAttibute().getName().equalsIgnoreCase("TIME")){
                txt+="Ranking of Interval Temporal= [";
                HashMap<String, Double> allIntervals = (HashMap)atv.getValue();

                for (Map.Entry<String, Double> eachInt : allIntervals.entrySet()){
                    txt+= eachInt.getKey()+" > "+eachInt.getValue()+", ";
                }
                
                txt+=" ], ";
                
                
            } else if(!(atv.getValue() instanceof Map)){
                txt += atv.getAttibute().getName() + "= " + atv.getValue() + "";
            }else{
                
                txt+="Ranking of "+atv.getAttibute().getName()+"= [";
                HashMap<String, Double> allValues = (HashMap)atv.getValue();
                
//                allValues.forEach((key, value) -> System.out.println(key + " - " + value));
//                System.out.println("-----");
                
                for (Map.Entry<String, Double> eachValue : allValues.entrySet()){
                    txt+= eachValue.getKey().replace(",",";")+" -> "+formatNumber.format(eachValue.getValue())+", "; 
                }
                
                txt+=" ], ";
             }
        }
        txt += "";

        return txt;
    }
        
        
        public String oldShowAttrValues() {
            
        Locale.setDefault(Locale.US);
        DecimalFormat formatNumber = new DecimalFormat("0.###");
        
        
        String txt = "";
//        System.out.println(listAttrValues);
        for (AttributeValue atv : super.listAttrValues) {
            txt+="\n";
//            System.out.println(atv.getAttibute());
            if(atv.getAttibute().getName().equalsIgnoreCase("Time")){
                txt+="Ranking of Interval Temporal= [";
//                System.out.println("tipo do dado"+atv.getValue().getClass());
                HashMap<String, Integer> allIntervals = (HashMap)atv.getValue();
                
                
                
                
                for (Map.Entry<String, Integer> eachInt : allIntervals.entrySet()){
                    String interval = eachInt.getKey();
                    String auxInterval;
                    if(interval.contains("-")){
                        auxInterval = formatDate.format(Util.convertMinutesToDate(Integer.parseInt(interval.substring(0, interval.indexOf("-")))));
                        auxInterval += " - "+formatDate.format(Util.convertMinutesToDate(Integer.parseInt(interval.substring(interval.indexOf("-")+1))));
                    } else {
                        auxInterval = formatDate.format(Util.convertMinutesToDate(Integer.parseInt(interval)));
                    }
//                    txt+= auxInterval+" -> "+formatNumeric.format(((double)eachInt.getValue()/pointListSource.size())*100)+"%, ";
                    txt+= auxInterval+" > "+formatNumber.format(((double)eachInt.getValue()/pointListSource.size()))+", ";
                }
                
                txt+=" ], ";
                
                
            } else if(!(atv.getValue() instanceof Map)){
                txt += atv.getAttibute().getName() + "= " + atv.getValue() + "";
            }else{
                txt+="Ranking of "+atv.getAttibute().getName()+"= [";
//                System.out.println("tipo do dado"+atv.getValue().getClass());
                HashMap<String, Integer> allValues = (HashMap)atv.getValue();
                
                        
                for (Map.Entry<String, Integer> eachValue : allValues.entrySet()){
//                    txt+= eachValue.getKey()+" - "+formatNumeric.format(((double)eachValue.getValue()/pointListSource.size())*100)+"%, "; // Opção com %
                    txt+= eachValue.getKey()+" - "+formatNumber.format(((double)eachValue.getValue()/pointListSource.size()))+", "; 
                }
                
                txt+=" ], ";
             }
        }
        txt += "";

        return txt;
    }
    
}
