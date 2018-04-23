package com.company.wumpusworld;

import com.company.logic.propositional.inference.DPLL;
import com.company.logic.propositional.inference.DPLLSatisfiable;
import com.company.logic.propositional.kb.data.SetOps;
import com.company.wumpusworld.search.framework.SearchForActions;
import com.company.wumpusworld.search.framework.problem.GeneralProblem;
import com.company.wumpusworld.search.framework.problem.Problem;
import com.company.wumpusworld.search.framework.qsearch.GraphSearch;
import com.company.wumpusworld.search.informed.AStarSearch;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;

public class HybridWumpusAgent extends Agent {
    protected AgentPosition currentPosition;
    protected int t = 0;
    protected Queue<WumpusAction> plan = new LinkedList<>(); // FIFOQueue
    private WumpusKB kb = null;

    public HybridWumpusAgent() {
        this(4, 4);
    }

    public HybridWumpusAgent(int caveXDim, int caveYDim) {
        this(caveXDim, caveYDim, new DPLLSatisfiable());
    }

    public HybridWumpusAgent(int caveXDim, int caveYDim, DPLL satSolver) {
        this(caveXDim, caveYDim, new WumpusKB(caveXDim, caveYDim, satSolver));
    }

    public HybridWumpusAgent(int caveXDim, int caveYDim, WumpusKB kb) {
        this.kb = kb;
    }

    public WumpusKB getKB() {
        return kb;
    }

    @Override
    protected void setup() {
        addBehaviour(new ActionPlaner());
    }

    /**
     * Returns a sequence of actions using A* Search.
     *
     * @param goals   a set of squares; try to plan a route to one of them
     * @param allowed a set of squares that can form part of the route
     * @return the best sequence of actions that the agent have to do to reach a
     * goal from the current position.
     */
    public List<WumpusAction> planRouteToRooms(Set<Room> goals, Set<Room> allowed) {
        final Set<AgentPosition> goalPositions = new LinkedHashSet<>();
        for (Room goalRoom : goals) {
            int x = goalRoom.getX();
            int y = goalRoom.getY();
            for (AgentPosition.Orientation orientation : AgentPosition.Orientation.values())
                goalPositions.add(new AgentPosition(x, y, orientation));
        }
        return planRoute(goalPositions, allowed);
    }

    /**
     * Returns a sequence of actions using A* Search.
     *
     * @param goals   a set of agent positions; try to plan a route to one of them
     * @param allowed a set of squares that can form part of the route
     * @return the best sequence of actions that the agent have to do to reach a
     * goal from the current position.
     */
    public List<WumpusAction> planRoute(Set<AgentPosition> goals, Set<Room> allowed) {

        WumpusCave cave = new WumpusCave(kb.getCaveXDimension(), kb.getCaveYDimension()).setAllowed(allowed);
        Problem<AgentPosition, WumpusAction> problem = new GeneralProblem<>(currentPosition, WumpusFunctions.createActionsFunction(cave), WumpusFunctions.createResultFunction(cave), goals::contains);
        SearchForActions<AgentPosition, WumpusAction> search = new AStarSearch<AgentPosition, WumpusAction>(new GraphSearch<AgentPosition, WumpusAction>(), new ManhattanHeuristicFunction(goals));
        Optional<List<WumpusAction>> actions = search.findActions(problem);

        return actions.isPresent() ? actions.get() : Collections.emptyList();
    }

    /**
     * @param possibleWumpus a set of squares where we don't know that there isn't the
     *                       wumpus.
     * @param allowed        a set of squares that can form part of the route
     * @return the sequence of actions to reach the nearest square that is in
     * line with a possible wumpus position. The last action is a shot.
     */
    public List<WumpusAction> planShot(Set<Room> possibleWumpus, Set<Room> allowed) {

        Set<AgentPosition> shootingPositions = new LinkedHashSet<>();

        for (Room p : possibleWumpus) {
            int x = p.getX();
            int y = p.getY();

            for (int i = 1; i <= kb.getCaveXDimension(); i++) {
                if (i < x) shootingPositions.add(new AgentPosition(i, y, AgentPosition.Orientation.FACING_EAST));
                if (i > x) shootingPositions.add(new AgentPosition(i, y, AgentPosition.Orientation.FACING_WEST));
            }
            for (int i = 1; i <= kb.getCaveYDimension(); i++) {
                if (i < y) shootingPositions.add(new AgentPosition(x, i, AgentPosition.Orientation.FACING_NORTH));
                if (i > y) shootingPositions.add(new AgentPosition(x, i, AgentPosition.Orientation.FACING_SOUTH));
            }
        }

        // Can't have a shooting position from any of the rooms the wumpus could
        // reside
        for (Room p : possibleWumpus)
            for (AgentPosition.Orientation orientation : AgentPosition.Orientation.values())
                shootingPositions.remove(new AgentPosition(p.getX(), p.getY(), orientation));

        List<WumpusAction> actions = new ArrayList<>();
        actions.addAll(planRoute(shootingPositions, allowed));
        actions.add(WumpusAction.SHOOT);
        return actions;
    }

    private class ActionPlaner extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                Set<Room> safe = null;
                Set<Room> unvisited = null;

                if (plan.isEmpty()) {
                    currentPosition = kb.askCurrentPosition(t);
                    safe = kb.askSafeRooms(t);
                }

                if (plan.isEmpty() && kb.askGlitter(t)) {
                    Set<Room> goals = new LinkedHashSet<>();
                    goals.add(currentPosition.getRoom());
                    plan.add(WumpusAction.GRAB);
                    plan.addAll(planRouteToRooms(goals, safe));
                    plan.add(WumpusAction.CLIMB);
                }

                if (plan.isEmpty()) {
                    unvisited = kb.askUnvisitedRooms(t);
                    plan.addAll(planRouteToRooms(SetOps.intersection(unvisited, safe), safe));
                }

                if (plan.isEmpty() && kb.askHaveArrow(t)) {
                    Set<Room> possibleWumpus = kb.askPossibleWumpusRooms(t);
                    plan.addAll(planShot(possibleWumpus, safe));
                }

                if (plan.isEmpty()) {
                    Set<Room> notUnsafe = kb.askNotUnsafeRooms(t);
                    plan.addAll(planRouteToRooms(unvisited, notUnsafe));
                }

                if (plan.isEmpty()) {
                    Set<Room> goal = new LinkedHashSet<>();
                    goal.add(currentPosition.getRoom());
                    plan.addAll(planRouteToRooms(goal, safe));
                    plan.add(WumpusAction.CLIMB);
                }
                WumpusAction action = plan.remove();
                kb.makeActionSentence(action, t);
                t = t + 1;

                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.PROPAGATE);
                reply.setContent(action.getSymbol());
            } else {
                block();
            }
        }
    }
}
