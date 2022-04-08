/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufsc.methods;

import br.ufsc.model.Attribute;
import br.ufsc.model.AttributeValue;
import br.ufsc.model.Centroid;
import br.ufsc.model.MultipleAspectTrajectory;
import br.ufsc.model.Point;
import static br.ufsc.model.Point.formatDate;
import br.ufsc.model.SemanticType;
import br.ufsc.model.Util;
import br.ufsc.util.CSVWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author vanes
 */
public class MATSG {

        // setting to execute method
    String SEPARATOR;
    String[] valuesNulls;
    // --------------------- AUX ----------------------
    private static List<int[]> trajectories;
    private static int rId;
    private static int cId;
    int ord;

    private static String auxTid;

    // -- Load
    private static List<Point> points;
    private static List<Attribute> attributes;
    private static Map<String, BitSet> spatialCellGrid;
    private static Map<String, List<Double>> sematicNumericFusionVal;
//        private static Map<String, Integer> sematicFusionCount;
    private static Map<String, Map<String, Integer>> sematicCategoricalFusionVal;

    // ------------- to Spatial division
    private static String filename;
    private static String directory;
    private static String extension; // Of the filename
    
    private static double spatialThreshold;
//	private static int temporalThreshold;
    private static double cellSizeSpace;
    private static int valueZ;

    private static MultipleAspectTrajectory trajectory;
    private static List<MultipleAspectTrajectory> listaTrajetorias;

    private static MultipleAspectTrajectory representativeTrajectory;

    // -------------- to Temporal fusion
    private List<Integer> listTimesInCell;


    // ------------- to Spatial fusion
    private double avgX, avgY;
    
    /// --------------- to determine categoricals values pre-defined
    List<String> lstCategoricalsPD;
    
       
    // --- Define initial index value to semantic attributes
    
    private static int INDEX_SEMANTIC = 4;
    private static SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    //V9 - parameters to MAT-SG
    private float threshold_rc;
    private float threshold_rv;
    

    //methods load e add were static and I remove this.
    /**
     *
     * @param spatialT Spatial Trashhold
     * @param file name of file
     * @param ext extension of file
     * @throws IOException
     *
     * function to
     *
     */
    public void execute(String dir, String file, String ext, String[] lstCategoricalPD, String SEPARATOR, String[] valuesNULL, int numberSpatialDistance, float rc, float threshold_rv) throws IOException, ParseException {
        directory = dir;
        filename = file;
        extension = ext;
        this.SEPARATOR = SEPARATOR;
        this.valuesNulls = valuesNULL;
        this.valueZ = numberSpatialDistance;

//        spatialThreshold = computeSpatialThreshold();

        DecimalFormat df = new DecimalFormat("###.######");
//        cellSizeSpace = spatialThreshold * 0.7071;//Double.parseDouble(df.format(spatialThreshold * 0.7071));
//        cellSizeSpace = Math.sqrt((spatialThreshold * spatialThreshold)/2);

//        cellSizeSpace = 3;
        /////////////////////////////// Dúvida: O que representa esse valor? metros?

//        System.out.println("Cell size: " + cellSizeSpace);

        rId = 0;
        auxTid = "-1";
        cId = -1;

        listTimesInCell = new ArrayList<Integer>();

        spatialCellGrid = new HashMap<String, BitSet>();


        sematicNumericFusionVal = new HashMap<String, List<Double>>();
//		sematicFusionCount = new HashMap<String, Integer>();
        sematicCategoricalFusionVal = new HashMap<String, Map<String, Integer>>();
//        
        points = new ArrayList<Point>();
        attributes = new ArrayList<Attribute>();
        listaTrajetorias = new ArrayList<MultipleAspectTrajectory>();
        representativeTrajectory = new MultipleAspectTrajectory("representative");
        
        lstCategoricalsPD = Arrays.asList(lstCategoricalPD);
        

        load();
        
        spatialThreshold = computeSpaceThreshold()*valueZ;
        cellSizeSpace = spatialThreshold * 0.7071;//Double.parseDouble(df.format(spatialThreshold * 0.7071));
//        cellSizeSpace = Math.sqrt((spatialThreshold * spatialThreshold)/2);
//        System.out.println("Threshold: "+spatialThreshold+" - Cell Size: "+cellSizeSpace);
        allocateAllPointsInCellSpace();
//        System.out.println("Cell Grid: "+spatialCellGrid);
        
        //parameter for defining representativeness values and compute relevant cell
        this.threshold_rv = threshold_rv;
        threshold_rc = rc>0.0?(rc*points.size()):2;
//        System.out.println("Threshold RC = "+threshold_rc);

        findCentroid();

//        System.out.println("Logical Grid:\n" + spatialCellGrid);
        
        //Write the Representative Trajectory on file
        writeRepresentativeTrajectory("..\\"+directory+filename+"[output] - z"+this.valueZ, ext);

    }

