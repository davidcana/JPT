package org.javapagetemplates.twoPhasesImpl.model.expressions.bool;

import java.util.ArrayList;
import java.util.List;

import org.javapagetemplates.common.ExpressionTokenizer;
import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.common.exceptions.ExpressionSyntaxException;
import org.javapagetemplates.twoPhasesImpl.model.expressions.EvaluableToBoolean;
import org.javapagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpressionImpl;

import bsh.Interpreter;

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
	public BooleanExpression(String stringExpression){
		super(stringExpression);
	}

	
	public List<JPTExpression> getExpressions() {
		return this.expressions;
	}

	public void setExpressions(List<JPTExpression> expressions) {
		this.expressions = expressions;
	}
	
	public void addExpression(JPTExpression expression){
		this.expressions.add(expression);
	}
	
	@Override
	public Object evaluate(Interpreter beanShell) throws ExpressionEvaluationException {
		return this.evaluateToBoolean(beanShell);
	}
	
	protected static void addExpressionsFromTokenizer(BooleanExpression booleanExpression,
			ExpressionTokenizer segments) throws ExpressionSyntaxException {
				
		String segment1 = segments.nextToken().trim();
	    booleanExpression.addExpression(
	    		ExpressionUtils.generate(segment1) ); 
	    		
	    while ( segments.hasMoreTokens() ) {
	        String segment = segments.nextToken().trim();
	        booleanExpression.addExpression(
	        		ExpressionUtils.generate(segment) ); 
	    }
	}
}
