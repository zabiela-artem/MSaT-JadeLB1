package com.company.wumpusworld.search.uninformed;

import com.company.wumpusworld.search.framework.QueueBasedSearch;
import com.company.wumpusworld.search.framework.QueueFactory;
import com.company.wumpusworld.search.framework.qsearch.QueueSearch;

/**
 * Artificial Intelligence A Modern Approach (3rd Edition): page 85.<br>
 * <br>
 * Depth-first search always expands the deepest node in the current frontier of
 * the search tree. <br>
 * <br>
 * <b>Note:</b> Supports TreeSearch, GraphSearch, and BidirectionalSearch. Just
 * provide an instance of the desired QueueSearch implementation to the
 * constructor!
 *
 * @author Ruediger Lunde
 * @author Ravi Mohan
 */
public class DepthFirstSearch<S, A> extends QueueBasedSearch<S, A> {

    public DepthFirstSearch(QueueSearch<S, A> impl) {
        super(impl, QueueFactory.createLifoQueue());
    }
}
