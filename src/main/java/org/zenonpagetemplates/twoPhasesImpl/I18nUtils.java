package org.zenonpagetemplates.twoPhasesImpl;

import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.ZPTContext;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.I18N.I18NParams;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpression;

/**
 * <p>
 *   Utility methods for i18n.
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
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.0 $
 */
public class I18nUtils {

    static public String evaluateContent( EvaluationHelper evaluationHelper, String i18nContent, I18NParams i18nParams ) 
    		throws EvaluationException {
        
        // Get the i18n instance
        List<I18n> i18nList = getI18n( evaluationHelper );
        
        try {
			// Translate with no params
			if ( i18nParams == null ){
			    return ZPTContext.getInstance().getTranslator().tr(
			    		i18nList, 
			    		i18nContent );
			}
			
			// Translate with params
			return ZPTContext.getInstance().getTranslator().tr(
					i18nList, 
					i18nContent, 
			        getArrayFromI18nParams( i18nParams, evaluationHelper ) );
			
		} catch ( NullPointerException e ) {
			throw new EvaluationException( "I18n subsystem of ZPT was not initialized." );
		}
    }
    
    @SuppressWarnings("unchecked")
    static private List<I18n> getI18n( EvaluationHelper evaluationHelper ) throws EvaluationException {

    	Object result = evaluationHelper.get( TwoPhasesPageTemplateImpl.I18N_DOMAIN_VAR_NAME );
        
        if ( result instanceof List<?> ){
            return ( List<I18n> ) result;
        }
        
        throw new EvaluationException( TwoPhasesPageTemplateImpl.I18N_DOMAIN_VAR_NAME + " instance not found." );
    }
    
    static private Object[] getArrayFromI18nParams(I18NParams i18nParams, EvaluationHelper evaluationHelper) 
    		throws EvaluationException {
    	
        Object[] result = new Object[ TwoPhasesPageTemplateImpl.MAXIMUM_NUMBER_OF_ATTRIBUTES ];
        
        int i = 0;
        
        for ( ZPTExpression zptExpression : i18nParams.getParams() ){
            try {
                result[ i++ ] = zptExpression.evaluate( evaluationHelper );
                
            } catch ( ArrayIndexOutOfBoundsException e ) {
                throw new EvaluationException( "Too many number of attributes, the maximum is " 
                		+ TwoPhasesPageTemplateImpl.MAXIMUM_NUMBER_OF_ATTRIBUTES );
            }
        }
        
        return result;
    }
}