    /**
     *
     * @throws IOException
     */
    private void load() throws IOException, ParseException {
        
        java.io.Reader input = new FileReader(directory + filename + extension);
        BufferedReader reader = new BufferedReader(input);

        String datasetRow = reader.readLine();
        //To Get the header of dataset
        String[] datasetColumns = datasetRow.split(SEPARATOR);
//        System.out.println("Line: "+datasetRow);
        
        int order = 0;
        for (String s : Arrays.copyOfRange(datasetColumns, INDEX_SEMANTIC, datasetColumns.length)) {
            if(lstCategoricalsPD.contains(s.toUpperCase()))
                attributes.add(new Attribute(s, order++, SemanticType.CATEGORICAL));
            else 
                attributes.add(new Attribute(s, order++));

        }

        datasetRow = reader.readLine();

        //EoF - To get the data of dataset of each line
        while (datasetRow != null) {
            datasetColumns = datasetRow.split(SEPARATOR);
            addAttributeValues(datasetColumns);
            datasetRow = reader.readLine();
        }

        reader.close();

    }


    private void addAttributeValues(String[] attrValues) throws ParseException {

        ++rId; //Id given to each data point 

        String[] semantics = Arrays.copyOfRange(attrValues, INDEX_SEMANTIC, attrValues.length);
        
        addTrajectoryData(attrValues[0], attrValues[1].split(" "), formatDate.parse(attrValues[2]), semantics);
//        String[] coord = {attrValues[2], attrValues[3]};
//        addTrajectoryData(attrValues[0], coord, Integer.parseInt(attrValues[5]), semantics);
        //allocateInSpaceCell(attrValues[1].split(" "));

    }


    private void addTrajectoryData(String tId, String[] coordinates, Date time, String[] semantics) {

        if (!tId.equals(auxTid)) {
            //Usar o cID como um auxiliar para ID das trajetórias, começando no índice 0
            //++cId;
            auxTid = tId;
            listaTrajetorias.add(new MultipleAspectTrajectory(Integer.valueOf(tId)));
            trajectory = listaTrajetorias.get(listaTrajetorias.size() - 1);

        }

        //order: price, weather, type
        ArrayList<AttributeValue> attrs = new ArrayList<>();
        ord = 0;
        
        Attribute a;
        for (String val : semantics) {
            a = findAttributeForOrder(ord++);
            if(a.getType() != null && a.getType().equals(SemanticType.CATEGORICAL))
                val = "*"+val; // Use character '*' to force the number value to be a categorical value
            if(Arrays.asList(valuesNulls).contains(val)){
                val = "Unknown";
            }
            attrs.add(new AttributeValue(val, a));
        }
        a = null; //clean memory 

        trajectory.addPoint(new Point(rId,// para mexer no id do ponto
                Double.parseDouble(coordinates[0]),
                Double.parseDouble(coordinates[1]),
                time,
                attrs));
//        System.out.println(trajectory.getLastPoint());

        points.add(trajectory.getLastPoint());
        
        
    }


    /**
     * add each point of the relative cell in grid
     *
     * @param coordinates Coordinates of each trajectory point
     *
     */
    private static void allocateInSpaceCell(Point p) {

        // x,y
//        String key = getCellPosition(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]));
        String key = getCellPosition(p.getX(), p.getY());

        BitSet rIds = spatialCellGrid.get(key);

        if (rIds == null) {
            rIds = new BitSet();
            rIds.set(p.getrId());
            spatialCellGrid.put(key, rIds);
        } else {
            rIds.set(p.getrId());
            spatialCellGrid.replace(key, rIds);
        }
        

    }

