package org.zenonpagetemplates.twoPhasesImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.zenonpagetemplates.common.TemplateError;
import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.NoSuchPathException;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.common.helpers.DateHelper;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.HTMLFragment;
import org.zenonpagetemplates.twoPhasesImpl.ZPTContext;
import org.zenonpagetemplates.twoPhasesImpl.model.ZPTDocument;
import org.zenonpagetemplates.twoPhasesImpl.model.ZPTElement;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.AttributesUtils;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.StaticAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.I18N.I18NAttributes;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.I18N.I18NContent;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.I18N.I18NDefine;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.I18N.I18NDomain;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.I18N.I18NOnError;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.I18N.I18NParams;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.METAL.METALDefineMacro;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.METAL.METALDefineSlot;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.METAL.METALFillSlot;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.METAL.METALUseMacro;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.TAL.TALAttributes;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.TAL.TALCondition;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.TAL.TALContent;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.TAL.TALDefine;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.TAL.TALOmitTag;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.TAL.TALOnError;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.TAL.TALRepeat;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.TAL.TALTag;
import org.zenonpagetemplates.twoPhasesImpl.model.content.ContentItem;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpression;

/**
 * <p>
 *   Main class to implement ZPT in two phases.
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
 * @author <a href="mailto:chris@christophermrossi.com">Chris Rossi</a>
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.1 $
 */
public class TwoPhasesPageTemplateImpl implements TwoPhasesPageTemplate {
	
	private static final String STRING_TEMPLATE_URL_HOST = "";
	private static final String STRING_TEMPLATE_URL_PROTOCOL = "file";
	public static final String CDATA = "CDATA";
	public static final String VOID_STRING = "";
	public static final int MAXIMUM_NUMBER_OF_ATTRIBUTES = 20;
    public static final String I18N_DOMAIN_VAR_NAME = "i18nDomain";
    private static final String ON_ERROR_VAR_NAME = "on-error";
	
    private ZPTDocument zptDocument;
    private URI uri;
    private Resolver userResolver = null;
    
	// Map of macros contained in this template
    private Map<String, Macro> macros = null;
    
    
    public TwoPhasesPageTemplateImpl( URI uri ) throws PageTemplateException {
        this( uri, null );
    }
    
    public TwoPhasesPageTemplateImpl( URI uri, Resolver resolver ) throws PageTemplateException {
    	
    	try {
			//this.uri = new URI( url.toString() );
			this.uri = uri;
			this.zptDocument = ZPTDocumentFactory.getInstance().getZPTDocument( this.uri );
			this.userResolver = resolver == null? new DefaultResolver( this.uri ): resolver;
			
    	} catch ( PageTemplateException e ) {
    		throw ( e );
    		
		} catch ( Exception e ) {
			throw new PageTemplateException( e );
		}
    }
    
    public TwoPhasesPageTemplateImpl( String string ) throws PageTemplateException {
        this( string, "", null );
    }
    
    public TwoPhasesPageTemplateImpl( String string, String templatePath ) throws PageTemplateException {
        this( string, templatePath, null );
    }
    
