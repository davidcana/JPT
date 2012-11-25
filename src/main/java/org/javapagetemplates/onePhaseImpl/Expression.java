package org.javapagetemplates.onePhaseImpl;

import bsh.Interpreter;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.javapagetemplates.common.BeanShellScript;
import org.javapagetemplates.common.ExpressionTokenizer;
import org.javapagetemplates.common.LazyEvaluation;
import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.common.exceptions.ExpressionSyntaxException;
import org.javapagetemplates.common.exceptions.NoSuchPathException;
import org.javapagetemplates.common.exceptions.PageTemplateException;

/**
 * <p>
 *   Evaluates all types of valid expressions.
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
 * @version $Revision: 1.11 $
 */
enum MathOperation { add, sub, mul, div, mod }

public abstract class Expression {
	
    private static final String CLASS_EXTENSION = ".class";
	private static final String DOT = ".";
    
	private static final String ADD = "add";
	private static final String SUBTRACT = "subtract";
	private static final String MULTIPLY = "multiply";
	private static final String DIVIDE = "divide";
	private static final String MODULE = "module";
	
	
	public static final Object evaluate( String expression, Interpreter beanShell ) 
        throws PageTemplateException
    {
        try {
            Object result;
            
        	String effectiveExpression = ExpressionTokenizer.removeParenthesisIfAny(
    				expression ).trim();
            
            if ( effectiveExpression.startsWith( OnePhasePageTemplate.EXPR_STRING ) ) {
                result = evaluateString( effectiveExpression, beanShell );
            }
            else if ( effectiveExpression.startsWith( OnePhasePageTemplate.EXPR_EXISTS ) ) {
                result = evaluateExists( effectiveExpression, beanShell );
            }
            else if ( effectiveExpression.startsWith( OnePhasePageTemplate.EXPR_NOT ) ) {
                result = evaluateNot( effectiveExpression, beanShell );
            }
            else if ( effectiveExpression.startsWith( OnePhasePageTemplate.EXPR_JAVA ) ) {
                result = evaluateJava( effectiveExpression, beanShell );
            }
            else if ( effectiveExpression.startsWith( OnePhasePageTemplate.EXPR_EQUALS ) ) {
                result = evaluateEquals( effectiveExpression, beanShell );
            }
            else if ( effectiveExpression.startsWith( OnePhasePageTemplate.EXPR_GREATER ) ) {
                result = evaluateGreater( effectiveExpression, beanShell );
            }
            else if ( effectiveExpression.startsWith( OnePhasePageTemplate.EXPR_LOWER ) ) {
                result = evaluateLower( effectiveExpression, beanShell );
            }
            else if ( effectiveExpression.startsWith( OnePhasePageTemplate.EXPR_ADD ) ) {
                result = evaluateAdd( effectiveExpression, beanShell );
            }
            else if ( effectiveExpression.startsWith( OnePhasePageTemplate.EXPR_SUB ) ) {
                result = evaluateSub( effectiveExpression, beanShell );
            }
            else if ( effectiveExpression.startsWith( OnePhasePageTemplate.EXPR_MUL ) ) {
                result = evaluateMul( effectiveExpression, beanShell );
            }
            else if ( effectiveExpression.startsWith( OnePhasePageTemplate.EXPR_DIV ) ) {
                result = evaluateDiv( effectiveExpression, beanShell );
            }
            else if ( effectiveExpression.startsWith( OnePhasePageTemplate.EXPR_MOD ) ) {
                result = evaluateMod( effectiveExpression, beanShell );
            }
            else if ( effectiveExpression.startsWith( OnePhasePageTemplate.EXPR_OR ) ) {
                result = evaluateOr( effectiveExpression, beanShell );
            }
            else if ( effectiveExpression.startsWith( OnePhasePageTemplate.EXPR_AND ) ) {
                result = evaluateAnd( effectiveExpression, beanShell );
            }
            else if ( effectiveExpression.startsWith( OnePhasePageTemplate.EXPR_COND ) ) {
                result = evaluateCond( effectiveExpression, beanShell );
            }
            else {
                result = evaluatePath( effectiveExpression, beanShell );
            }
            
            return result;
        }
        catch( ExpressionEvaluationException e ) {
            e.setExpression( expression );
            throw e;
        }
    }
    
