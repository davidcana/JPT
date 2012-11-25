package org.javapagetemplates.twoPhasesImpl;

import bsh.BshClassManager;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import org.javapagetemplates.common.TemplateError;
import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.common.exceptions.NoSuchPathException;
import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.common.helpers.DateHelper;
import org.javapagetemplates.twoPhasesImpl.HTMLFragment;
import org.javapagetemplates.twoPhasesImpl.JPTContext;
import org.javapagetemplates.twoPhasesImpl.model.JPTDocument;
import org.javapagetemplates.twoPhasesImpl.model.JPTElement;
import org.javapagetemplates.twoPhasesImpl.model.attributes.AttributesUtils;
import org.javapagetemplates.twoPhasesImpl.model.attributes.StaticAttribute;
import org.javapagetemplates.twoPhasesImpl.model.attributes.I18N.I18NAttributes;
import org.javapagetemplates.twoPhasesImpl.model.attributes.I18N.I18NContent;
import org.javapagetemplates.twoPhasesImpl.model.attributes.I18N.I18NDefine;
import org.javapagetemplates.twoPhasesImpl.model.attributes.I18N.I18NDomain;
import org.javapagetemplates.twoPhasesImpl.model.attributes.I18N.I18NOnError;
import org.javapagetemplates.twoPhasesImpl.model.attributes.I18N.I18NParams;
import org.javapagetemplates.twoPhasesImpl.model.attributes.METAL.METALDefineMacro;
import org.javapagetemplates.twoPhasesImpl.model.attributes.METAL.METALDefineSlot;
import org.javapagetemplates.twoPhasesImpl.model.attributes.METAL.METALFillSlot;
import org.javapagetemplates.twoPhasesImpl.model.attributes.METAL.METALUseMacro;
import org.javapagetemplates.twoPhasesImpl.model.attributes.TAL.TALAttributes;
import org.javapagetemplates.twoPhasesImpl.model.attributes.TAL.TALCondition;
import org.javapagetemplates.twoPhasesImpl.model.attributes.TAL.TALContent;
import org.javapagetemplates.twoPhasesImpl.model.attributes.TAL.TALDefine;
import org.javapagetemplates.twoPhasesImpl.model.attributes.TAL.TALOmitTag;
import org.javapagetemplates.twoPhasesImpl.model.attributes.TAL.TALOnError;
import org.javapagetemplates.twoPhasesImpl.model.attributes.TAL.TALRepeat;
import org.javapagetemplates.twoPhasesImpl.model.content.ContentItem;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;

/**
 * <p>
 *   Main class to implement JPT in two phases.
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
 * @version $Revision: 1.1 $
 */
public class TwoPhasesPageTemplateImpl implements TwoPhasesPageTemplate {
	
	private static final String STRING_TEMPLATE_URL_HOST = "";
	private static final String STRING_TEMPLATE_URL_PROTOCOL = "file";
	private static final String GLOBAL = "global";
	public static final String CDATA = "CDATA";
	public static final String VOID_STRING = "";
	public static final int MAXIMUM_NUMBER_OF_ATTRIBUTES = 20;
    public static final String I18N_DOMAIN_VAR_NAME = "i18nDomain";
    private static final String ON_ERROR_VAR_NAME = "on-error";
	
    private JPTDocument jptDocument;
    private URI uri;
    private Resolver userResolver = null;
    //private Interpreter beanShell;
    
	// Map of macros contained in this template
    private Map<String, Macro> macros = null;
    
    private static BshClassManager bshClassManager;
    
    
    public TwoPhasesPageTemplateImpl( URL url ) throws PageTemplateException {
        this( url, null );
    }
    
    public TwoPhasesPageTemplateImpl( URL url, Resolver resolver ) throws PageTemplateException {
    	
    	try {
			this.uri = new URI( url.toString() );
			this.jptDocument = JPTDocumentFactory.getInstance().getJPTDocument( url );
			this.userResolver = resolver == null? new DefaultResolver( this.uri ): resolver;
			
    	} catch (PageTemplateException e) {
    		throw (e);
    		
		} catch (URISyntaxException e) {
			throw new PageTemplateException(e);
		}
    }
    
