package org.javapagetemplates.twoPhasesImpl.model.expressions.comparison;

import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.twoPhasesImpl.model.expressions.EvaluableToBoolean;
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
 *
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.1 $
 */
abstract public class TwoMembersComparisonExpression extends JPTExpressionImpl implements EvaluableToBoolean {

	private static final long serialVersionUID = -6156728023036891309L;
	
	protected JPTExpression expression1;
	protected JPTExpression expression2;
	
	public TwoMembersComparisonExpression(){}
	public TwoMembersComparisonExpression(String stringExpression){
		super(stringExpression);
	}
	public TwoMembersComparisonExpression(String stringExpression, JPTExpression expression1, JPTExpression expression2){
		super(stringExpression);
		
		this.expression1 = expression1;
		this.expression2 = expression2;
	}

	
	public JPTExpression getExpression1() {
		return this.expression1;
	}

	public void setExpression1(JPTExpression expression1) {
		this.expression1 = expression1;
	}

	public JPTExpression getExpression2() {
		return this.expression2;
	}

	public void setExpression2(JPTExpression expression2) {
		this.expression2 = expression2;
	}

	@Override
	public Object evaluate(Interpreter beanShell) throws ExpressionEvaluationException {
		return this.evaluateToBoolean(beanShell);
	}

	protected Number getNumber1(Interpreter beanShell) throws ExpressionEvaluationException {
		return ExpressionUtils.evaluateToNumber(
	    		this.expression1, beanShell);
	}
	
	protected Number getNumber2(Interpreter beanShell) throws ExpressionEvaluationException {
		return ExpressionUtils.evaluateToNumber(
	    		this.expression2, beanShell);
	}
}
