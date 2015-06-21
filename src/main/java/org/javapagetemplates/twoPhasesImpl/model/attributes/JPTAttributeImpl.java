package org.javapagetemplates.twoPhasesImpl.model.attributes;

import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.javapagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;

/**
 * <p>
 *   Simple class that implements JPTAttribute interface.
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
abstract public class JPTAttributeImpl implements JPTAttribute {

	private static final long serialVersionUID = -3488234069646856564L;
	private static final String QUALIFIED_NAME_DELIMITER = ":";


	private String namespaceUri;
	
	public JPTAttributeImpl(){}
	public JPTAttributeImpl( String namespaceUri ){
		this.namespaceUri = namespaceUri;
	}
	
	
	public void setNamespaceURI( String namespaceUri ) {
		this.namespaceUri = namespaceUri;
	}

	@Override
	public String getNamespaceUri() {
		return this.namespaceUri;
	}

	@Override
	public String getQualifiedName() {
		return this.namespaceUri + QUALIFIED_NAME_DELIMITER + this.getAttributeName();
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
