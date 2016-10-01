package org.zenonpagetemplates.twoPhasesImpl.model.attributes;

/**
 * <p>
 *   Simple class that implements StaticAttribute interface.
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
public class StaticAttributeImpl extends JPTAttributeImpl implements StaticAttribute {
	
	private static final long serialVersionUID = -2520079638396474376L;
	
	private String attributeName;
	private String value;
	
	public StaticAttributeImpl( String namespaceUri, String name, String value ){
		super( namespaceUri );
		this.attributeName = name;
		this.value = value;
	}
	
	@Override
	public String getAttributeName() {
		return this.attributeName;
	}
	
	public void setAttributeName( String name ) {
		this.attributeName = name;
	}
	
	@Override
	public String getValue() {
		return this.value;
	}

	public void setValue( String value ) {
		this.value = value;
	}

	@Override
	public String getQualifiedName() {
		
		if ( this.getNamespaceUri().isEmpty() ){
			return this.attributeName;
		}
		
		return super.getQualifiedName();
	}

}
