package com.company.logic.propositional.parsing;

import com.company.logic.propositional.parsing.ast.ComplexSentence;
import com.company.logic.propositional.parsing.ast.PropositionSymbol;
import com.company.logic.propositional.parsing.ast.Sentence;

/**
 * Abstract implementation of the PLVisitor interface that provides default
 * behavior for each of the methods.
 *
 * @param <A> the argument type to be passed to the visitor methods.
 * @author Ravi Mohan
 * @author Ciaran O'Reilly
 */
public abstract class AbstractPLVisitor<A> implements PLVisitor<A, Sentence> {

    @Override
    public Sentence visitPropositionSymbol(PropositionSymbol s, A arg) {
        // default behavior is to treat propositional symbols as atomic
        // and leave unchanged.
        return s;
    }

    @Override
    public Sentence visitUnarySentence(ComplexSentence s, A arg) {
        // a new Complex Sentence with the same connective but possibly
        // with its simpler sentence replaced by the visitor.
        return new ComplexSentence(s.getConnective(), s.getSimplerSentence(0).accept(this, arg));
    }

    @Override
    public Sentence visitBinarySentence(ComplexSentence s, A arg) {
        // a new Complex Sentence with the same connective but possibly
        // with its simpler sentences replaced by the visitor.
        return new ComplexSentence(s.getConnective(), s.getSimplerSentence(0).accept(this, arg), s.getSimplerSentence(1).accept(this, arg));
    }
}
