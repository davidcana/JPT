package org.javapagetemplates.onePhaseImpl;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import java.util.Iterator;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXWriter;

import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.SAXException;
import org.javapagetemplates.common.exceptions.PageTemplateException;

/**
 * <p>
 *   Represents a HTML fragment and allows parsing of its HTML code.
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
 * @author <a href="mailto:chris@christophermrossi.com">Chris Rossi</a>
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.2 $
 */
public class HTMLFragment implements Serializable {

    private static final String BODY = "body";
	private static final long serialVersionUID = 5100769357480308079L;
    private String html;
    private transient Element dom = null;
    
    public HTMLFragment( String html ) throws PageTemplateException {
        
    	this.html = html;
        
        if ( JPTContext.getInstance().isParseHTMLFragments() ){ 
        	parseFragment();
        }
    }
    
    public String getHtml() {
        return this.html;
    }
    
    public void setHtml( String html ) {
        this.html = html;
    }
    
    public String getXhtml() throws PageTemplateException {
    	
        try {
            StringWriter buffer = new StringWriter();
            toXhtml( buffer );
            buffer.close();
            return buffer.toString();
            
        } catch( IOException e ) {
            throw new RuntimeException( e );
        }
    }
    
    
	@SuppressWarnings("unchecked")
	public void toXhtml( ContentHandler contentHandler, LexicalHandler lexicalHandler ) 
        throws PageTemplateException, SAXException {
		
        if ( this.dom == null ) {
            parseFragment();
        }
       
        //SAXWriter writer = new SAXWriter( contentHandler, lexicalHandler);
        SAXWriter writer = new JPTSAXWriter( contentHandler, lexicalHandler );
        for ( Iterator<Node> i = this.dom.nodeIterator(); i.hasNext(); ) {
            Node node = i.next();
            writer.write( node );
        }
    }
    
	
    @SuppressWarnings({ "unchecked" })
	public void toXhtml( Writer writer )
        throws PageTemplateException, IOException {
    	
        if ( this.dom == null ) {
            parseFragment();
        }
        for ( Iterator<Node> i = this.dom.nodeIterator(); i.hasNext(); ) {
            Node node = ( Node ) i.next();
            node.write( writer );
        }
    }
    
    private void parseFragment() throws PageTemplateException {
    	
        try {
            StringBuffer fragment = new StringBuffer( this.html.length() + 26 );
            fragment.append( "<html><body>" );
            fragment.append( this.html );
            fragment.append( "</body></html>" );
            
            Reader input = new StringReader( fragment.toString() );
            SAXReader reader = PageTemplateImpl.getXMLReader();
            try {
                this.dom = reader.read( input ).getRootElement().element( BODY );
            } catch( DocumentException e ) {
                try {
                    reader = PageTemplateImpl.getHTMLReader();
                    if (reader == null){
                        throw (e);
                    }
                    input.close();
                    input = new StringReader( fragment.toString() );
                    this.dom = reader.read( input ).getRootElement().element( BODY );
                } catch( NoClassDefFoundError ee ) {
                    // Allow user to omit nekohtml package
                    // to disable html parsing
                    //System.err.println( "Warning: no nekohtml" );
                    //ee.printStackTrace();
                    throw e;
                }
            }
        }
        catch( Exception e ) {
            throw new PageTemplateException( e );
        }
    }
    
    @Override
    public String toString() {
        return getHtml();
    }
    
    
    public Element getDom() throws PageTemplateException{
    	
    	if ( this.dom == null ){
    		this.parseFragment();
    	}
    	
    	return this.dom;
    }
}

