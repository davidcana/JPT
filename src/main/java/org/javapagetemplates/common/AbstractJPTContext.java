package org.javapagetemplates.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javapagetemplates.common.exceptions.EvaluationException;
import org.javapagetemplates.common.scripting.Evaluator;
import org.javapagetemplates.common.scripting.beanShell.BeanShellEvaluator;
import org.javapagetemplates.common.scripting.groovy.GroovyEvaluator;

/**
 * <p>
 *   Configuration options for JPT. Common options for any JPTContext.
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

abstract public class AbstractJPTContext {
	
	private Translator translator = new TranslatorImpl();
	private boolean scriptExpressionsOn = true;
	private boolean useHtmlReader = false;
	private boolean cacheOn = false;
	private boolean parseHTMLFragments = true;
    //private XMLWriter xmlWriter;
    //private Set<String> omitElementCloseSet;
	private Set<String> emptyTags;
	private Evaluator expressionEvaluator = BeanShellEvaluator.getInstance();
	private Map<String, Evaluator> evaluators;
	private char charToReplaceAmpersand = '_';

    
	public Evaluator getExpressionEvaluator() {
		return this.expressionEvaluator;
	}

	public void setExpressionEvaluator( Evaluator expressionEvaluator ) {
		this.expressionEvaluator = expressionEvaluator;
	}

	public boolean isParseHTMLFragments() {
		return this.parseHTMLFragments;
	}

	public void setParseHTMLFragments( boolean parseHTMLFragments ) {
		this.parseHTMLFragments = parseHTMLFragments;
	}

	public boolean isCacheOn() {
		return this.cacheOn;
	}

	public void setCacheOn( boolean cacheOn ) {
		this.cacheOn = cacheOn;
	}

	public boolean isUseHtmlReader() {
		return this.useHtmlReader;
	}

	public void setUseHtmlReader( boolean useHtmlReader ) {
		this.useHtmlReader = useHtmlReader;
	}

	public boolean isScriptExpressionsOn() {
		return this.scriptExpressionsOn;
	}

	public void setScriptExpressionsOn( boolean scriptExpressionsOn ) {
		this.scriptExpressionsOn = scriptExpressionsOn;
	}

	public Translator getTranslator() {
		return this.translator;
	}

	public void setTranslator( Translator translator ) {
		this.translator = translator;
	}


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
    
    public boolean isOmitElementCloseSet( String qualifiedName ){
    	return this.isEmptyTag( qualifiedName );
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

    public boolean isEmptyTag( String qualifiedName ){
        return this.getEmptyTags().contains(
        		qualifiedName.toLowerCase() );
    }
    
	public Set<String> getEmptyTags() {
		
		if ( this.emptyTags == null ){
			this.emptyTags = new HashSet<String>();
			this.loadEmptyTags( this.emptyTags );
		}
		
		return this.emptyTags;
	}
	
    protected void loadEmptyTags( Set<String> set ) {
        set.add( "area" );
        set.add( "base" );
        set.add( "br" );
        set.add( "col" );
        set.add( "hr" );
        set.add( "img" );
        set.add( "input" );
        set.add( "link" );
        set.add( "meta" );
        //set.add( "p" );
        set.add( "param" );
    }
    /*
    protected void loadEmptyTags(Set<String> set) {
        set.add("input");
        set.add("img");
        set.add("br");
    }*/
	public void setEmptyTags( Set<String> emptyTags ) {
		this.emptyTags = emptyTags;
	}

	
	/* Evaluators */
	
	private void initDefaultEvaluators() {
		this.evaluators = new HashMap<String, Evaluator>();
		this.evaluators.put( "bsh", BeanShellEvaluator.getInstance() );
		this.evaluators.put( "groovy", GroovyEvaluator.getInstance() );
	}
	
	public void initEvaluators() {
		this.evaluators = new HashMap<String, Evaluator>();
	}
	
	public void registerEvaluator( Evaluator evaluator, String extension ) {
		this.evaluators.put( extension, evaluator );
	}
	
	public void registerEvaluator( Evaluator evaluator, List<String> extensions ) {
		
		for ( String extension : extensions ){
			this.registerEvaluator( evaluator, extension );
		}
	}
	
	public Map<String, Evaluator> getEvaluators() {
		
		if ( this.evaluators == null ){
			this.initDefaultEvaluators();
		}
		
		return this.evaluators;
	}
	

	public void setEvaluators( Map<String, Evaluator> evaluators ) {
		this.evaluators = evaluators;
	}

    
	public Evaluator getEvaluator( String extension ){
		return this.getEvaluators().get( extension );
    }
    
	static private String getExtension( String path ){
	    return path.substring( 
	    		path.lastIndexOf( '.' ) + 1 );
    }
	
	
    public Evaluator resolveScriptEvaluator( String path ) throws EvaluationException {
    	
		String extension = getExtension( path );
		Evaluator result = this.getEvaluators().get( extension );
		
		if ( result == null ){
			throw new EvaluationException( "No evaluator found for scripts with extension '" + extension + "'" );
		}
		
		return result;
    }
    
    
    public char getCharToReplaceAmpersand() {
		return this.charToReplaceAmpersand;
	}

	public void setCharToReplaceAmpersand( char charToReplaceAmpersand ) {
		this.charToReplaceAmpersand = charToReplaceAmpersand;
	}

	public String restoreAmpersandsToScriptExpression(String scriptExpression){
    	return scriptExpression.replace( this.charToReplaceAmpersand, '&' );
    }
    
}
