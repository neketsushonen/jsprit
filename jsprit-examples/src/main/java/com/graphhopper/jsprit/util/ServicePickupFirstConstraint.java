
package com.graphhopper.jsprit.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.graphhopper.jsprit.core.algorithm.state.StateId;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.problem.constraint.HardActivityConstraint;
import com.graphhopper.jsprit.core.problem.job.Delivery;
import com.graphhopper.jsprit.core.problem.job.Pickup;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.misc.JobInsertionContext;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.*;

public class ServicePickupFirstConstraint implements HardActivityConstraint {
    Map<Integer,List<String>> pedidos  ;
    
    StateManager stateManager;
    
    private Map<Integer,StateId> states;
    

    public ServicePickupFirstConstraint(Map<Integer, List<Service>> pedidosOriginal) {
        pedidos = new HashMap<Integer,List<String>>();
        for(Map.Entry<Integer,List<Service>> entry: pedidosOriginal.entrySet()){
           if(!pedidos.containsKey(entry.getKey()))
                pedidos.put(entry.getKey(),new ArrayList<String>());
            pedidos.get(entry.getKey()).addAll(entry.getValue().stream().map(f -> f.getId()).collect(Collectors.toList()));
        }
       // this.pedidos = pedidos.entrySet().stream().map(f-> f.get)
    }

    public ServicePickupFirstConstraint(){

    }
    
    public ServicePickupFirstConstraint(StateManager stateManager,Map<Integer,StateId> states,Map<Integer, List<Service>> pedidosOriginal ){
    	
    	this( pedidosOriginal);
    	this.stateManager = stateManager;
    	this.states = states;
    }

    @Override
    public ConstraintsStatus fulfilled(JobInsertionContext iFacts, TourActivity prevAct, TourActivity newAct, TourActivity nextAct, double prevActDepTime) {
        //System.out.println(newAct.getClass().getName()+"=>"+prevAct.getClass().getName());
      
        /*
        List<String> orderIds = new ArrayList<String>();
        List<TourActivity> activities = iFacts.getRoute().getActivities();
        for(TourActivity act: activities){
            if(act instanceof Start || act instanceof End ){
                continue;
            }else{
                //System.out.println(act.getName()); 

                if(act instanceof PickupService){
                    Pickup s = (Pickup) ((PickupService)act).getJob();
                   
                    orderIds.add(s.getId());
                }else if(act instanceof DeliverService){
                   
                    if(orderIds.isEmpty()) 
                        return ConstraintsStatus.NOT_FULFILLED_BREAK;    
                    Delivery s = (Delivery) ((DeliverService)act).getJob();  
                    String orderIdOriginal = s.getId().split("::")[0];
                   
                    if(!orderIds.contains(orderIdOriginal))  return ConstraintsStatus.NOT_FULFILLED;
                } 
            }
        }
        */
  
        
        if(prevAct instanceof Start && newAct instanceof  DeliverService){
            return ConstraintsStatus.NOT_FULFILLED;
        }
        
     
        
        if(newAct instanceof  PickupService) {
        	Pickup s = (Pickup) ((PickupService)newAct).getJob();  
        	String orderIdOriginal = s.getId() ;
        	
        	int total = 0;
        	List<TourActivity> activities = iFacts.getRoute().getActivities();
            for(TourActivity act: activities){
                if(act instanceof Start || act instanceof End ){
                    continue;
                }else{

                    if(act instanceof PickupService){
                        Pickup sxxx = (Pickup) ((PickupService)act).getJob();
                        total += this.pedidos.get(Integer.parseInt(sxxx.getId())).size()-1;
                    } 
                }
            }
            
           // System.out.println(iFacts.getNewVehicle().getType().getCapacityDimensions().get(0) + "::: "+(this.pedidos.get(Integer.parseInt(orderIdOriginal)).size()-1 +total) );
            
        	
            if(iFacts.getNewVehicle().getType().getCapacityDimensions().get(0) < 
            		this.pedidos.get(Integer.parseInt(orderIdOriginal)).size()-1 +total
            		
            		)return ConstraintsStatus.NOT_FULFILLED;
            		
        }
        
       
        
        if(nextAct instanceof End){
        	 
        	Map<String, Integer> total = new HashMap<String,Integer>();
        	boolean existeDelivery=false;
        	 List<String> orderIds = new ArrayList<String>();
             List<TourActivity> activities = iFacts.getRoute().getActivities();
             for(TourActivity act: activities){
                 if(act instanceof Start || act instanceof End ){
                     continue;
                 }else{
 
                     if(act instanceof PickupService){
                         Pickup s = (Pickup) ((PickupService)act).getJob();
                        
                         orderIds.add(s.getId());
                         total.put( s.getId(), 0);
                        // if(s.getId().equalsIgnoreCase("1"))
                         //System.out.println(s.getId());
                         
                     }else if(act instanceof DeliverService){
                        
                    	 existeDelivery = true;
                         Delivery s = (Delivery) ((DeliverService)act).getJob();  
                         String orderIdOriginal = s.getId().split("::")[0];
                         
                        // System.out.println(s.getId());
                         if(!orderIds.contains(orderIdOriginal))  return ConstraintsStatus.NOT_FULFILLED;
                         total.put(orderIdOriginal, 1+total.get(orderIdOriginal));
                     } 
                 }
             }
             
            
             
             
              
        }
         

        return ConstraintsStatus.FULFILLED;
        
    }

}