    @SuppressWarnings("rawtypes")
	public static final boolean evaluateBoolean( String expression, Interpreter beanShell ) 
        throws PageTemplateException
    {
        Object result = evaluate( expression, beanShell );
        if ( result == null ) {
            return false;
        }
        
        else if ( result instanceof Boolean ) {
            return ((Boolean)result).booleanValue();
        }
        
        else if ( result instanceof String ) {
            return ((String)result).length() != 0;
        }
        
        else if ( result instanceof Long ) {
            return ((Long)result).longValue() != 0l;
        }

        else if ( result instanceof Integer ) {
            return ((Integer)result).intValue() != 0;
        }

        else if ( result instanceof Double ) {
            return ((Double)result).doubleValue() != 0.0d;
        }

        else if ( result instanceof Long ) {
            return ((Float)result).floatValue() != 0.0;
        }
        
        else if ( result instanceof Collection ) {
            return ((Collection)result).size() != 0;
        }
        
        else if ( result instanceof Map ) {
            return ((Map)result).size() != 0;
        }
        
        return true;
    }

    private static final int STATE_SCANNING = 0;
    private static final int STATE_AT_DOLLAR = 1;
    private static final int STATE_IN_EXPRESSION = 2;
    private static final int STATE_IN_BRACKETED_EXPRESSION = 3;
    private static final String evaluateString( String exp, Interpreter beanShell )
        throws PageTemplateException
    {
        String expression = exp;
        
        // empty expression evaluates to empty string
        if ( expression.length() == OnePhasePageTemplate.EXPR_STRING.length() ) {
            return PageTemplateImpl.VOID_STRING;
        }

        StringBuffer result = new StringBuffer( expression.length() * 2 );
        expression = expression.substring( OnePhasePageTemplate.EXPR_STRING.length() );
        
        // Let's use a finite state machine
        StringBuffer subexpression = new StringBuffer(20);
        int state = STATE_SCANNING;
        int length = expression.length();
        for ( int i = 0; i < length; i++ ) {
            char ch = expression.charAt(i);

            switch( state ) {
                // In the string part of the expression
                case STATE_SCANNING:
                    // Found a dollar sign
                    if ( ch == '$' ) {
                        state = STATE_AT_DOLLAR;
                    }
                    // Just keep appending to result buffer
                    else {
                        result.append( ch );
                    }
                    break;

                // Next character after dollar sign
                case STATE_AT_DOLLAR:
                    // An escaped dollar sign
                    if ( ch == '$' ) {
                        result.append( '$' );
                        state = STATE_SCANNING;
                    }

                    // Beginning of a bracketed expression
                    else if ( ch == '{' ) {
                        subexpression.setLength(0);
                        state = STATE_IN_BRACKETED_EXPRESSION;
                    }

                    // Beginning of a non bracketed expression
                    else {
                        subexpression.setLength(0);
                        subexpression.append( ch );
                        state = STATE_IN_EXPRESSION;
                    }
                    break;

                // In subexpression
                case STATE_IN_BRACKETED_EXPRESSION:
                case STATE_IN_EXPRESSION:
                    // Check for end
                    if ( ( state == STATE_IN_BRACKETED_EXPRESSION && ch == '}' ) ||
                         ( state == STATE_IN_EXPRESSION && Character.isWhitespace( ch ) ) ) {
                        result.append( String.valueOf( evaluate( subexpression.toString(), beanShell ) ) );
                        if ( state == STATE_IN_EXPRESSION ) {
                            result.append( ch );
                        }
                        state = STATE_SCANNING;
                    }
                    
                    // Keep appending to subexpression
                    else {
                        subexpression.append( ch );
                    }
            }
        }

        // Ended in unclosed bracket
        if ( state == STATE_IN_BRACKETED_EXPRESSION ) {
            throw new ExpressionSyntaxException( "unclosed left curly brace: " + expression );
        }

        // Ended at expression
        else if ( state == STATE_IN_EXPRESSION ) {
            result.append( evaluate( subexpression.toString(), beanShell ) );
        }

        return result.toString();
    }

    private static final Boolean evaluateNot( String expression, Interpreter beanShell ) 
        throws PageTemplateException
    {
		return new Boolean( ! evaluateBoolean( 
				expression.substring( OnePhasePageTemplate.EXPR_NOT.length() ).trim() ,
				beanShell ) );
    }

