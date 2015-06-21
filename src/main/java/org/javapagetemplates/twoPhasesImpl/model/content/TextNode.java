package org.javapagetemplates.twoPhasesImpl.model.content;

import java.io.IOException;

import org.javapagetemplates.twoPhasesImpl.JPTXMLWriter;
import org.xml.sax.SAXException;

/**
 * <p>
 *   Represents a TextNode from the contents of a JPTElement.
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
public class TextNode implements ContentItem {

	private static final long serialVersionUID = -6429935563418031686L;

	private String text;
	
	public TextNode(){}
	public TextNode( String text ){
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	public void setText( String text ) {
		this.text = text;
	}
	
	@Override
	public void writeToXmlWriter( JPTXMLWriter xmlWriter ) throws IOException,
			SAXException {
		xmlWriter.writeTextNode( this );
	}
	
}
