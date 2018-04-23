package com.company.wumpusworld.search.uninformed;

import com.company.wumpusworld.search.framework.*;
import com.company.wumpusworld.search.framework.problem.Problem;
import com.company.wumpusworld.util.Tasks;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Artificial Intelligence A Modern Approach (3rd Edition): Figure 3.18, page
 * 89.<br>
 * <br>
 * <p>
 * <pre>
 * function ITERATIVE-DEEPENING-SEARCH(problem) returns a solution, or failure
 *   for depth = 0 to infinity  do
 *     result &lt;- DEPTH-LIMITED-SEARCH(problem, depth)
 *     if result != cutoff then return result
 * </pre>
 * <p>
 * Figure 3.18 The iterative deepening search algorithm, which repeatedly
 * applies depth-limited search with increasing limits. It terminates when a
 * solution is found or if the depth- limited search returns failure, meaning
 * that no solution exists.
 *
 * @author Ruediger Lunde
 * @author Ravi Mohan
 * @author Ciaran O'Reilly
 */
public class IterativeDeepeningSearch<S, A> implements SearchForActions<S, A>, SearchForStates<S, A> {
    public static final String METRIC_NODES_EXPANDED = "nodesExpanded";
    public static final String METRIC_PATH_COST = "pathCost";

    private final NodeExpander<S, A> nodeExpander;
    private final Metrics metrics;

    public IterativeDeepeningSearch() {
        this(new NodeExpander<>());
    }

    public IterativeDeepeningSearch(NodeExpander<S, A> nodeExpander) {
        this.nodeExpander = nodeExpander;
        this.metrics = new Metrics();
    }


    // function ITERATIVE-DEEPENING-SEARCH(problem) returns a solution, or
    // failure
    @Override
    public Optional<List<A>> findActions(Problem<S, A> p) {
        nodeExpander.useParentLinks(true);
        return SearchUtils.toActions(findNode(p));
    }

    @Override
    public Optional<S> findState(Problem<S, A> p) {
        nodeExpander.useParentLinks(false);
        return SearchUtils.toState(findNode(p));
    }

    /**
     * Returns a solution node if a solution was found, empty if no solution is reachable or the task was cancelled
     * by the user.
     *
     * @param p
     * @return
     */
    private Optional<Node<S, A>> findNode(Problem<S, A> p) {
        clearMetrics();
        // for depth = 0 to infinity do
        for (int i = 0; !Tasks.currIsCancelled(); i++) {
            // result <- DEPTH-LIMITED-SEARCH(problem, depth)
            DepthLimitedSearch<S, A> dls = new DepthLimitedSearch<>(i, nodeExpander);
            Optional<Node<S, A>> result = dls.findNode(p);
            updateMetrics(dls.getMetrics());
            // if result != cutoff then return result
            if (!dls.isCutoffResult(result)) return result;
        }
        return Optional.empty();
    }

    @Override
    public Metrics getMetrics() {
        return metrics;
    }

    @Override
    public void addNodeListener(Consumer<Node<S, A>> listener) {
        nodeExpander.addNodeListener(listener);
    }

    @Override
    public boolean removeNodeListener(Consumer<Node<S, A>> listener) {
        return nodeExpander.removeNodeListener(listener);
    }


    //
    // PRIVATE METHODS
    //

    /**
     * Sets the nodes expanded and path cost metrics to zero.
     */
    private void clearMetrics() {
        metrics.set(METRIC_NODES_EXPANDED, 0);
        metrics.set(METRIC_PATH_COST, 0);
    }

    private void updateMetrics(Metrics dlsMetrics) {
        metrics.set(METRIC_NODES_EXPANDED, metrics.getInt(METRIC_NODES_EXPANDED) + dlsMetrics.getInt(METRIC_NODES_EXPANDED));
        metrics.set(METRIC_PATH_COST, dlsMetrics.getDouble(METRIC_PATH_COST));
    }
}
