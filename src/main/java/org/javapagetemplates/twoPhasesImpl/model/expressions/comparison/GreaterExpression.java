package org.javapagetemplates.twoPhasesImpl.model.expressions.comparison;

import org.javapagetemplates.common.ExpressionTokenizer;
import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.common.exceptions.ExpressionSyntaxException;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.javapagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;

import bsh.Interpreter;

/**
 * <p>
 *   Defines an expression returning <code>true</code> if the first operator
 *   is greater than the second one; otherwise, it returns <code>false</code>.
 * </p>
 * 
 * 
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
public class GreaterExpression extends TwoMembersComparisonExpression {

	private static final long serialVersionUID = 5006927625514718715L;
	
	public GreaterExpression(){}
	public GreaterExpression(String stringExpression, JPTExpression expression1, JPTExpression expression2){
		super(stringExpression, expression1, expression2);
	}

	
	static public GreaterExpression generate(String exp) 
			throws ExpressionSyntaxException {
        
        String expression = exp.substring( TwoPhasesPageTemplate.EXPR_GREATER.length() ).trim();
        
        // Check some conditions
        if ( expression.length() == 0 ) {
            throw new ExpressionSyntaxException("Greater expression void.");
        }

        ExpressionTokenizer segments = new ExpressionTokenizer( 
        		expression, 
        		TwoPhasesPageTemplate.EXPRESSION_DELIMITER );
        
        if ( segments.countTokens() != 2 ) {
        	throw new ExpressionSyntaxException("Wrong number of elements, greater expressions only support two.");
        }
        
        // Get both segments
        String segment1 = segments.nextToken().trim();
        String segment2 = segments.nextToken().trim();
        
        return new GreaterExpression(
        		exp,
        		ExpressionUtils.generate(segment1),
        		ExpressionUtils.generate(segment2));
	}
	
	@Override
	public Boolean evaluateToBoolean(Interpreter beanShell)
			throws ExpressionEvaluationException {
		
        return this.getNumber1(beanShell).longValue() > this.getNumber2(beanShell).longValue();
	}
	
	static public boolean evaluate(String exp, Interpreter beanShell) 
			throws ExpressionSyntaxException, ExpressionEvaluationException {
		return generate(exp).evaluateToBoolean(beanShell); 
	}

}
