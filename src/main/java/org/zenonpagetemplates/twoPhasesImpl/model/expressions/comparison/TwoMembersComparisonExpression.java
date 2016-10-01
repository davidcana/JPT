package org.zenonpagetemplates.twoPhasesImpl.model.expressions.comparison;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.EvaluableToBoolean;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpression;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpressionImpl;

/**
 * <p>
 *   Abstract class that defines an expression with 2 operators.
 * </p>
 * 
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
 *
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.1 $
 */
abstract public class TwoMembersComparisonExpression extends ZPTExpressionImpl implements EvaluableToBoolean {

	private static final long serialVersionUID = -6156728023036891309L;
	
	protected ZPTExpression expression1;
	protected ZPTExpression expression2;
	
	public TwoMembersComparisonExpression(){}
	public TwoMembersComparisonExpression( String stringExpression ){
		super( stringExpression );
	}
	public TwoMembersComparisonExpression( String stringExpression, ZPTExpression expression1, ZPTExpression expression2 ){
		super( stringExpression );
		
		this.expression1 = expression1;
		this.expression2 = expression2;
	}

	
	public ZPTExpression getExpression1() {
		return this.expression1;
	}

	public void setExpression1( ZPTExpression expression1 ) {
		this.expression1 = expression1;
	}

	public ZPTExpression getExpression2() {
		return this.expression2;
	}

	public void setExpression2( ZPTExpression expression2 ) {
		this.expression2 = expression2;
	}

	@Override
	public Object evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		return this.evaluateToBoolean( evaluationHelper );
	}

	protected Number getNumber1( EvaluationHelper evaluationHelper ) throws EvaluationException {
		return ExpressionUtils.evaluateToNumber( this.expression1, evaluationHelper );
	}
	
	protected Number getNumber2( EvaluationHelper evaluationHelper ) throws EvaluationException {
		return ExpressionUtils.evaluateToNumber( this.expression2, evaluationHelper );
	}
}
