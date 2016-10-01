package org.zenonpagetemplates.twoPhasesImpl.model.expressions.scripting;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.ExpressionSyntaxException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.common.scripting.Evaluator;
import org.zenonpagetemplates.common.scripting.beanShell.BeanShellEvaluator;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;

/**
 * <p>
 *   Evaluates an expression using Bean shell as the Evaluator.
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
public class BeanShellExpression extends AbstractScriptExpression {

	private static final long serialVersionUID = -3138512000373794137L;
	
	
	public BeanShellExpression(){}
	public BeanShellExpression( String stringExpression, String scriptExpression ){
		super( stringExpression, scriptExpression );
	}
	
	static public BeanShellExpression generate( String expression ) 
			throws ExpressionSyntaxException {
		
		return new BeanShellExpression(
				expression, 
				expression.substring( TwoPhasesPageTemplate.EXPR_BSH.length() ) );
	}
	
	static private Evaluator getEvaluator(){
		return BeanShellEvaluator.getInstance();
	}
	
	@Override
	public Object evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		
		return evaluate( 
				this.scriptExpression, 
				evaluationHelper, 
				getEvaluator() );
	}
	
	static public Object evaluate( String scriptExpression, EvaluationHelper evaluationHelper ) 
			throws EvaluationException {
		
		return evaluate( 
				scriptExpression, 
				evaluationHelper, 
				getEvaluator() );
	}

}
