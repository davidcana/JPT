package org.zenonpagetemplates.onePhaseImpl;

import org.dom4j.Element;
import org.dom4j.io.SAXWriter;
import org.dom4j.tree.NamespaceStack;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <p>
 *   Extends SAXWriter class to add a simple space to empty tags.
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
 *
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.1 $
 */
public class JPTSAXWriter extends SAXWriter {

	private static final String SPACE = " ";

	
    public JPTSAXWriter(ContentHandler contentHandler,
            LexicalHandler lexicalHandler) {
    	super( contentHandler, lexicalHandler );
    }
    
    
	@Override
	protected void write(Element element, NamespaceStack namespaceStack)
			throws SAXException {
		
		if ( !element.hasContent() && !JPTContext.getInstance().isEmptyTag( element.getName() ) ){
	        int stackSize = namespaceStack.size();
	        AttributesImpl namespaceAttributes = startPrefixMapping( element, namespaceStack );
	        startElement( element, namespaceAttributes );
	        write( SPACE );
	        endElement( element );
	        endPrefixMapping( namespaceStack, stackSize );
			return;
		}
		
		super.write( element, namespaceStack );
	}

}
