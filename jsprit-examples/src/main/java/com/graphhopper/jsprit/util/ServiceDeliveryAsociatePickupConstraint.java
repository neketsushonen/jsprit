
package com.graphhopper.jsprit.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.graphhopper.jsprit.core.algorithm.state.StateId;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.problem.constraint.HardRouteConstraint;
import com.graphhopper.jsprit.core.problem.job.Delivery;
import com.graphhopper.jsprit.core.problem.job.Job;
import com.graphhopper.jsprit.core.problem.job.Pickup;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.misc.JobInsertionContext;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.*;

public class ServiceDeliveryAsociatePickupConstraint implements HardRouteConstraint {
    Map<Integer,List<Service>> pedidos ;

    
    StateManager stateManager;
    
    private Map<Integer,StateId> states;
    
    public ServiceDeliveryAsociatePickupConstraint( Map<Integer,List<Service>> pedidos){
        this.pedidos = pedidos;
    }
    
    public ServiceDeliveryAsociatePickupConstraint(StateManager stateManager,Map<Integer,StateId> states ){
    	this.stateManager = stateManager;
    	this.states = states;
    	
    }

    @Override
    public boolean fulfilled(JobInsertionContext iFacts) {
    	 Job job = iFacts.getJob();
    	 
    	 
    	 if (job instanceof Delivery) {
    		 Delivery s = (Delivery) job;   
    		 //System.out.println(s.getId()+" size "+ iFacts.getRoute().getActivities() .size());
             String orderIdOriginal = s.getId().split("::")[0];
             StateId sx = this.states.get(Integer.parseInt(orderIdOriginal));
            
            /*
             VehicleRoute route = stateManager.getProblemState(sx, VehicleRoute.class);
             if(route!=null && route != iFacts.getRoute())  return false;
             else if(route==null) return false;
            // System.out.println("size "+ iFacts.getAssociatedActivities().size()); 
             if( iFacts.getRoute().getActivities().isEmpty()) return false;
             */
             
             
             List<String> orderIds = new ArrayList<String>();
             for(TourActivity ac:  iFacts.getRoute().getActivities()){
                 if(ac instanceof PickupService){
                     
                     Pickup spick = (Pickup) ((PickupService)ac).getJob();
                     orderIds.add(spick.getId());
                     if(spick.getId().equalsIgnoreCase(orderIdOriginal)){
                         
                       // if(job.getIndex()<= ac.getIndex()+1) return false;
                     }
                    // System.out.println(orderIds);
                 }else if(ac instanceof DeliverService){
                     
                      Delivery sdekivert = (Delivery) ((DeliverService)ac).getJob();       
                     String orderIdOriginalsdekivert = sdekivert.getId().split("::")[0];
                     return orderIds.contains(orderIdOriginalsdekivert);   
                 } 
             } 
             
    	 }else {
    		 for(TourActivity ac:  iFacts.getRoute().getActivities()){
                 if(ac instanceof PickupService){
                     
                     Pickup spick = (Pickup) ((PickupService)ac).getJob();
                   
                 }else if(ac instanceof DeliverService){
                	 Delivery sdekivert = (Delivery) ((DeliverService)ac).getJob();       
                	// System.out.println(sdekivert.getId());
                	 
                     
                 } 
             } 
    		 
    		 
    	 }
    	 return true;
       
    	
    	 /*
    	System.out.println(job.getId());
    	 
       
      
        if(iFacts.getAssociatedActivities() .isEmpty()){
           
            return (job instanceof Pickup) ; 
        }else{
            for(TourActivity ac: iFacts.getAssociatedActivities()){
                if(ac instanceof PickupService){
                    
                    Pickup s = (Pickup) ((PickupService)ac).getJob();
                    orderIds.add(s.getId());
                }else if(ac instanceof DeliverService){
                    
                    if(orderIds.isEmpty()) return false;     
                    Delivery s = (Delivery) ((DeliverService)ac).getJob();       
                    String orderIdOriginal = s.getId().split("::")[0];
                    return orderIds.contains(orderIdOriginal);   
                } 
            }

        }

        if((job instanceof Pickup))
            return true;
        else {
            Delivery s = (Delivery) job;     
            String orderIdOriginal = s.getId().split("::")[0];
            return orderIds.contains(orderIdOriginal);   
        }
        
        */
        
    }

     

}
