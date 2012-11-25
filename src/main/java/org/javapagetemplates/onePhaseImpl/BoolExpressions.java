package org.javapagetemplates.onePhaseImpl;

import org.javapagetemplates.common.ExpressionTokenizer;
import org.javapagetemplates.common.exceptions.PageTemplateException;

import bsh.Interpreter;

/**
 * <p>
 *   Simple class to implement boolean expression (or: and: and cond:).
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
 * @version $Revision: 1.1 $
 */

public class BoolExpressions {
	
    private static final String COND = "COND";
	private static final String OR = "OR";
	private static final String AND = "AND";
	
	
	public static boolean and( String exp, Interpreter beanShell ) throws PageTemplateException {
		return and( exp, beanShell, OnePhasePageTemplate.EXPRESSION_DELIMITER );
	}
	
	public static boolean and( String exp, Interpreter beanShell, char delimiter ) throws PageTemplateException {
    	
        String expression = exp.trim();
        
        if ( expression.length() == 0 ) {
            throw new PageTemplateException("And expression void.");
        }

        ExpressionTokenizer segments = new ExpressionTokenizer( expression, delimiter );
        if ( segments.countTokens() == 1 ) {
        	throw new PageTemplateException("Only one element in and expression, please add at least one more.");
        }
        
        while ( segments.hasMoreTokens() ) {
            if (!evaluateSegment(
            		AND,
            		expression, 
            		segments.nextToken().trim(), 
            		beanShell)){
            	return false;
            }
        }

        return true;
    }
	
	public static boolean or( String exp, Interpreter beanShell ) throws PageTemplateException {
		return or(exp, beanShell, OnePhasePageTemplate.EXPRESSION_DELIMITER );
	}
	
    public static boolean or( String exp, Interpreter beanShell, char delimiter ) throws PageTemplateException {
    	
        String expression = exp.trim();
        
        if ( expression.length() == 0 ) {
            throw new PageTemplateException("Or expression void.");
        }

        ExpressionTokenizer segments = new ExpressionTokenizer( expression, delimiter );
        if ( segments.countTokens() == 1 ) {
        	throw new PageTemplateException("Only one element in or expression, please add at least one more.");
        }
        
        while ( segments.hasMoreTokens() ) {
            if (evaluateSegment(
            		OR, 
            		expression, 
            		segments.nextToken().trim(), 
            		beanShell)){
            	return true;
            }
        }

        return false;
    }
    
	public static Object cond( String exp, Interpreter beanShell ) throws PageTemplateException {
		return cond( exp, beanShell, OnePhasePageTemplate.EXPRESSION_DELIMITER );
	}
	
    public static Object cond( String exp, Interpreter beanShell, char delimiter ) throws PageTemplateException {
    	
        String expression = exp.trim();
        
        if ( expression.length() == 0 ) {
            throw new PageTemplateException("Cond expression void.");
        }

        ExpressionTokenizer segments = new ExpressionTokenizer( expression, delimiter );
        if ( segments.countTokens() != 3 ) {
        	throw new PageTemplateException("3 element are needed in cond expression.");
        }
        
        // Evaluate first expression
        String segment = segments.nextToken().trim();
        boolean fistExpressionResult = evaluateSegment(
        		COND,
        		expression, 
        		segment, 
        		beanShell);
        
        // If true, evaluate second expression
        segment = segments.nextToken().trim();
        if (fistExpressionResult){
        	return Expression.evaluate( segment, beanShell );
        }

        // If false, evaluate third expression
        segment = segments.nextToken().trim();
        return Expression.evaluate( segment, beanShell );
    }
    
    private static boolean evaluateSegment(String name, String expression, String segment, Interpreter beanShell )
    		throws PageTemplateException {
    	
    	Object result = Expression.evaluate( segment, beanShell );
        if (!(result instanceof Boolean)){
        	throw new PageTemplateException("Element '"  + segment + "' in " + name + " expression '" 
        			+ expression + "' is not a valid boolean.");
        }
        
    	return (Boolean) result;
    }
    
}