package org.javapagetemplates.twoPhasesImpl;

import org.dom4j.io.OutputFormat;
import org.javapagetemplates.twoPhasesImpl.model.JPTDocument;

/**
 * <p>
 *   Encapsulates data about the output format such as encoding,
 *   docType, omit XML declaration...
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
public class JPTOutputFormat {

	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final JPTOutputFormat DEFAULT_INSTANCE = new JPTOutputFormat( null );
	
	private String encoding = DEFAULT_ENCODING;
	private boolean suppressDeclaration = true;
	private DocType docType;
	private boolean xmlMode = true;
	
	
	public JPTOutputFormat( JPTDocument jptDocument ){
		
		this.xmlMode = getXmlModeFromDocType( 
				jptDocument == null?
				null:
				jptDocument.getDocType() );
	}
	
	
	public String getEncoding() {
		return this.encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isSuppressDeclaration() {
		return this.suppressDeclaration;
	}

	public void setSuppressDeclaration( boolean suppressDeclaration ) {
		this.suppressDeclaration = suppressDeclaration;
	}

	public DocType getDocType() {
		return this.docType;
	}

	public void setDocType( DocType docType ) {
		this.docType = docType;
		this.xmlMode = getXmlModeFromDocType(docType);
	}

	static private boolean getXmlModeFromDocType( DocType docType ) {
		return docType == null?
			  true:
			  docType.isXml();
	}
	
	public void setXmlMode( boolean xmlMode ) {
		this.xmlMode = xmlMode;
	}

	public boolean isXmlMode() {
		return this.xmlMode;
	}

	public OutputFormat getOutputFormat(){
		
		OutputFormat result = new OutputFormat();
		
		result.setEncoding( this.encoding );
		//result.setOmitEncoding(true);
		result.setSuppressDeclaration( this.suppressDeclaration );
		//result.setExpandEmptyElements(expandEmptyElements)
		
		return result;
	}
	
	static public JPTOutputFormat getDefault(){
		return DEFAULT_INSTANCE;
	}
}
