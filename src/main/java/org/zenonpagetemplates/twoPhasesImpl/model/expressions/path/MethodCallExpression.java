package org.zenonpagetemplates.twoPhasesImpl.model.expressions.path;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.zenonpagetemplates.common.ExpressionTokenizer;
import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.ExpressionSyntaxException;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpression;

/**
 * <p>
 *   Evaluates a non lazy method call from an previously evaluated expression.
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
 *
 * @author <a href="mailto:chris@christophermrossi.com">Chris Rossi</a>
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.2 $
 */
public class MethodCallExpression extends AbstractMethodCallExpression {

	private static final long serialVersionUID = 6006642365133151640L;
	
	private List<ZPTExpression> arguments = new ArrayList<ZPTExpression>();
    
	
	public MethodCallExpression(){}

	public MethodCallExpression( String stringExpression, String methodName ){
		super( stringExpression, methodName );
	}


	public List<ZPTExpression> getArguments() {
		return this.arguments;
	}

	public void setArguments( List<ZPTExpression> arguments ) {
		this.arguments = arguments;
	}
	
	public void addArgument( ZPTExpression argument ){
		this.arguments.add( argument );
	}
	
	@Override
	public Object evaluate( Object parent, EvaluationHelper evaluationHelper ) 
			throws EvaluationException {
		
        Object object;
        @SuppressWarnings("rawtypes")
		Class clazz;

        try {
            // Get class and object for method call
            // Parent may be an object or a class
            if ( parent instanceof StaticCall ) {
                // Must be static method
                object = null;
                clazz = ( ( StaticCall ) parent ).clazz;
            }
            else {
                object = parent;
                clazz = parent.getClass();
            }

            return evaluateNonLazyMethodCall( this.methodName, this.arguments, evaluationHelper, object, clazz );
            
        } catch( Exception e ) {
            if ( e instanceof InvocationTargetException ){
                InvocationTargetException e2 = ( InvocationTargetException ) e;
                throw new EvaluationException( e2.getTargetException() );
            }
            throw new EvaluationException( e );
        }
	}
	
	@SuppressWarnings({ "rawtypes" })
	private static Object evaluateNonLazyMethodCall( String methodName,
			List<ZPTExpression> argumentExpressions, EvaluationHelper evaluationHelper, Object object,
			Class clazz ) 
					throws PageTemplateException, IllegalAccessException, InvocationTargetException {
		
		// Parse and evaluate arguments

		Object[] arguments = new Object[ argumentExpressions.size() ];
		for ( int i = 0; i < arguments.length; i++ ) {
			ZPTExpression expression = argumentExpressions.get( i );
		    arguments[ i ] = expression.evaluate( evaluationHelper );
		}
		
		Method method = generateMethod( methodName, clazz, arguments );
		if ( method != null ) {
		    return method.invoke( object, arguments );
		}
		
        throw new EvaluationException( 
        		generateErrorMessage( methodName, clazz, arguments ) );
	}
	
    static public final MethodCallExpression generate( String token ) throws ExpressionSyntaxException {
    	
        int leftParen = token.indexOf( '(' );
        if ( leftParen != -1 ) {
            if ( ! token.endsWith( ")" ) ) {
                throw new ExpressionSyntaxException( "Syntax error: bad method call: " + token );
            }
            String methodName = token.substring( 0, leftParen ).trim();
            String argumentString = token.substring( leftParen + 1, token.length() - 1 );
            
            MethodCallExpression result = new MethodCallExpression( token, methodName);
            
            // Parse arguments
            ExpressionTokenizer argumentTokens = new ExpressionTokenizer( argumentString, ',' );
            while ( argumentTokens.hasMoreTokens() ){
            	String argumentExpression = argumentTokens.nextToken().trim();
            	result.addArgument(
            			ExpressionUtils.generate( argumentExpression ) );
            }
            
            return result;
        }
        
        return null;
	}
}