    public TwoPhasesPageTemplateImpl( String string ) 
    		throws PageTemplateException {
        this( string, "", null );
    }
    
    public TwoPhasesPageTemplateImpl( String string, String templatePath ) 
    		throws PageTemplateException {
        this( string, templatePath, null );
    }
    
    public TwoPhasesPageTemplateImpl( String string, String templatePath, Resolver resolver ) 
    		throws PageTemplateException {
    	
    	try {
			URL url = new URL( 
					STRING_TEMPLATE_URL_PROTOCOL, 
					STRING_TEMPLATE_URL_HOST,
					templatePath + "(" + Integer.toString( string.hashCode() )+ ")" );
			this.jptDocument = JPTDocumentFactory.getInstance().getJPTDocument( url, string );
			this.userResolver = resolver == null? new DefaultResolver( null ): resolver;
			
    	} catch (PageTemplateException e) {
    		throw (e);
    		
		} catch (MalformedURLException e) {
			throw new PageTemplateException(e);
		}
    }

    @Override
    public Resolver getResolver() {
        return this.userResolver;
    }
    
    @Override
    public void setResolver( Resolver resolver ) {
        this.userResolver = resolver;
    }
    
    @Override
    public void process( OutputStream output, Object context )
        throws PageTemplateException {
        process( output, context, null );
    }
    
    @Override
	public void process( OutputStream output, Object context, Map<String, Object> dictionary)
        throws PageTemplateException {
		process( output, context, dictionary, null );
	}
	
	@Override
	public void process( OutputStream output, Object context, Map<String, Object> dictionary,
			JPTOutputFormat jptOutputFormat)
        throws PageTemplateException {
		
        try {
        	if (jptOutputFormat == null){
        		//jptOutputFormat = JPTOutputFormat.getDefault();
        		jptOutputFormat = new JPTOutputFormat( this.jptDocument );
        	}
        	
            // Initialize the bean shell and register in the context
        	Interpreter beanShell = this.getInterpreter(context, dictionary);
            //JPTContext.getInstance().registerInterPreter(beanShell);
            
            // Process
            JPTXMLWriter xmlWriter = new JPTXMLWriter(output, jptOutputFormat);
            xmlWriter.setEscapeText(
            		JPTContext.getInstance().isParseHTMLFragments());
    		xmlWriter.startDocument();
    		xmlWriter.writeDocType(this.jptDocument);
    		processJPTElement(
    				xmlWriter, 
    				this.jptDocument.getRoot(), 
    				beanShell, 
    				new Stack<Map<String, Slot>>());
    		xmlWriter.endDocument();
            
        } catch( PageTemplateException e ) {
            throw e;
            
        } catch( Exception e ) {
            throw new PageTemplateException(e);
        }
    }
    
	private Interpreter getInterpreter(Object context, Map<String, Object> dictionary)
    throws EvalError {
        
        Interpreter result = new Interpreter();
        NameSpace globalNameSpace = new NameSpace( getBshClassManager(), GLOBAL );
        result.setNameSpace(globalNameSpace);
        
        this.addVarsToBeanshell(context, dictionary, result);
        
        return result;
    }
	
    private BshClassManager getBshClassManager() {
    	
    	if (bshClassManager == null){
    		bshClassManager = BshClassManager.createClassManager(null);
    	}
    	
		return bshClassManager;
	}
    
	private void addVarsToBeanshell(Object context, Map<String, Object> dictionary, Interpreter beanShell) 
			throws EvalError {
        
        if ( dictionary != null ) {
            for ( Iterator<Map.Entry<String, Object>> i = dictionary.entrySet().iterator(); i.hasNext(); ) {
                Map.Entry<String, Object> entry = i.next();
                beanShell.set( (String)entry.getKey(), entry.getValue() );
            }
        }
        
        beanShell.set( HERE_VAR_NAME, context );
        beanShell.set( TEMPLATE_VAR_NAME, this );
        beanShell.set( RESOLVER_VAR_NAME, this.userResolver );
        //beanShell.set( BOOL_HELPER_VAR_NAME, new BoolHelper() );
        beanShell.set( DATE_HELPER_VAR_NAME, new DateHelper() );
    }
    
