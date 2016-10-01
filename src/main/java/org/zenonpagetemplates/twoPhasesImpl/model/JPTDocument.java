package org.zenonpagetemplates.twoPhasesImpl.model;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.TreeMap;

import org.dom4j.Namespace;
import org.xml.sax.SAXException;
import org.zenonpagetemplates.twoPhasesImpl.DocType;
import org.zenonpagetemplates.twoPhasesImpl.JPTOutputFormat;
import org.zenonpagetemplates.twoPhasesImpl.JPTXMLWriter;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;

/**
 * <p>
 *   Encapsulates all data about a JPT.
 * </p>
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
public class JPTDocument {

	private static final String HTTP_WWW_W3_ORG = "http://www.w3.org/";
	
	private String id;
	private URI uri;
	private DocType docType;
	private JPTElement root;
	private Map<String, String> namespaces = new TreeMap<String, String>();
	private String templateName;
	
	transient private String talPrefix;
	transient private String i18nPrefix;
	transient private String metalPrefix;
	
	
	public JPTDocument(){}
	public JPTDocument( URI uri ) throws URISyntaxException {
		this.id = uri.toString();
		this.uri = uri;
	}
	

	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}
	
	public URI getUri() {
		return uri;
	}
	
	public void setUri( URI uri ) {
		this.uri = uri;
	}
	
	public DocType getDocType() {
		return docType;
	}
	
	public void setDocType( DocType docType ) {
		this.docType = docType;
	}
	
	public JPTElement getRoot() {
		return this.root;
	}
	
	public void setRoot( JPTElement root ) {
		this.root = root;
	}

	public void addNamespace( Namespace namespace ){
		this.namespaces.put( namespace.getPrefix(), namespace.getURI() ); 
	}
	
	public boolean isNamespaceToDeclare( Namespace namespace ){
		return ! namespace.getURI().startsWith( HTTP_WWW_W3_ORG );
	}
	
	public String getTemplateName() {
		return this.templateName;
	}

	public void setTemplateName( String templateName ) {
		this.templateName = templateName;
	}
	
	public String getNamespace( String prefix ){
		return this.namespaces.get( prefix );
	}
	
	public String getTalPrefix(){
		
		if ( this.talPrefix == null ){
			this.talPrefix = this.searchByPrefixByUri(
					TwoPhasesPageTemplate.TAL_NAMESPACE_URI );
		}
		
		return this.talPrefix;
	}
	
	public String getI18nPrefix(){
		
		if ( this.i18nPrefix == null ){
			this.i18nPrefix = this.searchByPrefixByUri(
					TwoPhasesPageTemplate.I18N_NAMESPACE_URI );
		}
		
		return this.i18nPrefix;
	}
	
	public String getMetalPrefix(){
		
		if ( this.metalPrefix == null ){
			this.metalPrefix = this.searchByPrefixByUri(
					TwoPhasesPageTemplate.METAL_NAMESPACE_URI );
		}
		
		return this.metalPrefix;
	}
	
	private String searchByPrefixByUri( String talNamespaceUri ) {
		
		for ( Map.Entry<String, String> mapEntry: this.namespaces.entrySet() ){
			String uri = mapEntry.getValue();
			
			if ( talNamespaceUri.equals( uri ) ){
				return mapEntry.getKey();
			}
		}
		
		return null;
	}
	
	public String toHtml() throws IOException, SAXException {
		
		Writer writer = new StringWriter();
		
		JPTXMLWriter xmlWriter = new JPTXMLWriter(
				writer, JPTOutputFormat.getDefault() );
		this.writeToXmlWriter( xmlWriter );
		xmlWriter.close();
		
		String result = writer.toString();
		writer.close();
		
		return result;
	}
	
	private void writeToXmlWriter( JPTXMLWriter xmlWriter ) throws IOException, SAXException {
		
		xmlWriter.startDocument();
		this.root.writeToXmlWriter( xmlWriter );
		xmlWriter.endDocument();
	}
}
