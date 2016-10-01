package org.zenonpagetemplates.onePhaseImpl;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.xni.parser.XMLDocumentFilter;

import org.cyberneko.html.parsers.SAXParser;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

import org.xnap.commons.i18n.I18n;
import org.zenonpagetemplates.common.ExpressionTokenizer;
import org.zenonpagetemplates.common.Filter;
import org.zenonpagetemplates.common.TemplateError;
import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.ExpressionSyntaxException;
import org.zenonpagetemplates.common.exceptions.NoSuchPathException;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.common.helpers.DateHelper;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.ScriptFactory;

/**
 * <p>
 *   Main class to implement ZPT in one phase.
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
 * @version $Revision: 1.16 $
 */
public class PageTemplateImpl implements OnePhasePageTemplate {
	
	private static final String CDATA = "CDATA";
	public static final String VOID_STRING = "";
	private static final String ENCODING = "UTF-8";
	private static final int MAXIMUM_NUMBER_OF_ATTRIBUTES = 20;
    private static final String I18N_DOMAIN_VAR_NAME = "i18nDomain";
    private static final String ON_ERROR_VAR_NAME = "on-error";
    public static final String TEMPLATE_ERROR_VAR_NAME = "error";
    private static final String I18N_EXPRESSION_PREFIX = "i18nExp: ";
    private URI uri;
    private Document template;
    private Resolver userResolver = null;
    private String id;
    private boolean recoveredFromCache = false;
    
	// Map of macros contained in this template
    Map<String, Macro> macros = null;

    
    private static SAXReader htmlReader = null;
    static final SAXReader getHTMLReader() throws Exception {
        if ( htmlReader == null && ZPTContext.getInstance().isUseHTMLReader()) {
            htmlReader = new SAXReader();
            SAXParser parser = new SAXParser();
            parser.setProperty( "http://cyberneko.org/html/properties/names/elems", "match" );
            parser.setProperty( "http://cyberneko.org/html/properties/names/attrs", "no-change" );
            //parser.setProperty( "http://cyberneko.org/html/properties/default-encoding", "ISO-8859-1" );
            
            parser.setProperty( "http://cyberneko.org/html/properties/default-encoding", ENCODING );
            //parser.setProperty( "http://cyberneko.org/html/features/balance-tags/document-fragment ", "true");
            //parser.setProperty( "http://cyberneko.org/html/features/balance-tags", "false" );
            //parser.setProperty( "http://apache.org/xml/features/scanner/notify-builtin-refs", "true");
            //parser.setProperty( "http://cyberneko.org/html/features/scanner/script/strip-comment-delims", "true"); 
            
            // Attach ZenonWriter to the parser as a filter
            XMLDocumentFilter[] filters = { new Filter() };
            parser.setProperty( "http://cyberneko.org/html/properties/filters", filters );
            
            htmlReader.setXMLReader( parser );
        }
        return htmlReader;
    }

