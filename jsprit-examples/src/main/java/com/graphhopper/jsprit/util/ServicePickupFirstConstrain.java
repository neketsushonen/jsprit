
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

public class ServicePickupFirstConstrain implements HardActivityConstraint {
    Map<Integer,List<String>> pedidos  ;
    
    StateManager stateManager;
    
    private Map<Integer,StateId> states;
    

    public ServicePickupFirstConstrain(Map<Integer, List<Service>> pedidosOriginal) {
        pedidos = new HashMap<Integer,List<String>>();
        for(Map.Entry<Integer,List<Service>> entry: pedidosOriginal.entrySet()){
           if(!pedidos.containsKey(entry.getKey()))
                pedidos.put(entry.getKey(),new ArrayList<String>());
            pedidos.get(entry.getKey()).addAll(entry.getValue().stream().map(f -> f.getId()).collect(Collectors.toList()));
        }
       // this.pedidos = pedidos.entrySet().stream().map(f-> f.get)
    }

    public ServicePickupFirstConstrain(){

    }
    
    public ServicePickupFirstConstrain(StateManager stateManager,Map<Integer,StateId> states,Map<Integer, List<Service>> pedidosOriginal ){
    	
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
        
     
        
        if(newAct instanceof  DeliverService) {
        	Delivery s = (Delivery) ((DeliverService)newAct).getJob();  
        	String orderIdOriginal = s.getId().split("::")[0];
            StateId sx = this.states.get(Integer.parseInt(orderIdOriginal));
            VehicleRoute route = stateManager.getProblemState(sx, VehicleRoute.class);
            if(route==null)  return ConstraintsStatus.NOT_FULFILLED;
            else if(route != iFacts.getRoute()) return ConstraintsStatus.NOT_FULFILLED;
            
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
                      
                       // if(s.getId().equalsIgnoreCase("1"))
                       // System.out.println(orderIds+":::"+sgg.getId());
                        
                    }
                    
                    if(act instanceof DeliverService){
                    	Delivery sgg = (Delivery) ((DeliverService)act).getJob();
                       
                       
                       if(!orderIds.contains(sgg.getId().split("::")[0]))  return ConstraintsStatus.NOT_FULFILLED;
                     //   System.out.println(orderIds+"---"+sgg.getId());
                        
                    }
                
                }
              }     
          //  System.out.println("========");
            // System.out.println(orderIdOriginal+"*****"+s.getId());
             if(!orderIds.contains(orderIdOriginal))  return ConstraintsStatus.NOT_FULFILLED;
             
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
             
             
             /*
             for(Map.Entry<String, Integer> totalC: total.entrySet()) {
            	 Integer key = Integer.parseInt(totalC.getKey());
            	 System.out.println(key+"::"+totalC.getValue()+"::"+(this.pedidos.get(key).size()-1)+"::"+orderIds); 
            	 //if(this.pedidos.get(key).size()!= totalC.getValue())  
            		// return ConstraintsStatus.NOT_FULFILLED;
            	 //-1 restar el propio pickup
            	 if(totalC.getValue()!=0 ) {
            		 if(this.pedidos.get(key).size()-1!= totalC.getValue())  
            			 return ConstraintsStatus.NOT_FULFILLED_BREAK;
            	 }else {
            		 if(existeDelivery)
            			 if(totalC.getValue() < this.pedidos.get(key).size()-1)
            				 return ConstraintsStatus.NOT_FULFILLED;
            	 }
            		
             }
             */
             
             
              
        }
         
        /* 
        
        if(nextAct instanceof End){
            if(iFacts.getRoute().getVehicle().getId().equalsIgnoreCase("Ñuñoa@[8,0]")){
                System.out.println(prevAct.getName()+ " :: "+  newAct.getName());
            }
            //System.out.println(iFacts.getRoute().getTourActivities().getActivities().size());
        }
        */
        
        /*
        if( newAct instanceof  DeliverService ){
            Delivery s = (Delivery) ((DeliverService)newAct).getJob();  
            if(iFacts.getRoute().getVehicle().getId().equalsIgnoreCase("Ñuñoa@[8,0]") &&  s.getId().equalsIgnoreCase("1::8")){
                
                    System.out.println(iFacts.getRoute().getTourActivities().getActivities().size());
            }
            
        }
        */
      

        /*
        if (newAct instanceof  DeliverService && nextAct instanceof  PickupService) {
            return ConstraintsStatus.NOT_FULFILLED;
        }
        if (newAct instanceof ServiceActivity && nextAct instanceof PickupService) {
            return ConstraintsStatus.NOT_FULFILLED;
        }
        if (newAct instanceof PickupService && prevAct instanceof  DeliverService) {
            return ConstraintsStatus.NOT_FULFILLED_BREAK;
        }
        if (newAct instanceof DeliverService && prevAct instanceof ServiceActivity) {
            return ConstraintsStatus.NOT_FULFILLED_BREAK;
        }

        */


        return ConstraintsStatus.FULFILLED;
        
    }

}
