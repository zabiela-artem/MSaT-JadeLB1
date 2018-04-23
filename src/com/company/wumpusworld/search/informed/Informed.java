package com.company.wumpusworld.search.informed;

import com.company.wumpusworld.search.framework.Node;

import java.util.function.ToDoubleFunction;

/**
 * Search algorithms which make use of heuristics to guide the search
 * are expected to implement this interface.
 *
 * @author Ruediger Lunde
 */
public interface Informed<S, A> {
    void setHeuristicFunction(ToDoubleFunction<Node<S, A>> h);
}