    /**
     * Compute the cell position based on x and y divided by the cell size
     * predefined
     *
     * @param x
     * @param y
     * @return Cell Position
     */
    private static String getCellPosition(double x, double y) {

        return ((int) Math.floor(x / cellSizeSpace)) + "," + ((int) Math.floor(y / cellSizeSpace));

    }

    public void findCentroid() {

        int sizePoints = points.size();
//        System.out.println("Size of Points: "+sizePoints);
        Iterator<String> cell = spatialCellGrid.keySet().iterator();
        while (cell.hasNext()) {
            String cellAnalyzed = cell.next();
//            System.out.println("Cell Analyzed: "+cellAnalyzed);
//            System.out.println("Points: "+spatialCellGrid.get(cellAnalyzed));

            int qntPoints = spatialCellGrid.get(cellAnalyzed).cardinality();
            
            if (qntPoints >= threshold_rc) { // IF number is at least a threshold RC

                resetValuesToSemanticFusion();
                Centroid representativePoint = new Centroid();

                // Loop in all points of the cell
                for (int pointId = spatialCellGrid.get(cellAnalyzed).nextSetBit(0);
                        pointId >= 0;
                        pointId = spatialCellGrid.get(cellAnalyzed).nextSetBit(pointId + 1)) {
//                    
                    Point p = points.get(pointId-1);
                    representativePoint.addPoint(p); //To mapping the origin of the RP

                    // Spatial data
                    avgX += p.getX();
                    avgY += p.getY();

                    //Semantic Data
                    Double val;
                    String attrActual;

                    
                    for (AttributeValue atv : p.getListAttrValues()) {
                        attrActual = "" + atv.getAttibute().getOrder();

                        // numeric values - median computation 
                        //in this scope just create bitset with sum and count of values foreach quantitative attribute
                        try {
                            
                            val = Double.parseDouble((String) atv.getValue()); // val -1 refers to empty value
                            
                            if (!sematicNumericFusionVal.containsKey(attrActual)) {
                                sematicNumericFusionVal.put(attrActual, new ArrayList<Double>());
                            }

                            // add into this key the attribute value 
                            sematicNumericFusionVal.get(attrActual).add(val);

                        } catch (java.lang.NumberFormatException e) { //categorical values
                            //in this scope create the sematicCategoricalFusionVal with all possible values of each categorical attribute
                            // and add its ids for after this step can computation the frequency of each one,
                            // and identify the value more frequency of each qualitative attribute

                            //BitSet pIds;
                            if (!sematicCategoricalFusionVal.containsKey(attrActual)) //IF not contains this key - attribute name
                            {
                                sematicCategoricalFusionVal.put(attrActual, new HashMap<String, Integer>());
                            }

                            if (!sematicCategoricalFusionVal.get(attrActual).containsKey(atv.getValue())) // IF this attribute not contains this value
                            {
                                sematicCategoricalFusionVal.get(attrActual).put((String) atv.getValue(), 1); //add this value to attribute and initialize the count
                            } else {
                                sematicCategoricalFusionVal.get(attrActual).replace((String) atv.getValue(), sematicCategoricalFusionVal.get(attrActual).get(atv.getValue()) + 1); //cont ++
                            }

//                            System.out.println(sematicCategoricalFusionVal.get(attrActual));
                        }
                    } //end FOR of all semantic attributes

                    //Temporal data
//                    avgTemporal += p.getTimeInMinutes();
                    listTimesInCell.add(p.getTimeInMinutes());
                    
                    
                    
                    
                }// end FOR each point in cell

                //System.out.println("Dados dos pontos: "+sematicNumericFusionVal + " - qnt: "+sematicFusionCount);
                //Spatial data fusion
                representativePoint.setSpatialDimension((avgX /= qntPoints), (avgY /= qntPoints));

                //
                //Loop for numeric attributes
                sematicNumericFusionVal.entrySet().forEach((entrada) -> {
                    Double median;
                    Collections.sort(entrada.getValue());
                    if (entrada.getValue().size() % 2 == 0) {
                        median = (entrada.getValue().get(entrada.getValue().size() / 2) + entrada.getValue().get(entrada.getValue().size() / 2 - 1)) / 2;
                    } else {
                        median = entrada.getValue().get(entrada.getValue().size() / 2);
                    }

                    representativePoint.addAttrValue("" + median,
                            findAttributeForOrder(Integer.parseInt(entrada.getKey())));

//                   
                });

                //begin -------- Loop for a categorical attributes
                //qnt categorical attributes 
                
                //System.out.println("Quantidade de attr categorical: "+sematicCategoricalFusionVal.size());
                

                for (Map.Entry<String, Map<String, Integer>> allCategorical : sematicCategoricalFusionVal.entrySet()) {

                    Map<String, Integer> internalCategoricalList
                            = allCategorical.getValue().entrySet()
                                    .stream()
                                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));

                    representativePoint.addAttrValue(normalizeRankingValues(internalCategoricalList, qntPoints),
                            findAttributeForOrder(Integer.parseInt(allCategorical.getKey())));

//                        System.out.println("Ordered Categorical: "+internalCategoricalList);
                } // end ------------ Loop for a categorical attributes

                


                //Temporal data
               representativePoint.addAttrValue(normalizeTimeRank(normalizeRankingValues(defineRankingTemporal(listTimesInCell),qntPoints)),
                       new Attribute("TIME"));
               
               
               representativeTrajectory.addPoint(representativePoint);
                
            } //End IF contains more than 1 point in cell
        } // end loop each cell
