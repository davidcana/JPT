package org.javapagetemplates.twoPhasesImpl.model.expressions;

import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.common.exceptions.ExpressionSyntaxException;
import org.javapagetemplates.twoPhasesImpl.JPTContext;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;


import bsh.EvalError;
import bsh.Interpreter;

/**
 * <p>
 *   Evaluates a java expression.
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
public class JavaExpression extends JPTExpressionImpl {

	private static final long serialVersionUID = -3138512000373794137L;
	
	private String javaExpression;
	
	
	public JavaExpression(){}
	public JavaExpression(String stringExpression, String javaExpression){
		super(stringExpression);
		
		this.javaExpression = javaExpression;
	}

	
	public String getJavaExpression() {
		return this.javaExpression;
	}

	public void setJavaExpression(String javaExpression) {
		this.javaExpression = javaExpression;
	}
	
	static public JavaExpression generate(String expression) 
			throws ExpressionSyntaxException {
		
		return new JavaExpression(
				expression, 
				expression.substring( TwoPhasesPageTemplate.EXPR_JAVA.length() ));
	}
	
	@Override
	public Object evaluate(Interpreter beanShell) throws ExpressionEvaluationException {
		return evaluate(this.javaExpression, beanShell);
	}
	
	static public Object evaluate(String javaExpression, Interpreter beanShell) 
			throws ExpressionEvaluationException {
    	
		if (!JPTContext.getInstance().isJavaExpressionsOn()){
    		throw new ExpressionEvaluationException("Java expressions not allowed.");
    	}
    	
		try {
			String filteredExpression = javaExpression.replace('*', '&');
			return beanShell.eval( filteredExpression );
			
		} catch (EvalError e) {
			throw new ExpressionEvaluationException(e);
		}		
	}
}
