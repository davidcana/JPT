package org.zenonpagetemplates.twoPhasesImpl.model.expressions.arithmethic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zenonpagetemplates.common.ExpressionTokenizer;
import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.ExpressionSyntaxException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.EvaluableToNumber;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.JPTExpression;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.JPTExpressionImpl;

/**
 * <p>
 *   Defines an arithmetic expression with two or more expressions.
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
abstract public class ArithmethicExpression extends JPTExpressionImpl implements EvaluableToNumber {

	private static final long serialVersionUID = 7507838228949531425L;

	private List<JPTExpression> expressions = new ArrayList<JPTExpression>();
	
	
	public ArithmethicExpression(){}
	public ArithmethicExpression( String stringExpression ){
		super( stringExpression );
	}
	

	public List<JPTExpression> getExpressions() {
		return this.expressions;
	}

	public void setExpressions( List<JPTExpression> expressions ) {
		this.expressions = expressions;
	}

	public void addExpression( JPTExpression expression ){
		this.expressions.add( expression );
	}

	abstract protected Number doOperation( Number value1, Number value2 );

	
	static public void configure( String exp, ArithmethicExpression arithmethicExpression, 
			String operationName, String initialChars ) throws ExpressionSyntaxException {
    	
    	// Check the expression and the number of segments
    	String expression = exp.substring( initialChars.length() ).trim();
        
        if ( expression.length() == 0 ) {
            throw new ExpressionSyntaxException( operationName + " expression void." );
        }

        ExpressionTokenizer segments = new ExpressionTokenizer( 
        		expression, 
        		TwoPhasesPageTemplate.EXPRESSION_DELIMITER );
        
        if ( segments.countTokens() == 1 ) {
        	throw new ExpressionSyntaxException(
        			"Only one element in " + operationName 
        			+ " expression, please add at least one more.");
        }

        // Set string expression
        arithmethicExpression.setStringExpression( exp );
        
        // Iterate through segments
        String segment1 = segments.nextToken().trim();
        arithmethicExpression.addExpression(
        		ExpressionUtils.generate( segment1 ) ); 
        		
        while ( segments.hasMoreTokens() ) {
            String segment = segments.nextToken().trim();
            arithmethicExpression.addExpression(
            		ExpressionUtils.generate( segment ) ); 
        }
	}
	
	@Override
	public Number evaluateToNumber( EvaluationHelper evaluationHelper ) throws EvaluationException {
		
		Iterator<JPTExpression> i = this.expressions.iterator();
		
		Number result = ExpressionUtils.evaluateToNumber( i.next(), evaluationHelper );
		
		while ( i.hasNext() ){
			JPTExpression expression = i.next();
			
			Number value = ExpressionUtils.evaluateToNumber(
					expression, evaluationHelper );
			
			result = this.doOperation( result, value );
		}
		
		return result;
	}
	
	@Override
	public Object evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		return this.evaluateToNumber( evaluationHelper );
	}

}
