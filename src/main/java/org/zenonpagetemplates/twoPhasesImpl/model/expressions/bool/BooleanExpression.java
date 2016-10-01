package org.zenonpagetemplates.twoPhasesImpl.model.expressions.bool;

import java.util.ArrayList;
import java.util.List;

import org.zenonpagetemplates.common.ExpressionTokenizer;
import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.ExpressionSyntaxException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.EvaluableToBoolean;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.JPTExpression;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.JPTExpressionImpl;

/**
 * <p>
 *   Defines a boolean expression with two or more expressions.
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
abstract public class BooleanExpression extends JPTExpressionImpl implements EvaluableToBoolean {

	private static final long serialVersionUID = -8470225293970958948L;

	protected List<JPTExpression> expressions = new ArrayList<JPTExpression>();
	
	
	public BooleanExpression(){}
	public BooleanExpression( String stringExpression ){
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
	
	@Override
	public Object evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		return this.evaluateToBoolean( evaluationHelper );
	}
	
	protected static void addExpressionsFromTokenizer( BooleanExpression booleanExpression,
			ExpressionTokenizer segments ) throws ExpressionSyntaxException {
				
		String segment1 = segments.nextToken().trim();
	    booleanExpression.addExpression(
	    		ExpressionUtils.generate( segment1 ) ); 
	    		
	    while ( segments.hasMoreTokens() ) {
	        String segment = segments.nextToken().trim();
	        booleanExpression.addExpression(
	        		ExpressionUtils.generate( segment ) ); 
	    }
	}
}
