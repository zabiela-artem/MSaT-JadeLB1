package com.company.wumpusworld.agent.impl;

import com.company.wumpusworld.WumpusAction;
import com.company.wumpusworld.agent.Agent;
import com.company.wumpusworld.agent.AgentProgram;
import com.company.wumpusworld.agent.Percept;

/**
 * @author Ravi Mohan
 * @author Ciaran O'Reilly
 * @author Mike Stampone
 */
public abstract class AbstractAgent implements Agent {

    protected AgentProgram program;
    private boolean alive = true;

    public AbstractAgent() {

    }

    /**
     * Constructs an Agent with the specified AgentProgram.
     *
     * @param aProgram the Agent's program, which maps any given percept sequences to
     *                 an action.
     */
    public AbstractAgent(AgentProgram aProgram) {
        program = aProgram;
    }

    //
    // START-Agent
    public WumpusAction execute(Percept p) {
        if (null != program) {
            return program.execute(p);
        }
        return WumpusAction.CLIMB;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    // END-Agent
    //
}
