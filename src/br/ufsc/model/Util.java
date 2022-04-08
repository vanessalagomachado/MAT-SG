/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufsc.model;

import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author vanes
 */
public class Util {
    
    public static Date convertMinutesToDate(int min) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, ((int) min / 60));
        c.set(Calendar.MINUTE, min % 60);
        
        return c.getTime();
    }
    
        public static BitSet concatenateBitSets(BitSet vector_1_in, BitSet vector_2_in) {
        BitSet vector_1_in_clone = (BitSet) vector_1_in.clone();
        BitSet vector_2_in_clone = (BitSet) vector_2_in.clone();
        int n = vector_1_in.cardinality() - 1;//_desired length of the first (leading) vector
        int index = -1;
        while (index < (vector_2_in_clone.length() - 1)) {
            index = vector_2_in_clone.nextSetBit(index + 1);
            vector_1_in_clone.set(index + n);
        }
//        System.out.println("Concatenate: " + vector_1_in_clone);
        return vector_1_in_clone;
    }
        
        public static double euclideanDistance(Point p1, Point p2){
            return Math.sqrt( Math.pow(p1.getX() - p2.getX(),2) + Math.pow(p1.getY() - p2.getY(),2) );
        }
}
