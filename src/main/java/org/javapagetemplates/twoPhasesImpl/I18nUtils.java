package org.javapagetemplates.twoPhasesImpl;

import java.util.List;

import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.twoPhasesImpl.JPTContext;
import org.javapagetemplates.twoPhasesImpl.model.attributes.I18N.I18NParams;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;

import org.xnap.commons.i18n.I18n;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * <p>
 *   Utility methods for i18n.
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
public class I18nUtils {

	
    static public String evaluateContent(Interpreter beanShell, String i18nContent, I18NParams i18nParams) 
    		throws ExpressionEvaluationException {
        
        // Get the i18n instance
        List<I18n> i18nList = getI18n(beanShell);
        
        try {
			// Translate with no params
			if (i18nParams == null){
			    return JPTContext.getInstance().getTranslator().tr(
			    		i18nList, 
			    		i18nContent);
			}
			
			// Translate with params
			return JPTContext.getInstance().getTranslator().tr(
					i18nList, 
					i18nContent, 
			        getArrayFromI18nParams(i18nParams, beanShell));
			
		} catch (NullPointerException e) {
			throw new ExpressionEvaluationException("I18n subsystem of JPT was not initialized.");
		}
    }
    
    @SuppressWarnings("unchecked")
    static private List<I18n> getI18n(Interpreter beanShell) throws ExpressionEvaluationException {

        try {
        	Object result = beanShell.get(TwoPhasesPageTemplateImpl.I18N_DOMAIN_VAR_NAME);
            
            if (result instanceof List<?>){
                return (List<I18n>) result;
            }
        } catch (EvalError e) {
            throw new ExpressionEvaluationException(
            		"The " + TwoPhasesPageTemplateImpl.I18N_DOMAIN_VAR_NAME 
            		+ " var is not an instance ofI18n class");
        }
        
        throw new ExpressionEvaluationException(TwoPhasesPageTemplateImpl.I18N_DOMAIN_VAR_NAME + " instance not found.");
    }
    
    static private Object[] getArrayFromI18nParams(I18NParams i18nParams, Interpreter beanShell) 
    		throws ExpressionEvaluationException {
    	
        Object[] result = new Object[TwoPhasesPageTemplateImpl.MAXIMUM_NUMBER_OF_ATTRIBUTES];
        
        int i = 0;
        
        for (JPTExpression jptExpression: i18nParams.getParams()){
            try {
                result[i++] = jptExpression.evaluate(beanShell);
                
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ExpressionEvaluationException("Too many number of attributes, the maximum is " 
                		+ TwoPhasesPageTemplateImpl.MAXIMUM_NUMBER_OF_ATTRIBUTES);
            }
        }
        
        return result;
    }
}