    public static void setVar(Interpreter beanshell, 
    		List<String> varsToUnset, Map<String, Object> varsToSet, String name, Object value) 
    		throws ExpressionEvaluationException {
        
        try {
			Object currentValue = beanshell.get(name);
			
			if (currentValue != null){
			    varsToSet.put(name, currentValue);
			    
			} else {
			    varsToUnset.add(name);
			}
			
			beanshell.set(name, value);
			
		} catch (EvalError e) {
			throw new ExpressionEvaluationException(e);
		}
    }
    /*
	@Override
	public Object evaluateVar(String varName) throws ExpressionEvaluationException {
		
		try {
			return this.beanShell.get( varName );
		} catch (EvalError e) {
			throw new ExpressionEvaluationException(e);
		}
	}
	
	@Override
	public TemplateError getError() throws ExpressionEvaluationException {
		return (TemplateError) this.evaluateVar(TEMPLATE_ERROR_VAR_NAME);
	}*/
	
    protected void processJPTElement( 
    		JPTXMLWriter xmlWriter,
    		JPTElement jptElement, 
            Interpreter beanShell, 
            Stack <Map<String, Slot>>slotStack )
        throws PageTemplateException, IOException, SAXException {
    	
		List<String> varsToUnset = new ArrayList<String>();
		Map<String, Object> varsToSet = new HashMap<String, Object>();
		
        try {
			// Process instructions
        	TALRepeat talRepeat = jptElement.getTALRepeat(this.jptDocument);
			if (talRepeat != null){
				
				// Process repeat
				Loop loop = new Loop( talRepeat, beanShell, varsToUnset, varsToSet );
				while( loop.repeat( beanShell ) ) {
				    if (!processJPTElement(
				    		xmlWriter, jptElement, beanShell, 
				    		slotStack, varsToUnset, varsToSet, true)){
			        	return;
			        }
				}
				
			} else {
				
				 // Process non repeat
			    if (!processJPTElement(
			    		xmlWriter, jptElement, beanShell, 
			    		slotStack, varsToUnset, varsToSet, false)){
			    	return;
			    }
			}
			
			processVars(beanShell, varsToUnset, varsToSet);
			
		} catch (PageTemplateException e) {
			
			if (!treatError(xmlWriter, jptElement, beanShell, varsToUnset, varsToSet, e)){
				throw (e);
			}
		}
    }

	private boolean treatError(
    		JPTXMLWriter xmlWriter,
    		JPTElement jptElement, 
    		Interpreter beanShell,
			List<String> varsToUnset, Map<String, Object> varsToSet, Exception exception) 
					throws PageTemplateException {
		
		// Exit if there is no on-error expression defined
		Object object = null;
		try {
			object = beanShell.get(ON_ERROR_VAR_NAME);
		} catch (EvalError e) {
			throw new ExpressionEvaluationException(e);
		}
		if (object == null){
			return false;
		}
		
		// Init vars
		I18NOnError i18nOnError = null; 
		I18NParams i18nParams = null;
		TALOnError talOnError = null;
		
		if (object instanceof TALOnError){
			talOnError = (TALOnError)object;
			
		} else if (object instanceof I18NOnError){
			i18nOnError = (I18NOnError)object;
			
		} else {
			throw new PageTemplateException("Invalid object of type '" + object.getClass().getCanonicalName() 
					+ "' found in '" + ON_ERROR_VAR_NAME + "' variable.");
		}

		// Set the error variable
		TemplateError templateError = new TemplateError(exception);
		setVar(beanShell, varsToUnset, varsToSet, TEMPLATE_ERROR_VAR_NAME, templateError);
		
		// Evaluate content
		Object jptContent = null;
    	try {
			if (i18nOnError != null){
				// i18n
				jptContent = i18nOnError.evaluate(beanShell, i18nParams);
				
			} else {
				// Non i18n
				jptContent = talOnError.evaluate(beanShell);
			}
			
		} catch (Exception e) {
			processVars(beanShell, varsToUnset, varsToSet);
			throw new PageTemplateException(e);
		}
    	
    	// Write content using xmlWriter
		writeJptContent( jptContent, xmlWriter );
		
		// Process vars
		processVars(beanShell, varsToUnset, varsToSet);

		return true;
	}

