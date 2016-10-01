package org.zenonpagetemplates.twoPhasesImpl.model.attributes.I18N;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.ExpressionSyntaxException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.I18nUtils;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.ZPTAttributeImpl;

/**
 * <p>
 *   Similar to <code>tal:content</code> but the value of the content
 *   will not be evaluated, it will be used as a i18n key.
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
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.0 $
 */
public class I18NContent extends ZPTAttributeImpl implements DynamicAttribute {

	private static final long serialVersionUID = 8917785194106816239L;
	
	private String i18nKey;
	
	
	public I18NContent(){}
	public I18NContent( String namespaceURI, String content ){
		super( namespaceURI );
		this.i18nKey = content;
	}
	

	public String getI18nKey() {
		return this.i18nKey;
	}

	public void setI18nKey( String i18nKey ) {
		this.i18nKey = i18nKey;
	}
	
	@Override
	public String getAttributeName() {
		return TwoPhasesPageTemplate.I18N_CONTENT;
	}
	
	@Override
	public String getValue() {
		return this.i18nKey;
	}
	
	public String evaluate( EvaluationHelper evaluationHelper, I18NParams i18nParams ) 
			throws ExpressionSyntaxException, EvaluationException {
		
		return I18nUtils.evaluateContent(
				evaluationHelper, 
				this.i18nKey, 
				i18nParams );
	}
}
