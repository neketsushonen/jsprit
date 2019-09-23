
package com.graphhopper.jsprit.util;

import java.util.HashSet;
import java.util.Set;

import com.graphhopper.jsprit.core.problem.constraint.SoftRouteConstraint;
import com.graphhopper.jsprit.core.problem.job.Delivery;
import com.graphhopper.jsprit.core.problem.job.Pickup;
import com.graphhopper.jsprit.core.problem.misc.JobInsertionContext;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.*;

public class ServiceCostDeliveryAsociatePickupConstraint implements SoftRouteConstraint {

   

    @Override
    public double getCosts(JobInsertionContext iFacts) {
        VehicleRoute route = iFacts.getRoute();
        Set<String> orderIds = new HashSet<String>();
        for(TourActivity ac: route.getTourActivities().getActivities()){
            
            if(ac instanceof PickupService){
                Pickup s = (Pickup) ((PickupService)ac).getJob();
                orderIds.add(s.getId());
                
            }else if(ac instanceof DeliverService){
                if(orderIds.isEmpty())  return Double.MAX_VALUE;
 
                Delivery s = (Delivery) ((DeliverService)ac).getJob();
               

                for(String idOrder: orderIds){
                    if(!s.getId().contains(idOrder+"::"))  
                        return Double.MAX_VALUE;
 
                }
                
            } 
        }
        return 0;
    }

     

}