    private static final Boolean evaluateExists( String expression, Interpreter beanShell )
        throws PageTemplateException
    {
        boolean exists = false;
        try {
            exists = evaluate( expression.substring( OnePhasePageTemplate.EXPR_EXISTS.length() ), beanShell ) != null;
        } catch( NoSuchPathException e ) {}

        return new Boolean( exists );
    }
    
    private static final Boolean evaluateEquals( String exp, Interpreter beanShell )
            throws PageTemplateException
    {   
        String expression = exp.substring( OnePhasePageTemplate.EXPR_EQUALS.length() ).trim();
        
        if ( expression.length() == 0 ) {
            throw new PageTemplateException("Equals expression void.");
        }

        ExpressionTokenizer segments = new ExpressionTokenizer( expression, OnePhasePageTemplate.EXPRESSION_DELIMITER );
        if ( segments.countTokens() == 1 ) {
        	throw new PageTemplateException("Only one element in equals expression, please add at least one more.");
        }
        
        String segment1 = segments.nextToken().trim();
        Object result1 = evaluate( segment1, beanShell );
        
        while ( segments.hasMoreTokens() ) {
            String segment = segments.nextToken().trim();
            Object result = evaluate( segment, beanShell );
			if (!areEquivalent(result1, result)){
            	return false;
            }
        }

        return true;
    }
    
    private static boolean areEquivalent(Object object1, Object object2){
    	
    	if (object1 instanceof Number && object2 instanceof Number){
    		Number number1 = (Number) object1;
    		Number number2 = (Number) object2;
    		
    		return number1.longValue() == number2.longValue();
    	}
    	
    	return object1.equals(object2);
    }
    
    
    private static final Boolean evaluateGreater( String exp, Interpreter beanShell )
            throws PageTemplateException
    {   
        String expression = exp.substring( OnePhasePageTemplate.EXPR_GREATER.length() ).trim();
        
        if ( expression.length() == 0 ) {
            throw new PageTemplateException("Greater expression void.");
        }

        ExpressionTokenizer segments = new ExpressionTokenizer( expression, OnePhasePageTemplate.EXPRESSION_DELIMITER );
        if ( segments.countTokens() != 2 ) {
        	throw new PageTemplateException("Wrong number of elements, greater expressions only support two.");
        }
        
        String segment1 = segments.nextToken().trim();
        Number number1 = (Number) evaluate( segment1, beanShell );
        
        String segment2 = segments.nextToken().trim();
        Number number2 = (Number) evaluate( segment2, beanShell );

        return number1.longValue()  > number2.longValue();
    }
    
    private static final Boolean evaluateLower( String exp, Interpreter beanShell )
            throws PageTemplateException
    {   
        String expression = exp.substring( OnePhasePageTemplate.EXPR_LOWER.length() ).trim();
        
        if ( expression.length() == 0 ) {
            throw new PageTemplateException("Lower expression void.");
        }

        ExpressionTokenizer segments = new ExpressionTokenizer( expression, OnePhasePageTemplate.EXPRESSION_DELIMITER );
        if ( segments.countTokens() != 2 ) {
        	throw new PageTemplateException("Wrong number of elements, lower expressions only support two.");
        }
        
        String segment1 = segments.nextToken().trim();
        Number number1 = (Number) evaluate( segment1, beanShell );
        
        String segment2 = segments.nextToken().trim();
        Number number2 = (Number) evaluate( segment2, beanShell );

        return number1.longValue() < number2.longValue();
    }
    
    private static final Integer evaluateAdd( String exp, Interpreter beanShell )
            throws PageTemplateException
    {   
    	return evaluateArithmetic(exp, beanShell, MathOperation.add);
    }
    
    private static final Integer evaluateSub( String exp, Interpreter beanShell )
            throws PageTemplateException
    {   
    	return evaluateArithmetic(exp, beanShell, MathOperation.sub);
    }
    
    private static final Integer evaluateMul( String exp, Interpreter beanShell )
            throws PageTemplateException
    {   
    	return evaluateArithmetic(exp, beanShell, MathOperation.mul);
    }
    
    private static final Integer evaluateDiv( String exp, Interpreter beanShell )
            throws PageTemplateException
    {   
    	return evaluateArithmetic(exp, beanShell, MathOperation.div);
    }
    
