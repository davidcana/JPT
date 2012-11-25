package org.javapagetemplates.twoPhasesImpl.model.expressions.path;

import org.javapagetemplates.common.BeanShellScript;
import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * <p>
 *   Evaluates a bean shell script.
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
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.0 $
 */
public class BeanShellScriptExpression implements FirstPathToken, NextPathToken {

	private static final long serialVersionUID = -4903031873047204665L;
	
	private BeanShellScript beanShellScript;
	
	
	public BeanShellScriptExpression(){}

	public BeanShellScriptExpression(BeanShellScript beanShellScript){
		this.beanShellScript = beanShellScript;
	}


	public BeanShellScript getBeanShellScript() {
		return this.beanShellScript;
	}

	public void setBeanShellScript(BeanShellScript beanShellScript) {
		this.beanShellScript = beanShellScript;
	}

	@Override
	public Object evaluate(Interpreter beanShell) throws ExpressionEvaluationException {
		return evaluate(this.beanShellScript, beanShell);
	}
	
	static public Object evaluate(BeanShellScript beanShellScript, Interpreter beanShell) 
			throws ExpressionEvaluationException {
		
		try {
			return beanShell.eval( beanShellScript.getScript() );
			
		} catch (EvalError e) {
			throw new ExpressionEvaluationException(e);
		}
	}
	
	static public BeanShellScriptExpression generate(Object object){
		
        if ( object instanceof BeanShellScript ) {
            BeanShellScript beanShellScript = (BeanShellScript) object;
            return new BeanShellScriptExpression( beanShellScript );
        }
		
        return null;
	}

	@Override
	public String getStringExpression() {
		return this.beanShellScript.toString();
	}

	@Override
	public Object evaluate(Object parent, Interpreter beanShell)
			throws ExpressionEvaluationException {
		return this.evaluate(beanShell);
	}
	
	@Override
    public String toString(){
    	return this.beanShellScript.toString();
    }
}
