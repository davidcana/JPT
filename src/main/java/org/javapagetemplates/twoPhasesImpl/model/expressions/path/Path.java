package org.javapagetemplates.twoPhasesImpl.model.expressions.path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.javapagetemplates.common.BeanShellScript;
import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.common.exceptions.NoSuchPathException;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * <p>
 *   Implements an expression using 2 components: a required 
 *   FirstPathToken (an array, class, literal, static call or 
 *   variable name expression) and an optional list of 
 *   NextPathToken (an array, bean shell script, indirection,
 *   method call or property expression).
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
public class Path implements JPTExpression {
	
	private static final long serialVersionUID = -4568653205721733649L;
	
	private FirstPathToken firstToken;
	private List<NextPathToken> tokens = new ArrayList<NextPathToken>();
	
	public Path(){}
	public Path(FirstPathToken firstToken){
		this.firstToken = firstToken;
	}
	public Path(FirstPathToken firstToken, List<NextPathToken> tokens){
		this.firstToken = firstToken;
		this.tokens = tokens;
	}

	public FirstPathToken getFirstToken() {
		return this.firstToken;
	}

	public void setFirstToken(FirstPathToken firstToken) {
		this.firstToken = firstToken;
	}

	public List<NextPathToken> getTokens() {
		return this.tokens;
	}

	public void setTokens(List<NextPathToken> tokens) {
		this.tokens = tokens;
	}
	
	public void addToken(NextPathToken token){
		this.tokens.add(token);
	}
	
	@Override
	public Object evaluate(Interpreter beanShell) throws ExpressionEvaluationException {
		
		try {
			String expression = this.getStringExpression();
			Object result = this.firstToken.evaluate(beanShell);
			String token = this.firstToken.toString();
			Iterator<NextPathToken> i = this.tokens.iterator();

			while (i.hasNext()){
				
			    // Only last element can be null
			    if ( result == null ) {
			        throw new NoSuchPathException( new NullPointerException( token + " in '" + expression + "' is null" ) );
			    }
			    
				NextPathToken nextPathToken = i.next();
				
				result = nextPathToken.evaluate(result, beanShell);
				
				if (result instanceof BeanShellScript){
					BeanShellScript script = (BeanShellScript) result;
					result = script.evaluate(beanShell);
				}
				token = nextPathToken.getStringExpression();
			}
			
			return result;
			
		} catch (EvalError e) {
			throw new ExpressionEvaluationException(e);
		}
	}
	
	@Override
	public String getStringExpression(){
		
		StringBuilder sb = new StringBuilder(
				this.firstToken.getStringExpression());
		
		for (NextPathToken nextPathToken: this.tokens){
			sb.append('/');
			sb.append(nextPathToken.getStringExpression());
		}
		
		return sb.toString();
	}
	
	@Override
	public String toString(){
		return this.getStringExpression();
	}
}
