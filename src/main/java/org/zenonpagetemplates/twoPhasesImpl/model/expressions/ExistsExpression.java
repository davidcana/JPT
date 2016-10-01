package org.zenonpagetemplates.twoPhasesImpl.model.expressions;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.ExpressionSyntaxException;
import org.zenonpagetemplates.common.exceptions.NoSuchPathException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;

/**
 * <p>
 *   Defines an expression that evaluates to <code>true</code> if the given 
 *   expression evaluates to a a value different to null; otherwise, it 
 *   evaluates to <code>false</code>.
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
public class ExistsExpression extends JPTExpressionImpl implements EvaluableToBoolean {

	private static final long serialVersionUID = 6282707534539128919L;
	
	private JPTExpression expression;
	
	
	public ExistsExpression(){}
	public ExistsExpression( String stringExpression, JPTExpression expression ){
		super( stringExpression );
		this.expression = expression;
	}

	
	public JPTExpression getExpression() {
		return this.expression;
	}

	public void setExpression( JPTExpression expression ) {
		this.expression = expression;
	}

	static public ExistsExpression generate( String expression ) 
			throws ExpressionSyntaxException {
		
		return new ExistsExpression(
				expression, 
				ExpressionUtils.generate(
						expression.substring( TwoPhasesPageTemplate.EXPR_EXISTS.length() ) ) );
	}
	
	@Override
	public Boolean evaluateToBoolean( EvaluationHelper evaluationHelper )
			throws EvaluationException {
		
        try {
            return this.expression.evaluate( evaluationHelper ) != null;
            
        } catch( NoSuchPathException e ) {}

        return false;
	}
	
	@Override
	public Object evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		return this.evaluateToBoolean( evaluationHelper );
	}
	
	static public boolean evaluate( String string, EvaluationHelper evaluationHelper ) 
			throws ExpressionSyntaxException, EvaluationException {
		return generate( string ).evaluateToBoolean( evaluationHelper );
	}

}