    private static final Integer evaluateMod( String exp, Interpreter beanShell )
            throws PageTemplateException
    {   
    	return evaluateArithmetic(exp, beanShell, MathOperation.mod);
    }
    
    private static final boolean evaluateOr( String exp, Interpreter beanShell )
            throws PageTemplateException
    {   
    	return BoolExpressions.or(
    			exp.substring( OnePhasePageTemplate.EXPR_OR.length() ).trim(), 
    			beanShell);
    }
    
    private static final boolean evaluateAnd( String exp, Interpreter beanShell )
            throws PageTemplateException
    {   
    	return BoolExpressions.and(
    			exp.substring( OnePhasePageTemplate.EXPR_AND.length() ).trim(), 
    			beanShell);
    }
    
    private static final Object evaluateCond( String exp, Interpreter beanShell )
            throws PageTemplateException
    {   
    	return BoolExpressions.cond(
    			exp.substring( OnePhasePageTemplate.EXPR_COND.length() ).trim(), 
    			beanShell);
    }
    

	private static final Integer evaluateArithmetic( String exp, Interpreter beanShell, MathOperation mathOperation )
            throws PageTemplateException
    {   
    	// Init vars
    	String initialChars = null;
    	String operationName = null;
    	
    	switch (mathOperation) {
		case add:
			initialChars = OnePhasePageTemplate.EXPR_ADD;
			operationName = ADD;
			break;
		case sub:
			initialChars = OnePhasePageTemplate.EXPR_SUB;
			operationName = SUBTRACT;
			break;
		case mul:
			initialChars = OnePhasePageTemplate.EXPR_MUL;
			operationName = MULTIPLY;
			break;
		case div:
			initialChars = OnePhasePageTemplate.EXPR_DIV;
			operationName = DIVIDE;
			break;
		case mod:
			initialChars = OnePhasePageTemplate.EXPR_MOD;
			operationName = MODULE;
			break;
		default:
			throw new PageTemplateException("The evaluateArithmetic method can't handle '" 
					+ mathOperation + "' math operation");
		}
    	
    	// Check the expression and the number of segements
    	String expression = exp.substring( initialChars.length() ).trim();
        
        if ( expression.length() == 0 ) {
            throw new PageTemplateException(operationName + " expression void.");
        }

        ExpressionTokenizer segments = new ExpressionTokenizer( expression, OnePhasePageTemplate.EXPRESSION_DELIMITER );
        if ( segments.countTokens() == 1 ) {
        	throw new PageTemplateException("Only one element in " + operationName + " expression, please add at least one more.");
        }

        // Evaluate segments
		Integer result = 0;
		int c = 0;
		
		while ( segments.hasMoreTokens() ) {
		    String segment = segments.nextToken().trim();
		    Object evaluate = evaluate( segment, beanShell );
		    
			if (!(evaluate instanceof Number)){
				throw new PageTemplateException("Error trying to " + operationName + " integers, value '" + evaluate.toString() 
						+ "' is not a valid integer value in expression '" + exp + "'");
			}
			
		    Integer value = ((Number) evaluate).intValue();
			
			if (c++ == 0){
				result = value;
				continue;
			}
			
	    	switch (mathOperation) {
			case add:
				result += value;
				break;
			case sub:
				result -=  value;
				break;
			case mul:
				result *=  value;
				break;
			case div:
				result /=  value;
				break;
			case mod:
				result %=  value;
				break;
			default:
				throw new PageTemplateException("The evaluateArithmetic method can't handle '" 
						+ mathOperation + "' math operation");
			}
		}

        return result;
    }
    
    private static final Object evaluateJava( String expression, Interpreter beanShell )
        throws PageTemplateException
    {
        try {
        	
        	if (!JPTContext.getInstance().isJavaExpressionsOn()){
        		throw new PageTemplateException("Java expressions not allowed.");
        	}
        	
            String filteredExpression = expression.replace('*', '&');
            return beanShell.eval( filteredExpression.substring( OnePhasePageTemplate.EXPR_JAVA.length() ) );
        } catch( bsh.EvalError e ) {
            throw new ExpressionEvaluationException(e);
        }
    }
    