//        System.out.println("Representative traj: " + representativeTrajectory);
    }// End method findCentroid

    /**
     * find the Attribute object by the order
     *
     * @param order
     * @return Attribute
     */
    public Attribute findAttributeForOrder(int order) {
        for (Attribute attr : attributes) {
            if (attr.getOrder() == order) {
                return attr;
            }
        }
        return null;
    }

    public void resetValuesToSemanticFusion() {
        //Data reset

        //spatial data
        avgX = 0;
        avgY = 0;

        // semantic data (multiple aspects)
        sematicNumericFusionVal.clear();
//                sematicFusionCount.clear();
        sematicCategoricalFusionVal.clear();

        //temporal data
        listTimesInCell.clear();
    }
    
    public Map<String, Integer> defineRankingTemporal(List<Integer> times){
        //order times
        Collections.sort(times);
//        System.out.println("Times ordenate: "+times);
        
        List<Integer> differences = new ArrayList<>();
        

        //determine threshold by avg
        int threshold = 100;
        
        
        //int countIntervals = 0;
        float sumIntervals = 0;
        for (int i = 1; i < times.size(); i++) {
            differences.add(times.get(i) - times.get(i-1));
            sumIntervals += differences.get(differences.size()-1);
            //countIntervals++;
            
        }
        
        float avg = sumIntervals / differences.size();
        
        sumIntervals = 0;
        
        if(differences.size()>1){
            
            Collections.sort(differences);
//            System.out.println("Intervals list: "+differences);
            
        
            //compute the valid interval to remove the outliers
            // computation: valid interval Median minus and plus SD.
            if(differences.size()>2){ 
                //compute the median value of the difference values 
                int med;
                if(differences.size()%2 == 1)
                    med = differences.get(Math.floorDiv(differences.size(),2));
                else
                    med = (differences.get(differences.size()/2-1)+differences.get((differences.size()/2))) / 2;
//                System.out.println("Median: "+med);
                
                
                //Compute the SD
                for (int i = 0; i < differences.size(); i++) {
                    sumIntervals += Math.pow((differences.get(i) - avg), 2);
                }
                float SD = sumIntervals / differences.size();
                SD = (float)Math.sqrt(SD);
                
                //compute valid interval
                float lessValue = med - SD;
                float upperValue = med+ SD;
//                System.out.println("Less value: "+lessValue+" | Upper value: "+upperValue);
                
                threshold = Math.floorDiv((int)(Math.abs(upperValue) - Math.abs(lessValue)), 2);
//                System.out.println("Interval maximum: "+threshold);
                
                //remove outliers
                sumIntervals = 0;
                //remove less value
                for (int i = 0; i < differences.size(); i++) {
                    if(differences.get(i) < lessValue || differences.get(i)>upperValue){
                        differences.remove(i);
                    } else {
                        sumIntervals += differences.get(i);
                    }
                }
//                System.out.println("Intervals list - valid values: "+differences);
                threshold = Math.floorDiv((int)sumIntervals, differences.size());
                
                
            } 
            
        }
        
//        System.out.println("Threshold: "+threshold);
        
        String aux = null;
        int cont = 1;
        Map<String, Integer> temporalRanking = new HashMap<>();
        for (int i = 0; i < times.size(); i++) {
            
                if(i != times.size()-1 && (times.get(i)+threshold) >= times.get(i+1)){
                    if(aux == null)
                        aux = ""+times.get(i);
                    cont++;
                } else {
                    if(aux == null)
                        aux = ""+times.get(i);
                    else
                        aux += "-"+times.get(i);

                    temporalRanking.put(aux, cont);
                    cont = 1;
                    aux = null;
                }
            }
            // Ordernate temporal ranking 
                temporalRanking
                            = temporalRanking.entrySet()
                                    .stream()
                                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
                
                return temporalRanking;
        }
        
        public double computeSpaceThreshold(){
            //compute avg of spatial distance
            double minDistance = 999999999999999999L;
            double localDistance;
            double sumDistance = 0;
            double avgDistance;
            for(Point p: points){
                for (Point q: points) {
                    if(!p.equals(q)){
                        localDistance = Util.euclideanDistance(p, q);
                        if(localDistance < minDistance)
                            minDistance = localDistance;
                    }
                }
                sumDistance += minDistance;
//                System.out.println("Point: "+p.getrId()+" - Distance: "+minDistance);
                minDistance = 999999999999999999L;
                localDistance = 0;
                
            }
//            System.out.println("Média de distância mínima: "+(sumDistance / points.size()));
            return (sumDistance / points.size());
            
        }
        
        public void allocateAllPointsInCellSpace(){
            for (Point p: points) {
                allocateInSpaceCell(p);
            }
        }
        
        
        
        public void writeRepresentativeTrajectory(String fileOutput, String ext){
            try {
                                        CSVWriter mxWriter = new CSVWriter("datasets/" + fileOutput+ ext);
					
					
					
					for(Point p: representativeTrajectory.getPointList()) {
						mxWriter.writeLine(p.toString());
						mxWriter.flush();
					}
					
					mxWriter.close();
				} catch (IOException e) {
//					Logger.log(Type.ERROR, pfx + e.getMessage());
					e.printStackTrace();
				}
        }
        
        /**
         * Normalizing the Map of ranking values on semantic or time dimension
         * - Where the quantity of occurences of each attribute value is changed by 
         * the ratio of this value relation on the size of the cell
         * @param mapRank
         * @param sizeCell
         * @return normalized 
         */
        public Map<Object, Double> normalizeRankingValues(Map<String, Integer> mapRank, int sizeCell) {
               
                Map<Object, Double> newMap = new HashMap<>();
                double trendEachVal;
                for (Map.Entry<String, Integer> eachValue: mapRank.entrySet()){
                    trendEachVal = (double)eachValue.getValue()/sizeCell; 
                    if(trendEachVal >= threshold_rv)
                        newMap.put(eachValue.getKey(), trendEachVal);
                }
                
                Map<Object, Double> newMapSorted = newMap.entrySet().stream()
			        .sorted(Map.Entry.<Object, Double>comparingByValue().reversed())
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
                
                return newMapSorted;
        }
        
        public Map<Object,Double> normalizeTimeRank(Map<Object,Double> mapTime){
                Map<Object,Double> newMap = new HashMap<>();
                for (Map.Entry<Object, Double> eachInt : mapTime.entrySet()){
                    String interval = (String)eachInt.getKey();
                    String auxInterval;
                    if(interval.contains("-")){
                        auxInterval = formatDate.format(Util.convertMinutesToDate(Integer.parseInt(interval.substring(0, interval.indexOf("-")))));
                        auxInterval += " - "+formatDate.format(Util.convertMinutesToDate(Integer.parseInt(interval.substring(interval.indexOf("-")+1))));
                    } else {
                        auxInterval = formatDate.format(Util.convertMinutesToDate(Integer.parseInt(interval)));
                    }
//                    txt+= auxInterval+" > "+formatNumeric.format(((double)eachInt.getValue()/pointListSource.size()))+", ";
                    newMap.put(auxInterval, mapTime.get(interval));
                }
                return newMap;
        }
    
}
