
package com.graphhopper.jsprit.util;

import java.util.HashSet;
import java.util.Set;

import com.graphhopper.jsprit.core.problem.constraint.HardActivityConstraint;
import com.graphhopper.jsprit.core.problem.job.Delivery;
import com.graphhopper.jsprit.core.problem.job.Pickup;
import com.graphhopper.jsprit.core.problem.misc.JobInsertionContext;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.*;

public class SericePickuptFirstConstraing implements HardActivityConstraint {

    @Override
    public ConstraintsStatus fulfilled(JobInsertionContext iFacts, TourActivity prevAct, TourActivity newAct, TourActivity nextAct, double prevActDepTime) {
        /*
        VehicleRoute route = iFacts.getRoute();
        Set<String> orderIds = new HashSet<String>();
        for(TourActivity ac: route.getActivities()){
            if(ac instanceof PickupService){
                Pickup s = (Pickup) ((PickupService)ac).getJob();
                orderIds.add(s.getId());
                
            }else if(ac instanceof DeliverService){
                if(orderIds.isEmpty())  return ConstraintsStatus.NOT_FULFILLED;
 
                Delivery s = (Delivery) ((DeliverService)ac).getJob();
               
                
                for(String ids: orderIds){
                    if(!ids.contains(s.getId()+"::"))  
                        return ConstraintsStatus.NOT_FULFILLED;
 
                }
                
            }else{
               
            }
        }
        return ConstraintsStatus.FULFILLED;
        */

        if (newAct instanceof DeliverService  && nextAct instanceof PickupService) {
            return ConstraintsStatus.NOT_FULFILLED;
        }
        if (newAct instanceof ServiceActivity && nextAct instanceof PickupService) {
            return ConstraintsStatus.NOT_FULFILLED;
        }
        if (newAct instanceof PickupService  && prevAct instanceof DeliverService) {
            return ConstraintsStatus.NOT_FULFILLED_BREAK;
        }
        if (newAct instanceof PickupService && prevAct instanceof ServiceActivity) {
            return ConstraintsStatus.NOT_FULFILLED_BREAK;
        }

        if (newAct instanceof PickupShipment  && prevAct instanceof DeliverService) {
            return ConstraintsStatus.NOT_FULFILLED_BREAK;
        }
        if (newAct instanceof PickupService && prevAct instanceof PickupShipment) {
            return ConstraintsStatus.NOT_FULFILLED_BREAK;
        }
        if (newAct instanceof DeliverShipment && nextAct instanceof PickupService) {
            return ConstraintsStatus.NOT_FULFILLED;
        }
        if (newAct instanceof PickupShipment && nextAct instanceof PickupService) {
            return ConstraintsStatus.NOT_FULFILLED;
        }
 
        return ConstraintsStatus.FULFILLED;

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

        if (newAct instanceof DeliverService && prevAct instanceof PickupShipment) {
            return ConstraintsStatus.NOT_FULFILLED_BREAK;
        }
        if (newAct instanceof DeliverService && prevAct instanceof DeliverShipment) {
            return ConstraintsStatus.NOT_FULFILLED_BREAK;
        }
        if (newAct instanceof PickupShipment && nextAct instanceof DeliverService) {
            return ConstraintsStatus.NOT_FULFILLED;
        }
        if (newAct instanceof DeliverShipment && nextAct instanceof DeliverService) {
            return ConstraintsStatus.NOT_FULFILLED;
        }

        return ConstraintsStatus.FULFILLED;ç
        */
    }

}
