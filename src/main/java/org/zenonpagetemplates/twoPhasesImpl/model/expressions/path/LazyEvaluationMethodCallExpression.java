package org.zenonpagetemplates.twoPhasesImpl.model.expressions.path;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.StringExpression;

/**
 * <p>
 *   Evaluates a lazy method call from an previously evaluated expression.
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
public class LazyEvaluationMethodCallExpression extends AbstractMethodCallExpression {

	private static final long serialVersionUID = -4037908261658390836L;

    private String argumentString = StringExpression.VOID_STRING;
    
	
	public LazyEvaluationMethodCallExpression(){}

	public LazyEvaluationMethodCallExpression( String stringExpression, String methodName, String argumentString ){
		super( stringExpression, methodName );
		this.argumentString = argumentString;
	}


	public String getArgumentString() {
		return this.argumentString;
	}

	public void setArgumentString( String argumentString ) {
		this.argumentString = argumentString;
	}

	@Override
	public Object evaluate( Object parent, EvaluationHelper evaluationHelper ) throws EvaluationException {
		return evaluate( parent, this.methodName, this.argumentString, evaluationHelper );
	}
	
    static public final LazyEvaluationMethodCallExpression generate( String token, EvaluationHelper evaluationHelper ) 
    	throws PageTemplateException {
    	
        int leftParen = token.indexOf( '(' );
        if ( leftParen != -1 ) {
            if ( ! token.endsWith( ")" ) ) {
                throw new EvaluationException( "Syntax error: bad method call: " + token );
            }
            String methodName = token.substring( 0, leftParen ).trim();
            String arguments = token.substring( leftParen + 1, token.length() - 1 );
            
            return new LazyEvaluationMethodCallExpression( token, methodName, arguments );
        }
        
        return null;
	}
}
