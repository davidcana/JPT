package org.zenonpagetemplates.twoPhasesImpl.model.attributes.I18N;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.I18nUtils;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.JPTAttributeImpl;

/**
 * <p>
 *   Similar to <code>tal:on-error</code> but the value of the content
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
public class I18NOnError extends JPTAttributeImpl implements DynamicAttribute {

	private static final long serialVersionUID = -5245133657454528565L;

	private String i18nKey;
	
	
	public I18NOnError(){}
	
	public I18NOnError( String namespaceUri, String expression ){
		super( namespaceUri );
		this.i18nKey = expression;
	}
	
	
	public String getI18nKey() {
		return i18nKey;
	}

	public void setI18nKey(String i18nKey) {
		this.i18nKey = i18nKey;
	}
	
	@Override
	public String getAttributeName() {
		return TwoPhasesPageTemplate.I18N_ON_ERROR;
	}

	@Override
	public String getValue() {
		return this.i18nKey;
	}
	
	public String evaluate( EvaluationHelper evaluationHelper, I18NParams i18nParams ) 
			throws EvaluationException {
		
		return I18nUtils.evaluateContent(
				evaluationHelper, 
				this.i18nKey, 
				i18nParams );
	}
}
