package com.graphhopper.jsprit.examples.state;

import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.state.StateId;
import com.graphhopper.jsprit.core.algorithm.state.StateManager;
import com.graphhopper.jsprit.core.algorithm.state.StateUpdater;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.constraint.ConstraintManager;
import com.graphhopper.jsprit.core.problem.constraint.HardActivityConstraint;
import com.graphhopper.jsprit.core.problem.constraint.HardRouteConstraint;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.misc.JobInsertionContext;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.ActivityVisitor;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PedidoStatusUpdater implements StateUpdater, ActivityVisitor {
    
    private StateManager stateManager;

    private VehicleRoute route;

    private Map<Integer,StateId> states;

    public PedidoStatusUpdater(StateManager stateManager, Map<Integer,StateId> states){
        this.stateManager = stateManager;
        this.states = states;
    }

    public void begin(VehicleRoute route) {
       this.route = route;
    }

    public void visit(TourActivity activity) {
        try{
        	
            String orden = ((TourActivity.JobActivity) activity).getJob().getId();
            int ordenMap = Integer.parseInt(orden);
            stateManager.putProblemState(this.states.get(ordenMap), VehicleRoute.class, route);
            //if(stateManager.getProblemState(this.states.get(ordenMap), VehicleRoute.class))
        }catch(NumberFormatException e){
        	
        }

    }

    public void finish() {
        

    }

}
