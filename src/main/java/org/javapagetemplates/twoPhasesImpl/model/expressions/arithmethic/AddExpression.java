package org.javapagetemplates.twoPhasesImpl.model.expressions.arithmethic;

import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.common.exceptions.ExpressionSyntaxException;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;

import bsh.Interpreter;

/**
 * <p>
 *   Defines an expression to add two or more expressions.
 * </p>
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
public class AddExpression extends ArithmethicExpression {

	private static final long serialVersionUID = 5727478855728948759L;
	private static final String NAME = "add";
	
	public AddExpression(){
		super();
	}


	@Override
	protected Number doOperation(Number value1, Number value2) {
		return value1.intValue() + value2.intValue();
	}

	static public AddExpression generate(String exp) 
			throws ExpressionSyntaxException {
		
		AddExpression result = new AddExpression();
		
		configure(
				exp,
				result,
				NAME,
				TwoPhasesPageTemplate.EXPR_ADD);
		
		return result;
	}
	
	static public Integer evaluate(String exp, Interpreter beanShell) 
			throws ExpressionSyntaxException, ExpressionEvaluationException {
		return generate(exp).evaluateToNumber(beanShell).intValue();
	}
}
