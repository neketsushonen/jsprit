
package com.graphhopper.jsprit.examples;

import  com.graphhopper.jsprit.core.problem.solution.route.activity.*;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer.Label;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.constraint.ConstraintManager;
import com.graphhopper.jsprit.core.problem.constraint.ServiceDeliveriesFirstConstraint;
import com.graphhopper.jsprit.core.problem.job.Delivery;
import com.graphhopper.jsprit.core.problem.job.Pickup;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl.Builder;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.ManhattanCosts;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.io.problem.VrpXMLWriter;
import com.graphhopper.jsprit.util.Examples;
import com.graphhopper.jsprit.util.MetroCosts;
import com.graphhopper.jsprit.util.ServiceCostDeliveryAsociatePickupConstraint;
import com.graphhopper.jsprit.util.ServiceDeliveryAsociatePickupConstraint;

import java.util.Arrays;
import java.util.Collection;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import cl.lai.routing.helper.EuclieanVertexGeograficFactory;
import cl.lai.routing.kmeans.KMean;
import cl.lai.routing.vo.Cluster;
import cl.lai.routing.vo.DataPoint;


public class EbackRouting {

    public static Statement getStatementP() throws SQLException{
        Connection c=null;
               try {
                  Class.forName("org.postgresql.Driver");
                } catch (ClassNotFoundException cnfe) {
                  System.err.println("Couldn't find driver class:");
                  cnfe.printStackTrace();
                }
                try {
                    c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ucn", "chunhaulai", "");
                } catch (SQLException se) {
                    System.out.println("Couldn't connect: print out a stack trace and exit.");
                    se.printStackTrace();
                    System.exit(1);
                }
                  Statement s = null;
                  try {
                    s = c.createStatement();
                  } catch (SQLException se) {
                    System.out.println("We got an exception while creating a statement:" +
                                       "that probably means we're no longer connected.");
                    se.printStackTrace();
                    System.exit(1);
                  }

                  return s;
    }
    

    public static void main( String[] args ) throws FileNotFoundException, IOException, SQLException {
        Statement s = getStatementP();

        Workbook workbook = new XSSFWorkbook(new FileInputStream(new File("/Users/chunhaulai/Google Drive/dropbox/adevcom/Ebacker/filetest.xlsx")));
       
        EuclieanVertexGeograficFactory factory = new EuclieanVertexGeograficFactory();
        List<DataPoint> cities = new ArrayList<DataPoint>();
        Sheet s1 = workbook.getSheetAt(0);
        int r = 0;
        for (Row row : s1) {
            if(r++!=0 && row.getCell(0)!=null){
                String sqlFormat = String.format("select st_x(geom) as x,st_y(geom) as y from st_transform( ST_GeomFromText('POINT(%s %s)',4326),32718) as geom",String.valueOf(row.getCell(5).getNumericCellValue()),String.valueOf(row.getCell(4).getNumericCellValue()));
                ResultSet rs = s.executeQuery(sqlFormat);
                if(rs.next()){
                    DataPoint punto = new DataPoint(factory,rs.getDouble("x"),rs.getDouble("y"));
                    punto.setObjName(String.valueOf(row.getCell(0).getNumericCellValue()));
                    cities.add(punto);
                }
            }
        }
        List<Cluster> G = null;
        int k = 2;
        boolean todoCalza = false;
        while(todoCalza==false ){
            KMean kmean = new KMean(k, 2000, cities, factory);
            kmean.startAnalysis();
            
            G = new ArrayList<Cluster>(Arrays.asList(kmean.getClusters()));
           // Collections.sort(G, new SortByElementsSize());
            todoCalza = true;
            for(Cluster c: G){
                for(DataPoint p: c.getDataPoints()){
                    if(c.getCentroid().calcDistance(p)>2000){
                        todoCalza = false;
                        break;
                    } 
                }
            }

            if(todoCalza==false) k++;
        }

        for(Cluster c: G){
            System.out.println("Cluster: "+c.getName());
            for(DataPoint p: c.getDataPoints()){
                System.out.println("\t"+p.getObjName()+"::"+c.getCentroid().calcDistance(p));
                
            }
        }
        workbook.close();


        final int WEIGHT_INDEX = 0;

        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType").addCapacityDimension(0,300);
        vehicleTypeBuilder.setCostPerDistance(1.0);
        VehicleType vehicleType = vehicleTypeBuilder.build();

        Builder vehicleBuilder1 = VehicleImpl.Builder.newInstance("B.M.1");
        vehicleBuilder1.setStartLocation(loc(Coordinate.newInstance(915695, 6296635))).setReturnToDepot(true);
        vehicleBuilder1.setType(vehicleType);
        VehicleImpl vehicle1 = vehicleBuilder1.build();

        Builder vehicleBuilder2 = VehicleImpl.Builder.newInstance("B.M.2");
        vehicleBuilder2.setStartLocation(loc(Coordinate.newInstance(915695, 6296635))).setReturnToDepot(true);
        vehicleBuilder2.setType(vehicleType);
        VehicleImpl vehicle2 = vehicleBuilder2.build();


        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addVehicle(vehicle1).addVehicle(vehicle2);
        

        for(Cluster c: G){
            Service service = Service.Builder.newInstance(c.getName())
            .addTimeWindow(0,120)
            .setServiceTime(10)
            .addSizeDimension(WEIGHT_INDEX, c.getNumDataPoints()).setLocation(Location.newInstance(c.getCentroid().getX(),c.getCentroid().getY())).build();
           
            vrpBuilder.addJob(service);
        }
        //833 = 50 km/h	= 833.3333333333 m/min
        vrpBuilder.setRoutingCost(new ManhattanCosts(833));

        vrpBuilder.setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);

        VehicleRoutingProblem problem = vrpBuilder.build();
        
        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);

		 
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

	 
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);

       new GraphStreamViewer(problem, bestSolution).labelWith(Label.ID).setRenderDelay(200).display();

       

    }

    private static Location loc(Coordinate coordinate) {
        return Location.Builder.newInstance().setCoordinate(coordinate).build();
    }


}

