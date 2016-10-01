package org.zenonpagetemplates.twoPhasesImpl.model.expressions.bool;

import org.zenonpagetemplates.common.ExpressionTokenizer;
import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.ExpressionSyntaxException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpression;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpressionImpl;

/**
 *  Zenon Page Templates
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
public class CondExpression extends ZPTExpressionImpl {

	private static final long serialVersionUID = -8747540098927157322L;
	
	private ZPTExpression condition;
	private ZPTExpression trueExpression;
	private ZPTExpression falseExpression;
	
	public CondExpression(){}
	public CondExpression( String stringExpression ){
		super( stringExpression );
	}
	public CondExpression( String stringExpression, ZPTExpression condition, ZPTExpression trueExpression, ZPTExpression falseExpression ){
		super( stringExpression );
		
		this.condition = condition;
		this.trueExpression = trueExpression;
		this.falseExpression = falseExpression;
	}
	
	
	static public CondExpression generate( String exp )	throws ExpressionSyntaxException {
        
		String expression = exp.substring( TwoPhasesPageTemplate.EXPR_COND.length() ).trim();
        
		// Check some conditions
        if ( expression.length() == 0 ) {
            throw new ExpressionSyntaxException( "COND expression void." );
        }

        ExpressionTokenizer segments = new ExpressionTokenizer( 
        		expression, 
        		TwoPhasesPageTemplate.EXPRESSION_DELIMITER );
        
        if ( segments.countTokens() != 3 ) {
        	throw new ExpressionSyntaxException( "3 element are needed in COND expression." );
        }
        
        // Get segments
        String conditionSegment = segments.nextToken().trim();
        String trueSegment = segments.nextToken().trim();
        String falseSegment = segments.nextToken().trim();
        
        return new CondExpression(
        		exp, 
        		ExpressionUtils.generate( conditionSegment ), 
        		ExpressionUtils.generate( trueSegment ), 
        		ExpressionUtils.generate( falseSegment ) );
	}
	
	
	public ZPTExpression getCondition() {
		return this.condition;
	}

	public void setCondition( ZPTExpression condition ) {
		this.condition = condition;
	}

	public ZPTExpression getTrueExpression() {
		return this.trueExpression;
	}

	public void setTrueExpression( ZPTExpression trueExpression ) {
		this.trueExpression = trueExpression;
	}

	public ZPTExpression getFalseExpression() {
		return this.falseExpression;
	}

	public void setFalseExpression( ZPTExpression falseExpression ) {
		this.falseExpression = falseExpression;
	}
	
	@Override
	public Object evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		
        // Evaluate first expression
        boolean fistExpressionResult = ExpressionUtils.evaluateToBoolean( 
    			this.condition, evaluationHelper );
        
        // If true, evaluate second expression
        if ( fistExpressionResult ){
        	return this.trueExpression.evaluate( evaluationHelper );
        }

        // If false, evaluate third expression
    	return this.falseExpression.evaluate( evaluationHelper );
	}
	
	static public Object evaluate( String exp, EvaluationHelper evaluationHelper ) 
			throws ExpressionSyntaxException, EvaluationException {
		return generate( exp ).evaluate( evaluationHelper );
	}
	
}
