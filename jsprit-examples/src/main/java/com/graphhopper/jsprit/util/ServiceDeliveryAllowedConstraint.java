
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

public class ServiceDeliveryAllowedConstraint implements HardActivityConstraint {
    Map<Integer,List<String>> pedidos  ;
    
    StateManager stateManager;
    
    private Map<Integer,StateId> states;
    

    public ServiceDeliveryAllowedConstraint(Map<Integer, List<Service>> pedidosOriginal) {
        pedidos = new HashMap<Integer,List<String>>();
        for(Map.Entry<Integer,List<Service>> entry: pedidosOriginal.entrySet()){
           if(!pedidos.containsKey(entry.getKey()))
                pedidos.put(entry.getKey(),new ArrayList<String>());
            pedidos.get(entry.getKey()).addAll(entry.getValue().stream().map(f -> f.getId()).collect(Collectors.toList()));
        }
       // this.pedidos = pedidos.entrySet().stream().map(f-> f.get)
    }

    public ServiceDeliveryAllowedConstraint(){

    }
    
    public ServiceDeliveryAllowedConstraint(StateManager stateManager,Map<Integer,StateId> states,Map<Integer, List<Service>> pedidosOriginal ){
    	
    	this( pedidosOriginal);
    	this.stateManager = stateManager;
    	this.states = states;
    }

    @Override
    public ConstraintsStatus fulfilled(JobInsertionContext iFacts, TourActivity prevAct, TourActivity newAct, TourActivity nextAct, double prevActDepTime) {

        
        
        if(newAct instanceof  DeliverService){
            Delivery s = (Delivery) ((DeliverService)newAct).getJob();  
            String orderIdOriginal = s.getId().split("::")[0];
            List<String> orderIds = new ArrayList<String>();
            List<TourActivity> activities = iFacts.getRoute().getActivities();
            for(TourActivity act: activities){
                if(act instanceof PickupService){
                    Pickup sgg = (Pickup) ((PickupService)act).getJob();
                    orderIds.add(sgg.getId());
                }
                if(prevAct == act){
                    if(!orderIds.contains(orderIdOriginal))  
                        return ConstraintsStatus.NOT_FULFILLED;
                }
                
            }
        }
        
/*
        if(newAct instanceof  DeliverService && nextAct instanceof PickupService){
            Delivery s = (Delivery) ((DeliverService)newAct).getJob();  
            String orderIdOriginal = s.getId().split("::")[0];
            Pickup sgg = (Pickup) ((PickupService)nextAct).getJob();
            if(orderIdOriginal.equalsIgnoreCase(sgg.getId()))  return ConstraintsStatus.NOT_FULFILLED;
        }
        if(newAct instanceof  DeliverService) {

            /*

            
            Delivery s = (Delivery) ((DeliverService)newAct).getJob();  
            String orderIdOriginal = s.getId().split("::")[0];
            List<TourActivity> activities = iFacts.getRoute().getActivities();
            boolean insertable = false;
            if(activities.isEmpty()) return   ConstraintsStatus.NOT_FULFILLED_BREAK;
            for( int indice = newAct.getIndex()-1; indice>=0;indice--){
                if(indice >= activities.size()) return   ConstraintsStatus.NOT_FULFILLED_BREAK;
                TourActivity act = activities.get(indice);
                if(act instanceof PickupService){
                    Pickup sgg = (Pickup) ((PickupService)act).getJob();
                    if(sgg.getId().equalsIgnoreCase(orderIdOriginal)) 
                        insertable = true;
                }
            }

            if(insertable)
                return ConstraintsStatus.FULFILLED;
            else return ConstraintsStatus.NOT_FULFILLED;
            */

            /*
            
            Map<String, Integer> ordenPosicion = new HashMap<String, Integer>();


        	Delivery s = (Delivery) ((DeliverService)newAct).getJob();  
        	String orderIdOriginal = s.getId().split("::")[0];
            StateId sx = this.states.get(Integer.parseInt(orderIdOriginal));
          
            List<String> orderIds = new ArrayList<String>();
            List<TourActivity> activities = iFacts.getRoute().getActivities();
            //System.out.println("========");
            for(TourActivity act: activities){
                if(act instanceof Start || act instanceof End ){
                    continue;
                }else{

                    if(act instanceof PickupService){
                        Pickup sgg = (Pickup) ((PickupService)act).getJob();
                       
                        orderIds.add(sgg.getId());
                        ordenPosicion.put(sgg.getId(),sgg.getIndex());
              
                       // if(s.getId().equalsIgnoreCase("1"))
                       // System.out.println(orderIds+":::"+sgg.getId());
                        
                    }
                    
                    if(act instanceof DeliverService){
                    	Delivery sgg = (Delivery) ((DeliverService)act).getJob();
                        if(!orderIds.contains(sgg.getId().split("::")[0]))  
                            return ConstraintsStatus.NOT_FULFILLED;
                        
                    }
                
                }
              }     
            if(!orderIds.contains(orderIdOriginal))  return ConstraintsStatus.NOT_FULFILLED;

            */
           
            /*
            if(ordenPosicion.get(orderIdOriginal) > ((Delivery) ((DeliverService)newAct).getJob()) .getIndex()) return ConstraintsStatus.NOT_FULFILLED;
            
            if(nextAct instanceof PickupService){
                Pickup sgg = (Pickup) ((PickupService)nextAct).getJob();
                if(sgg.getId().equalsIgnoreCase(orderIdOriginal)) 
                return ConstraintsStatus.NOT_FULFILLED;

            }

             }
*/
            

       
        
        

        /*
        if(nextAct instanceof  PickupService) {
            Pickup sx = (Pickup) ((PickupService)nextAct).getJob();
            List<TourActivity> activities = iFacts.getRoute().getActivities();
            for(TourActivity act: activities){
                 

                    if(act instanceof DeliverService){
                        Delivery s = (Delivery) ((DeliverService)act).getJob();  
                        String orderIdOriginal = s.getId().split("::")[0];
                        if(orderIdOriginal.equalsIgnoreCase(sx.getId()))
                        return ConstraintsStatus.NOT_FULFILLED_BREAK;
                        
                    } 
                 
            }
        }
        */

        return ConstraintsStatus.FULFILLED;
        
    }

}
