package org.javapagetemplates.common;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 *   Configuration options for JPT.
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
 * @version $Revision: 1.2 $
 */

public class AbstractJPTContext {
	
	//public static final String GLOBAL_NAMESPACE = "global";
	//private static final String ENCODING = "UTF-8";
	
	private Translator translator = new TranslatorImpl();
	//private Interpreter beanShell;
	private boolean javaExpressionsOn = true;
	private boolean useHtmlReader = false;
	private boolean cacheOn = true;
	private boolean parseHTMLFragments = true;
    //private BshClassManager bshClassManager;
    //private XMLWriter xmlWriter;
    //private Set<String> omitElementCloseSet;
	private Set<String> emptyTags;

    
	public boolean isParseHTMLFragments() {
		return this.parseHTMLFragments;
	}

	public void setParseHTMLFragments(boolean parseHTMLFragments) {
		this.parseHTMLFragments = parseHTMLFragments;
	}

	public boolean isCacheOn() {
		return this.cacheOn;
	}

	public void setCacheOn(boolean cacheOn) {
		this.cacheOn = cacheOn;
	}

	public boolean isUseHtmlReader() {
		return this.useHtmlReader;
	}

	public void setUseHtmlReader(boolean useHtmlReader) {
		this.useHtmlReader = useHtmlReader;
	}

	public boolean isJavaExpressionsOn() {
		return this.javaExpressionsOn;
	}

	public void setJavaExpressionsOn(boolean javaExpressionsOn) {
		this.javaExpressionsOn = javaExpressionsOn;
	}

	public Translator getTranslator() {
		return this.translator;
	}

	public void setTranslator(Translator translator) {
		this.translator = translator;
	}
	/*
	public void registerInterPreter(Interpreter beanShell) {
		this.beanShell = beanShell;
	}
	
	public Object getVar(String name) throws EvalError {
		return this.beanShell.get(name);
	}
	
	public TemplateError getError() {
		
		try {
			return (TemplateError) this.getVar(
					PageTemplate.TEMPLATE_ERROR_VAR_NAME);
			
		} catch (EvalError e) {
			return null;
		}
	}*/
	/*
	public BshClassManager getBshClassManager() {
		
    	if (this.bshClassManager == null){
    		this.bshClassManager = BshClassManager.createClassManager(null);
    	}
    	
		return this.bshClassManager;
	}*/
	/*
	public XMLWriter getXmlWriter() throws UnsupportedEncodingException {
		
		if (this.xmlWriter == null){
			OutputFormat outputFormat = new OutputFormat();
			outputFormat.setEncoding(ENCODING);
			
			this.xmlWriter = new XMLWriter( outputFormat );
		}
		
		return this.xmlWriter;
	}

	public void setXmlWriter(XMLWriter xmlWriter) {
		this.xmlWriter = xmlWriter;
	}*/
	/*
    public void setOmitElementCloseSet(Set<String> omitElementCloseSet) {
		this.omitElementCloseSet = omitElementCloseSet;
	}

    public Set<String> getOmitElementCloseSet() {
    	
        if (this.omitElementCloseSet == null) {
        	this.omitElementCloseSet = new HashSet<String>();
            loadOmitElementCloseSet(this.omitElementCloseSet);
        }

        return this.omitElementCloseSet;
    }*/
    
    public boolean isOmitElementCloseSet(String qualifiedName){
    	return this.isEmptyTag(qualifiedName);
        //return this.getOmitElementCloseSet().contains(
        //		qualifiedName.toLowerCase());
    }
    /*
    protected void loadOmitElementCloseSet(Set<String> set) {
        set.add("area");
        set.add("base");
        set.add("br");
        set.add("col");
        set.add("hr");
        set.add("img");
        set.add("input");
        set.add("link");
        set.add("meta");
        //set.add("p");
        set.add("param");
    }*/

    public boolean isEmptyTag(String qualifiedName){
        return this.getEmptyTags().contains(
        		qualifiedName.toLowerCase());
    }
    
	public Set<String> getEmptyTags() {
		
		if (this.emptyTags == null){
			this.emptyTags = new HashSet<String>();
			loadEmptyTags(this.emptyTags);
		}
		
		return this.emptyTags;
	}
	
    protected void loadEmptyTags(Set<String> set) {
        set.add("area");
        set.add("base");
        set.add("br");
        set.add("col");
        set.add("hr");
        set.add("img");
        set.add("input");
        set.add("link");
        set.add("meta");
        //set.add("p");
        set.add("param");
    }
    /*
    protected void loadEmptyTags(Set<String> set) {
        set.add("input");
        set.add("img");
        set.add("br");
    }*/
	public void setEmptyTags(Set<String> emptyTags) {
		this.emptyTags = emptyTags;
	}

}
