package org.zenonpagetemplates.twoPhasesImpl.model.expressions.path;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.zenonpagetemplates.common.ExpressionTokenizer;
import org.zenonpagetemplates.common.LazyEvaluation;
import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.StringExpression;

/**
 * <p>
 *   Evaluates a method call from an previously evaluated expression.
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
abstract public class AbstractMethodCallExpression implements NextPathToken {

	private static final long serialVersionUID = 5648576844018973203L;
	
	private String stringExpression;
	protected String methodName;
	
	public AbstractMethodCallExpression(){}

	public AbstractMethodCallExpression( String stringExpression, String methodName ){
		this.stringExpression = stringExpression;
		this.methodName = methodName;
	}

	@Override
	public String getStringExpression() {
		return this.stringExpression;
	}

	public void setStringExpression( String stringExpression ) {
		this.stringExpression = stringExpression;
	}

	public String getMethodName() {
		return this.methodName;
	}

	public void setMethodName( String methodName ) {
		this.methodName = methodName;
	}
	
    @SuppressWarnings({ "rawtypes" })
    static public final Object evaluate( Object parent, String methodName, String argumentString, EvaluationHelper evaluationHelper )
        throws EvaluationException {
    	
        Object object;
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

            // If is a lazy class run the other method
            if ( object instanceof LazyEvaluation ){
            	return evaluateLazyMethodCall(
            			methodName, argumentString,
            			evaluationHelper, object, clazz );
            }
            
            return evaluateNonLazyMethodCall(
            		methodName, argumentString, 
            		evaluationHelper, object, clazz );

        }
        catch ( Exception e ) {
            if ( e instanceof InvocationTargetException ){
                InvocationTargetException e2 = ( InvocationTargetException ) e;
                throw new EvaluationException( e2.getTargetException() );
            }
            throw new EvaluationException( e );
        }
    }

	@SuppressWarnings({ "rawtypes" })
	private static Object evaluateNonLazyMethodCall(String methodName,
			String argumentString, EvaluationHelper evaluationHelper, Object object,
			Class clazz) 
					throws PageTemplateException, IllegalAccessException, InvocationTargetException {
		
		// Parse and evaluate arguments
		ExpressionTokenizer argumentTokens = new ExpressionTokenizer(
				argumentString, 
				',' );
		Object[] arguments = new Object[ argumentTokens.countTokens() ];
		for ( int i = 0; i < arguments.length; i++ ) {
		    String argumentExpression = argumentTokens.nextToken().trim();
		    arguments[ i ] = StringExpression.evaluate( argumentExpression, evaluationHelper );
		}
		
		Method method = generateMethod( methodName, clazz, arguments );
		if ( method != null ) {
		    return method.invoke( object, arguments );
		}
		
        throw new EvaluationException( 
        		generateErrorMessage( methodName, clazz, arguments ) );
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static Method generateMethod(String methodName, Class clazz, Object[] arguments) {
		
		// Lookup method
		Method[] methods = clazz.getMethods();
		Method method = null;
		for ( int i = 0; i < methods.length; i++ ) {
		    if ( methods[ i ].getName().equals( methodName ) ) {

		        // See if arguments match
		        Class[] parms = methods[ i ].getParameterTypes();
		        if ( parms.length == arguments.length ) {
		            boolean match = true;
		            for ( int j = 0; j < parms.length; j++ ) {
		                // null is universally assignable (almost)
		                if ( arguments[ j ] == null ) {
		                    continue;
		                }
		                else if ( ! primitiveToClass( parms[ j ] ).isAssignableFrom( arguments[ j ].getClass() ) ) {
		                    match = false;
		                }
		                break;
		            }
		            if ( match ) {
		                method = methods[ i ];
		                break;
		            }
		        }
		    }
		}
		return method;
	}

	protected static String generateErrorMessage(String methodName,
			@SuppressWarnings("rawtypes") Class clazz, Object[] arguments) {
		
		StringBuffer errorMessage;
		errorMessage = new StringBuffer( 100 );
		errorMessage.append( "No such method: " );
		errorMessage.append( clazz.getName() );
		errorMessage.append( StringExpression.DOT );
		errorMessage.append( methodName );
		errorMessage.append( "(" );
		for ( int i = 0; i < arguments.length; i++ ) {
		    errorMessage.append( arguments[i] == null ? "<null>" : arguments[ i ].getClass().getName() );
		    if ( i < arguments.length - 1 ) {
		        errorMessage.append( ',' );
		    }
		}
		errorMessage.append( ')' );
		
		return errorMessage.toString();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object evaluateLazyMethodCall(String methodName,
			String argumentString, EvaluationHelper evaluationHelper, Object object,
			Class clazz) 
					throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		
		// Get the method instance
		Class parameterTypes[] = new Class[ 2 ];
		parameterTypes[ 0 ] = String.class;
		parameterTypes[ 1 ] = evaluationHelper.getClassType();
		//parameterTypes[ 1 ] = Interpreter.class;
		Method method = clazz.getMethod( methodName, parameterTypes );
		
		// Get the arguments
		Object[] arguments = new Object[ 2 ];
		arguments[ 0 ] = argumentString;
		arguments[ 1 ] = evaluationHelper;
		//arguments[ 1 ] = beanShell;
		
		// Invoke method
		return method.invoke( object, arguments );
	}
	/*
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object evaluateLazyMethodCall(String methodName,
			String argumentString, Interpreter beanShell, Object object,
			Class clazz) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		
		// Get the method instance
		Class parameterTypes[] = new Class[ 2 ];
		parameterTypes[ 0 ] = String.class;
		parameterTypes[ 1 ] = Interpreter.class;
		Method method = clazz.getMethod( methodName, parameterTypes );
		
		// Get the arguments
		Object[] arguments = new Object[ 2 ];
		arguments[ 0 ] = argumentString;
		arguments[ 1 ] = beanShell;
		
		// Invoke method
		return method.invoke( object, arguments );
	}*/
	
    /**
     * In order to match method descriptors with primitive type paremeters,
     * since our arguments will always be objects.  Oh curse Java for making
     * a distinction between the two.
     */
    @SuppressWarnings("rawtypes")
	private static final Class primitiveToClass( Class clazz ) {
    	
        if ( clazz.isPrimitive() ) {
            if ( Boolean.TYPE == clazz ) {
                return Boolean.class;
            }
            else if ( Character.TYPE == clazz ) {
                return Character.class;
            }
            else if ( Byte.TYPE == clazz ) {
                return Byte.class;
            }
            else if ( Short.TYPE == clazz ) {
                return Short.class;
            }
            else if ( Integer.TYPE == clazz ) {
                return Integer.class;
            }
            else if ( Long.TYPE == clazz ) {
                return Long.class;
            }
            else if ( Float.TYPE == clazz ) {
                return Float.class;
            }
            else if ( Double.TYPE == clazz ) {
                return Double.class;
            }
        }
        return clazz;
    }

    @Override
    public String toString(){
    	return this.stringExpression;
    }
    
}