	private boolean processJPTElement(
    		JPTXMLWriter xmlWriter,
    		JPTElement jptElement, 
			Interpreter beanShell, 
			Stack <Map<String, Slot>>slotStack,
			List<String> varsToUnset,
			Map<String, Object> varsToSet,
			boolean inLoop) 
					throws PageTemplateException, IOException, SAXException {
		
		/* High priority elements */
		
		// tal:on-error or i18n:on-error
		processOnErrors(jptElement, beanShell, varsToUnset, varsToSet);
        
		// tal:define
		processDefine(jptElement, beanShell, varsToUnset, varsToSet);
        
        // i18n:domain
		processI18nDomain(jptElement, beanShell, varsToUnset, varsToSet);
        
        // i18n:define
        processI18nDefine(jptElement, beanShell, varsToUnset, varsToSet);
        
        // tal:condition
        if (! evaluateTalCondition(jptElement, beanShell)){
            // Skip this element (and children)
            return false;
        }
        
    
        /* Macro related elements */
        
		// metal:use-macro
        if (processMacro( jptElement, beanShell, slotStack, xmlWriter )){
        	return false;
        }

        // metal:fill-slot
        if (processDefineSlot( jptElement, beanShell, slotStack, xmlWriter )){
            return false;
        }
        
        
        /* Content elements and attributes */
		
		// tal:content (including tal:replace) or i18n:content
		Object jptContent = evaluateContent( jptElement, beanShell );
		
		// tal:attributes, i18n:attributes and static attributes
		AttributesImpl attributes = processAttributes( jptElement, beanShell );

		// tal:omit-tag
		boolean omitTag = evaluateOmitTag( jptElement, beanShell );
		
		
		/* Write to xmlWriter */
		
		// Void tag with no end element
		if (processEmptyElement( jptElement, jptContent, attributes, xmlWriter ) ){
			return true;
		}
		
		// Start element
		if ( ! omitTag ) {
			xmlWriter.startElement( 
					jptElement.getNamespace(), 
					jptElement.getName(), 
					jptElement.getQualifiedName(), 
					attributes );
		}
		
		// Content
		if ( jptContent != null ) {
		    writeJptContent( jptContent, xmlWriter );
		}
		else {
		    writeJptElementChildren( jptElement, xmlWriter, beanShell, varsToUnset, varsToSet, slotStack );
		}
   
		// End element
		if ( ! omitTag ) {
			xmlWriter.endElement( 
					jptElement.getNamespace(), 
					jptElement.getName(), 
					jptElement.getQualifiedName() );
		}
		
		// Write a new line char to separate elements in loop lists
		if ( inLoop ){
			xmlWriter.writeNewLine();
		}
		
		return true;
	}
	
	static private boolean processEmptyElement( JPTElement jptElement, Object jptContent, AttributesImpl attributes, 
			JPTXMLWriter xmlWriter ) throws IOException, SAXException {
		
		if (jptContent == null 
				&& jptElement.isEmpty() 
				&& JPTContext.getInstance().isOmitElementCloseSet( jptElement.getName() )){
			xmlWriter.writeEmptyElement(jptElement, attributes);
			return true;
		}
		
		return false;
	}

	private boolean processDefineSlot(
			JPTElement jptElement, 
			Interpreter beanShell,
            Stack <Map<String, Slot>> slotStack,
            JPTXMLWriter xmlWriter ) 
            		throws PageTemplateException, IOException, SAXException {
		
		METALDefineSlot metalDefineSlot = jptElement.getMETALDefineSlot(this.jptDocument);
		
		if (metalDefineSlot == null){
			return false;
		}
		
		if ( ! slotStack.isEmpty() ) {
		    Map<String, Slot> slots = slotStack.pop();
		    Slot slot = slots.get( metalDefineSlot.getValue() );
		    if ( slot != null ) {
		        slot.process( xmlWriter, jptElement, beanShell, slotStack );
		        slotStack.push( slots );
		        return true;
		    }
		    // else { use content in macro }
		    slotStack.push( slots );
		    
		    return true;
		}

		throw new PageTemplateException( "slot definition not allowed outside of macro" );
	}

