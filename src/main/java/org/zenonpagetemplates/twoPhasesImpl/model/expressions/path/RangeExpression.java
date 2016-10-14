package org.zenonpagetemplates.twoPhasesImpl.model.expressions.path;

import java.util.ArrayList;
import java.util.List;

import org.zenonpagetemplates.common.ExpressionTokenizer;
import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.ExpressionSyntaxException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpression;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpressionImpl;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.path.literals.IntegerLiteralExpression;

public class RangeExpression extends ZPTExpressionImpl implements ListExpressionItem {

	private static final long serialVersionUID = 4635634769824978330L;
	private static final ZPTExpression DEFAULT_START = new IntegerLiteralExpression( 0 );
	private static final ZPTExpression DEFAULT_STEP = new IntegerLiteralExpression( 1 );
    
	private ZPTExpression start;
	private ZPTExpression end;
	private ZPTExpression step;
	
	public RangeExpression(){}
	
	public RangeExpression(String stringExpression, ZPTExpression start, ZPTExpression end, ZPTExpression step){
		super( stringExpression );
		
		this.start = start;
		this.end = end;
		this.step = step;
		/*
		this.start = start == null? DEFAULT_START: start;
		this.end = end;
		this.step = step == null? DEFAULT_STEP: step;*/
	}

	public ZPTExpression getStart() {
		return start;
	}

	public void setStart(ZPTExpression start) {
		this.start = start;
	}

	public ZPTExpression getEnd() {
		return end;
	}

	public void setEnd(ZPTExpression end) {
		this.end = end;
	}

	public ZPTExpression getStep() {
		return step;
	}

	public void setStep(ZPTExpression step) {
		this.step = step;
	}

	@Override
	public Object evaluate(EvaluationHelper evaluationHelper)
			throws EvaluationException {
		
		// Evaluate properties to integer values
		int intStart = ExpressionUtils.evaluateToNumber( this.start, evaluationHelper ).intValue();
		int intEnd = ExpressionUtils.evaluateToNumber( this.end, evaluationHelper ).intValue();
		int intStep = ExpressionUtils.evaluateToNumber( this.step, evaluationHelper ).intValue();
		
		// Evaluate the range
		List<Object> result = new ArrayList<Object>();
        boolean forward = intStep > 0; 
        
        int c = intStart;
        while( forward? c <= intEnd: c >= intEnd ){
            result.add( c );
            c += intStep;
        }
        
        return result;
	}
	
	static public RangeExpression generate( String exp ) throws ExpressionSyntaxException {
		
        String expression = exp.trim();
        
        // Return null if there is no range delimiter
        if ( expression.indexOf( TwoPhasesPageTemplate.RANGE_DELIMITER ) == -1){
        	return null;
        }
        
        ExpressionTokenizer segments = new ExpressionTokenizer( expression, TwoPhasesPageTemplate.RANGE_DELIMITER );
        
        int numberOfTokens = segments.countTokens();
        if ( numberOfTokens != 2 && numberOfTokens != 3 ) {
            throw new ExpressionSyntaxException( "Malformed range expression: " + expression );
        }
        
        // Build start expression if any
        String start = segments.nextToken().trim();
        ZPTExpression startExpression = 
        		start.equals( "" )? 
        		DEFAULT_START: 
        		ExpressionUtils.generate( start );
        
        // Build end expression
        String end = segments.nextToken().trim();
        ZPTExpression endExpression = ExpressionUtils.generate( end );
        
        // Build step expression if any
        ZPTExpression stepExpression = DEFAULT_STEP;
        if ( numberOfTokens == 3 ){
            String step = segments.nextToken().trim();
            stepExpression = ExpressionUtils.generate( step );
        }
        
        return new RangeExpression( expression, startExpression, endExpression, stepExpression );
	}

}
