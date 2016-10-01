package org.zenonpagetemplates.twoPhasesImpl.model.attributes.I18N;

import java.util.ArrayList;
import java.util.List;

import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.AttributesUtils;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.ZPTAttributeImpl;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpression;

/**
 * <p>
 *   Defines a list of parameters to use in i18n.
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
public class I18NParams extends ZPTAttributeImpl implements DynamicAttribute {

	private static final long serialVersionUID = 6081559484584499271L;
	
	private List<ZPTExpression> params = new ArrayList<ZPTExpression>();
	
	
	public I18NParams(){}
	public I18NParams( String namespaceURI, String expression ) throws PageTemplateException {
		super( namespaceURI );
		this.params = AttributesUtils.getExpressions( expression );
	}

	public List<ZPTExpression> getParams() {
		return this.params;
	}

	public void setParams( List<ZPTExpression> params ) {
		this.params = params;
	}

	public void addParam( ZPTExpression params ){
		this.params.add( params );
	}
	
	@Override
	public String getAttributeName() {
		return TwoPhasesPageTemplate.I18N_PARAMS;
	}
	
	@Override
	public String getValue() {
		return AttributesUtils.getStringFromExpressions( this.params );
	}
	
}
