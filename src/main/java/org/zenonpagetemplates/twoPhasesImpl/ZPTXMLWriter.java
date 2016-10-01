package org.zenonpagetemplates.twoPhasesImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;

import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.zenonpagetemplates.twoPhasesImpl.ZPTContext;
import org.zenonpagetemplates.twoPhasesImpl.model.ZPTDocument;
import org.zenonpagetemplates.twoPhasesImpl.model.ZPTElement;
import org.zenonpagetemplates.twoPhasesImpl.model.content.CDATANode;
import org.zenonpagetemplates.twoPhasesImpl.model.content.ContentItem;
import org.zenonpagetemplates.twoPhasesImpl.model.content.TextNode;

/**
 * <p>
 *   Extends XMLWriter class to make it easy to write XML documents
 *   using ZPTOutputFormat and ZPTElement instances.
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
 *
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.0 $
 */
public class ZPTXMLWriter extends XMLWriter {

	private static final String OPEN_EMPTY_TAG = "<";
	private static final String CLOSE_XML_EMPTY_TAG = " />";
	private static final String CLOSE_HTML_TAG = ">";
	private static final String NEW_LINE = "\n";

	
	private ZPTOutputFormat zptOutputFormat;
	
	
	public ZPTXMLWriter( Writer writer, ZPTOutputFormat zptOutputFormat ){
		super( writer, zptOutputFormat.getOutputFormat() );
		
		this.zptOutputFormat = zptOutputFormat;
	}
	
	public ZPTXMLWriter( OutputStream outputStream, ZPTOutputFormat zptOutputFormat )  
			throws UnsupportedEncodingException {
		super( outputStream, zptOutputFormat.getOutputFormat() );
		
		this.zptOutputFormat = zptOutputFormat;
	}
	
	public ZPTOutputFormat getZptOutputFormat() {
		return this.zptOutputFormat;
	}

	public void writeDocType( ZPTDocument zptDocument ) throws IOException, SAXException {
		
		DocType docType = zptDocument.getDocType() != null? 
				zptDocument.getDocType():
				this.zptOutputFormat.getDocType();
		
		if ( docType == null ){
			return;
		}
		
		this.writeDocType(
				docType.getName(), 
				docType.getPublicId(), 
				docType.getSystemId());
		
		this.writeNewLine();
	}
	
	public void writeZPTElement( ZPTElement zptElement ) throws IOException, SAXException {
		
		// Special empty tags
		if (zptElement.isEmpty() 
				&& ZPTContext.getInstance().isOmitElementCloseSet( zptElement.getName() )){
			
			// Write empty tag
			this.writeEmptyElement( zptElement );
			return;
		}
		
		// Write non empty tag
		this.startElement( 
				zptElement.getNamespace(), 
				zptElement.getName(), 
				zptElement.getQualifiedName(),
				zptElement.generateAttributes() );

		for ( ContentItem contentItem: zptElement.getContents() ){
			contentItem.writeToXMLWriter( this );
		}
		
		this.endElement( 
				zptElement.getNamespace(), 
				zptElement.getName(), 
				zptElement.getQualifiedName() );
	}
	
    public void writeEmptyElement( ZPTElement zptElement )
            throws IOException, SAXException {
        this.writeEmptyElement( zptElement, zptElement.generateAttributes() );
        /*
    	this.writer.write(OPEN_EMPTY_TAG);
        this.writer.write(zptElement.getQualifiedName());
        writeNamespaces();
        writeAttributes(zptElement.generateAttributes());
        this.writer.write(
        		this.zptOutputFormat.isXmlMode()? 
        		CLOSE_XML_EMPTY_TAG:
        		CLOSE_HTML_TAG);*/
    }
    
    
    public void writeEmptyElement( ZPTElement zptElement, AttributesImpl attributes )
            throws IOException, SAXException {
        
    	this.writer.write( OPEN_EMPTY_TAG );
        this.writer.write( zptElement.getQualifiedName() );
        writeNamespaces();
        writeAttributes( attributes );
        this.writer.write(
        		this.zptOutputFormat.isXMLMode()? 
        		CLOSE_XML_EMPTY_TAG:
        		CLOSE_HTML_TAG );
    }
    
	public void writeCDATANode( CDATANode cdataNode ) throws IOException, SAXException {

		this.startCDATA();
		this.writeText( cdataNode.getText() );
		this.endCDATA();
	}
    
	
	public void writeTextNode( TextNode textNode ) throws IOException, SAXException {
        this.writeText( textNode.getText() );
	}
	
	
	public void writeText( String text ) throws SAXException {
		char[] cdata = text == null? NullContent.NULL_CHAR_ARRAY: text.toCharArray();
        this.characters( cdata, 0, cdata.length );
	}
	
	public void writeHTML( Element element ) throws IOException {
		
        for ( @SuppressWarnings("unchecked")
		Iterator<Node> i = element.nodeIterator(); i.hasNext(); ) {
            Node node = ( Node ) i.next();
            this.write( node );
        }
	}
	
	public void writeNewLine() throws SAXException {
		this.writeText( NEW_LINE );
	}
	
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    	
    	if ( ! this.zptOutputFormat.isXMLMode() 
    			&& ZPTContext.getInstance().isOmitElementCloseSet( localName )){
    		return;
    	}
    	
    	super.endElement( namespaceURI, localName, qName );
    }
    
	@Override
    protected void writeEmptyElementClose( String qualifiedName ) throws IOException {

		if ( ZPTContext.getInstance().isEmptyTag( qualifiedName ) ){
        	this.writer.write( "/>" );
        } else {
        	this.writer.write( "></" );
        	this.writer.write( qualifiedName );
        	this.writer.write( ">" );
        }
    }

}
