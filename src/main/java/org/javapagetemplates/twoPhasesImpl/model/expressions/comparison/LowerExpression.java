package org.javapagetemplates.twoPhasesImpl.model.expressions.comparison;

import org.javapagetemplates.common.ExpressionTokenizer;
import org.javapagetemplates.common.exceptions.EvaluationException;
import org.javapagetemplates.common.exceptions.ExpressionSyntaxException;
import org.javapagetemplates.common.scripting.EvaluationHelper;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.javapagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;

/**
 * <p>
 *   Defines an expression returning <code>true</code> if the first operator
 *   is lower than the second one; otherwise, it returns <code>false</code>.
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
public class LowerExpression extends TwoMembersComparisonExpression {

	private static final long serialVersionUID = -1973025855031855661L;

	public LowerExpression(){}
	public LowerExpression( String stringExpression, JPTExpression expression1, JPTExpression expression2 ){
		super( stringExpression, expression1, expression2 );
	}

	
	static public LowerExpression generate( String exp ) throws ExpressionSyntaxException {
        
        String expression = exp.substring( TwoPhasesPageTemplate.EXPR_LOWER.length() ).trim();
        
        // Check some conditions
        if ( expression.length() == 0 ) {
            throw new ExpressionSyntaxException( "LOWER expression void." );
        }

        ExpressionTokenizer segments = new ExpressionTokenizer( 
        		expression, 
        		TwoPhasesPageTemplate.EXPRESSION_DELIMITER );
        
        if ( segments.countTokens() != 2 ) {
        	throw new ExpressionSyntaxException(
        			"Wrong number of elements, LOWER expressions only support two." );
        }
        
        // Get both segments
        String segment1 = segments.nextToken().trim();
        String segment2 = segments.nextToken().trim();
        
        return new LowerExpression(
        		exp,
        		ExpressionUtils.generate( segment1 ),
        		ExpressionUtils.generate( segment2 ) );
	}
	
	@Override
	public Boolean evaluateToBoolean( EvaluationHelper evaluationHelper ) throws EvaluationException {
        return this.getNumber1( evaluationHelper ).longValue() < this.getNumber2( evaluationHelper ).longValue();
	}
	
	static public boolean evaluate( String exp, EvaluationHelper evaluationHelper ) 
			throws ExpressionSyntaxException, EvaluationException {
		return generate( exp ).evaluateToBoolean( evaluationHelper ); 
	}

}
