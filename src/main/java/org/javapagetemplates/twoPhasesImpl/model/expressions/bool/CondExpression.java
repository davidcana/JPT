package org.javapagetemplates.twoPhasesImpl.model.expressions.bool;

import org.javapagetemplates.common.ExpressionTokenizer;
import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.common.exceptions.ExpressionSyntaxException;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.javapagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpressionImpl;

import bsh.Interpreter;

/**
 *  Java Page Templates
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.0 $
 */
public class CondExpression extends JPTExpressionImpl {

	private static final long serialVersionUID = -8747540098927157322L;
	
	private JPTExpression condition;
	private JPTExpression trueExpression;
	private JPTExpression falseExpression;
	
	public CondExpression(){}
	public CondExpression(String stringExpression){
		super(stringExpression);
	}
	public CondExpression(String stringExpression, JPTExpression condition, JPTExpression trueExpression, JPTExpression falseExpression){
		super(stringExpression);
		
		this.condition = condition;
		this.trueExpression = trueExpression;
		this.falseExpression = falseExpression;
	}
	
	
	static public CondExpression generate(String exp) 
			throws ExpressionSyntaxException {
        
		String expression = exp.substring( TwoPhasesPageTemplate.EXPR_COND.length() ).trim();
        
		// Check some conditions
        if ( expression.length() == 0 ) {
            throw new ExpressionSyntaxException("Cond expression void.");
        }

        ExpressionTokenizer segments = new ExpressionTokenizer( 
        		expression, 
        		TwoPhasesPageTemplate.EXPRESSION_DELIMITER );
        
        if ( segments.countTokens() != 3 ) {
        	throw new ExpressionSyntaxException("3 element are needed in cond expression.");
        }
        
        // Get segments
        String conditionSegment = segments.nextToken().trim();
        String trueSegment = segments.nextToken().trim();
        String falseSegment = segments.nextToken().trim();
        
        return new CondExpression(
        		exp, 
        		ExpressionUtils.generate( conditionSegment ), 
        		ExpressionUtils.generate( trueSegment ), 
        		ExpressionUtils.generate( falseSegment ));
	}
	
	
	public JPTExpression getCondition() {
		return this.condition;
	}

	public void setCondition(JPTExpression condition) {
		this.condition = condition;
	}

	public JPTExpression getTrueExpression() {
		return this.trueExpression;
	}

	public void setTrueExpression(JPTExpression trueExpression) {
		this.trueExpression = trueExpression;
	}

	public JPTExpression getFalseExpression() {
		return this.falseExpression;
	}

	public void setFalseExpression(JPTExpression falseExpression) {
		this.falseExpression = falseExpression;
	}
	
	@Override
	public Object evaluate(Interpreter beanShell)
			throws ExpressionEvaluationException {
		
        // Evaluate first expression
        boolean fistExpressionResult = ExpressionUtils.evaluateToBoolean( 
    			this.condition, beanShell );
        
        // If true, evaluate second expression
        if (fistExpressionResult){
        	return this.trueExpression.evaluate( beanShell );
        }

        // If false, evaluate third expression
    	return this.falseExpression.evaluate( beanShell );
	}
	
	static public Object evaluate(String exp, Interpreter beanShell) 
			throws ExpressionSyntaxException, ExpressionEvaluationException {
		return generate(exp).evaluate(beanShell);
	}
	
}
