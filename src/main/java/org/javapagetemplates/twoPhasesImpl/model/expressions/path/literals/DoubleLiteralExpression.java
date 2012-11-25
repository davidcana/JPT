package org.javapagetemplates.twoPhasesImpl.model.expressions.path.literals;

import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.javapagetemplates.twoPhasesImpl.model.expressions.EvaluableToNumber;
import org.javapagetemplates.twoPhasesImpl.model.expressions.path.FirstPathToken;

import bsh.Interpreter;

/**
 * <p>
 *   Represents a double literal (as <code>5.2d</code>).
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
 *
 * @author <a href="mailto:chris@christophermrossi.com">Chris Rossi</a>
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.2 $
 */
public class DoubleLiteralExpression implements EvaluableToNumber, FirstPathToken {

	private static final long serialVersionUID = 932368239696476145L;
	
	private Double literal;
	
	
	public DoubleLiteralExpression(){}
	
	public DoubleLiteralExpression(Double literal){
		this.literal = literal;
	}

	
	@Override
	public Object evaluate(Interpreter beanShell) throws ExpressionEvaluationException {
		return this.literal;
	}
	
	@Override
	public Number evaluateToNumber(Interpreter beanShell)
			throws ExpressionEvaluationException {
		return this.literal;
	}
	
	static public Object evaluate(String expression, Interpreter beanShell) 
			throws PageTemplateException {
		return new Double( expression.substring( 0, expression.length() - 1 ) );
	}
	
	static public DoubleLiteralExpression generate(String expression){
		
		if ( expression.endsWith( TwoPhasesPageTemplate.DOUBLE_LITERAL_SUFFIX ) ) {
            try {
                return new DoubleLiteralExpression(
                		new Double( expression.substring( 0, expression.length() - 1 ) ));
                
            } catch( NumberFormatException e ) {}
        }
		
    	return null;
	}
	
	@Override
	public String getStringExpression() {
		return this.literal.toString();
	}
	
	@Override
	public String toString(){
		return '\'' + this.literal.toString() + '\'' ;
	}
}
