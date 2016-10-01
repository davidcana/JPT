package org.zenonpagetemplates.onePhaseImpl;

/**
 * <p>
 *   Simple class to get and set data from JPT attributes.
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
 * @version $Revision: 1.1 $
 */
public class Expressions {
	
	// tal namespace
    String define = null;
    String condition = null;
    String repeat = null;
    String content = null;
    String attributes = null;
    String omitTag = null;
    String onError = null;
    String tag = null;
    
    // metal namespace
    String useMacro = null;
    String defineSlot = null;

    // i18n namespace
    String i18nDomain = null;
    String i18nDefine = null;
    String i18nContent = null;
    String i18nParams = null;
    String i18nAttributes = null;
    String i18nOnError = null;
    
    public void addToDefine( String expression ){
    	
        this.define = getNewValue(
        		this.define, 
        		expression, 
        		OnePhasePageTemplate.DEFINE_DELIMITER );
    }
    
    public void addToAttributes( String expression ){
    	
        this.attributes = getNewValue(
        		this.attributes, 
        		expression, 
        		OnePhasePageTemplate.ATTRIBUTE_DELIMITER );
    }
    
    static private String getNewValue( String oldValue, String expression, char delimiter ){
        
        if ( oldValue == null ){
            return expression;
        }
        return oldValue + delimiter + expression;
    } 
}