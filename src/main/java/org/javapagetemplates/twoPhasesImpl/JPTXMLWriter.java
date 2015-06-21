package org.javapagetemplates.twoPhasesImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;

import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.XMLWriter;

import org.javapagetemplates.twoPhasesImpl.JPTContext;
import org.javapagetemplates.twoPhasesImpl.model.JPTDocument;
import org.javapagetemplates.twoPhasesImpl.model.JPTElement;
import org.javapagetemplates.twoPhasesImpl.model.content.CDATANode;
import org.javapagetemplates.twoPhasesImpl.model.content.ContentItem;
import org.javapagetemplates.twoPhasesImpl.model.content.TextNode;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <p>
 *   Extends XMLWriter class to make it easy to write XML documents
 *   using JPTOutputFormat and JPTElement instances.
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
 * @version $Revision: 1.0 $
 */
public class JPTXMLWriter extends XMLWriter {

	private static final String OPEN_EMPTY_TAG = "<";
	private static final String CLOSE_XML_EMPTY_TAG = " />";
	private static final String CLOSE_HTML_TAG = ">";
	private static final String NEW_LINE = "\n";

	
	private JPTOutputFormat jptOutputFormat;
	
	
	public JPTXMLWriter( Writer writer, JPTOutputFormat jptOutputFormat ){
		super( writer, jptOutputFormat.getOutputFormat() );
		
		this.jptOutputFormat = jptOutputFormat;
	}
	
	public JPTXMLWriter( OutputStream outputStream, JPTOutputFormat jptOutputFormat )  
			throws UnsupportedEncodingException {
		super( outputStream, jptOutputFormat.getOutputFormat() );
		
		this.jptOutputFormat = jptOutputFormat;
	}
	
	public JPTOutputFormat getJptOutputFormat() {
		return this.jptOutputFormat;
	}

	public void writeDocType( JPTDocument jptDocument ) throws IOException, SAXException {
		
		DocType docType = jptDocument.getDocType() != null? 
				jptDocument.getDocType():
				this.jptOutputFormat.getDocType();
		
		if ( docType == null ){
			return;
		}
		
		this.writeDocType(
				docType.getName(), 
				docType.getPublicId(), 
				docType.getSystemId());
		
		this.writeNewLine();
	}
	
	public void writeJPTElement( JPTElement jptElement ) throws IOException, SAXException {
		
		// Special empty tags
		if (jptElement.isEmpty() 
				&& JPTContext.getInstance().isOmitElementCloseSet( jptElement.getName() )){
			
			// Write empty tag
			this.writeEmptyElement( jptElement );
			return;
		}
		
		// Write non empty tag
		this.startElement( 
				jptElement.getNamespace(), 
				jptElement.getName(), 
				jptElement.getQualifiedName(),
				jptElement.generateAttributes() );

		for ( ContentItem contentItem: jptElement.getContents() ){
			contentItem.writeToXmlWriter( this );
		}
		
		this.endElement( 
				jptElement.getNamespace(), 
				jptElement.getName(), 
				jptElement.getQualifiedName() );
	}
	
    public void writeEmptyElement( JPTElement jptElement )
            throws IOException, SAXException {
        this.writeEmptyElement( jptElement, jptElement.generateAttributes() );
        /*
    	this.writer.write(OPEN_EMPTY_TAG);
        this.writer.write(jptElement.getQualifiedName());
        writeNamespaces();
        writeAttributes(jptElement.generateAttributes());
        this.writer.write(
        		this.jptOutputFormat.isXmlMode()? 
        		CLOSE_XML_EMPTY_TAG:
        		CLOSE_HTML_TAG);*/
    }
    
    
    public void writeEmptyElement( JPTElement jptElement, AttributesImpl attributes )
            throws IOException, SAXException {
        
    	this.writer.write( OPEN_EMPTY_TAG );
        this.writer.write( jptElement.getQualifiedName() );
        writeNamespaces();
        writeAttributes( attributes );
        this.writer.write(
        		this.jptOutputFormat.isXmlMode()? 
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
    	
    	if ( ! this.jptOutputFormat.isXmlMode() 
    			&& JPTContext.getInstance().isOmitElementCloseSet( localName )){
    		return;
    	}
    	
    	super.endElement( namespaceURI, localName, qName );
    }
    
	@Override
    protected void writeEmptyElementClose( String qualifiedName ) throws IOException {

		if ( JPTContext.getInstance().isEmptyTag( qualifiedName ) ){
        	this.writer.write( "/>" );
        } else {
        	this.writer.write( "></" );
        	this.writer.write( qualifiedName );
        	this.writer.write( ">" );
        }
    }

}
