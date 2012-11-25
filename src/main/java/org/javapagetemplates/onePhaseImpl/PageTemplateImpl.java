package org.javapagetemplates.onePhaseImpl;

import bsh.BshClassManager;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

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

import org.javapagetemplates.common.ExpressionTokenizer;
import org.javapagetemplates.common.Filter;
import org.javapagetemplates.common.TemplateError;
import org.javapagetemplates.common.exceptions.ExpressionSyntaxException;
import org.javapagetemplates.common.exceptions.NoSuchPathException;
import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.common.helpers.DateHelper;

/**
 * <p>
 *   Main class to implement JPT in one phase.
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
 * @version $Revision: 1.16 $
 */
public class PageTemplateImpl implements OnePhasePageTemplate {
	
	private static final String GLOBAL = "global";
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
    //private Interpreter beanShell;
    
	// Map of macros contained in this template
    Map<String, Macro> macros = null;

    private static BshClassManager bshClassManager;
    
    private static SAXReader htmlReader = null;
    static final SAXReader getHTMLReader() throws Exception {
        if ( htmlReader == null && JPTContext.getInstance().isUseHtmlReader()) {
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
            XMLDocumentFilter[] filters = {new Filter()};
            parser.setProperty("http://cyberneko.org/html/properties/filters", filters);
            
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
                    if (reader == null){
                        throw (e);
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
        catch( Exception e ) {
            throw new PageTemplateException(e);
        }
    }

    public PageTemplateImpl( URL url ) throws PageTemplateException {
        try {
        	this.id = url.toString();
            this.uri = new URI( url.toString() );
            
            this.recoveredFromCache = this.recoverFromCache(); 
        	if (this.recoveredFromCache){
        		return;
        	}
            
            SAXReader reader = getXMLReader();
            try {
                this.template = reader.read( url );
            } catch( DocumentException e ) {
                try {
                    reader = getHTMLReader();
                    if (reader == null){
                        throw (e);
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
        catch( RuntimeException e ) {
            throw e;
        }
        catch( Exception e ) {
            throw new PageTemplateException(e);
        }
    }

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
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
        throws PageTemplateException
    {
        process( output, context, null );
    }
    
    
    static final SAXTransformerFactory factory = (SAXTransformerFactory)TransformerFactory.newInstance();
    @Override
	public void process( OutputStream output, Object context, Map<String, Object> dictionary )
        throws PageTemplateException
    {
        try {
            TransformerHandler handler = factory.newTransformerHandler();
            Transformer transformer = handler.getTransformer();
            
            transformer.setOutputProperty(OutputKeys.ENCODING, ENCODING);
            //transformer.setOutputProperty(OutputKeys.METHOD, "html");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            
            //OutputStreamWriter writer = new OutputStreamWriter(output, ENCODING);
            //handler.setResult(new StreamResult(writer));
            handler.setResult(new StreamResult(output));
            //handler.processingInstruction(Result.PI_DISABLE_OUTPUT_ESCAPING, "");

            process( handler, handler, context, dictionary );
            
        } catch( PageTemplateException e ) {
            throw e;
            
        } catch( Exception e ) {
            throw new PageTemplateException(e);
        }
    }

    @Override
	public void process( ContentHandler contentHandler, 
                         LexicalHandler lexicalHandler, 
                         Object context, 
                         Map<String, Object> dictionary )
        throws PageTemplateException 
    {
        try {
            // Initialize the bean shell and register in the context
            Interpreter beanShell = this.getInterpreter(context, dictionary);
            //JPTContext.getInstance().registerInterPreter(beanShell);
            
            // Process
            Element root = this.template.getRootElement();
            contentHandler.startDocument();
            processElement( root, contentHandler, lexicalHandler, beanShell, new Stack<Map<String, Slot>>() );
            contentHandler.endDocument();
            
            this.saveToCache();
            
        } catch( PageTemplateException e ) {
            throw e;
        } catch( Exception e ) {
            throw new PageTemplateException(e);
        }
    }
    
    
    private void saveToCache() throws IOException {

    	try {
    		if (!JPTContext.getInstance().isCacheOn()
    				|| this.id == null 
    				|| this.recoveredFromCache 
    				|| JPTContext.getInstance().getTemplateCache() == null){
    			return;
    		}
    		
			JPTContext.getInstance().getTemplateCache().put(this.id, this.template);
			
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
    
    private boolean recoverFromCache() throws IOException {
    	
    	try {
            if (!JPTContext.getInstance().isCacheOn() 
            		|| this.id == null 
            		|| JPTContext.getInstance().getTemplateCache() == null){
            	return false;
            }
            
			this.template = JPTContext.getInstance().getTemplateCache().get(this.id);
			
			return !(this.template == null);
			
		} catch (Exception e) {
			throw new IOException(e);
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
        
        beanShell.set( OnePhasePageTemplate.HERE_VAR_NAME, context );
        beanShell.set( OnePhasePageTemplate.TEMPLATE_VAR_NAME, this );
        beanShell.set( OnePhasePageTemplate.RESOLVER_VAR_NAME, new DefaultResolver() );
        //beanShell.set( PageTemplate.BOOL_HELPER_VAR_NAME, new BoolHelper() );
        beanShell.set( OnePhasePageTemplate.DATE_HELPER_VAR_NAME, new DateHelper() );
    }
    
	private Map<String, String> namespaces = new TreeMap<String, String>();
    private String getNamespaceURIFromPrefix( String prefix ) {
        String uri = (String)this.namespaces.get( prefix );
        return uri == null ? VOID_STRING : uri;
    }

    public static void setVar(Interpreter beanshell, 
    		List<String> varsToUnset, Map<String, Object> varsToSet, String name, Object value) 
    		throws EvalError {
        
        Object currentValue = beanshell.get(name);
        
        if (currentValue != null){
            varsToSet.put(name, currentValue);
            
        } else {
            varsToUnset.add(name);
        }
        
        beanshell.set(name, value);
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
	
    protected void processElement( Element element, 
                                 ContentHandler contentHandler, 
                                 LexicalHandler lexicalHandler, 
                                 Interpreter beanShell, 
                                 Stack <Map<String, Slot>>slotStack )
        throws SAXException, PageTemplateException, IOException, EvalError
    {
		Expressions expressions = new Expressions();
		AttributesImpl attributes = getAttributes( element, expressions );
		
		List<String> varsToUnset = new ArrayList<String>();
		Map<String, Object> varsToSet = new HashMap<String, Object>();
		
        try {
			// Process instructions
			if (expressions.repeat != null){
				
				// Process repeat
				Loop loop = new Loop( expressions.repeat, beanShell, varsToUnset, varsToSet );
				while( loop.repeat( beanShell ) ) {
			        if (!processElement(element, contentHandler,
			    			lexicalHandler, beanShell, slotStack, expressions, attributes, varsToUnset, varsToSet)){
			        	return;
			        }
				}
				
			} else {
				
				 // Process non repeat
			    if (!processElement(element, contentHandler,
						lexicalHandler, beanShell, slotStack, expressions, attributes, varsToUnset, varsToSet)){
			    	return;
			    }
			}
			
			processVars(beanShell, varsToUnset, varsToSet);
			
		} catch (PageTemplateException e) {
			
			if (!treatError(element, contentHandler,
					lexicalHandler, beanShell, varsToUnset, varsToSet, e)){
				throw (e);
			}
		}
    }

	private boolean treatError(Element element, ContentHandler contentHandler,
			LexicalHandler lexicalHandler, Interpreter beanShell,
			List<String> varsToUnset, Map<String, Object> varsToSet, Exception exception) 
					throws EvalError, PageTemplateException, SAXException {
		
		// Exit if there is no on-error expression defined
		String onErrorExpression = (String) beanShell.get(ON_ERROR_VAR_NAME);
		if (onErrorExpression == null){
			return false;
		}
		
		String content = null;
		String i18nContent = null;
		String i18nParams = null;

		if (onErrorExpression.startsWith(I18N_EXPRESSION_PREFIX)){
			i18nContent = onErrorExpression.substring( I18N_EXPRESSION_PREFIX.length() );
		} else {
			content = onErrorExpression;
		}
		
		// Set the error variable
		TemplateError templateError = new TemplateError(exception);
		setVar(beanShell, varsToUnset, varsToSet, TEMPLATE_ERROR_VAR_NAME, templateError);
		
		try {
			// Process content
			jptContent(
					contentHandler, 
					lexicalHandler, 
					processContent( content, beanShell, i18nContent, i18nParams ));
			
		} catch (Exception e) {
			processVars(beanShell, varsToUnset, varsToSet);
			throw new PageTemplateException(e);
		}
		
		processVars(beanShell, varsToUnset, varsToSet);

		return true;
	}

	private boolean processElement(Element element, ContentHandler contentHandler,
			LexicalHandler lexicalHandler, Interpreter beanShell, 
			Stack <Map<String, Slot>>slotStack,
			Expressions expressions, AttributesImpl attributes, List<String> varsToUnset,
			Map<String, Object> varsToSet) throws PageTemplateException, SAXException, IOException, EvalError {
		
		/* Normal elements */
		
		// on-error
        if ( expressions.onError != null || expressions.i18nOnError != null) {
            processOnErrors( expressions.onError, expressions.i18nOnError, beanShell, varsToUnset, varsToSet );
        }
        
		// define
        if ( expressions.define != null ) {
            processDefine( expressions.define, beanShell, varsToUnset, varsToSet );
        }
        
        // i18nDomain
        if ( expressions.i18nDomain != null ) {
            processI18nDomain( expressions.i18nDomain, beanShell, varsToUnset, varsToSet );
        }
        
        // i18n:define
        if ( expressions.i18nDefine != null ) {
            processI18nDefine( expressions.i18nDefine, expressions.i18nParams, beanShell, varsToUnset, varsToSet );
        }
        
        // condition
        if ( expressions.condition != null &&
             ! Expression.evaluateBoolean( expressions.condition, beanShell ) ) {
            // Skip this element (and children)
            return false;
        }
        
    
        /* Macro related elements */
        
		// use macro
        if ( expressions.useMacro != null ) {
            processMacro( expressions.useMacro, element, contentHandler, lexicalHandler, beanShell, slotStack );
            return false;
        }

        // fill slot
        if ( expressions.defineSlot != null && 
        	!processDefineSlot(contentHandler, lexicalHandler, beanShell,
					slotStack, expressions)){
            return false;
        }
        
        
        /* Content elements */
		
		// content or replace
		Object jptContent = null;
		if ( expressions.content != null || expressions.i18nContent != null ) {
		    jptContent = processContent( expressions.content, beanShell, expressions.i18nContent, expressions.i18nParams );
		}
		
		// attributes
		if ( expressions.attributes != null || expressions.i18nAttributes != null) {
		    processAttributes( attributes, expressions.attributes, expressions.i18nParams, beanShell,  expressions.i18nAttributes);
		}
		
		// omit-tag
		boolean jptOmitTag = getJptOmitTag(beanShell, expressions, element);
		
		// Declare element
		//Namespace namespace = element.getNamespace();
		if ( ! jptOmitTag ) {
		    contentHandler.startElement( VOID_STRING, element.getName(), element.getQualifiedName(), attributes );
		    //contentHandler.startElement( namespace.getURI(), element.getName(), element.getQualifiedName(), attributes );
		}
		
		// Content
		if ( jptContent != null ) {
		    jptContent(contentHandler, lexicalHandler, jptContent);
		}
		else {
		    defaultContent( element, contentHandler, lexicalHandler, beanShell, slotStack );
		}
   
		// End element
		if ( ! jptOmitTag ) {
		    contentHandler.endElement( VOID_STRING, element.getName(), element.getQualifiedName() );
		    //contentHandler.endElement( namespace.getURI(), element.getName(), element.getQualifiedName() );
		}
		
		return true;
	}


	private boolean  processDefineSlot(ContentHandler contentHandler,
			LexicalHandler lexicalHandler, Interpreter beanShell,
			Stack <Map<String, Slot>>slotStack, Expressions expressions) throws SAXException,
			PageTemplateException, IOException, EvalError {
		
		if ( ! slotStack.isEmpty() ) {
		    Map<String, Slot> slots = slotStack.pop();
		    Slot slot = slots.get( expressions.defineSlot );
		    if ( slot != null ) {
		        slot.process( contentHandler, lexicalHandler, beanShell, slotStack );
		        slotStack.push( slots );
		        return false;
		    }
		    // else { use content in macro }
		    slotStack.push( slots );
		    
		    return true;
		}
		else {
		    throw new PageTemplateException( "slot definition not allowed outside of macro" );
		}
	}

	private void jptContent(ContentHandler contentHandler,
			LexicalHandler lexicalHandler, Object jptContent)
			throws PageTemplateException, SAXException {
		
		// Content for this element has been generated dynamically
		if (jptContent instanceof HTMLFragment ) {
			
			HTMLFragment html = (HTMLFragment)jptContent;
			
			if (JPTContext.getInstance().isParseHTMLFragments()){ 
				html.toXhtml( contentHandler, lexicalHandler );
			} else {
				char[] text = html.getHtml().toCharArray();
				contentHandler.characters( text, 0, text.length );
			}
		} 
		
		// plain text
		else {
		    char[] text = ((String)jptContent).toCharArray();
		    contentHandler.characters( text, 0, text.length );
		}
	}

	private void processOnErrors(String onError, String i18nOnError,
			Interpreter beanShell, List<String> varsToUnset,
			Map<String, Object> varsToSet) throws EvalError, PageTemplateException {

		if (onError != null && i18nOnError != null){
			throw new PageTemplateException("tal:on-error and i18n:on-error can not be at the same tag, "
					+ "please remove one of them.");
		}
		
		if (onError != null){
			setVar(beanShell, varsToUnset, varsToSet, ON_ERROR_VAR_NAME, onError);
			return;
		}
		
		setVar(beanShell, varsToUnset, varsToSet, ON_ERROR_VAR_NAME, I18N_EXPRESSION_PREFIX + i18nOnError);
	}

	static private boolean getJptOmitTag(Interpreter beanShell, Expressions expressions, Element element)
			throws PageTemplateException {
		
		boolean jptOmitTag = false;
		
		// Omit tag when it is from TAL name space
		if ( TAL_NAMESPACE_URI.equals( element.getNamespace().getURI() ) ) {
			return true;
		}
		
		// Omit tag depending on the value of omitTag attribute
		if ( expressions.omitTag != null ) {
		    if ( expressions.omitTag.equals( VOID_STRING ) ) {
		        jptOmitTag = true;
		    }
		    else {
		        jptOmitTag = Expression.evaluateBoolean( expressions.omitTag, beanShell );
		    }
		}
		return jptOmitTag;
	}
    
    static private void processVars(Interpreter beanShell, List<String> varsToUnset, Map<String, Object> varsToSet) 
    		throws EvalError {

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
    }

    @SuppressWarnings({ "unchecked" })
    private void defaultContent( Element element, 
                                 ContentHandler contentHandler, 
                                 LexicalHandler lexicalHandler, 
                                 Interpreter beanShell, 
                                 Stack <Map<String, Slot>> slotStack )
        throws SAXException, PageTemplateException, IOException, EvalError
    {   
        // Use default template content
        for ( Iterator<Node> i = element.nodeIterator(); i.hasNext(); ) {
            Node node = i.next();
            switch( node.getNodeType() ) {
            case Node.ELEMENT_NODE:
                processElement( (Element)node, contentHandler, lexicalHandler, beanShell, slotStack );
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
                Namespace declared = (Namespace)node;
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
        throws PageTemplateException
    {
        AttributesImpl attributes = new AttributesImpl();
        for ( Iterator<Attribute> i = element.attributeIterator(); i.hasNext(); ) {
            Attribute attribute = i.next();
            Namespace namespace = attribute.getNamespace();
            String name = attribute.getName();
            
            // Handle JPT attributes
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
                
                // error
                else {
                    throw new PageTemplateException( "unknown tal attribute: " + name 
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
                    throw new PageTemplateException( "unknown metal attribute: " + name 
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
                    throw new PageTemplateException( "unknown i18n attribute: " + name 
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
	private List<I18n> getI18n(Interpreter beanShell) throws PageTemplateException {

        try {
        	Object result = beanShell.get(I18N_DOMAIN_VAR_NAME);
            
            if (result instanceof List<?>){
                return (List<I18n>) result;
            }
        } catch (EvalError e) {
            throw new PageTemplateException("The " + I18N_DOMAIN_VAR_NAME + " var is not an instance ofI18n class "
                    + " in '" + this.template.getName() + "' template");
        }
        
        throw new PageTemplateException(I18N_DOMAIN_VAR_NAME + " instance not found.");
    }
    
    
    private Object processContent( String expression, Interpreter beanShell, String i18nTranslate, String i18nParams )
    throws PageTemplateException
    {
        // Nothing to translate, process content as usual
        if (i18nTranslate == null){
            return processContent( expression, beanShell);
        }
        
        return processI18nContent(beanShell, i18nTranslate, i18nParams);
    }


    private Object processI18nContent(Interpreter beanShell, String i18nContent, String i18nParams) 
    		throws PageTemplateException {
        
        // Get the i18n instance
        List<I18n> i18nList = getI18n(beanShell);
        
        try {
			// Translate with no params
			if (i18nParams == null){
			    return JPTContext.getInstance().getTranslator().tr(
			    		i18nList, 
			    		i18nContent);
			}
			
			// Translate with params
			return JPTContext.getInstance().getTranslator().tr(
					i18nList, 
					i18nContent, 
			        this.getArrayFromI18nParams(i18nParams, beanShell));
			
		} catch (NullPointerException e) {
			throw new PageTemplateException("I18n subsystem of JPT was not initialized.");
		}
    }

    private Object[] getArrayFromI18nParams(String i18nParams, Interpreter beanShell) throws PageTemplateException {
        
        Object[] result = new Object[MAXIMUM_NUMBER_OF_ATTRIBUTES];
        
        int i = 0;
        ExpressionTokenizer tokens = new ExpressionTokenizer( i18nParams, ' ' );
        while( tokens.hasMoreTokens() ) {
            String valueExpression = tokens.nextToken();
            Object value = Expression.evaluate( valueExpression, beanShell );
            try {
                result[i++] = value;
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new PageTemplateException("Too many number of attributes, the maximum is " + MAXIMUM_NUMBER_OF_ATTRIBUTES
                        + " in '" + this.template.getName() + "' template");
            }
        }
        
        return result;
    }
    
    private Object processContent( String exp, Interpreter beanShell)
        throws PageTemplateException
    {
        String expression = exp;
        
        // Structured text, preserve xml structure
        if ( expression.startsWith( STRING_EXPR_STRUCTURE ) ) {
            expression = expression.substring( STRING_EXPR_STRUCTURE.length() );
            Object content = Expression.evaluate( expression, beanShell );
            if ( ! ( content instanceof HTMLFragment ) ) {
                content = new HTMLFragment( String.valueOf( content ) );
            }
            return content;
        }
        else if ( expression.startsWith( STRING_EXPR_TEXT ) ) {
            expression = expression.substring( STRING_EXPR_TEXT.length() );
        }
        return String.valueOf( Expression.evaluate( expression, beanShell ) );
    }
    
    private void processI18nDomain( String expression, Interpreter beanShell, List<String> varsToUnset, Map<String, Object> varsToSet )
    throws PageTemplateException
    {
        try {
            List<I18n> i18nList = new ArrayList<I18n>();
            ExpressionTokenizer tokens = new ExpressionTokenizer( expression, ' ', true );
            while( tokens.hasMoreTokens() ) {
                String valueExpression = tokens.nextToken().trim();
                I18n i18n = (I18n) Expression.evaluate( valueExpression, beanShell );
                i18nList.add(i18n);
            }
            setVar(beanShell, varsToUnset, varsToSet, I18N_DOMAIN_VAR_NAME, i18nList);
            
        } catch( bsh.EvalError e ) {
            throw new PageTemplateException(e);
        }
    }
    
    private void processI18nDefine( String expression, String i18nParams, Interpreter beanShell, List<String> varsToUnset, Map<String, Object> varsToSet )
    throws PageTemplateException
    {
        this.processDefineOrI18nDefine(expression, i18nParams, beanShell, varsToUnset, varsToSet, true);
    }
    
    private void processDefine( String expression, Interpreter beanShell, List<String> varsToUnset, Map<String, Object> varsToSet )
    throws PageTemplateException
    {
        this.processDefineOrI18nDefine(expression, null, beanShell, varsToUnset, varsToSet, false);
    }
    
    private void processDefineOrI18nDefine( String expression, String i18nParams, Interpreter beanShell, List<String> varsToUnset, Map<String, Object> varsToSet, boolean translate )
        throws PageTemplateException
    {
        try {
            ExpressionTokenizer tokens = new ExpressionTokenizer( expression, DEFINE_DELIMITER, true );
            while( tokens.hasMoreTokens() ) {
                String variable = tokens.nextToken().trim();
                int space = variable.indexOf( IN_DEFINE_DELIMITER );
                if ( space == -1 ) {
                    throw new ExpressionSyntaxException( "bad variable definition: " + variable 
                            + " in '" + this.template.getName() + "' template");
                }
                String name = variable.substring( 0, space );
                String valueExpression = variable.substring( space + 1 ).trim();
                Object value = Expression.evaluate( valueExpression, beanShell );
                
                if (translate){
                    setVar(beanShell, varsToUnset, varsToSet, name, processI18nContent(beanShell, valueExpression, i18nParams));
                } else {
                    setVar(beanShell, varsToUnset, varsToSet, name, value);
                }
            }
        } catch( bsh.EvalError e ) {
            throw new PageTemplateException(e);
        }
    }

    private void processAttributes( AttributesImpl attributes, String expression, String i18nParams, 
    		Interpreter beanShell, String i18nAttributes )
    throws PageTemplateException
    {
        if (i18nAttributes != null){
            processAttributes( attributes, i18nAttributes, i18nParams, beanShell, true );
        }
        if (expression != null){
            processAttributes( attributes, expression, null, beanShell, false );
        }
    }
    
    private void processAttributes( AttributesImpl attributes, String expression, String i18nParams, 
    		Interpreter beanShell, boolean translate )
        throws PageTemplateException
    {
        ExpressionTokenizer tokens = new ExpressionTokenizer( expression, ATTRIBUTE_DELIMITER, true );
        while( tokens.hasMoreTokens() ) {
            String attribute = tokens.nextToken().trim();
            int space = attribute.indexOf( IN_ATTRIBUTE_DELIMITER );
            if ( space == -1 ) {
                throw new ExpressionSyntaxException( "bad attributes expression: " + attribute 
                        + " in '" + this.template.getName() + "' template");
            }
            String qualifiedName = attribute.substring( 0, space );
            String valueExpression = attribute.substring( space + 1 ).trim();
            Object value = translate? 
                           processI18nContent(beanShell, valueExpression, i18nParams):
                           Expression.evaluate( valueExpression, beanShell );
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
                               Interpreter beanShell,
                               Stack <Map<String, Slot>> slotStack )
        throws SAXException, PageTemplateException, IOException, EvalError
    {
        Object object = Expression.evaluate( expression, beanShell );
        if ( object == null ) {
            throw new NoSuchPathException( "could not find macro: " + expression 
                    + " in '" + this.template.getName() + "' template");
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
            macro.process( contentHandler, lexicalHandler, beanShell, slotStack );
        }
        else {
            throw new PageTemplateException( "expression '" + expression + "' does not evaluate to macro: " + 
                                             object.getClass().getName() 
                                             + " in '" + this.template.getName() + "' template");
        }
    }

    /**
     * With all of our namespace woes, getting an XPath expression
     * to work has proven futile, so we'll recurse through the tree
     * ourselves to find what we need.
     */
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
            findSlots( (Element)i.next(), slots );
        }
    }
    
    /**
     * With all of our namespace woes, getting an XPath expression
     * to work has proven futile, so we'll recurse through the tree
     * ourselves to find what we need.
     */
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
            findMacros( (Element)i.next(), macros );
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
            if (PageTemplateImpl.this.uri != null ) {
                this.uriResolver = new URIResolver( PageTemplateImpl.this.uri );
            }
        }
        
        @Override
        public URL getResource( String path ) 
            throws java.net.MalformedURLException
        {
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
            throws PageTemplateException, java.net.MalformedURLException
        {
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
                             Interpreter beanShell,
                             Stack <Map<String, Slot>>slotStack )
            throws SAXException, PageTemplateException, IOException, EvalError
        {
            processElement( this.element, contentHandler, lexicalHandler, beanShell, slotStack );
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
                             Interpreter beanShell,
                             Stack <Map<String, Slot>>slotStack )
            throws SAXException, PageTemplateException, IOException, EvalError
        {
            processElement( this.element, contentHandler, lexicalHandler, beanShell, slotStack );
            saveToCache();
        }
    }
}

