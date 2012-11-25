package org.javapagetemplates.twoPhasesImpl.model.expressions;

import java.util.Collection;
import java.util.Map;

import org.javapagetemplates.common.ExpressionTokenizer;
import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.common.exceptions.ExpressionSyntaxException;
import org.javapagetemplates.common.exceptions.PageTemplateException;
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
 *   Utility methods for generating and evaluation of expressions.
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
public class ExpressionUtils {

    static public Number evaluateToNumber(JPTExpression expression, Interpreter beanShell) 
    		throws ExpressionEvaluationException {
    	
    	try {
			if (expression instanceof EvaluableToNumber){
				EvaluableToNumber evaluableToInteger = (EvaluableToNumber) expression;
				return evaluableToInteger.evaluateToNumber(beanShell);
			}
			
			Object result = expression.evaluate(beanShell);
			return Integer.valueOf(result.toString());
			
		} catch (Exception e) {
			String stringExpression = expression == null? null: expression.getStringExpression();
			throw new ExpressionEvaluationException("Error trying to evaluate to number expression '"
					+ stringExpression + "': " + e.getMessage());
		}
    }
    
    static public Number evaluateToNumber(String string, Interpreter beanShell) 
    		throws ExpressionEvaluationException, ExpressionSyntaxException{
    	
    	Object result = StringExpression.evaluate(string, beanShell);
    	return Integer.valueOf(result.toString());
    }
    
    static public Boolean evaluateToBoolean(JPTExpression expression, Interpreter beanShell) 
    		throws ExpressionEvaluationException {
    	
    	if (expression instanceof EvaluableToBoolean){
    		EvaluableToBoolean evaluableToBoolean = (EvaluableToBoolean) expression;
    		return evaluableToBoolean.evaluateToBoolean(beanShell);
    	}
    	
    	return evaluateToBoolean(expression.evaluate(beanShell));
    }
    
    static public Boolean evaluateToBoolean(String string, Interpreter beanShell) 
    		throws PageTemplateException {
    	return evaluateToBoolean(StringExpression.evaluate(string, beanShell));
    }
    
    @SuppressWarnings("rawtypes")
    static public Boolean evaluateToBoolean(Object object) throws ExpressionEvaluationException {
    	
        if ( object == null ) {
            return false;
        }
        
        else if ( object instanceof Boolean ) {
            return ((Boolean)object).booleanValue();
        }
        
        else if ( object instanceof String ) {
            return ((String)object).length() != 0;
        }
        
        else if ( object instanceof Long ) {
            return ((Long)object).longValue() != 0l;
        }

        else if ( object instanceof Integer ) {
            return ((Integer)object).intValue() != 0;
        }

        else if ( object instanceof Double ) {
            return ((Double)object).doubleValue() != 0.0d;
        }

        else if ( object instanceof Long ) {
            return ((Float)object).floatValue() != 0.0;
        }
        
        else if ( object instanceof Collection ) {
            return ((Collection)object).size() != 0;
        }
        
        else if ( object instanceof Map ) {
            return ((Map)object).size() != 0;
        }
        
        return true;
    }
    
    static public JPTExpression generate(String expression)
    		throws ExpressionSyntaxException {
		
    	String effectiveExpression = ExpressionTokenizer.removeParenthesisIfAny(
				expression ).trim();
		
        if ( effectiveExpression.startsWith( TwoPhasesPageTemplate.EXPR_STRING ) ) {
            return StringExpression.generate( effectiveExpression );
        }
        else if ( effectiveExpression.startsWith( TwoPhasesPageTemplate.EXPR_EXISTS ) ) {
        	return ExistsExpression.generate( effectiveExpression );
        }
        else if ( effectiveExpression.startsWith( TwoPhasesPageTemplate.EXPR_NOT ) ) {
        	return NotExpression.generate( effectiveExpression );
        }
        else if ( effectiveExpression.startsWith( TwoPhasesPageTemplate.EXPR_JAVA ) ) {
        	return JavaExpression.generate( effectiveExpression );
        }
        else if ( effectiveExpression.startsWith( TwoPhasesPageTemplate.EXPR_EQUALS ) ) {
        	return EqualsExpression.generate(effectiveExpression);
        }
        else if ( effectiveExpression.startsWith( TwoPhasesPageTemplate.EXPR_GREATER ) ) {
        	return GreaterExpression.generate(effectiveExpression);
        }
        else if ( effectiveExpression.startsWith( TwoPhasesPageTemplate.EXPR_LOWER ) ) {
        	return LowerExpression.generate(effectiveExpression);
        }
        else if ( effectiveExpression.startsWith( TwoPhasesPageTemplate.EXPR_ADD ) ) {
        	return AddExpression.generate(effectiveExpression);
        }
        else if ( effectiveExpression.startsWith( TwoPhasesPageTemplate.EXPR_SUB ) ) {
        	return SubExpression.generate(effectiveExpression);
        }
        else if ( effectiveExpression.startsWith( TwoPhasesPageTemplate.EXPR_MUL ) ) {
        	return MulExpression.generate(effectiveExpression);
        }
        else if ( effectiveExpression.startsWith( TwoPhasesPageTemplate.EXPR_DIV ) ) {
        	return DivExpression.generate(effectiveExpression);
        }
        else if ( effectiveExpression.startsWith( TwoPhasesPageTemplate.EXPR_MOD ) ) {
        	return ModExpression.generate(effectiveExpression);
        }
        else if ( effectiveExpression.startsWith( TwoPhasesPageTemplate.EXPR_OR ) ) {
        	return OrExpression.generate(effectiveExpression);
        }
        else if ( effectiveExpression.startsWith( TwoPhasesPageTemplate.EXPR_AND ) ) {
        	return AndExpression.generate(effectiveExpression);
        }
        else if ( effectiveExpression.startsWith( TwoPhasesPageTemplate.EXPR_COND ) ) {
        	return CondExpression.generate(effectiveExpression);
        }

        return PathExpression.generate(effectiveExpression);
    }

}
