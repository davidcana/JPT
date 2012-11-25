package org.javapagetemplates.twoPhasesImpl;

import java.io.Serializable;

import org.dom4j.Document;
import org.dom4j.DocumentType;

/**
 * <p>
 *   Encapsulates data about a HTML/HTML document type.
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
public class DocType implements Serializable {

	private static final long serialVersionUID = 504180209711114842L;
	
	static public final DocType html4Transitional = new DocType(
			"HTML", "-//W3C//DTD HTML 4.01 Transitional//EN", "http://www.w3.org/TR/html4/loose.dtd");
	static public final DocType html4Strict = new DocType(
			"HTML", "-//W3C//DTD HTML 4.01//EN", "http://www.w3.org/TR/html4/strict.dtd");
	static public final DocType html4Frameset = new DocType(
			"HTML", "-//W3C//DTD HTML 4.01 Frameset//EN", "http://www.w3.org/TR/html4/frameset.dtd");
	static public final DocType xhtmlTransitional = new DocType(
			"html", "-//W3C//DTD XHTML 1.0 Transitional//EN", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
	static public final DocType xhtmlStrict = new DocType(
			"html", "-//W3C//DTD XHTML 1.0 Strict//EN", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
	static public final DocType xhtmlFrameset = new DocType(
			"html", "-//W3C//DTD XHTML 1.0 Frameset//EN", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd");
	static public final DocType html5 = new DocType(
			"html", "", "");
	
	private static final String HTML = "HTML";
	private static final String W3C_DTD_XHTML = "-//W3C//DTD XHTML ";

	private String name;
	private String publicId;
	private String systemId;
	private boolean xml;
	
	
	public DocType(){}
	public DocType(String name, String publicId, String systemId){
		this.name = name;
		this.publicId = publicId;
		this.systemId = systemId;
		this.xml = getDefaultXml(this);
	}
	public DocType(String name, String publicId, String systemId, boolean xml){
		this.name = name;
		this.publicId = publicId;
		this.systemId = systemId;
		this.xml = xml;
	}
	
	static private boolean getDefaultXml(DocType docType){
		
		if (! docType.name.toUpperCase().equals(HTML)){
			return true;
		}
		
		if ( docType.publicId == null 
				|| docType.publicId.toUpperCase().startsWith( W3C_DTD_XHTML ) ){
			return true;
		}
		
		return false;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPublicId() {
		return publicId;
	}
	
	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}
	
	public String getSystemId() {
		return systemId;
	}
	
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	
	public boolean isXml(){
		return this.xml;
	}
	
	public void setXml(boolean xml) {
		this.xml = xml;
	}
	
	static public DocType generateDocTypeFromDom4jDocument(Document document){
		
		DocumentType documentType = document.getDocType();
		
		if (documentType == null){
			return null;
		}
		
		return new DocType(
				documentType.getName(),
				documentType.getPublicID(),
				documentType.getSystemID());
	}
	
}