	private void writeJptContent( Object jptContent, JPTXMLWriter xmlWriter )
			throws PageTemplateException {
		
		try {
			// Content for this element has been generated dynamically
			if (jptContent instanceof HTMLFragment ) {
				
				HTMLFragment html = (HTMLFragment)jptContent;
				
				if (JPTContext.getInstance().isParseHTMLFragments()){ 
					xmlWriter.writeHTML( html.getDom() );
					
				} else {
					xmlWriter.writeText( html.getHtml() );
				}
			} 
			
			// plain text
			else {
				xmlWriter.writeText( jptContent.toString() );
			}
			
		} catch (PageTemplateException e) {
			throw e;
			
		} catch (Exception e) {
			throw new PageTemplateException(e);
		}
	}

	private boolean evaluateTalCondition(
			JPTElement jptElement,
			Interpreter beanShell) throws PageTemplateException {

		TALCondition talCondition = jptElement.getTALCondition(this.jptDocument);
		
		if (talCondition == null){
			return true;
		}
		
		return talCondition.evaluate(beanShell);
	}
	
	private void processOnErrors(JPTElement jptElement,
			Interpreter beanShell, List<String> varsToUnset,
			Map<String, Object> varsToSet) throws PageTemplateException {

		TALOnError talOnError = jptElement.getTALOnError(this.jptDocument);
		I18NOnError i18nOnError = jptElement.getI18NOnError(this.jptDocument);
		
		if (talOnError == null && i18nOnError == null){
			return;
		}
		//if (talOnError != null && i18nOnError != null){
		//	throw new PageTemplateException("tal:on-error and i18n:on-error can not be at the same tag, "
		//			+ "please remove one of them.");
		//}

		/*
		if (talOnError != null){
			setVar(beanShell, varsToUnset, varsToSet, ON_ERROR_VAR_NAME, talOnError.getExpression());
			return;
		}
		
		setVar(beanShell, varsToUnset, varsToSet, ON_ERROR_VAR_NAME, I18N_EXPRESSION_PREFIX + i18nOnError.getI18nKey());
		*/
		setVar(
				beanShell, 
				varsToUnset, 
				varsToSet, 
				ON_ERROR_VAR_NAME,
				i18nOnError == null? talOnError: i18nOnError);
	}

	private boolean evaluateOmitTag( JPTElement jptElement, Interpreter beanShell )
			throws PageTemplateException {
		
		// Omit tag when it is from TAL name space
		if ( jptElement.getNamespace().equals( 
				this.jptDocument.getTalPrefix() ) ){
			return true;
		}
		
		// Not omit tag when there is no omitTag
		TALOmitTag talOmitTag = jptElement.getTALOmitTag(this.jptDocument);
		
		if ( talOmitTag == null ){
			return false;
		}
		
		// Omit tag depending on the value of omitTag attribute
		return talOmitTag.evaluate(beanShell);
	}
    
    static private void processVars(Interpreter beanShell, List<String> varsToUnset, Map<String, Object> varsToSet) 
    		throws ExpressionEvaluationException {

        try {
			Iterator<String> i = varsToUnset.iterator();
			
			while (i.hasNext()){
			    String varName = i.next();
			    beanShell.unset(varName);
			}
			
			i = varsToSet.keySet().iterator();
			
			while (i.hasNext()){
			    String name = i.next();
			    Object value = varsToSet.get(name);
			    beanShell.set(name, value);
			}
		} catch (EvalError e) {
			throw new ExpressionEvaluationException();
		}
    }

    private void writeJptElementChildren( 
    				JPTElement jptElement, 
    				JPTXMLWriter xmlWriter, 
                    Interpreter beanShell, 
                    List<String> varsToUnset,
                    Map<String, Object> varsToSet,
                    Stack <Map<String, Slot>> slotStack )
        throws SAXException, PageTemplateException, IOException {
    	
    	for ( ContentItem contentItem: jptElement.getContents() ){
    		
    		if ( contentItem instanceof JPTElement ){
    			
    			processJPTElement( 
    					xmlWriter,
    					(JPTElement) contentItem,
    					beanShell, 
    					slotStack);
    			
    		} else {
    			contentItem.writeToXmlWriter( xmlWriter );
    		}
        }
    }
    
