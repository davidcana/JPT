package org.javapagetemplates.twoPhasesImpl.model.expressions.path;

import org.javapagetemplates.common.exceptions.EvaluationException;
import org.javapagetemplates.common.exceptions.ExpressionSyntaxException;
import org.javapagetemplates.common.scripting.EvaluationHelper;
import org.javapagetemplates.twoPhasesImpl.model.expressions.StringExpression;

/**
 * <p>
 *   Evaluates an indirection.
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
 * @version $Revision: 1.0 $
 */
public class Indirection implements NextPathToken {

	private static final long serialVersionUID = -8917736831375898659L;

	private String expression;
	
	public Indirection(){}
	public Indirection( String expression ){
		this.expression = expression;
	}
	
	
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Override
	public Object evaluate( Object parent, EvaluationHelper evaluationHelper ) throws EvaluationException {
		
		try {
			String token = StringExpression.evaluate( this.expression, evaluationHelper ).toString();
			return PathExpression.evaluateNextPathToken( token, parent, evaluationHelper );
			
		} catch ( ExpressionSyntaxException e ) {
			throw new EvaluationException( e );
		}
	}

	@Override
	public String getStringExpression() {
		return this.expression;
	}
	
    static public final Indirection generate( String token ) throws ExpressionSyntaxException {
    	
    	if ( token.charAt( 0 ) != '?' ) {
    		return null;
    	}
    	
    	return new Indirection( token.substring( 1 ) );
    }
}