    private static final Boolean booleanLiteral( String expression ) {
        if ( OnePhasePageTemplate.TRUE_STRING.equals( expression ) ) {
            return Boolean.TRUE;
        }
        else if ( OnePhasePageTemplate.FALSE_STRING.equals( expression ) ) {
            return Boolean.FALSE;
        }
        return null;
    }

    private static final String stringLiteral( String expression ) {
        if ( expression.startsWith( "'" ) && 
             expression.endsWith( "'" ) ) {
            return expression.substring( 1, expression.length() - 1 );
        }
        return null;
    }

    private static final Number numericLiteral( String expression ) {
        if ( expression.endsWith( OnePhasePageTemplate.LONG_LITERAL_SUFFIX ) ) {
            try {
                return new Long( expression.substring( 0, expression.length() - 1 ) );
            } catch( NumberFormatException e ) {}
        }
        else if ( expression.endsWith( OnePhasePageTemplate.DOUBLE_LITERAL_SUFFIX ) ) {
            try {
                return new Double( expression.substring( 0, expression.length() - 1 ) );
            } catch( NumberFormatException e ) {}
        }
        else if ( expression.endsWith( OnePhasePageTemplate.FLOAT_LITERAL_SUFFIX ) ) {
            try {
                return new Float( expression.substring( 0, expression.length() - 1 ) );
            } catch( NumberFormatException e ) {}
        }
        else if ( expression.indexOf( DOT ) != -1 ) {
            try {
                return new Float( expression );
            } catch( NumberFormatException e ) {}
        }
        else {
            try {
                return new Integer( expression );
            } catch( NumberFormatException e ) {}
        }
        return null;
    }

    private static final Object evaluatePath( String exp, Interpreter beanShell ) 
        throws PageTemplateException
    {
        String expression = new String( exp );
        
        // Blank expression evaluates to blank string
        if ( expression.trim().length() == 0 ) {
            return PageTemplateImpl.VOID_STRING;
        }

        ExpressionTokenizer segments = new ExpressionTokenizer( expression, OnePhasePageTemplate.PATH_DELIMITER );
        if ( segments.countTokens() == 1 ) {
            return evaluatePathSegment( expression, beanShell );
        }
        NoSuchPathException exception = null;
        Object result = null;
        while ( segments.hasMoreTokens() ) {
            try {
                String segment = segments.nextToken().trim();
                exception = null;
                result = evaluate( segment, beanShell );
                if ( result != null ) {
                    return result;
                }
            } catch( NoSuchPathException e ) {
                exception = e;
            }
        }
        if ( exception != null ) {
            throw exception;
        }
        return null;
    }

    private static final Object evaluatePathSegment( String expression, Interpreter beanShell ) 
        throws PageTemplateException
    {
        // Blank expression evaluates to blank string
        if ( expression.length() == 0 ) {
            return "";
        }
        
        // Evaluate first token
        ExpressionTokenizer path = new ExpressionTokenizer( expression, '/' );
        String token = path.nextToken().trim();
        Object result = evaluateFirstPathToken( token, beanShell );
        
        // Traverse the portalObjectPath
        while ( path.hasMoreTokens() ) {
            // Only last element can be null
            if ( result == null ) {
                throw new NoSuchPathException( new NullPointerException( token + " in '" + expression + "' is null" ) );
            }
            
            token = path.nextToken().trim();
            result = evaluateNextPathToken( result, token, beanShell );
        }

        return result;
    }

