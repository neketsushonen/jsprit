/*
 * Licensed to GraphHopper GmbH under one or more contributor
 * license agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * GraphHopper GmbH licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 *
 */
package com.graphhopper.jsprit.util;

import java.util.HashMap;
import java.util.Map;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.driver.Driver;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;



/**
 * @author stefan schroeder
 */
public class MetroCosts implements VehicleRoutingTransportCosts {

    private Map<String,Double> transportDistance = new HashMap<String,Double>();

    private Map<String,Double> transportTime = new HashMap<String,Double>();
    private DirectedWeightedMultigraph<Integer, DefaultWeightedEdge> graph;
    DijkstraShortestPath<Integer, DefaultWeightedEdge> dij = null;
 
    public MetroCosts(DirectedWeightedMultigraph<Integer, DefaultWeightedEdge>  graph) {
        this.graph = graph;
        this.dij = new DijkstraShortestPath<Integer, DefaultWeightedEdge>(this.graph);
     }

    

    @Override
    public double getTransportTime(Location from, Location to, double departureTime, Driver driver, Vehicle vehicle) {
        if(!transportTime.containsKey(from.getId()+"-"+to.getId())){
            double distance = dij.getPathWeight(Integer.parseInt(from.getId()),Integer.parseInt(to.getId())) ; 
            transportTime.put(from.getId()+"-"+to.getId(),distance); 
        }
        return transportTime.get(from.getId()+"-"+to.getId());

 
    }

    @Override
    public double getBackwardTransportTime(Location from, Location to, double arrivalTime, Driver driver, Vehicle vehicle) {
        return getTransportTime(from, to, arrivalTime, driver, vehicle);
    }

    @Override
    public double getTransportCost(Location from, Location to, double departureTime, Driver driver, Vehicle vehicle) {

        double distance = getDistance(from, to, departureTime, vehicle);
        if (vehicle != null && vehicle.getType() != null) {
            return distance * vehicle.getType().getVehicleCostParams().perDistanceUnit;
        }
        return distance;
 
    }

    @Override
    public double getBackwardTransportCost(Location from, Location to, double arrivalTime, Driver driver,   Vehicle vehicle) {
        return getTransportCost(from, to, arrivalTime, driver, vehicle);
    }

    @Override
    public double getDistance(Location from, Location to, double departureTime, Vehicle vehicle) {
        if(!transportDistance.containsKey(from.getId()+"-"+to.getId())){
            double distance = dij.getPath(Integer.parseInt(from.getId()),Integer.parseInt(to.getId())).getVertexList().size(); 
            transportDistance.put(from.getId()+"-"+to.getId(),distance); 
        }
        return transportDistance.get(from.getId()+"-"+to.getId());
	}
}
