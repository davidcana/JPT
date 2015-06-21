package org.javapagetemplates.twoPhasesImpl.model.attributes.I18N;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javapagetemplates.common.exceptions.EvaluationException;
import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.common.scripting.EvaluationHelper;
import org.javapagetemplates.twoPhasesImpl.I18nUtils;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplateImpl;
import org.javapagetemplates.twoPhasesImpl.model.attributes.AttributesUtils;
import org.javapagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.javapagetemplates.twoPhasesImpl.model.attributes.JPTAttributeImpl;
import org.javapagetemplates.twoPhasesImpl.model.attributes.KeyValuePair;

/**
 * <p>
 *   Similar to <code>tal:define</code> but the value of the definitions
 *   will not be evaluated, it will be used as a i18n key.
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
public class I18NDefine extends JPTAttributeImpl implements DynamicAttribute {

	private static final long serialVersionUID = 7825312273948045668L;
	
	private List<KeyValuePair<String>> definitions = new ArrayList<KeyValuePair<String>>();
	
	
	public I18NDefine(){}
	public I18NDefine( String namespaceUri, String expression ) throws PageTemplateException {
		super( namespaceUri );
		this.definitions = AttributesUtils.getDefinitionsFromString( expression );
	}
	
	public List<KeyValuePair<String>> getDefinitions() {
		return this.definitions;
	}

	public void setDefinitions( List<KeyValuePair<String>> definitions ) {
		this.definitions = definitions;
	}
	
	public void addDefinition( KeyValuePair<String> definition ){
		this.definitions.add( definition );
	}
	
	@Override
	public String getAttributeName() {
		return TwoPhasesPageTemplate.I18N_DEFINE;
	}
	
	@Override
	public String getValue() {
		return AttributesUtils.getStringFromDefinitions( this.definitions );
	}
	
    public void evaluate( EvaluationHelper evaluationHelper, I18NParams i18nParams,
    		List<String> varsToUnset, Map<String, Object> varsToSet ) throws EvaluationException {
    	
		for ( KeyValuePair<String> definition: this.definitions ){
			
			try {
				TwoPhasesPageTemplateImpl.setVar(
						evaluationHelper, 
						varsToUnset, 
						varsToSet, 
						definition.getKey(), 
						I18nUtils.evaluateContent( 
								evaluationHelper, 
								definition.getValue(), 
								i18nParams ));
				
			} catch ( EvaluationException e ) {
				e.setInfo(
						null,
						this.getQualifiedName() );
				throw e;
				
			} catch ( Exception e ) {
				EvaluationException e2 = new EvaluationException( e );
				e2.setInfo(
						null,
						this.getQualifiedName() );
				throw e2;
			}
		}
    }
    
}