    private static final Object evaluateFirstPathToken( String tok, Interpreter beanShell )
        throws PageTemplateException
    {
        String token = new String( tok );
        
        // Seperate identifier from any array accessors
        String arrayAccessor = null;
        int bracket = findArrayAccessor( token );
        if ( bracket != -1 ) {
            arrayAccessor = token.substring( bracket ).trim();
            token = token.substring( 0, bracket ).trim();
        }

        // First token must come from dictionary or be a literal

        // First see if it's a string literal
        Object result = stringLiteral( token );
        
        // If it's not, try to see if it's a number
        if ( result == null ) {
            result = numericLiteral( token );
            
            // Maybe it's a boolean literal
            if ( result == null ) {
                result = booleanLiteral( token );
                                    
                // It could be a class, for a static method call
                if ( result == null ) {
                    try {
                        result = new StaticCall( Class.forName( token ) );
                    } catch( ClassNotFoundException e ) {
                        // Or it could be an actual reference to a class object
                        try {
                            if ( token.endsWith( CLASS_EXTENSION ) ) {
                                token = token.substring( 0, token.length() - CLASS_EXTENSION.length() );
                                result = Class.forName( token );
                            }
                        } catch( ClassNotFoundException ee ) {}
                        // Must be an object in dictionary
                        if ( result == null ) {
                            try {
                                result = beanShell.get( token );
                                //System.err.println( "beanShell.get( " + token + "): " + result );
                            } catch( bsh.EvalError eee ) {
                                throw new ExpressionEvaluationException( eee );
                            }
                        }
                    }
                }
            }
        }
        if ( arrayAccessor != null ) {
            result = evaluateArrayAccess( token, result, arrayAccessor, beanShell );
        }

        if ( result instanceof BeanShellScript ) {
            try {
                BeanShellScript script = (BeanShellScript) result;
                result = beanShell.eval( (script.getScript() ));
            } catch( bsh.EvalError e ) {
                throw new PageTemplateException(e);
            }
        }
        return result;
    }

    private static final Object evaluateNextPathToken( Object parent, String tok, Interpreter beanShell )
        throws PageTemplateException
    {
        String token = tok;
        
        // Separate identifier from any array accessors
        String arrayAccessor = null;
        int bracket = findArrayAccessor( token );
        if ( bracket != -1 ) {
            arrayAccessor = token.substring( bracket ).trim();
            token = token.substring( 0, bracket ).trim();
        }

        // Element is a method or property of parent
        Object result = null;
        
        // Test for indirection
        if ( token.charAt(0) == '?' ) {
        //if ( token.startsWith( "?" ) ) {
            String indirectToken = String.valueOf( evaluateFirstPathToken( token.substring(1), beanShell ) );
            result = evaluateNextPathToken( parent, indirectToken, beanShell );
        }

        else {
            // A method call?
            int leftParen = token.indexOf( '(' );
            if ( leftParen != -1 ) {
                if ( ! token.endsWith( ")" ) ) {
                    throw new ExpressionEvaluationException( "syntax error: bad method call: " + token );
                }
                String methodName = token.substring( 0, leftParen ).trim();
                String arguments = token.substring( leftParen + 1, token.length() - 1 );
                result = evaluateMethodCall( parent, methodName, arguments, beanShell );
            }    
            else {
                // A property
                result = getProperty( parent, token );
            }
        }
        
        if ( arrayAccessor != null ) {
            result = evaluateArrayAccess( token, result, arrayAccessor, beanShell );
        }
        
        if ( result instanceof BeanShellScript ) {
            try {
                BeanShellScript script = (BeanShellScript)result;
                result = beanShell.eval( (script.getScript() ));
            } catch( bsh.EvalError e ) {
                throw new PageTemplateException(e);
            }
        }
        
        return result;
    }

    private static final int SCANNING = 0;
    private static final int IN_PAREN = 1;
    private static final int IN_QUOTE = 2;
    private static final int findArrayAccessor( String token ) {
        int length = token.length();
        int state = SCANNING;
        int parenDepth = 0;
        for ( int i = 0; i < length; i++ ) {
            char ch = token.charAt( i );
            switch( state ) {
                case IN_PAREN:
                    if ( ch == ')' ) {
                        parenDepth--;
                        if ( parenDepth == 0 ) {
                            state = SCANNING;
                        }
                    }
                    else if ( ch == '(' ) {
                        parenDepth++;
                    }
                    break;
                    
                case IN_QUOTE:
                    if ( ch == '\'' ) {
                        state = SCANNING;
                    }
                    break;
                    
                case SCANNING:
                    if ( ch == '\'' ) {
                        state = IN_QUOTE;
                    }
                    else if ( ch == '(' ) {
                        parenDepth++;
                        state = IN_PAREN;
                    }
                    else if ( ch == '[' ) {
                        return i;
                    }
            }
        }
        return -1;
    }
    
