package org.zenonpagetemplates.twoPhasesImpl.model.expressions;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.ExpressionSyntaxException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;

/**
 * <p>
 *   Avoid rendering the results of an expression.
 * </p>
 * 
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
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.0 $
 */
public class NocallExpression extends ZPTExpressionImpl implements ZPTExpression, Nocallable {
	
	private static final long serialVersionUID = 8440989606532293191L;
	
	private ZPTExpression expression;
	
	public NocallExpression(){}
	public NocallExpression( String stringExpression, ZPTExpression expression ){
		super( stringExpression );
		this.expression = expression;
	}
	
	static public NocallExpression generate( String expression ) 
			throws ExpressionSyntaxException {

		return new NocallExpression(
				expression, 
				ExpressionUtils.generate(
						expression.substring( TwoPhasesPageTemplate.EXPR_NOCALL.length() ) ) );
	}
	
	public ZPTExpression getExpression() {
		return this.expression;
	}

	public void setExpression( ZPTExpression expression ) {
		this.expression = expression;
	}
	
	@Override
	public Object evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		return this;
	}
	
	static public Object evaluate( String string, EvaluationHelper evaluationHelper ) 
			throws ExpressionSyntaxException, EvaluationException {
		return generate( string ).evaluate( evaluationHelper );
	}
	
	@Override
	public Object evaluateNocallable( EvaluationHelper evaluationHelper ) throws EvaluationException {
		return this.expression.evaluate( evaluationHelper );
	}
}
