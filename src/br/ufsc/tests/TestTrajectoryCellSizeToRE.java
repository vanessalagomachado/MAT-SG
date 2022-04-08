/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufsc.tests;


import br.ufsc.methods.TrajectoryFusionv6;

import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author vanes
 */
public class TestTrajectoryCellSizeToRE {

    public static String filename;
    public static String extension;

    public static void main(String[] args) throws IOException, ParseException {


        filename = "Running_Example_v2";
        extension = ".csv";


        TrajectoryFusionv6 method = new TrajectoryFusionv6();
        
        //informando lista de att a ser forçados como categoricos, mesmo contendo números
        String[] lstCategoricalsPreDefined = {"PRICE"};
        //

        String SEPARATOR = ",";
        String[] valuesNulls = {"Unknown", "*-1"};
        
        double thresholdCellSize = 6;
        method.execute(filename, extension, lstCategoricalsPreDefined, SEPARATOR, valuesNulls,thresholdCellSize);
    }
}