    private static final Object evaluateArrayAccess( String tok, 
                                                     Object res, 
                                                     String acc, 
                                                     Interpreter beanShell )
        throws PageTemplateException
    {
        Object result = res;
        String token = tok;
        String accessor = acc;
        try {
            // Array accessor must begin and end with brackets
            int close = accessor.indexOf( ']' );
            if ( accessor.charAt(0) != '[' || close == -1 ) {
                throw new ExpressionEvaluationException( "bad array accessor for " + token + ": "  + accessor );
            }
            
            // Array accessor must operate on an array
            if ( !  result.getClass().isArray() ) {
                throw new ExpressionEvaluationException( token + " is not an array: " + result.getClass() );
            }
            
            if ( result.getClass().getComponentType().isPrimitive() ) {
                result = convertPrimitiveArray( result );
            }
            Object[] array = (Object[])result;
            Object index = evaluate( accessor.substring( 1, close ), beanShell );
            if ( ! ( index instanceof Integer ) ) {
                throw new ExpressionEvaluationException( "array index must be an integer" );
            }
            result = array[ ((Integer)index).intValue() ];

            // continue evaluating array access for multidimensional arrays
            close++;
            if ( accessor.length() > close ) {
                token += accessor.substring( 0, close );
                accessor = accessor.substring( close );
                result = evaluateArrayAccess( token, result, accessor, beanShell );
            }
            return result;
        } catch( ArrayIndexOutOfBoundsException e ) {
            throw new ExpressionEvaluationException(e);
        }
    }

    /**
     * Oh curse Java!
     */
    static final Object[] convertPrimitiveArray( Object o ) {
        Object[] newArray = null;
        if ( o instanceof int[] ) {
            int[] oldArray = (int[])o;
            newArray = new Integer[ oldArray.length ];
            for ( int i = 0; i < oldArray.length; i++ ) {
                newArray[i] = new Integer( oldArray[i] );
            }
        }
        else if ( o instanceof long[] ) {
            long[] oldArray = (long[])o;
            newArray = new Long[ oldArray.length ];
            for ( int i = 0; i < oldArray.length; i++ ) {
                newArray[i] = new Long( oldArray[i] );
            }
        }
        else if ( o instanceof boolean[] ) {
            boolean[] oldArray = (boolean[])o;
            newArray = new Boolean[ oldArray.length ];
            for ( int i = 0; i < oldArray.length; i++ ) {
                newArray[i] = new Boolean( oldArray[i] );
            }
        }
        else if ( o instanceof char[] ) {
            char[] oldArray = (char[])o;
            newArray = new Character[ oldArray.length ];
            for ( int i = 0; i < oldArray.length; i++ ) {
                newArray[i] = new Character( oldArray[i] );
            }
        }
        else if ( o instanceof byte[] ) {
            byte[] oldArray = (byte[])o;
            newArray = new Byte[ oldArray.length ];
            for ( int i = 0; i < oldArray.length; i++ ) {
                newArray[i] = new Byte( oldArray[i] );
            }
        }
        else if ( o instanceof float[] ) {
            float[] oldArray = (float[])o;
            newArray = new Float[ oldArray.length ];
            for ( int i = 0; i < oldArray.length; i++ ) {
                newArray[i] = new Float( oldArray[i] );
            }
        }
        else if ( o instanceof double[] ) {
            double[] oldArray = (double[])o;
            newArray = new Double[ oldArray.length ];
            for ( int i = 0; i < oldArray.length; i++ ) {
                newArray[i] = new Double( oldArray[i] );
            }
        }
        else if ( o instanceof short[] ) {
            short[] oldArray = (short[])o;
            newArray = new Short[ oldArray.length ];
            for ( int i = 0; i < oldArray.length; i++ ) {
                newArray[i] = new Short( oldArray[i] );
            }
        }
        return newArray;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final Object evaluateMethodCall( Object parent, 
                                                    String methodName, 
                                                    String argumentString, 
                                                    Interpreter beanShell )
        throws PageTemplateException
    {
        StringBuffer errorMessage = null;
        Object object;
        Class clazz;

        try {
            // Get class and object for method call
            // Parent may be an object or a class
            if ( parent instanceof StaticCall ) {
                // Must be static method
                object = null;
                clazz = ((StaticCall)parent).clazz;
            }
            else {
                object = parent;
                clazz = parent.getClass();
            }

            // If is a lazy class run the other method
            if (object instanceof LazyEvaluation){
            	return evaluateLazyMethodCall(methodName, argumentString,
						beanShell, object, clazz);
            }
            
            // Parse and evaluate arguments
            ExpressionTokenizer argumentTokens = new ExpressionTokenizer(
            		argumentString, 
            		',' );
            Object[] arguments = new Object[ argumentTokens.countTokens() ];
            for ( int i = 0; i < arguments.length; i++ ) {
                String argumentExpression = argumentTokens.nextToken().trim();
                arguments[i] = evaluate( argumentExpression, beanShell );
            }
            
            // Lookup method
            Method[] methods = clazz.getMethods();
            Method method = null;
            for ( int i = 0; i < methods.length; i++ ) {
                if ( methods[i].getName().equals( methodName ) ) {

                    // See if arguments match
                    Class[] parms = methods[i].getParameterTypes();
                    if ( parms.length == arguments.length ) {
                        boolean match = true;
                        for ( int j = 0; j < parms.length; j++ ) {
                            // null is universally assignable (almost)
                            if ( arguments[j] == null ) {
                                continue;
                            }
                            else if ( ! primitiveToClass( parms[j] ).isAssignableFrom( arguments[j].getClass() ) ) {
                                match = false;
                            }
                            break;
                        }
                        if ( match ) {
                            method = methods[i];
                            break;
                        }
                    }
                }
            }
            if ( method != null ) {
                return method.invoke( object, arguments );
            }

            errorMessage = new StringBuffer( 100 );
            errorMessage.append( "no such method: " );
            errorMessage.append( clazz.getName() );
            errorMessage.append( DOT );
            errorMessage.append( methodName );
            errorMessage.append( "(" );
            for ( int i = 0; i < arguments.length; i++ ) {
                errorMessage.append( arguments[i] == null ? "<null>" : arguments[i].getClass().getName() );
                if ( i < arguments.length - 1 ) {
                    errorMessage.append( ',' );
                }
            }
            errorMessage.append( ')' );
        }
        //catch( RuntimeException e ) {
        //    throw e;
        //}
        catch( Exception e ) {
            if (e instanceof InvocationTargetException){
                InvocationTargetException e2 = (InvocationTargetException) e;
                throw new ExpressionEvaluationException(e2.getTargetException());
            }
            throw new ExpressionEvaluationException(e);
        }
        throw new ExpressionEvaluationException( errorMessage.toString() );
    }

    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object evaluateLazyMethodCall(String methodName,
			String argumentString, Interpreter beanShell, Object object,
			Class clazz) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		
		// Get the method instance
		Class parameterTypes[] = new Class[2];
		parameterTypes[0] = String.class;
		parameterTypes[1] = Interpreter.class;
		Method method = clazz.getMethod(methodName, parameterTypes);
		
		// Get the arguments
		Object[] arguments = new Object[2];
		arguments[0] = argumentString;
		arguments[1] = beanShell;
		
		// Invoke method
		return method.invoke( object, arguments );
	}

	
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
                            
