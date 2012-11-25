package org.javapagetemplates.twoPhasesImpl.model.expressions.path.literals;

import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.javapagetemplates.twoPhasesImpl.model.expressions.EvaluableToNumber;
import org.javapagetemplates.twoPhasesImpl.model.expressions.path.FirstPathToken;

import bsh.Interpreter;

/**
 * <p>
 *   Represents a float literal (as <code>5.2</code> or <code>5f</code>).
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
public class FloatLiteralExpression implements EvaluableToNumber, FirstPathToken {

	private static final long serialVersionUID = -8153155161638393490L;
	private static final String DOT = ".";
	
	private Float literal;
	
	
	public FloatLiteralExpression(){}
	
	public FloatLiteralExpression(Float literal){
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
	
	static public FloatLiteralExpression generate(String expression){
		
		if ( expression.endsWith( TwoPhasesPageTemplate.FLOAT_LITERAL_SUFFIX ) ) {
            try {
                return new FloatLiteralExpression(
                		new Float( expression.substring( 0, expression.length() - 1 ) ));
            } catch( NumberFormatException e ) {}
		}
		
		if ( expression.indexOf( DOT ) != -1 ) {
	        try {
	            return new FloatLiteralExpression(
	            		new Float( expression ));
	        } catch( NumberFormatException e ) {}
		}
		
		return null;
	}
	
	static public Object evaluate(String expression, Interpreter beanShell) 
			throws PageTemplateException {
		return new Float( expression.substring( 0, expression.length() - 1 ) );
	}
	
	static public Object evaluate2(String expression, Interpreter beanShell) 
			throws PageTemplateException {
		return new Float( expression );
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
