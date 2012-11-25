package org.javapagetemplates.twoPhasesImpl.model.expressions;

import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.common.exceptions.ExpressionSyntaxException;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.javapagetemplates.twoPhasesImpl.model.expressions.arithmethic.AddExpression;
import org.javapagetemplates.twoPhasesImpl.model.expressions.arithmethic.DivExpression;
import org.javapagetemplates.twoPhasesImpl.model.expressions.arithmethic.ModExpression;
import org.javapagetemplates.twoPhasesImpl.model.expressions.arithmethic.MulExpression;
import org.javapagetemplates.twoPhasesImpl.model.expressions.arithmethic.SubExpression;
import org.javapagetemplates.twoPhasesImpl.model.expressions.bool.AndExpression;
import org.javapagetemplates.twoPhasesImpl.model.expressions.bool.CondExpression;
import org.javapagetemplates.twoPhasesImpl.model.expressions.bool.NotExpression;
import org.javapagetemplates.twoPhasesImpl.model.expressions.bool.OrExpression;
import org.javapagetemplates.twoPhasesImpl.model.expressions.comparison.EqualsExpression;
import org.javapagetemplates.twoPhasesImpl.model.expressions.comparison.GreaterExpression;
import org.javapagetemplates.twoPhasesImpl.model.expressions.comparison.LowerExpression;
import org.javapagetemplates.twoPhasesImpl.model.expressions.path.PathExpression;

import bsh.Interpreter;

/**
 * <p>
 *   Defines a String expression.
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
public class StringExpression extends JPTExpressionImpl {

	private static final long serialVersionUID = -3138512000373794137L;
	public static final String DOT = ".";
	public static final String VOID_STRING = "";
	public static final String CLASS_EXTENSION = ".class";
	
	private String string;
	
	
	public StringExpression(){}
	
	public StringExpression(String stringExpression, String string){
		super(stringExpression);
		
		this.string = string;
	}

	
	public String getString() {
		return this.string;
	}

	public void setString(String string) {
		this.string = string;
	}

	static public StringExpression generate(String expression){
		
		return new StringExpression(
				expression, 
				expression.substring( TwoPhasesPageTemplate.EXPR_STRING.length() ));
	}

	@Override
	public Object evaluate(Interpreter beanShell) throws ExpressionEvaluationException {
		
        try {
			return evaluateString(this.string, beanShell);
			
		} catch (ExpressionSyntaxException e) {
			throw new ExpressionEvaluationException(e);
		}
	}

	public static final Object evaluate( String string, Interpreter beanShell ) 
	        throws ExpressionSyntaxException, ExpressionEvaluationException {
        try {
            Object result;
            if ( string.startsWith( TwoPhasesPageTemplate.EXPR_STRING ) ) {
                result = evaluateIntern( string, beanShell );
            }
            //else if ( string.startsWith( TwoPhasesPageTemplate.EXPR_STRINP ) ) {
            //	result = StrinpExpression.evaluate(string, beanShell);
            //}
            else if ( string.startsWith( TwoPhasesPageTemplate.EXPR_EXISTS ) ) {
            	result = ExistsExpression.evaluate(string, beanShell);
            }
            else if ( string.startsWith( TwoPhasesPageTemplate.EXPR_NOT ) ) {
            	result = NotExpression.evaluate(string, beanShell);
            }
            else if ( string.startsWith( TwoPhasesPageTemplate.EXPR_JAVA ) ) {
            	result = JavaExpression.evaluate(string, beanShell);
            }
            else if ( string.startsWith( TwoPhasesPageTemplate.EXPR_EQUALS ) ) {
            	result = EqualsExpression.evaluate(string, beanShell);
            }
            else if ( string.startsWith( TwoPhasesPageTemplate.EXPR_GREATER ) ) {
            	result = GreaterExpression.evaluate(string, beanShell);
            }
            else if ( string.startsWith( TwoPhasesPageTemplate.EXPR_LOWER ) ) {
            	result = LowerExpression.evaluate(string, beanShell);
            }
            else if ( string.startsWith( TwoPhasesPageTemplate.EXPR_ADD ) ) {
            	result = AddExpression.evaluate(string, beanShell);
            }
            else if ( string.startsWith( TwoPhasesPageTemplate.EXPR_SUB ) ) {
            	result = SubExpression.evaluate(string, beanShell);
            }
            else if ( string.startsWith( TwoPhasesPageTemplate.EXPR_MUL ) ) {
            	result = MulExpression.evaluate(string, beanShell);
            }
            else if ( string.startsWith( TwoPhasesPageTemplate.EXPR_DIV ) ) {
            	result = DivExpression.evaluate(string, beanShell);
            }
            else if ( string.startsWith( TwoPhasesPageTemplate.EXPR_MOD ) ) {
            	result = ModExpression.evaluate(string, beanShell);
            }
            else if ( string.startsWith( TwoPhasesPageTemplate.EXPR_OR ) ) {
            	result = OrExpression.evaluate(string, beanShell);
            }
            else if ( string.startsWith( TwoPhasesPageTemplate.EXPR_AND ) ) {
            	result = AndExpression.evaluate(string, beanShell);
            }
            else if ( string.startsWith( TwoPhasesPageTemplate.EXPR_COND ) ) {
            	result = CondExpression.evaluate(string, beanShell);
            }
            else {
            	result = PathExpression.evaluate(string, beanShell);
            }
            
            return result;
        }
        catch( ExpressionEvaluationException e ) {
            e.setExpression( string );
            throw e;
        }
    }
	
    private static Object evaluateIntern(String expression, Interpreter beanShell) 
    		throws ExpressionEvaluationException, ExpressionSyntaxException {
		
		return evaluateString(
				expression.substring( TwoPhasesPageTemplate.EXPR_EXISTS.length() ),
				beanShell);
	}


	private static final int STATE_SCANNING = 0;
    private static final int STATE_AT_DOLLAR = 1;
    private static final int STATE_IN_EXPRESSION = 2;
    private static final int STATE_IN_BRACKETED_EXPRESSION = 3;
    
	public static final String evaluateString( String string, Interpreter beanShell ) 
	        throws ExpressionEvaluationException, ExpressionSyntaxException {
		
		String expression = new String(string);
		
        // empty expression evaluates to empty string
        if ( expression.isEmpty() ) {
            return VOID_STRING;
        }

        StringBuilder result = new StringBuilder( expression.length() * 2 );
        
        // Let's use a finite state machine
        StringBuilder subexpression = new StringBuilder(20);
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
	
}