    public TwoPhasesPageTemplateImpl( String string, String templatePath, Resolver resolver ) throws PageTemplateException {
    	
    	try {
    		/*
			URL url = new URL( 
					STRING_TEMPLATE_URL_PROTOCOL, 
					STRING_TEMPLATE_URL_HOST,
					templatePath + "(" + Integer.toString( string.hashCode() )+ ")" );*/
			this.uri = new URI( 
					STRING_TEMPLATE_URL_PROTOCOL + ":/"  
					+ STRING_TEMPLATE_URL_HOST + ""
					+ templatePath + "(" + Integer.toString( string.hashCode() )+ ")" );
			this.zptDocument = ZPTDocumentFactory.getInstance().getZPTDocument( this.uri, string );
			this.userResolver = resolver == null? new DefaultResolver( null ): resolver;
			
    	} catch ( PageTemplateException e ) {
    		throw ( e );
    		
		} catch (Exception e) {
			throw new PageTemplateException( e );
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
    public void process( OutputStream output, Object context ) throws PageTemplateException {
        process( output, context, null );
    }
    
    @Override
	public void process( OutputStream output, Object context, Map<String, Object> dictionary ) throws PageTemplateException {
		process( output, context, dictionary, null );
	}
	
	@Override
	public void process( OutputStream output, Object context, Map<String, Object> dictionary,
			ZPTOutputFormat zptOutputFormat ) throws PageTemplateException {
		
        try {
        	if ( zptOutputFormat == null ){
        		zptOutputFormat = new ZPTOutputFormat( this.zptDocument );
        	}
        	
            // Initialize the evaluationHelper and register in the context
        	EvaluationHelper evaluationHelper = this.getEvaluationHelper( context, dictionary );
        			
            // Process
            ZPTXMLWriter xmlWriter = new ZPTXMLWriter( output, zptOutputFormat );
            xmlWriter.setEscapeText(
            		ZPTContext.getInstance().isParseHTMLFragments() );
    		xmlWriter.startDocument();
    		xmlWriter.writeDocType( this.zptDocument );
    		processZPTElement(
    				xmlWriter, 
    				this.zptDocument.getRoot(), 
    				evaluationHelper, 
    				new Stack<Map<String, Slot>>() );
    		xmlWriter.endDocument();
            
        } catch ( PageTemplateException e ) {
            throw e;
            
        } catch ( Exception e ) {
            throw new PageTemplateException( e );
        }
    }
    
	
	private EvaluationHelper getEvaluationHelper( Object context, Map<String, Object> dictionary )
		    throws EvaluationException {
		
		EvaluationHelper result = ScriptFactory.getInstance().createEvaluationHelper();
        
        this.addVarsToEvaluationHelper( context, dictionary, result );
        
        return result;
	}
    
	
	private void addVarsToEvaluationHelper( Object context, Map<String, Object> dictionary, EvaluationHelper evaluationHelper ) 
			throws EvaluationException {
        
		// Add vars from dictionary
        if ( dictionary != null ) {
			for ( Entry<String, Object> entry : dictionary.entrySet() ){
				evaluationHelper.set ( 
						entry.getKey(), entry.getValue() );
			}
        }
        
        // Add more vars
        evaluationHelper.set( HERE_VAR_NAME, context );
        evaluationHelper.set( TEMPLATE_VAR_NAME, this );
        evaluationHelper.set( RESOLVER_VAR_NAME, this.userResolver );
        evaluationHelper.set( DATE_HELPER_VAR_NAME, new DateHelper() );
        evaluationHelper.set( SHELL_VAR_NAME, evaluationHelper );
    }
    
	
	public static void setVar( EvaluationHelper evaluationHelper, 
    		List<String> varsToUnset, Map<String, Object> varsToSet, String name, Object value ) 
    		throws EvaluationException {

		Object currentValue = evaluationHelper.get( name );
		
		if ( currentValue != null ){
		    varsToSet.put( name, currentValue );
		    
		} else {
		    varsToUnset.add( name );
		}
		
		evaluationHelper.set( name, value );
    }

	
    protected void processZPTElement( 
    		ZPTXMLWriter xmlWriter,
    		ZPTElement zptElement, 
    		EvaluationHelper evaluationHelper, 
            Stack <Map<String, Slot>>slotStack )
        throws PageTemplateException, IOException, SAXException {
    	
		List<String> varsToUnset = new ArrayList<String>();
		Map<String, Object> varsToSet = new HashMap<String, Object>();
		
        try {
			// Process instructions
        	TALRepeat talRepeat = zptElement.getTALRepeat( this.zptDocument );
			if ( talRepeat != null ){
				
				// Process repeat
				Loop loop = new Loop( talRepeat, evaluationHelper, varsToUnset, varsToSet );
				while ( loop.repeat( evaluationHelper ) ) {
				    if ( ! processZPTElement(
				    		xmlWriter, zptElement, evaluationHelper, 
				    		slotStack, varsToUnset, varsToSet, true ) ){
			        	return;
			        }
				}
				
			} else {
				
				 // Process non repeat
			    if ( ! processZPTElement(
			    		xmlWriter, zptElement, evaluationHelper, 
			    		slotStack, varsToUnset, varsToSet, false ) ){
			    	return;
			    }
			}
			
			processVars( evaluationHelper, varsToUnset, varsToSet );
			
		} catch ( PageTemplateException e ) {
			
			if ( ! treatError(
					xmlWriter, zptElement, evaluationHelper, 
					varsToUnset, varsToSet, e ) ){
				throw ( e );
			}
		}
    }

    
	private boolean treatError( ZPTXMLWriter xmlWriter, ZPTElement zptElement, EvaluationHelper evaluationHelper,
			List<String> varsToUnset, Map<String, Object> varsToSet, Exception exception ) throws PageTemplateException {
		
		// Exit if there is no on-error expression defined
		Object object = evaluationHelper.get( ON_ERROR_VAR_NAME );
		if ( object == null ){
			return false;
		}
		
		// Init vars
		I18NOnError i18nOnError = null; 
		I18NParams i18nParams = null;
		TALOnError talOnError = null;
		
		if ( object instanceof TALOnError ){
			talOnError = ( TALOnError ) object;
			
		} else if ( object instanceof I18NOnError ){
			i18nOnError = ( I18NOnError ) object;
			
		} else {
			throw new PageTemplateException( 
					"Invalid object of type '" + object.getClass().getCanonicalName() 
					+ "' found in '" + ON_ERROR_VAR_NAME + "' variable." );
		}

		// Set the error variable
		TemplateError templateError = new TemplateError( exception );
		setVar( evaluationHelper, varsToUnset, varsToSet, TEMPLATE_ERROR_VAR_NAME, templateError );
		
		// Evaluate content
		Object zptContent = null;
    	try {
			if ( i18nOnError != null ){
				// i18n
				zptContent = i18nOnError.evaluate( evaluationHelper, i18nParams );
				
			} else {
				// Non i18n
				zptContent = talOnError.evaluate( evaluationHelper );
			}
			
		} catch ( Exception e ) {
			processVars( evaluationHelper, varsToUnset, varsToSet );
			throw new PageTemplateException( e );
		}
    	
    	// Write content using xmlWriter
		writeZPTContent( zptContent, xmlWriter );
		
		// Process vars
		processVars( evaluationHelper, varsToUnset, varsToSet );

		return true;
	}

	
	private boolean processZPTElement(
    		ZPTXMLWriter xmlWriter,
    		ZPTElement zptElement, 
    		EvaluationHelper evaluationHelper, 
			Stack <Map<String, Slot>>slotStack,
			List<String> varsToUnset,
			Map<String, Object> varsToSet,
			boolean inLoop) 
					throws PageTemplateException, IOException, SAXException {
		
		/* High priority elements */
		
		// tal:on-error or i18n:on-error
		processOnErrors( zptElement, evaluationHelper, varsToUnset, varsToSet );
        
		// tal:define
		processDefine( zptElement, evaluationHelper, varsToUnset, varsToSet );
        
        // i18n:domain
		processI18nDomain( zptElement, evaluationHelper, varsToUnset, varsToSet );
        
        // i18n:define
        processI18nDefine( zptElement, evaluationHelper, varsToUnset, varsToSet );
        
        // tal:condition
        if ( ! evaluateTalCondition( zptElement, evaluationHelper ) ){
            // Skip this element (and children)
            return false;
        }
        
    
        /* Macro related elements */
        
		// metal:use-macro
        if ( processMacro( zptElement, evaluationHelper, slotStack, xmlWriter ) ){
        	return false;
        }

        // metal:fill-slot
        if ( processDefineSlot( zptElement, evaluationHelper, slotStack, xmlWriter ) ){
            return false;
        }
        
        
        /* Content elements and attributes */
		
		// tal:content (including tal:replace) or i18n:content
		Object zptContent = evaluateContent( zptElement, evaluationHelper );
		
		// tal:attributes, i18n:attributes and static attributes
		AttributesImpl attributes = processAttributes( zptElement, evaluationHelper );

		// tal:omit-tag
		boolean omitTag = evaluateOmitTag( zptElement, evaluationHelper );
		
		// tal:tag
		String configurableTag = omitTag? null: evaluateConfigurableTag( zptElement, evaluationHelper );
		
		/* Write to xmlWriter */
		
		// Void tag with no end element
		if ( processEmptyElement( zptElement, zptContent, attributes, xmlWriter ) ){
			return true;
		}
		
		// Start element
		if ( ! omitTag ) {
			startElement( xmlWriter, zptElement, attributes, configurableTag );
		}
		
		// Content
		if ( zptContent != null ) {
		    writeZPTContent( zptContent, xmlWriter );
		}
		else {
		    writeZPTElementChildren( zptElement, xmlWriter, evaluationHelper, varsToUnset, varsToSet, slotStack );
		}
   
		// End element
		if ( ! omitTag ) {
			endElement( xmlWriter, zptElement, configurableTag );
		}
		
		// Write a new line char to separate elements in loop lists
		if ( inLoop ){
			xmlWriter.writeNewLine();
		}
		
		return true;
	}
	

	static protected void startElement( ZPTXMLWriter xmlWriter, ZPTElement zptElement,
			AttributesImpl attributes, String configurableTag ) throws SAXException {
		
		if ( configurableTag != null ){
			
			xmlWriter.startElement( 
					"", 
					configurableTag, 
					configurableTag, 
					attributes );
			return;
		}
		
		xmlWriter.startElement( 
				zptElement.getNamespace(), 
				zptElement.getName(), 
				zptElement.getQualifiedName(), 
				attributes );
	}
	
	
	static protected void endElement( ZPTXMLWriter xmlWriter, ZPTElement zptElement, String configurableTag )
			throws SAXException {
		
		if ( configurableTag != null ){
			
			xmlWriter.endElement( 
					"", 
					configurableTag, 
					configurableTag );
			return;
		}
		
		xmlWriter.endElement( 
				zptElement.getNamespace(), 
				zptElement.getName(), 
				zptElement.getQualifiedName() );
	}

	
	static private boolean processEmptyElement( ZPTElement zptElement, Object zptContent, AttributesImpl attributes, 
			ZPTXMLWriter xmlWriter ) throws IOException, SAXException {
		
		if ( zptContent == null 
				&& zptElement.isEmpty() 
				&& ZPTContext.getInstance().isOmitElementCloseSet( zptElement.getName() )){
			xmlWriter.writeEmptyElement( zptElement, attributes );
			return true;
		}
		
		return false;
	}

	private boolean processDefineSlot(
			ZPTElement zptElement, 
			EvaluationHelper evaluationHelper,
            Stack <Map<String, Slot>> slotStack,
            ZPTXMLWriter xmlWriter ) 
            		throws PageTemplateException, IOException, SAXException {
		
		METALDefineSlot metalDefineSlot = zptElement.getMETALDefineSlot( this.zptDocument );
		
		if ( metalDefineSlot == null ){
			return false;
		}
		
		if ( ! slotStack.isEmpty() ) {
		    Map<String, Slot> slots = slotStack.pop();
		    Slot slot = slots.get( metalDefineSlot.getValue() );
		    if ( slot != null ) {
		        slot.process( xmlWriter, zptElement, evaluationHelper, slotStack );
		        slotStack.push( slots );
		        return true;
		    }
		    // else { use content in macro }
		    slotStack.push( slots );
		    
		    return true;
		}

		throw new PageTemplateException( "Slot definition not allowed outside of macro." );
	}

	static private void writeZPTContent( Object zptContent, ZPTXMLWriter xmlWriter )
			throws PageTemplateException {
		
		try {
			// Content for this element has been generated dynamically
			if ( zptContent instanceof HTMLFragment ) {
				
				HTMLFragment html = ( HTMLFragment ) zptContent;
				
				if ( ZPTContext.getInstance().isParseHTMLFragments() ){ 
					xmlWriter.writeHTML( html.getDOM() );
					
				} else {
					xmlWriter.writeText( html.getHTML() );
				}
			} 
			
			// plain text
			else {
				xmlWriter.writeText( zptContent.toString() );
			}
			
		} catch ( PageTemplateException e ) {
			throw e;
			
		} catch ( Exception e ) {
			throw new PageTemplateException( e );
		}
	}
	
	
	private String evaluateConfigurableTag(
			ZPTElement zptElement,
			EvaluationHelper evaluationHelper) throws PageTemplateException {

		TALTag talTag = zptElement.getTALTag( this.zptDocument );
		
		if ( talTag == null ){
			return null;
		}
		
		return talTag.evaluate( evaluationHelper );
	}
	
	
	private boolean evaluateTalCondition(
			ZPTElement zptElement,
			EvaluationHelper evaluationHelper) throws PageTemplateException {

		TALCondition talCondition = zptElement.getTALCondition( this.zptDocument );
		
		if ( talCondition == null ){
			return true;
		}
		
		return talCondition.evaluate( evaluationHelper );
	}
	
	private void processOnErrors(ZPTElement zptElement,
			EvaluationHelper evaluationHelper, List<String> varsToUnset,
			Map<String, Object> varsToSet) throws PageTemplateException {

		TALOnError talOnError = zptElement.getTALOnError( this.zptDocument );
		I18NOnError i18nOnError = zptElement.getI18NOnError( this.zptDocument );
		
		if ( talOnError == null && i18nOnError == null ){
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
				evaluationHelper, 
				varsToUnset, 
				varsToSet, 
				ON_ERROR_VAR_NAME,
				i18nOnError == null? talOnError: i18nOnError );
	}

	private boolean evaluateOmitTag( ZPTElement zptElement, EvaluationHelper evaluationHelper )
			throws PageTemplateException {
		
		// Omit tag when it is from TAL name space
		if ( zptElement.getNamespace().equals( 
				this.zptDocument.getTALPrefix() ) ){
			return true;
		}
		
		// Not omit tag when there is no omitTag
		TALOmitTag talOmitTag = zptElement.getTALOmitTag( this.zptDocument );
		
		if ( talOmitTag == null ){
			return false;
		}
		
		// Omit tag depending on the value of omitTag attribute
		return talOmitTag.evaluate( evaluationHelper );
	}
    
    static private void processVars( EvaluationHelper evaluationHelper, List<String> varsToUnset, Map<String, Object> varsToSet ) 
    		throws EvaluationException {

		for ( String name : varsToUnset ){
			evaluationHelper.unset( name );
		}
		
		for ( String name : varsToSet.keySet() ){
		    Object value = varsToSet.get( name );
		    evaluationHelper.set( name, value );
		}
    }

    private void writeZPTElementChildren( 
    				ZPTElement zptElement, 
    				ZPTXMLWriter xmlWriter, 
    				EvaluationHelper evaluationHelper, 
                    List<String> varsToUnset,
                    Map<String, Object> varsToSet,
                    Stack <Map<String, Slot>> slotStack )
        throws SAXException, PageTemplateException, IOException {
    	
    	for ( ContentItem contentItem: zptElement.getContents() ){
    		
    		if ( contentItem instanceof ZPTElement ){
    			
    			processZPTElement( 
    					xmlWriter,
    					( ZPTElement ) contentItem,
    					evaluationHelper, 
    					slotStack );
    			
    		} else {
    			contentItem.writeToXMLWriter( xmlWriter );
    		}
        }
    }
    
    private Object evaluateContent( ZPTElement zptElement, EvaluationHelper evaluationHelper )
    throws PageTemplateException {
    	
		I18NContent i18nContent = zptElement.getI18NContent( this.zptDocument );
		TALContent talContent = zptElement.getTALContent( this.zptDocument );
		
		if ( talContent == null && i18nContent == null ){
			return null;
		}
		
		// i18nContent
		if ( i18nContent != null ){
			return i18nContent.evaluate(
					evaluationHelper, 
					zptElement.getI18NParams( this.zptDocument ) );
		}
		
		// talContent
		return talContent.evaluate( evaluationHelper );
    }

    private void processI18nDomain( ZPTElement zptElement, EvaluationHelper evaluationHelper, 
    		List<String> varsToUnset, Map<String, Object> varsToSet ) throws PageTemplateException {
    	
    	I18NDomain i18nDomain = zptElement.getI18NDomain( this.zptDocument );
    	
    	if ( i18nDomain == null ){
    		return;
    	}
    	
    	i18nDomain.process( evaluationHelper, varsToUnset, varsToSet );
    }
    
    private void processDefine( ZPTElement zptElement, EvaluationHelper evaluationHelper, 
    		List<String> varsToUnset, Map<String, Object> varsToSet ) throws PageTemplateException {

		TALDefine talDefine = zptElement.getTALDefine( this.zptDocument );
		
		if ( talDefine == null ){
			return;
		}

		talDefine.process( evaluationHelper, varsToUnset, varsToSet );
    }
    
    private void processI18nDefine( ZPTElement zptElement, EvaluationHelper evaluationHelper, 
    		List<String> varsToUnset, Map<String, Object> varsToSet ) throws PageTemplateException {
    	
		I18NDefine i18nDefine = zptElement.getI18NDefine( this.zptDocument );
		
		if ( i18nDefine == null ){
			return;
		}
		
		i18nDefine.evaluate(
				evaluationHelper, 
				zptElement.getI18NParams( this.zptDocument ), 
				varsToUnset, 
				varsToSet);
    }
    
    private AttributesImpl processAttributes( ZPTElement zptElement, EvaluationHelper evaluationHelper )
    throws PageTemplateException {
    	
    	AttributesImpl result = new AttributesImpl();
    	
    	I18NAttributes i18NAttributes = zptElement.getI18NAttributes( this.zptDocument );
    	TALAttributes talAttributes = zptElement.getTALAttributes( this.zptDocument );
    	List<StaticAttribute> staticAttributes = zptElement.getStaticAttributes();
    	
    	// Return if there is no attribute to add to attributes list
    	if ( i18NAttributes == null && talAttributes == null && staticAttributes.isEmpty() ){
    		return result;
    	}

    	if ( ! staticAttributes.isEmpty() ){
    		AttributesUtils.processStaticAttributes(
    				staticAttributes, 
    				evaluationHelper, 
    				result, 
    				this.zptDocument );
    	}

    	if ( talAttributes != null ){
    		talAttributes.evaluate(
    				evaluationHelper, 
    				result, 
    				this.zptDocument );
    	}
    	
    	if ( i18NAttributes != null ){
    		i18NAttributes.evaluate(
    				evaluationHelper, 
    				zptElement.getI18NParams( this.zptDocument ), 
    				result, 
    				this.zptDocument );
    	}
    	
    	return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private boolean processMacro( ZPTElement zptElement, 
    							EvaluationHelper evaluationHelper, 
                                Stack <Map<String, Slot>> slotStack,
                                ZPTXMLWriter xmlWriter )
        throws PageTemplateException, IOException, SAXException {
    	
    	METALUseMacro metalUseMacro = zptElement.getMETALUseMacro( this.zptDocument );
    	
    	if ( metalUseMacro == null ){
    		return false;
    	}
    	
        ZPTExpression expression = metalUseMacro.getExpression();
		Object object = expression.evaluate( evaluationHelper );
		
        if ( object == null ) {
            throw new NoSuchPathException(
            		"Could not find macro: " 
            		+ expression.getStringExpression() 
                    + " in '" + this.zptDocument.getTemplateName() + "' template.") ;
        }

        if ( object instanceof Macro ) {
            // Find slots to fill inside this macro call
            Map<String, Slot> slots = new HashMap<String, Slot>();
            findSlots( zptElement, slots );

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
            Macro macro = ( Macro ) object;
            macro.process( xmlWriter, zptElement, evaluationHelper, slotStack );
            
            return true;
        }

        throw new PageTemplateException(
            		"Expression '" + expression.getStringExpression()  + "' does not evaluate to macro: " 
                    + object.getClass().getName() 
                    + " in '" + this.zptDocument.getTemplateName() + "' template.");
    }
    

    private void findSlots( ZPTElement zptElement, Map<String, Slot> slots ) 
    		throws PageTemplateException {
        
        // Look for our attribute
    	METALFillSlot metalFillSlot = zptElement.getMETALFillSlot( this.zptDocument );
    	
    	if ( metalFillSlot != null ){
    		String name = metalFillSlot.getName();
            if ( name != null ) {
                slots.put( name, new SlotImpl( zptElement ) );
            }
    	}

        // Recurse into child elements
    	for ( ZPTElement child : zptElement.getElementsContent() ){
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
            findMacros( this.zptDocument.getRoot(), this.macros );
        }
        
        return this.macros;
    }
	

	private void findMacros( ZPTElement zptElement, Map<String, Macro> macros ) throws PageTemplateException {
    	
        // Look for our attribute
		METALDefineMacro metalDefineMacro = zptElement.getMETALDefineMacro( this.zptDocument );
        if ( metalDefineMacro != null ) {
            macros.put( 
            		metalDefineMacro.getName(), 
            		new MacroImpl( zptElement ) );
        }

        // Recurse into child elements
        for ( ZPTElement child: zptElement.getElementsContent() ){
        	findMacros( child, macros );
        }
    }
	

    class MacroImpl implements Macro {
        ZPTElement zptElement;
        
        MacroImpl( ZPTElement zptElement ) {
            this.zptElement = zptElement;
        }

		@Override
		public void process( ZPTXMLWriter xmlWriter, ZPTElement zptElement,
				EvaluationHelper evaluationHelper, Stack<Map<String, Slot>> slotStack )
				throws PageTemplateException, IOException, SAXException {
			processZPTElement( xmlWriter, this.zptElement, evaluationHelper, slotStack );
		}
    }

    class SlotImpl implements Slot {
    	ZPTElement zptElement;

        SlotImpl( ZPTElement zptElement ) {
            this.zptElement = zptElement;
        }

		@Override
		public void process( ZPTXMLWriter xmlWriter, ZPTElement zptElement,
				EvaluationHelper evaluationHelper, Stack<Map<String, Slot>> slotStack )
				throws PageTemplateException, IOException, SAXException {
			processZPTElement( xmlWriter, this.zptElement, evaluationHelper, slotStack );
		}
    }
}