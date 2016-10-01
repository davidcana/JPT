package org.zenonpagetemplates.twoPhasesImpl.model.attributes.METAL;

import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.JPTAttributeImpl;

/**
 * <p>
 *   Allows to set a fill-slot definition.
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
public class METALFillSlot extends JPTAttributeImpl implements DynamicAttribute {

	private static final long serialVersionUID = 860619226504361776L;
	
	private String name;
	
	
	public METALFillSlot(){}
	public METALFillSlot( String namespaceUri, String name ){
		super( namespaceUri );
		this.name = name;
	}
	

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	@Override
	public String getAttributeName() {
		return TwoPhasesPageTemplate.METAL_FILL_SLOT;
	}

	@Override
	public String getValue() {
		return this.name;
	}
}
