package org.zenonpagetemplates.twoPhasesImpl.model.attributes.I18N;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.helpers.AttributesImpl;
import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.I18nUtils;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.JPTDocument;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.AttributesUtils;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.JPTAttributeImpl;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.KeyValuePair;

/**
 * <p>
 *   Similar to <code>tal:attributes</code> but the value of the attribute
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
public class I18NAttributes extends JPTAttributeImpl implements DynamicAttribute {

	private static final long serialVersionUID = -636861278841912537L;
	
	private List<KeyValuePair<String>> attributes = new ArrayList<KeyValuePair<String>>();
	
	
	public I18NAttributes(){}
	public I18NAttributes( String namespaceUri, String expression ) throws PageTemplateException {
		super( namespaceUri );
		this.attributes = AttributesUtils.getDefinitionsFromString( expression );
	}

	
	public List<KeyValuePair<String>> getAttributes() {
		return attributes;
	}

	public void setAttributes( List<KeyValuePair<String>> attributes ) {
		this.attributes = attributes;
	}

	public void addAttribute( KeyValuePair<String> attribute ){
		this.attributes.add( attribute );
	}
	
	@Override
	public String getAttributeName() {
		return TwoPhasesPageTemplate.I18N_ATTRIBUTES;
	}

	@Override
	public String getValue() {
		return AttributesUtils.getStringFromDefinitions( this.attributes );
	}
	
	public void evaluate(
			EvaluationHelper evaluationHelper, I18NParams i18nParams, AttributesImpl attributesImpl, JPTDocument jptDocument)
			throws EvaluationException {
		
		try {
			for ( KeyValuePair<String> attribute: this.attributes ){
			    String qualifiedName = attribute.getKey();
			    Object value = I18nUtils.evaluateContent(
			    		evaluationHelper, 
			    		attribute.getValue(), 
			    		i18nParams );
			            
			    AttributesUtils.addAttribute(
			    		qualifiedName, 
			    		value, 
			    		attributesImpl, 
			    		jptDocument );
			}
			
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