    private Object evaluateContent( JPTElement jptElement, Interpreter beanShell )
    throws PageTemplateException {
    	
		I18NContent i18nContent = jptElement.getI18NContent(this.jptDocument);
		TALContent talContent = jptElement.getTALContent(this.jptDocument);
		
		if ( talContent == null && i18nContent == null ){
			return null;
		}
		
		// i18nContent
		if (i18nContent != null){
			return i18nContent.evaluate(
					beanShell, 
					jptElement.getI18NParams(this.jptDocument));
		}
		
		// talContent
		return talContent.evaluate(beanShell);
    }

    private void processI18nDomain( JPTElement jptElement, Interpreter beanShell, List<String> varsToUnset, Map<String, Object> varsToSet )
    throws PageTemplateException {
    	
    	I18NDomain i18nDomain = jptElement.getI18NDomain(this.jptDocument);
    	
    	if (i18nDomain == null){
    		return;
    	}
    	
    	i18nDomain.process(beanShell, varsToUnset, varsToSet);
    }
    
    private void processDefine( JPTElement jptElement, Interpreter beanShell, 
    		List<String> varsToUnset, Map<String, Object> varsToSet )
    throws PageTemplateException {

		TALDefine talDefine = jptElement.getTALDefine(this.jptDocument);
		
		if (talDefine == null){
			return;
		}

		talDefine.process(beanShell, varsToUnset, varsToSet);
    }
    
    private void processI18nDefine( JPTElement jptElement, Interpreter beanShell, List<String> varsToUnset, Map<String, Object> varsToSet )
    throws PageTemplateException {
    	
		I18NDefine i18nDefine = jptElement.getI18NDefine(this.jptDocument);
		
		if (i18nDefine == null){
			return;
		}
		
		i18nDefine.evaluate(
				beanShell, 
				jptElement.getI18NParams(this.jptDocument), 
				varsToUnset, 
				varsToSet);
    }
    
