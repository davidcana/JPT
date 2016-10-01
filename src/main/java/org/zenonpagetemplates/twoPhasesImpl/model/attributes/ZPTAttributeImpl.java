package org.zenonpagetemplates.twoPhasesImpl.model.attributes;

import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;

/**
 * <p>
 *   Simple class that implements ZPTAttribute interface.
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
abstract public class ZPTAttributeImpl implements ZPTAttribute {

	private static final long serialVersionUID = -3488234069646856564L;
	private static final String QUALIFIED_NAME_DELIMITER = ":";


	private String namespaceURI;
	
	public ZPTAttributeImpl(){}
	public ZPTAttributeImpl( String namespaceURI ){
		this.namespaceURI = namespaceURI;
	}
	
	
	public void setNamespaceURI( String namespaceUri ) {
		this.namespaceURI = namespaceUri;
	}

	@Override
	public String getNamespaceURI() {
		return this.namespaceURI;
	}

	@Override
	public String getQualifiedName() {
		return this.namespaceURI + QUALIFIED_NAME_DELIMITER + this.getAttributeName();
	}
	
	protected static void configureTextEscapableAttribute(
			TextEscapableAttribute textEscapableAttribute, String exp ) throws PageTemplateException {
				
		String stringExpression = null;
		
	    // Structured text, preserve xml structure
	    if ( exp.startsWith( TwoPhasesPageTemplate.STRING_EXPR_STRUCTURE ) ) {
	    	stringExpression = exp.substring( TwoPhasesPageTemplate.STRING_EXPR_STRUCTURE.length() );
	    	textEscapableAttribute.setEscapeOn( false );
	        
	    } else if ( exp.startsWith( TwoPhasesPageTemplate.STRING_EXPR_TEXT ) ) {
	    	stringExpression = exp.substring( TwoPhasesPageTemplate.STRING_EXPR_TEXT.length() );
	    	
	    } else {
	    	stringExpression = exp;
	    }
	    
	    textEscapableAttribute.setContent ( ExpressionUtils.generate( stringExpression ) );
	}
	
}
