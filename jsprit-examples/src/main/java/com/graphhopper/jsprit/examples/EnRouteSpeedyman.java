
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
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.io.problem.VrpXMLWriter;
import com.graphhopper.jsprit.util.Examples;
import com.graphhopper.jsprit.util.MetroCosts;
import com.graphhopper.jsprit.util.SericePickuptFirstConstraing;

import java.util.Arrays;
import java.util.Collection;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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


public class EnRouteSpeedyman {

    public static void main(String[] args) throws IOException {

        DirectedWeightedMultigraph<Integer, DefaultWeightedEdge> g = new DirectedWeightedMultigraph<Integer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Map<String,Integer> nodeMaps = new HashMap<String,Integer>();
		Map<Integer, String> estacionmaps = new HashMap<Integer, String>();

		FileInputStream stream = new FileInputStream(new File("/Users/chunhaulai/Documents/workspace-speedyman/dijkstra-algorithm/metro.xlsx"));
		Workbook workbook = new XSSFWorkbook(stream);
		Sheet sheet = workbook.getSheetAt(0);
		int node = 0;
		int fila = 0;
		for (Row row: sheet) {
			if(fila!=0){
				String origen = row.getCell(0).getStringCellValue();
				String destino = row.getCell(1).getStringCellValue();
				if(!nodeMaps.containsKey(origen)){
					g.addVertex(node);
					nodeMaps.put(origen, node++);
					estacionmaps.put(node-1,origen);
					
				}
				if(!nodeMaps.containsKey(destino)){
					g.addVertex(node);
					nodeMaps.put(destino, node++);
					estacionmaps.put(node-1,destino);
				}
			}
			fila++;
        }
        

        PrintWriter writer = new PrintWriter(new File("/tmp/lai.csv"));
 		fila = 0;
		for (Row row: sheet) {
			if(fila!=0 && row.getCell(0)!=null){
				String origen = row.getCell(0).getStringCellValue();
				String destino = row.getCell(1).getStringCellValue();
				double tiempo = 0;
				try{
					tiempo = row.getCell(2).getNumericCellValue();
				}catch(IllegalStateException|NullPointerException e){
					tiempo = 300;
				}
				if(nodeMaps.get(origen)!=nodeMaps.get(destino)){
					if(!g.containsEdge(nodeMaps.get(origen), nodeMaps.get(destino))){
						DefaultWeightedEdge e = g.addEdge(nodeMaps.get(origen),nodeMaps.get(destino) );
						g.setEdgeWeight(e,tiempo);
					}
					if(!g.containsEdge(nodeMaps.get(destino), nodeMaps.get(origen))){
						DefaultWeightedEdge e = g.addEdge(nodeMaps.get(destino),nodeMaps.get(origen) );
						g.setEdgeWeight(e,tiempo);
					}

					
				}
				
			}
			fila++;
        }

        for(String key: nodeMaps.keySet()){
            writer.println(key);
        }
        writer.flush();
        writer.close();
        final int WEIGHT_INDEX = 0;
        Map<Integer,List<Service>> pedidos = new HashMap<Integer,List<Service>>();
        Map<Integer,Location> locations = new HashMap<Integer,Location>();
        Map<String,Coordinate> estacionCoordenada = new HashMap<String,Coordinate>();

		
        
        sheet = workbook.getSheetAt(2);
		fila = 0;
		for (Row row: sheet) {
			if(fila!=0 && row.getCell(1)!=null){
			
				String estacion = row.getCell(0).getStringCellValue();
                int x = (int)row.getCell(1).getNumericCellValue();
                int y = (int)row.getCell(2).getNumericCellValue();

                estacionCoordenada.put(estacion, Coordinate.newInstance(x, y));

				 
			}
			fila++;
		}
         
        //pedidos
		sheet = workbook.getSheetAt(1);
		fila = 0;
		for (Row row: sheet) {
			if(fila!=0 && row.getCell(1)!=null){
				int locationId =  (int)row.getCell(0).getNumericCellValue();
				String estacion = row.getCell(1).getStringCellValue();
                int orderId = (int)row.getCell(2).getNumericCellValue();
                int amount = (int)row.getCell(3).getNumericCellValue();
				if(nodeMaps.containsKey(estacion)){
					if(!pedidos.containsKey(orderId)){
						pedidos.put(orderId,  new ArrayList<Service>());
					}
                    //pedidos.get(orderId).add(estacion);
                    
                  
                    Location.Builder locx = Location.Builder.newInstance();
                    locx.setId(String.valueOf(nodeMaps.get(estacion)));
                    locx.setCoordinate(estacionCoordenada.get(estacion));
                    if(!locations.containsKey(orderId)){
                        locations.put(orderId, locx.build());
                        Pickup pickup1 = Pickup.Builder.newInstance(String.valueOf(orderId)).addSizeDimension(WEIGHT_INDEX, 1).setLocation(locx.build()).build();
                        
                        pedidos.get(orderId).add(pickup1);
                        
                      //  builder.setPickupLocation(locx.build());
                    }else{
                        Delivery delivery1 = Delivery.Builder.newInstance(orderId+ "::" +locationId ).addSizeDimension(0, 1).setLocation(locx.build()).build();
                        pedidos.get(orderId).add(delivery1);
                       

                      
                        /*
                        Shipment.Builder builder = Shipment.Builder.newInstance(orderId+ "::" +locationId );
                        builder.addSizeDimension(WEIGHT_INDEX,amount);
                        builder.setDeliveryLocation( locx.build()) ;
                        builder.setPickupLocation(locations.get(orderId));

                        Shipment shipment = builder.build();
                        pedidos.get(orderId).add(shipment);
                        */
 
                    }
				}else{
					//TODO estacion no encontrada en la base de datos
					System.out.println(estacion);
				}
			}
			fila++;
        }
        
        

        /*
         * some preparation - create output folder
		 */
        Examples.createOutputFolder();

		/*
         * get a vehicle type-builder and build a type with the typeId "vehicleType" and a capacity of 2
		 */
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType").addCapacityDimension(0,11);
        vehicleTypeBuilder.setCostPerDistance(1.0);
        VehicleType vehicleType = vehicleTypeBuilder.build();

		/*
         * define two vehicles and their start-locations
		 *
		 * the first two do need to return to depot
		 */

      

         Builder vehicleBuilder1 = VehicleImpl.Builder.newInstance("La Cisterna@[-3,-12]");
        vehicleBuilder1.setStartLocation(Location.Builder.newInstance().setId(String.valueOf(nodeMaps.get("La Cisterna"))).setCoordinate(estacionCoordenada.get("La Cisterna")).build()).setReturnToDepot(false);
        vehicleBuilder1.setType(vehicleType);
        VehicleImpl vehicle1 = vehicleBuilder1.build();

        Builder vehicleBuilder2 = VehicleImpl.Builder.newInstance("Pudahuel@[-14,1]");
        vehicleBuilder2.setStartLocation(Location.Builder.newInstance().setId(String.valueOf(nodeMaps.get("Pudahuel"))).setCoordinate(estacionCoordenada.get("Pudahuel")).build()).setReturnToDepot(false);
        vehicleBuilder2.setType(vehicleType);
        VehicleImpl vehicle2 = vehicleBuilder2.build();

        Builder vehicleBuilder3 = VehicleImpl.Builder.newInstance("Quinta Normal@[-5,1]");
        vehicleBuilder3.setStartLocation(Location.Builder.newInstance().setId(String.valueOf(nodeMaps.get("Quinta Normal"))).setCoordinate(estacionCoordenada.get("Quinta Normal")).build()).setReturnToDepot(false);;
        vehicleBuilder3.setType(vehicleType);
        VehicleImpl vehicle3 = vehicleBuilder3.build();

        Builder vehicleBuilder4 = VehicleImpl.Builder.newInstance("Macul@[11,-5]");
        vehicleBuilder4.setStartLocation(Location.Builder.newInstance().setId(String.valueOf(nodeMaps.get("Macul"))).setCoordinate(estacionCoordenada.get("Macul")).build()).setReturnToDepot(false);;
        vehicleBuilder4.setType(vehicleType);
        VehicleImpl vehicle4 = vehicleBuilder4.build();

        Builder vehicleBuilder5 = VehicleImpl.Builder.newInstance("Ñuñoa@[8,0]");
        vehicleBuilder5.setStartLocation(Location.Builder.newInstance().setId(String.valueOf(nodeMaps.get("Ñuñoa"))).setCoordinate(estacionCoordenada.get("Ñuñoa")).build()).setReturnToDepot(false);;
        vehicleBuilder5.setType(vehicleType);
        VehicleImpl vehicle5 = vehicleBuilder5.build();


         
        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addVehicle(vehicle1);//.addVehicle(vehicle2).addVehicle(vehicle3).addVehicle(vehicle4).addVehicle(vehicle5);
        vrpBuilder.setRoutingCost(new MetroCosts(g));

        
        for(Map.Entry<Integer, List<Service>> entry:pedidos.entrySet()){
            for(Service s: entry.getValue()){
                    vrpBuilder.addJob(s);
            }
            break;
        }

        /*
        vrpBuilder.addJob(shipment1).addJob(shipment2).addJob(shipment3).addJob(shipment4);
        vrpBuilder.addJob(shipment5).addJob(shipment6).addJob(shipment7).addJob(shipment8);
        vrpBuilder.addJob(shipment9).addJob(shipment10).addJob(shipment11).addJob(shipment12);
        vrpBuilder.addJob(shipment13).addJob(shipment14).addJob(shipment15).addJob(shipment16);
        */

        vrpBuilder.setFleetSize(FleetSize.FINITE);
        VehicleRoutingProblem problem = vrpBuilder.build();


        StateManager stateManager = new StateManager(problem);
        ConstraintManager constraintManager = new ConstraintManager(problem, stateManager);
        constraintManager.addConstraint(new SericePickuptFirstConstraing(), ConstraintManager.Priority.CRITICAL);

        VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem).setStateAndConstraintManager(stateManager,constraintManager).buildAlgorithm();

//		algorithm.setMaxIterations(30000);
        /*
         * and search a solution
		 */
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

        VehicleRoutingProblemSolution s = Solutions.bestOf(solutions);
        for(VehicleRoute route: s.getRoutes()){
           for(TourActivity ac: route.getActivities()){
            if(ac instanceof PickupService){
                Pickup xs = (Pickup) ((PickupService)ac).getJob();
                System.out.println(xs.getId()+"->"+estacionmaps.get(Integer.parseInt(xs.getLocation().getId())));
            }else if(ac instanceof DeliverService){
  
                Delivery xs = (Delivery) ((DeliverService)ac).getJob();
                System.out.println(xs.getId()+"->"+estacionmaps.get(Integer.parseInt(xs.getLocation().getId())));
               
            }
           }
            
        }
         
       

		/*
		 * print nRoutes and totalCosts of bestSolution
		 */
        //SolutionPrinter.print(problem, Solutions.bestOf(solutions), SolutionPrinter.Print.VERBOSE);
       // new GraphStreamViewer(problem, Solutions.bestOf(solutions)).setRenderDelay(100).display();
    }


    private static Location loc(Coordinate coordinate) {
        return Location.Builder.newInstance().setCoordinate(coordinate).build();
    }

}

