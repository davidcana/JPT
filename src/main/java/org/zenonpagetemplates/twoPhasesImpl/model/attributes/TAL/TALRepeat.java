package org.zenonpagetemplates.twoPhasesImpl.model.attributes.TAL;

import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.AttributesUtils;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.ZPTAttributeImpl;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.KeyValuePair;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpression;

/**
 * <p>
 *   Allows to iterate using a variable name and an expression to
 *   configure the loop.
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
public class TALRepeat extends ZPTAttributeImpl implements DynamicAttribute {

	private static final long serialVersionUID = 798497775749162543L;
	
	private KeyValuePair<ZPTExpression> repeat;
	
	
	public TALRepeat(){}
	public TALRepeat(String namespaceURI, String expression) throws PageTemplateException {
		super( namespaceURI );
		this.repeat = AttributesUtils.getDefinition( expression );
	}
	

	public KeyValuePair<ZPTExpression> getRepeat() {
		return this.repeat;
	}

	public void setRepeat(KeyValuePair<ZPTExpression> repeat) {
		this.repeat = repeat;
	}
	
	@Override
	public String getAttributeName() {
		return TwoPhasesPageTemplate.TAL_REPEAT;
	}
	
	@Override
	public String getValue() {
		return AttributesUtils.getStringFromDefinition( this.repeat );
	}
}