    private static final Object[] emptyArray = new Object[0];
    @SuppressWarnings("rawtypes")
	private static final Object getProperty( Object object, String name ) 
        throws PageTemplateException
    {
        try {
            // If object is a Map, use it like a dictionary and 
            // use property name as key
            if ( object instanceof Map ) {
                return ((Map)object).get( name );
            }

            // Use Bean introspection to get property of an object
            BeanInfo beanInfo = Introspector.getBeanInfo( object.getClass() );
            PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
            for ( int i = 0; i < properties.length; i++ ) {
                if ( properties[i].getName().equals( name ) ) {
                    Method reader = properties[i].getReadMethod();
                    if ( reader == null ) {
                        throw new ExpressionEvaluationException
                            ( "property '" + name + "' of " + object.getClass().getName() + " can't be read" );
                    }
                    return reader.invoke( object, emptyArray );
                }
            }
        }
        //catch( RuntimeException e ) {
        //    throw e;
        //}
        catch( Exception e ) {
            throw new ExpressionEvaluationException(
            		e.getCause() != null? e.getCause(): e);
        }
        throw new ExpressionEvaluationException( "no such property '" + name + "' of " + object.getClass().getName() );
    }
}

class StaticCall {
    @SuppressWarnings("rawtypes")
	Class clazz;
    
    @SuppressWarnings("rawtypes")
	StaticCall( Class clazz ) {
        this.clazz = clazz;
    }
}