    private static SAXReader xmlReader = null;
    static final SAXReader getXMLReader() throws Exception {
        if ( xmlReader == null ) {
            xmlReader = new SAXReader();
            xmlReader.setEncoding(ENCODING);
            xmlReader.setIgnoreComments(true);
            //xmlReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        }
        return xmlReader;
    }


    public PageTemplateImpl( InputStream input ) throws PageTemplateException {
        this( input, null );
    }

    public PageTemplateImpl( InputStream input, Resolver resolver ) throws PageTemplateException {
        try {
            this.uri = null;
            this.userResolver = resolver == null? new DefaultResolver(): resolver;
            
            SAXReader reader = getXMLReader();
            try {
                this.template = reader.read( input );
            } catch( DocumentException e ) {
                try {
                    reader = getHTMLReader();
                    if ( reader == null ){
                        throw ( e );
                    }
                    this.template = reader.read( input );
                } catch( NoClassDefFoundError ee ) {
                    // Allow user to omit nekohtml package
                    // to disable html parsing
                    //System.err.println( "Warning: no nekohtml" );
                    //ee.printStackTrace();
                    throw e;
                }
            }
        }
        catch ( Exception e ) {
            throw new PageTemplateException( e );
        }
    }

    public PageTemplateImpl( URL url ) throws PageTemplateException {
        try {
        	this.id = url.toString();
            this.uri = new URI( url.toString() );
            
            this.recoveredFromCache = this.recoverFromCache(); 
        	if ( this.recoveredFromCache ){
        		return;
        	}
            
            SAXReader reader = getXMLReader();
            try {
                this.template = reader.read( url );
            } catch( DocumentException e ) {
                try {
                    reader = getHTMLReader();
                    if ( reader == null ){
                        throw ( e );
                    }
                    this.template = reader.read( url );
                } catch( NoClassDefFoundError ee ) {
                    // Allow user to omit nekohtml package
                    // to disable html parsing
                    //System.err.println( "Warning: no nekohtml" );
                    //ee.printStackTrace();
                    throw e;
                }
            }
        }
        catch ( RuntimeException e ) {
            throw e;
        }
        catch ( Exception e ) {
            throw new PageTemplateException( e );
        }
    }

	public String getId() {
		return this.id;
	}

	public void setId( String id ) {
		this.id = id;
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
    
    
    static final SAXTransformerFactory factory = ( SAXTransformerFactory ) TransformerFactory.newInstance();
    @Override
	public void process( OutputStream output, Object context, Map<String, Object> dictionary )
        throws PageTemplateException {
    	
        try {
            TransformerHandler handler = factory.newTransformerHandler();
            Transformer transformer = handler.getTransformer();
            
            transformer.setOutputProperty( OutputKeys.ENCODING, ENCODING );
            //transformer.setOutputProperty(OutputKeys.METHOD, "html");
            transformer.setOutputProperty( OutputKeys.METHOD, "xml" );
            transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
            transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
            
            //OutputStreamWriter writer = new OutputStreamWriter(output, ENCODING);
            //handler.setResult(new StreamResult(writer));
            handler.setResult( 
            		new StreamResult( output ) );
            //handler.processingInstruction(Result.PI_DISABLE_OUTPUT_ESCAPING, "");

            process( handler, handler, context, dictionary );
            
        } catch( PageTemplateException e ) {
            throw e;
            
        } catch( Exception e ) {
            throw new PageTemplateException( e );
        }
    }

    @Override
	public void process( ContentHandler contentHandler, 
                         LexicalHandler lexicalHandler, 
                         Object context, 
                         Map<String, Object> dictionary )
        throws PageTemplateException {
    	
        try {
            // Initialize the bean shell and register in the context
        	EvaluationHelper evaluationHelper = this.getEvaluationHelper( context, dictionary );
            
            // Process
            Element root = this.template.getRootElement();
            contentHandler.startDocument();
            processElement( root, contentHandler, lexicalHandler, evaluationHelper, new Stack<Map<String, Slot>>() );
            contentHandler.endDocument();
            
            this.saveToCache();
            
        } catch( PageTemplateException e ) {
            throw e;
        } catch( Exception e ) {
            throw new PageTemplateException( e );
        }
    }
	
    
    private EvaluationHelper getEvaluationHelper( Object context, Map<String, Object> dictionary )
		    throws EvaluationException {
		
		EvaluationHelper result = ScriptFactory.getInstance().createEvaluationHelper();
        
        this.addVarsToEvaluationHelper( context, dictionary, result );
        
        return result;
	}
	
    
    private void saveToCache() throws IOException {

    	try {
    		if ( ! ZPTContext.getInstance().isCacheOn()
    				|| this.id == null 
    				|| this.recoveredFromCache 
    				|| ZPTContext.getInstance().getTemplateCache() == null ){
    			return;
    		}
    		
			ZPTContext.getInstance().getTemplateCache().put( this.id, this.template );
			
		} catch ( Exception e ) {
			throw new IOException( e );
		}
	}
    
    
    private boolean recoverFromCache() throws IOException {
    	
    	try {
            if ( ! ZPTContext.getInstance().isCacheOn() 
            		|| this.id == null 
            		|| ZPTContext.getInstance().getTemplateCache() == null ){
            	return false;
            }
            
			this.template = ZPTContext.getInstance().getTemplateCache().get( this.id );
			
			return ! ( this.template == null );
			
		} catch ( Exception e ) {
			throw new IOException( e );
		}
	}


	private void addVarsToEvaluationHelper(Object context, Map<String, Object> dictionary, EvaluationHelper evaluationHelper) 
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
        evaluationHelper.set( RESOLVER_VAR_NAME, new DefaultResolver() );
        evaluationHelper.set( DATE_HELPER_VAR_NAME, new DateHelper() );
        evaluationHelper.set( SHELL_VAR_NAME, evaluationHelper );
    }

    
	private Map<String, String> namespaces = new TreeMap<String, String>();
    private String getNamespaceURIFromPrefix( String prefix ) {
        String uri = ( String ) this.namespaces.get( prefix );
        return uri == null ? VOID_STRING : uri;
    }

	public static void setVar(EvaluationHelper evaluationHelper, 
    		List<String> varsToUnset, Map<String, Object> varsToSet, String name, Object value) 
    		throws EvaluationException {

		Object currentValue = evaluationHelper.get( name );
		
		if ( currentValue != null ){
		    varsToSet.put( name, currentValue );
		    
		} else {
		    varsToUnset.add( name );
		}
		
		evaluationHelper.set( name, value );
    }

	
    protected void processElement( Element element, 
                                 ContentHandler contentHandler, 
                                 LexicalHandler lexicalHandler, 
                                 EvaluationHelper evaluationHelper, 
                                 Stack <Map<String, Slot>>slotStack )
        throws SAXException, PageTemplateException, IOException, EvaluationException {
    	
		Expressions expressions = new Expressions();
		AttributesImpl attributes = getAttributes( element, expressions );
		
		List<String> varsToUnset = new ArrayList<String>();
		Map<String, Object> varsToSet = new HashMap<String, Object>();
		
        try {
			// Process instructions
			if (expressions.repeat != null){
				
				// Process repeat
				Loop loop = new Loop( expressions.repeat, evaluationHelper, varsToUnset, varsToSet );
				while ( loop.repeat( evaluationHelper ) ) {
			        if ( ! processElement(
			        		element, contentHandler,
			    			lexicalHandler, evaluationHelper, slotStack, expressions, attributes, varsToUnset, varsToSet ) ){
			        	return;
			        }
				}
				
			} else {
				
				 // Process non repeat
			    if ( ! processElement( 
			    		element, contentHandler,
						lexicalHandler, evaluationHelper, slotStack, expressions, attributes, varsToUnset, varsToSet ) ){
			    	return;
			    }
			}
			
			processVars( evaluationHelper, varsToUnset, varsToSet );
			
		} catch ( PageTemplateException e ) {
			
			if ( ! treatError(
					element, contentHandler,
					lexicalHandler, evaluationHelper, varsToUnset, varsToSet, e)){
				throw ( e );
			}
		}
    }

	private boolean treatError(Element element, ContentHandler contentHandler,
			LexicalHandler lexicalHandler, EvaluationHelper evaluationHelper,
			List<String> varsToUnset, Map<String, Object> varsToSet, Exception exception) 
					throws EvaluationException, PageTemplateException, SAXException {
		
		// Exit if there is no on-error expression defined
		String onErrorExpression = ( String ) evaluationHelper.get( ON_ERROR_VAR_NAME );
		if ( onErrorExpression == null ){
			return false;
		}
		
		String content = null;
		String i18nContent = null;
		String i18nParams = null;

		if ( onErrorExpression.startsWith( I18N_EXPRESSION_PREFIX ) ){
			i18nContent = onErrorExpression.substring( I18N_EXPRESSION_PREFIX.length() );
		} else {
			content = onErrorExpression;
		}
		
		// Set the error variable
		TemplateError templateError = new TemplateError( exception );
		setVar( evaluationHelper, varsToUnset, varsToSet, TEMPLATE_ERROR_VAR_NAME, templateError );
		
		try {
			// Process content
			zptContent(
					contentHandler, 
					lexicalHandler, 
					processContent( content, evaluationHelper, i18nContent, i18nParams ) );
			
		} catch ( Exception e ) {
			processVars( evaluationHelper, varsToUnset, varsToSet );
			throw new PageTemplateException( e );
		}
		
		processVars( evaluationHelper, varsToUnset, varsToSet );

		return true;
	}

	private boolean processElement(Element element, ContentHandler contentHandler,
			LexicalHandler lexicalHandler, EvaluationHelper evaluationHelper, 
			Stack <Map<String, Slot>>slotStack,
			Expressions expressions, AttributesImpl attributes, List<String> varsToUnset,
			Map<String, Object> varsToSet) throws PageTemplateException, SAXException, IOException, EvaluationException {
		
		String configurableTag = null;
		
		/* Normal elements */
		
		// on-error
        if ( expressions.onError != null || expressions.i18nOnError != null) {
            processOnErrors( expressions.onError, expressions.i18nOnError, evaluationHelper, varsToUnset, varsToSet );
        }
        
		// define
        if ( expressions.define != null ) {
            processDefine( expressions.define, evaluationHelper, varsToUnset, varsToSet );
        }
        
        // i18nDomain
        if ( expressions.i18nDomain != null ) {
            processI18nDomain( expressions.i18nDomain, evaluationHelper, varsToUnset, varsToSet );
        }
        
        // i18n:define
        if ( expressions.i18nDefine != null ) {
            processI18nDefine( expressions.i18nDefine, expressions.i18nParams, evaluationHelper, varsToUnset, varsToSet );
        }
        
        // condition
        if ( expressions.condition != null &&
             ! Expression.evaluateBoolean( expressions.condition, evaluationHelper ) ) {
            // Skip this element (and children)
            return false;
        }
        
        // tag
        if ( expressions.tag != null ) {
            configurableTag = (String) Expression.evaluate( expressions.tag, evaluationHelper );
        }
    
        /* Macro related elements */
        
		// use macro
        if ( expressions.useMacro != null ) {
            processMacro( expressions.useMacro, element, contentHandler, lexicalHandler, evaluationHelper, slotStack );
            return false;
        }

        // fill slot
        if ( expressions.defineSlot != null && 
        	! processDefineSlot( contentHandler, lexicalHandler, evaluationHelper, slotStack, expressions ) ){
            return false;
        }
        
        
        /* Content elements */
		
		// content or replace
		Object zptContent = null;
		if ( expressions.content != null || expressions.i18nContent != null ) {
		    zptContent = processContent( expressions.content, evaluationHelper, expressions.i18nContent, expressions.i18nParams );
		}
		
		// attributes
		if ( expressions.attributes != null || expressions.i18nAttributes != null) {
		    processAttributes( attributes, expressions.attributes, expressions.i18nParams, evaluationHelper,  expressions.i18nAttributes);
		}
		
		// omit-tag
		boolean zptOmitTag = getZPTOmitTag( evaluationHelper, expressions, element );
		
		// Declare element
		if ( ! zptOmitTag ) {
		    startElement( element, contentHandler, attributes, configurableTag );
		}
		
		// Content
		if ( zptContent != null ) {
		    zptContent( contentHandler, lexicalHandler, zptContent );
		}
		else {
		    defaultContent( element, contentHandler, lexicalHandler, evaluationHelper, slotStack );
		}
   
		// End element
		if ( ! zptOmitTag ) {
		    endElement( element, contentHandler, configurableTag );
		}
		
		return true;
	}


	protected void startElement(Element element, ContentHandler contentHandler,
			AttributesImpl attributes, String configurableTag) throws SAXException {
		
		if ( configurableTag != null ){
			contentHandler.startElement( 
					VOID_STRING, 
					configurableTag, 
					configurableTag, 
					attributes );
			return;
		}
		
		contentHandler.startElement( 
				VOID_STRING, 
				element.getName(), 
				element.getQualifiedName(), 
				attributes );
	}
	
	
	protected void endElement(Element element, ContentHandler contentHandler, String configurableTag)
			throws SAXException {
		
		if ( configurableTag != null ){
			contentHandler.endElement( 
					VOID_STRING, 
					configurableTag, 
					configurableTag );
			return;
		}
		
		contentHandler.endElement( 
				VOID_STRING, 
				element.getName(), 
				element.getQualifiedName() );
	}


	private boolean  processDefineSlot(ContentHandler contentHandler,
			LexicalHandler lexicalHandler, EvaluationHelper evaluationHelper,
			Stack <Map<String, Slot>>slotStack, Expressions expressions) throws SAXException,
			PageTemplateException, IOException, EvaluationException {
		
		if ( ! slotStack.isEmpty() ) {
		    Map<String, Slot> slots = slotStack.pop();
		    Slot slot = slots.get( expressions.defineSlot );
		    if ( slot != null ) {
		        slot.process( contentHandler, lexicalHandler, evaluationHelper, slotStack );
		        slotStack.push( slots );
		        return false;
		    }
		    // else { use content in macro }
		    slotStack.push( slots );
		    
		    return true;
		}
		else {
		    throw new PageTemplateException( "Slot definition not allowed outside of macro" );
		}
	}

	private void zptContent(ContentHandler contentHandler,
			LexicalHandler lexicalHandler, Object zptContent)
			throws PageTemplateException, SAXException {
		
		// Content for this element has been generated dynamically
		if ( zptContent instanceof HTMLFragment ) {
			
			HTMLFragment html = ( HTMLFragment ) zptContent;
			
			if ( ZPTContext.getInstance().isParseHTMLFragments() ){ 
				html.toXhtml( contentHandler, lexicalHandler );
			} else {
				char[] text = html.getHTML().toCharArray();
				contentHandler.characters( text, 0, text.length );
			}
		} 
		
		// plain text
		else {
		    char[] text = ( ( String ) zptContent ).toCharArray();
		    contentHandler.characters( text, 0, text.length );
		}
	}

	private void processOnErrors(String onError, String i18nOnError,
			EvaluationHelper evaluationHelper, List<String> varsToUnset,
			Map<String, Object> varsToSet) throws EvaluationException, PageTemplateException {

		if ( onError != null && i18nOnError != null ){
			throw new PageTemplateException( "tal:on-error and i18n:on-error can not be at the same tag, "
					+ "please remove one of them." );
		}
		
		if ( onError != null ){
			setVar( evaluationHelper, varsToUnset, varsToSet, ON_ERROR_VAR_NAME, onError );
			return;
		}
		
		setVar( evaluationHelper, varsToUnset, varsToSet, ON_ERROR_VAR_NAME, I18N_EXPRESSION_PREFIX + i18nOnError );
	}

	static private boolean getZPTOmitTag(EvaluationHelper evaluationHelper, Expressions expressions, Element element)
			throws PageTemplateException {
		
		boolean zptOmitTag = false;
		
		// Omit tag when it is from TAL name space
		if ( TAL_NAMESPACE_URI.equals( element.getNamespace().getURI() ) ) {
			return true;
		}
		
		// Omit tag depending on the value of omitTag attribute
		if ( expressions.omitTag != null ) {
		    if ( expressions.omitTag.equals( VOID_STRING ) ) {
		        zptOmitTag = true;
		    }
		    else {
		        zptOmitTag = Expression.evaluateBoolean( expressions.omitTag, evaluationHelper );
		    }
		}
		return zptOmitTag;
	}
    
    static private void processVars(EvaluationHelper evaluationHelper, List<String> varsToUnset, Map<String, Object> varsToSet) 
    		throws EvaluationException {

        Iterator<String> i = varsToUnset.iterator();
        
        while ( i.hasNext() ){
            String varName = i.next();
            evaluationHelper.unset( varName );
        }
        
        i = varsToSet.keySet().iterator();
        
        while ( i.hasNext() ){
            String name = i.next();
            Object value = varsToSet.get( name );
            evaluationHelper.set( name, value );
        }
    }

    @SuppressWarnings({ "unchecked" })
    private void defaultContent( Element element, 
                                 ContentHandler contentHandler, 
                                 LexicalHandler lexicalHandler, 
                                 EvaluationHelper evaluationHelper, 
                                 Stack <Map<String, Slot>> slotStack )
        throws SAXException, PageTemplateException, IOException, EvaluationException
    {   
        // Use default template content
        for ( Iterator<Node> i = element.nodeIterator(); i.hasNext(); ) {
            Node node = i.next();
            switch( node.getNodeType() ) {
            case Node.ELEMENT_NODE:
                processElement( (Element)node, contentHandler, lexicalHandler, evaluationHelper, slotStack );
                break;
                
            case Node.TEXT_NODE:
                char[] text = node.getText().toCharArray();
                contentHandler.characters( text, 0, text.length );
                break;
                
            case Node.COMMENT_NODE:
                char[] comment = node.getText().toCharArray();
                lexicalHandler.comment( comment, 0, comment.length );
                break;
                
            case Node.CDATA_SECTION_NODE:
                lexicalHandler.startCDATA();
                char[] cdata = node.getText().toCharArray();
                contentHandler.characters( cdata, 0, cdata.length );
                lexicalHandler.endCDATA();
                break;
                
            case Node.NAMESPACE_NODE:
                Namespace declared = ( Namespace ) node;
                //System.err.println( "Declared namespace: " + declared.getPrefix() + ":" + declared.getURI() );
                this.namespaces.put( declared.getPrefix(), declared.getURI() );
                //if ( declared.getURI().equals( TAL_NAMESPACE_URI ) ) {
                //    this.talNamespacePrefix = declared.getPrefix();
                //} 
                //else if (declared.getURI().equals( METAL_NAMESPACE_URI ) ) {
                //    this.metalNamespacePrefix = declared.getPrefix();
                //}
                break;
                
            case Node.ATTRIBUTE_NODE:
                // Already handled
                break;
                
            case Node.DOCUMENT_TYPE_NODE:
            case Node.ENTITY_REFERENCE_NODE:
            case Node.PROCESSING_INSTRUCTION_NODE:
            default:
                //System.err.println( "WARNING: Node type not supported: " + node.getNodeTypeName() );       
            }
        }
    }

	@SuppressWarnings("unchecked")
	AttributesImpl getAttributes( Element element, Expressions expressions ) 
        throws PageTemplateException {
		
        AttributesImpl attributes = new AttributesImpl();
        for ( Iterator<Attribute> i = element.attributeIterator(); i.hasNext(); ) {
            Attribute attribute = i.next();
            Namespace namespace = attribute.getNamespace();
            String name = attribute.getName();
            
            // Handle ZPT attributes
            if ( TAL_NAMESPACE_URI.equals( namespace.getURI() ) ) {
            	
                // tal:define
                if ( name.equals( TAL_DEFINE ) ) {
                    expressions.define = attribute.getValue();
                }

                // tal:condition
                else if ( name.equals( TAL_CONDITION ) ) {
                    expressions.condition = attribute.getValue();
                }

                // tal:repeat
                else if ( name.equals( TAL_REPEAT ) ) {
                    expressions.repeat = attribute.getValue();
                }

                // tal:content
                else if ( name.equals( TAL_CONTENT ) ) {
                    expressions.content = attribute.getValue();
                }

                // tal:replace
                else if ( name.equals( TAL_REPLACE ) ) {
                    if ( expressions.omitTag == null ) {
                        expressions.omitTag = VOID_STRING;
                    }
                    expressions.content = attribute.getValue();
                }

                // tal:attributes
                else if ( name.equals( TAL_ATTRIBUTES ) ) {
                    expressions.attributes = attribute.getValue();
                }

                // tal:omit-tag
                else if ( name.equals( TAL_OMIT_TAG ) ) {
                    expressions.omitTag = attribute.getValue();
                }

                // tal:on-error
                else if ( name.equals( TAL_ON_ERROR ) ) {
                    expressions.onError = attribute.getValue();
                }
                
                // tal:tag
                else if ( name.equals( TAL_TAG ) ) {
                    expressions.tag = attribute.getValue();
                }
                
                // error
                else {
                    throw new PageTemplateException( "Unknown tal attribute: " + name 
                            + " in '" + this.template.getName() + "' template");
                }
            }
            else if ( METAL_NAMESPACE_URI.equals( namespace.getURI() ) ) {
            	
                // metal:use-macro
                if ( name.equals( METAL_USE_MACRO ) ) {
                    expressions.useMacro = attribute.getValue();
                }
                
                // metal:define-slot
                else if ( name.equals( METAL_DEFINE_SLOT ) ) {
                    expressions.defineSlot = attribute.getValue();
                }

                // metal:define-macro
                // metal:fill-slot
                else if ( name.equals( METAL_DEFINE_MACRO ) ||
                          name.equals( METAL_FILL_SLOT ) ) {
                    // these are ignored here, as they don't affect processing of current
                    // template, but are called from other templates
                }

                // error
                else {
                    throw new PageTemplateException( "Unknown metal attribute: " + name 
                            + " in '" + this.template.getName() + "' template");
                }
            }
            
            else if ( I18N_NAMESPACE_URI.equals( namespace.getURI() ) ) {
            	
                // i18n:domain
                if ( name.equals( I18N_DOMAIN ) ) {
                    expressions.i18nDomain = attribute.getValue();
                }
                
                // i18n:define
                else if ( name.equals( I18N_DEFINE ) ) {
                    expressions.i18nDefine = attribute.getValue();
                }
                
                // i18n:content
                else if ( name.equals( I18N_CONTENT ) ) {
                    expressions.i18nContent = attribute.getValue();
                }
                
                // i18n:replace
                else if ( name.equals( I18N_REPLACE ) ) {
                    if ( expressions.omitTag == null ) {
                        expressions.omitTag = VOID_STRING;
                    }
                    expressions.i18nContent = attribute.getValue();
                }
                
                // i18n:attributes
                else if ( name.equals( I18N_ATTRIBUTES ) ) {
                    expressions.i18nAttributes = attribute.getValue();
                }
                
                // i18n:params
                else if ( name.equals( I18N_PARAMS ) ) {
                    expressions.i18nParams = attribute.getValue();
                }
                
                // i18n:on-error
                else if ( name.equals( I18N_ON_ERROR ) ) {
                    expressions.i18nOnError = attribute.getValue();
                }
                
                // error
                else {
                    throw new PageTemplateException( "Unknown i18n attribute: " + name 
                            + " in '" + this.template.getName() + "' template");
                }

            }
            
            // Pass on all other attributes
            else {
                attributes.addAttribute( namespace.getURI(), name, attribute.getQualifiedName(), 
                                         CDATA, attribute.getValue() );
            }
        }
        return attributes;
    }


    @SuppressWarnings("unchecked")
	private List<I18n> getI18n(EvaluationHelper evaluationHelper) throws PageTemplateException {

        try {
        	Object result = evaluationHelper.get( I18N_DOMAIN_VAR_NAME );
            
            if ( result instanceof List<?> ){
                return ( List<I18n> ) result;
            }
        } catch ( EvaluationException e ) {
            throw new PageTemplateException( "The " + I18N_DOMAIN_VAR_NAME + " var is not an instance ofI18n class "
                    + " in '" + this.template.getName() + "' template");
        }
        
        throw new PageTemplateException( I18N_DOMAIN_VAR_NAME + " instance not found." );
    }
    
    
    private Object processContent( String expression, EvaluationHelper evaluationHelper, String i18nTranslate, String i18nParams )
    throws PageTemplateException {
    	
        // Nothing to translate, process content as usual
        if ( i18nTranslate == null ){
            return processContent( expression, evaluationHelper );
        }
        
        return processI18nContent( evaluationHelper, i18nTranslate, i18nParams );
    }


    private Object processI18nContent( EvaluationHelper evaluationHelper, String i18nContent, String i18nParams ) 
    		throws PageTemplateException {
        
        // Get the i18n instance
        List<I18n> i18nList = getI18n( evaluationHelper );
        
        try {
			// Translate with no params
			if ( i18nParams == null ){
			    return ZPTContext.getInstance().getTranslator().tr(
			    		i18nList, 
			    		i18nContent );
			}
			
			// Translate with params
			return ZPTContext.getInstance().getTranslator().tr(
					i18nList, 
					i18nContent, 
			        this.getArrayFromI18nParams( i18nParams, evaluationHelper ) );
			
		} catch ( NullPointerException e ) {
			throw new PageTemplateException( "I18n subsystem of ZPT was not initialized." );
		}
    }

    private Object[] getArrayFromI18nParams( String i18nParams, EvaluationHelper evaluationHelper ) throws PageTemplateException {
        
        Object[] result = new Object[ MAXIMUM_NUMBER_OF_ATTRIBUTES ];
        
        int i = 0;
        ExpressionTokenizer tokens = new ExpressionTokenizer( i18nParams, ' ' );
        while( tokens.hasMoreTokens() ) {
            String valueExpression = tokens.nextToken();
            Object value = Expression.evaluate( valueExpression, evaluationHelper );
            try {
                result[ i++ ] = value;
            } catch ( ArrayIndexOutOfBoundsException e ) {
                throw new PageTemplateException(
                		"Too many number of attributes, the maximum is " + MAXIMUM_NUMBER_OF_ATTRIBUTES
                        + " in '" + this.template.getName() + "' template");
            }
        }
        
        return result;
    }
    
    private Object processContent( String exp, EvaluationHelper evaluationHelper)
        throws PageTemplateException {
    	
        String expression = exp;
        
        // Structured text, preserve xml structure
        if ( expression.startsWith( STRING_EXPR_STRUCTURE ) ) {
            expression = expression.substring( STRING_EXPR_STRUCTURE.length() );
            Object content = Expression.evaluate( expression, evaluationHelper );
            if ( ! ( content instanceof HTMLFragment ) ) {
                content = new HTMLFragment( String.valueOf( content ) );
            }
            return content;
        }
        else if ( expression.startsWith( STRING_EXPR_TEXT ) ) {
            expression = expression.substring( STRING_EXPR_TEXT.length() );
        }
        return String.valueOf( Expression.evaluate( expression, evaluationHelper ) );
    }
    
    private void processI18nDomain( String expression, EvaluationHelper evaluationHelper, 
    		List<String> varsToUnset, Map<String, Object> varsToSet )
    throws PageTemplateException {
    	
        List<I18n> i18nList = new ArrayList<I18n>();
        ExpressionTokenizer tokens = new ExpressionTokenizer( expression, ' ', true );
        while( tokens.hasMoreTokens() ) {
            String valueExpression = tokens.nextToken().trim();
            I18n i18n = (I18n) Expression.evaluate( valueExpression, evaluationHelper );
            i18nList.add(i18n);
        }
        
        setVar( evaluationHelper, varsToUnset, varsToSet, I18N_DOMAIN_VAR_NAME, i18nList );
    }
    
    private void processI18nDefine( String expression, String i18nParams, EvaluationHelper evaluationHelper, 
    		List<String> varsToUnset, Map<String, Object> varsToSet )
    throws PageTemplateException {
        this.processDefineOrI18nDefine( expression, i18nParams, evaluationHelper, varsToUnset, varsToSet, true );
    }
    
    private void processDefine( String expression, EvaluationHelper evaluationHelper, 
    		List<String> varsToUnset, Map<String, Object> varsToSet )
    throws PageTemplateException {
        this.processDefineOrI18nDefine( expression, null, evaluationHelper, varsToUnset, varsToSet, false );
    }
    
    private void processDefineOrI18nDefine( String expression, String i18nParams, EvaluationHelper evaluationHelper, 
    		List<String> varsToUnset, Map<String, Object> varsToSet, boolean translate )
        throws PageTemplateException {

        ExpressionTokenizer tokens = new ExpressionTokenizer( expression, DEFINE_DELIMITER, true );
        while( tokens.hasMoreTokens() ) {
            String variable = tokens.nextToken().trim();
            int space = variable.indexOf( IN_DEFINE_DELIMITER );
            if ( space == -1 ) {
                throw new ExpressionSyntaxException( "Bad variable definition: " + variable 
                        + " in '" + this.template.getName() + "' template");
            }
            String name = variable.substring( 0, space );
            String valueExpression = variable.substring( space + 1 ).trim();
            Object value = Expression.evaluate( valueExpression, evaluationHelper );
            
            if ( translate ){
                setVar( 
                		evaluationHelper, 
                		varsToUnset, 
                		varsToSet, 
                		name, 
                		processI18nContent( evaluationHelper, valueExpression, i18nParams ) );
            } else {
                setVar( 
                		evaluationHelper, 
                		varsToUnset, 
                		varsToSet, 
                		name, 
                		value);
            }
        }
    }

    private void processAttributes( AttributesImpl attributes, String expression, String i18nParams, 
    		EvaluationHelper evaluationHelper, String i18nAttributes )
    throws PageTemplateException {
    	
        if ( i18nAttributes != null ){
            processAttributes( attributes, i18nAttributes, i18nParams, evaluationHelper, true );
        }
        if ( expression != null ){
            processAttributes( attributes, expression, null, evaluationHelper, false );
        }
    }
    
    private void processAttributes( AttributesImpl attributes, String expression, String i18nParams, 
    		EvaluationHelper evaluationHelper, boolean translate )
        throws PageTemplateException {
    	
        ExpressionTokenizer tokens = new ExpressionTokenizer( expression, ATTRIBUTE_DELIMITER, true );
        while( tokens.hasMoreTokens() ) {
            String attribute = tokens.nextToken().trim();
            int space = attribute.indexOf( IN_ATTRIBUTE_DELIMITER );
            if ( space == -1 ) {
                throw new ExpressionSyntaxException( "Bad attributes expression: " + attribute 
                        + " in '" + this.template.getName() + "' template");
            }
            String qualifiedName = attribute.substring( 0, space );
            String valueExpression = attribute.substring( space + 1 ).trim();
            Object value = translate? 
                           processI18nContent( evaluationHelper, valueExpression, i18nParams ):
                           Expression.evaluate( valueExpression, evaluationHelper );
            //System.err.println( "attribute:\n" + qualifiedName + "\n" + valueExpression + "\n" + value );
            removeAttribute( attributes, qualifiedName );
            if ( value != null ) {
                String name = VOID_STRING;
                String uri = VOID_STRING;
                int colon = qualifiedName.indexOf( ":" );
                if ( colon != -1 ) {
                    String prefix = qualifiedName.substring( 0, colon );
                    name = qualifiedName.substring( colon + 1 );
                    uri = getNamespaceURIFromPrefix( prefix );
                }
                attributes.addAttribute( uri, name, qualifiedName, CDATA, String.valueOf( value ) );
            }
        }
    }

    private void removeAttribute( AttributesImpl attributes, String qualifiedName ) {
        int index = attributes.getIndex( qualifiedName );
        if ( index != -1 ) {
            attributes.removeAttribute( index );
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void processMacro( String expression, 
                               Element element, 
                               ContentHandler contentHandler,
                               LexicalHandler lexicalHandler,
                               EvaluationHelper evaluationHelper,
                               Stack <Map<String, Slot>> slotStack )
        throws SAXException, PageTemplateException, IOException, EvaluationException {
    	
        Object object = Expression.evaluate( expression, evaluationHelper );
        if ( object == null ) {
            throw new NoSuchPathException( "Could not find macro: '" + expression 
                    + "' in '" + this.template.getName() + "' template");
        }

        if ( object instanceof Macro ) {
            // Find slots to fill inside this macro call
            Map<String, Slot> slots = new HashMap<String, Slot>();
            findSlots( element, slots );

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
            macro.process( contentHandler, lexicalHandler, evaluationHelper, slotStack );
        }
        else {
            throw new PageTemplateException( 
            		"Expression '" + expression + "' does not evaluate to macro: " 
                    + object.getClass().getName() 
                    + " in '" + this.template.getName() + "' template");
        }
    }


    @SuppressWarnings({ "rawtypes" })
    private void findSlots( Element element, Map<String, Slot> slots ) {
        
        // Look for our attribute
        //String qualifiedAttributeName = this.metalNamespacePrefix + ":fill-slot";
        //String name = element.attributeValue( qualifiedAttributeName );
        String name = element.attributeValue( METAL_FILL_SLOT );
        if ( name != null ) {
            slots.put( name, new SlotImpl( element ) );
        }

        // Recurse into child elements
        for ( Iterator i = element.elementIterator(); i.hasNext(); ) {
            findSlots( ( Element ) i.next(), slots );
        }
    }
    

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void findMacros( Element element, Map<String, Macro> macros )
    {
        // Process any declared namespaces
        for ( Iterator<Namespace> i = element.declaredNamespaces().iterator(); i.hasNext(); ) {
            Namespace namespace = i.next();
            this.namespaces.put( namespace.getPrefix(), namespace.getURI() );
        }
        
        // Look for our attribute
        String name = element.attributeValue( METAL_DEFINE_MACRO );
        if ( name != null ) {
            macros.put( name, new MacroImpl( element ) );
        }

        // Recurse into child elements
        for ( Iterator i = element.elementIterator(); i.hasNext(); ) {
            findMacros( ( Element ) i.next(), macros );
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
	public Map<String, Macro> getMacros() {
    	
        if ( this.macros == null ) {
            this.macros = new HashMap<String, Macro>();
            findMacros( this.template.getRootElement(), this.macros );
        }
        return this.macros;
    }

    class DefaultResolver extends Resolver {
        URIResolver uriResolver;

        DefaultResolver() {
            if ( PageTemplateImpl.this.uri != null ) {
                this.uriResolver = new URIResolver( PageTemplateImpl.this.uri );
            }
        }
        
        @Override
        public URL getResource( String path ) throws MalformedURLException {
        	
            URL resource = null;
            
            // If user has supplied resolver, use it
            if ( PageTemplateImpl.this.userResolver != null ) {
                resource = PageTemplateImpl.this.userResolver.getResource( path );
            }

            // If resource not found by user resolver
            // fall back to resolving by uri
            if ( resource == null && this.uriResolver != null ) {
                resource = this.uriResolver.getResource( path );
            }

            return resource;
        }
        
        @Override
        public OnePhasePageTemplate getPageTemplate( String path )
            throws PageTemplateException, MalformedURLException {
        	
            OnePhasePageTemplate template = null;
            
            // If user has supplied resolver, use it
            if ( PageTemplateImpl.this.userResolver != null ) {
                template = PageTemplateImpl.this.userResolver.getPageTemplate( path );
                
                // template inherits user resolver
                template.setResolver( PageTemplateImpl.this.userResolver );
            }

            // If template not found by user resolver
            // fall back to resolving by uri
            if ( template == null && this.uriResolver != null ) {
                template = this.uriResolver.getPageTemplate( path );
            }

            return template;
        }
    }

    class MacroImpl implements Macro {
        Element element;

        MacroImpl( Element element ) {
            this.element = element;
        }
        
        @Override
		public void process( ContentHandler contentHandler, 
                             LexicalHandler lexicalHandler, 
                             EvaluationHelper evaluationHelper,
                             Stack <Map<String, Slot>>slotStack )
            throws SAXException, PageTemplateException, IOException, EvaluationException {
        	
            processElement( this.element, contentHandler, lexicalHandler, evaluationHelper, slotStack );
            saveToCache();
        }
    }

    class SlotImpl implements Slot {
        Element element;

        SlotImpl( Element element ) {
            this.element = element;
        }
        
        @Override
		public void process( ContentHandler contentHandler, 
                             LexicalHandler lexicalHandler, 
                             EvaluationHelper evaluationHelper,
                             Stack <Map<String, Slot>>slotStack )
            throws SAXException, PageTemplateException, IOException, EvaluationException {
        	
            processElement( this.element, contentHandler, lexicalHandler, evaluationHelper, slotStack );
            saveToCache();
        }
    }
}

