package org.javapagetemplates.twoPhasesImpl.model.expressions.bool;

import java.util.Iterator;

import org.javapagetemplates.common.ExpressionTokenizer;
import org.javapagetemplates.common.exceptions.EvaluationException;
import org.javapagetemplates.common.exceptions.ExpressionSyntaxException;
import org.javapagetemplates.common.scripting.EvaluationHelper;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.javapagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;

/**
 * <p>
 *   Defines an AND expression with two or more expressions.
 *   It uses lazy evaluation.
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
public class AndExpression extends BooleanExpression {

	private static final long serialVersionUID = -4354105870191373633L;

	public AndExpression(){
		super();
	}
	public AndExpression( String stringExpression ){
		super( stringExpression );
	}
	
	
	static public AndExpression generate( String exp ) throws ExpressionSyntaxException {
        
		String expression = exp.substring( TwoPhasesPageTemplate.EXPR_AND.length() ).trim();
        
		// Check some conditions
        if ( expression.length() == 0 ) {
            throw new ExpressionSyntaxException( "AND expression void." );
        }

        ExpressionTokenizer segments = new ExpressionTokenizer( 
        		expression, 
        		TwoPhasesPageTemplate.EXPRESSION_DELIMITER );
        
        if ( segments.countTokens() == 1 ) {
        	throw new ExpressionSyntaxException(
        			"Only one element in AND expression, please add at least one more." );
        }
        
        // Iterate through segments
        AndExpression result = new AndExpression( exp );
        
        addExpressionsFromTokenizer( result, segments );
        
        return result;
	}
	
	@Override
	public Boolean evaluateToBoolean( EvaluationHelper evaluationHelper ) throws EvaluationException {
		
		Iterator<JPTExpression> i = this.expressions.iterator();
		
        while ( i.hasNext() ) {
        	JPTExpression currentExpression = i.next();
        	if ( ! ExpressionUtils.evaluateToBoolean( currentExpression, evaluationHelper ) ){
            	return false;
            }
        }

        return true;
	}
	
	static public boolean evaluate( String exp, EvaluationHelper evaluationHelper ) 
			throws ExpressionSyntaxException, EvaluationException {
		return generate( exp ).evaluateToBoolean( evaluationHelper );
	}

}