    private AttributesImpl processAttributes( JPTElement jptElement, Interpreter beanShell )
    throws PageTemplateException {
    	
    	AttributesImpl result = new AttributesImpl();
    	
    	I18NAttributes i18NAttributes = jptElement.getI18NAttributes(this.jptDocument);
    	TALAttributes talAttributes = jptElement.getTALAttributes(this.jptDocument);
    	List<StaticAttribute> staticAttributes = jptElement.getStaticAttributes();
    	
    	// Return if there is no attribute to add to attributes list
    	if (i18NAttributes == null && talAttributes == null && staticAttributes.isEmpty()){
    		return result;
    	}

    	if (!staticAttributes.isEmpty()){
    		AttributesUtils.processStaticAttributes(
    				staticAttributes, 
    				beanShell, 
    				result, 
    				this.jptDocument);
    	}

    	if (talAttributes != null){
    		talAttributes.evaluate(
    				beanShell, 
    				result, 
    				this.jptDocument);
    	}
    	
    	if (i18NAttributes != null){
    		i18NAttributes.evaluate(
    				beanShell, 
    				jptElement.getI18NParams(this.jptDocument), 
    				result, 
    				this.jptDocument);
    	}
    	
    	return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private boolean processMacro( JPTElement jptElement, 
    							Interpreter beanShell, 
                                Stack <Map<String, Slot>> slotStack,
                                JPTXMLWriter xmlWriter )
        throws PageTemplateException, IOException, SAXException {
    	
    	METALUseMacro metalUseMacro = jptElement.getMETALUseMacro( this.jptDocument );
    	
    	if (metalUseMacro == null){
    		return false;
    	}
    	
        JPTExpression expression = metalUseMacro.getExpression();
		Object object = expression.evaluate(beanShell);
		
        if ( object == null ) {
            throw new NoSuchPathException( "could not find macro: " 
            		+ expression.getStringExpression() 
                    + " in '" + this.jptDocument.getTemplateName() + "' template");
        }

        if ( object instanceof Macro ) {
            // Find slots to fill inside this macro call
            Map<String, Slot> slots = new HashMap<String, Slot>();
            findSlots( jptElement, slots );

            // Slots filled in later templates (processed earlier) 
            // Take precedence over slots filled in intermediate
            // templates.
            if ( ! slotStack.isEmpty() ) {
                Map laterSlots = slotStack.peek();
                slots.putAll( laterSlots );
            }
            slotStack.push( slots );
            //System.err.println( "found slots: " + slots.keySet() );
            
            // Call macro
            Macro macro = (Macro)object;
            macro.process( xmlWriter, jptElement, beanShell, slotStack );
            
            return true;
        }

        throw new PageTemplateException(
            		"expression '" + expression.getStringExpression()  + "' does not evaluate to macro: " 
                    + object.getClass().getName() 
                    + " in '" + this.jptDocument.getTemplateName() + "' template");

    }
    
    /**
     * With all of our namespace woes, getting an XPath expression
     * to work has proven futile, so we'll recurse through the tree
     * ourselves to find what we need.
     * @throws PageTemplateException 
     */
    private void findSlots( JPTElement jptElement, Map<String, Slot> slots ) 
    		throws PageTemplateException {
        
        // Look for our attribute
    	METALFillSlot metalFillSlot = jptElement.getMETALFillSlot(this.jptDocument);
    	
    	if (metalFillSlot != null){
    		String name = metalFillSlot.getName();
            if ( name != null ) {
                slots.put( name, new SlotImpl( jptElement ) );
            }
    	}

        // Recurse into child elements
    	for (JPTElement child: jptElement.getElementsContent()){
    		findSlots( child, slots );
    	}
    }
    
    @Override
    public String toLetter( int n ) {
        return Loop.formatLetter( n );
    }
    
    @Override
    public String toCapitalLetter( int n ) {
        return Loop.formatCapitalLetter( n );
    }
    
    @Override
    public String toRoman( int n ) {
        return Loop.formatRoman( n );
    }
    
    @Override
    public String toCapitalRoman( int n ) {
        return Loop.formatCapitalRoman( n );
    }
    
    @Override
	public Map<String, Macro> getMacros() throws PageTemplateException {
		
        if ( this.macros == null ) {
            this.macros = new HashMap<String, Macro>();
            findMacros( this.jptDocument.getRoot(), this.macros );
        }
        
        return this.macros;
    }
	
    /**
     * With all of our namespace woes, getting an XPath expression
     * to work has proven futile, so we'll recurse through the tree
     * ourselves to find what we need.
     */
	private void findMacros( JPTElement jptElement, Map<String, Macro> macros ) throws PageTemplateException{
    	
        // Look for our attribute
		METALDefineMacro metalDefineMacro = jptElement.getMETALDefineMacro( this.jptDocument );
        if ( metalDefineMacro != null ) {
            macros.put( 
            		metalDefineMacro.getName(), 
            		new MacroImpl( jptElement ) );
        }

        // Recurse into child elements
        for (JPTElement child: jptElement.getElementsContent()){
        	findMacros( child, macros);
        }
    }

    class MacroImpl implements Macro {
        JPTElement jptElement;
        
        MacroImpl( JPTElement jptElement ) {
            this.jptElement = jptElement;
        }

		@Override
		public void process(JPTXMLWriter xmlWriter, JPTElement jptElement,
				Interpreter beanShell, Stack<Map<String, Slot>> slotStack)
				throws PageTemplateException, IOException, SAXException {
			processJPTElement( xmlWriter, this.jptElement, beanShell, slotStack );
		}
    }

    class SlotImpl implements Slot {
    	JPTElement jptElement;

        SlotImpl( JPTElement jptElement ) {
            this.jptElement = jptElement;
        }

		@Override
		public void process(JPTXMLWriter xmlWriter, JPTElement jptElement,
				Interpreter beanShell, Stack<Map<String, Slot>> slotStack)
				throws PageTemplateException, IOException, SAXException {
			processJPTElement( xmlWriter, this.jptElement, beanShell, slotStack );
		}
    }
}