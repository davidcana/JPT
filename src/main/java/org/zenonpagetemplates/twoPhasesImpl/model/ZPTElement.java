package org.zenonpagetemplates.twoPhasesImpl.model;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dom4j.Namespace;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.twoPhasesImpl.ZPTXMLWriter;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.ZPTAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.StaticAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.StaticAttributeImpl;
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

/**
 * <p>
 *   Encapsulates all data about an element included in a ZPTDocument. 
 *   Each ZPTElement instance in a ZPTDocument is related to a XML tag
 *   from the source ZPT.
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
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.0 $
 */
public class ZPTElement implements ContentItem {

	private static final long serialVersionUID = 1072854500442164885L;
	private static final String CDATA = "CDATA";
	private static final char QUALIFIED_NAME_DELIMITER = ':';
	private static final String XMLNS = "xmlns";
	
	private String namespace;
	private String name;
	private List<DynamicAttribute> dynamicAttributes = new ArrayList<DynamicAttribute>();
	private List<StaticAttribute> staticAttributes = new ArrayList<StaticAttribute>();
	private List<ContentItem> contents = new ArrayList<ContentItem>();
	
	transient private Map<String, DynamicAttribute> dynamicAttributesMap = new TreeMap<String, DynamicAttribute>();
	transient private Map<String, StaticAttribute> staticAttributesMap = new TreeMap<String, StaticAttribute>();
	transient private List<ZPTElement> zptElementContents;
	
	
	public ZPTElement(){}
	public ZPTElement( String name, String namespace ){
		this.namespace = namespace;
		this.name = name;
	}

	
	public String getNamespace() {
		return this.namespace;
	}
	
	public void setNamespace( String namespace ) {
		this.namespace = namespace;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	public String getQualifiedName(){
		
		return this.namespace.isEmpty()?
			   this.name:
		       this.namespace + QUALIFIED_NAME_DELIMITER + this.name;
	}
	
	public List<DynamicAttribute> getDynamicAttributes() {
		return this.dynamicAttributes;
	}

	public void setDynamicAttributes( List<DynamicAttribute> dynamicAttributes ) {
		this.dynamicAttributes = dynamicAttributes;
	}

	public List<StaticAttribute> getStaticAttributes() {
		return this.staticAttributes;
	}

	public void setStaticAttributes( List<StaticAttribute> staticAttributes ) {
		this.staticAttributes = staticAttributes;
	}
	
	public boolean isEmpty(){
		return this.contents == null || this.contents.isEmpty();
	}

	public void addDynamicAttribute( DynamicAttribute dynamicAttribute ){
		
		String key = dynamicAttribute.getQualifiedName();
		
		// Remove static attribute if exists a static one with the same qualified name
		if ( this.staticAttributesMap.containsKey( key ) ){
			this.staticAttributesMap.remove( key );
		}
		
		this.dynamicAttributes.add( dynamicAttribute );
		this.dynamicAttributesMap.put(
				key, 
				dynamicAttribute );
	}
	
	public DynamicAttribute getDynamicAttribute( String attributeName ){
		return this.dynamicAttributesMap.get( attributeName );
	}
	
	public boolean existsDynamicAttribute( String attributeName ){
		return this.dynamicAttributesMap.containsKey( attributeName );
	}
	
	public void addStaticAttribute(StaticAttribute staticAttribute){
		
		String key = staticAttribute.getQualifiedName();
		
		// Don't add attribute if exists a dynamic one with the same qualified name
		if ( this.dynamicAttributesMap.containsKey( key ) ){
			return;
		}
		
		this.staticAttributes.add( staticAttribute );
		this.staticAttributesMap.put(
				key, 
				staticAttribute );
	}
	
	public StaticAttribute getStaticAttribute( String attributeName ){
		return this.staticAttributesMap.get( attributeName );
	}
	
	public List<ContentItem> getContents() {
		return contents;
	}
	
	public void setContents( List<ContentItem> contents ) {
		this.contents = contents;
		this.zptElementContents = null;
	}

	public void addContent( ContentItem contentItem ){
		this.contents.add( contentItem );
		this.zptElementContents = null;
	}
	
	public List<ZPTElement> getElementsContent(){
		
		if (this.zptElementContents == null){
			this.zptElementContents = generateElementsContent();
		}
		
		return this.zptElementContents;
	}
	
	private List<ZPTElement> generateElementsContent() {
		
		List<ZPTElement> result = new ArrayList<ZPTElement>();
		
		for ( ContentItem contentItem : this.contents ){
			if ( contentItem instanceof ZPTElement ){
				result.add( ( ZPTElement ) contentItem );
			}
		}
		
		return result;
	}
	
	private Object readResolve() throws ObjectStreamException {

		this.dynamicAttributesMap = new TreeMap<String, DynamicAttribute>();
		for ( DynamicAttribute dynamicAttribute: this.dynamicAttributes ){
			this.dynamicAttributesMap.put(
					dynamicAttribute.getQualifiedName(), 
					dynamicAttribute );
		}
		
		this.staticAttributesMap = new TreeMap<String, StaticAttribute>();
		for ( StaticAttribute staticAttribute: this.staticAttributes ){
			this.staticAttributesMap.put(
					staticAttribute.getQualifiedName(), 
					staticAttribute );
		}
	
		return this; 
	}
	
	public String getTALQualifiedName( String attributeName, ZPTDocument zptDocument ){
		return zptDocument.getTALPrefix() + QUALIFIED_NAME_DELIMITER + attributeName;
	}
	public String getMETALQualifiedName( String attributeName, ZPTDocument zptDocument ){
		return zptDocument.getMETALPrefix() + QUALIFIED_NAME_DELIMITER + attributeName;
	}
	public String getI18nQualifiedName( String attributeName, ZPTDocument zptDocument ){
		return zptDocument.getI18nPrefix() + QUALIFIED_NAME_DELIMITER + attributeName;
	}
	
	public boolean existsTALAttribute( String attributeName, ZPTDocument zptDocument ){
		return this.existsDynamicAttribute(
				this.getTALQualifiedName( attributeName, zptDocument ) );
	}
	public boolean existsMETALAttribute( String attributeName, ZPTDocument zptDocument ){
		return this.existsDynamicAttribute(
				this.getMETALQualifiedName( attributeName, zptDocument ) );
	}
	public boolean existsI18nAttribute( String attributeName, ZPTDocument zptDocument ){
		return this.existsDynamicAttribute(
				this.getI18nQualifiedName( attributeName, zptDocument ) );
	}
	
	public DynamicAttribute getTALAttribute( String attributeName, ZPTDocument zptDocument ){
		return this.dynamicAttributesMap.get(
				this.getTALQualifiedName( attributeName, zptDocument ) );
	}
	public DynamicAttribute getMETALAttribute( String attributeName, ZPTDocument zptDocument ){
		return this.dynamicAttributesMap.get(
				this.getMETALQualifiedName( attributeName, zptDocument ) );
	}
	public DynamicAttribute getI18nAttribute( String attributeName, ZPTDocument zptDocument ){
		return this.dynamicAttributesMap.get(
				this.getI18nQualifiedName( attributeName, zptDocument ) );
	}
	
	public AttributesImpl generateAttributes(){
		
		AttributesImpl result = new AttributesImpl();
		
		for ( StaticAttribute attribute: this.staticAttributes ){
			addAttribute( result, attribute );
		}
		
		for ( DynamicAttribute attribute: this.dynamicAttributes ){
			addAttribute( result, attribute );
		}
		
        return result;
	}
	
	private void addAttribute(AttributesImpl attributesImpl, ZPTAttribute attribute) {
		
		attributesImpl.addAttribute( 
			attribute.getNamespaceURI(), 
			attribute.getAttributeName(), 
			attribute.getQualifiedName(), 
			CDATA, 
			attribute.getValue() );
	}

	@Override
	public void writeToXMLWriter( ZPTXMLWriter xmlWriter ) throws IOException, SAXException {
		xmlWriter.writeZPTElement( this );
	}
	
	public TALAttributes getTALAttributes( ZPTDocument zptDocument ) throws PageTemplateException {
		
		DynamicAttribute result = this.getTALAttribute(
				TwoPhasesPageTemplate.TAL_ATTRIBUTES, 
				zptDocument );
		
		if ( result == null ){
			return null;
		}
		
		if ( result instanceof TALAttributes ){
			return ( TALAttributes ) result;
		}
		
		throw generateAttributeInstanceException( TwoPhasesPageTemplate.TAL_ATTRIBUTES );
	}
	
	public TALCondition getTALCondition(ZPTDocument zptDocument) throws PageTemplateException {
		
		DynamicAttribute result = this.getTALAttribute(
				TwoPhasesPageTemplate.TAL_CONDITION, 
				zptDocument );
		
		if ( result == null ){
			return null;
		}
		
		if ( result instanceof TALCondition ){
			return ( TALCondition ) result;
		}
		
		throw generateAttributeInstanceException( TwoPhasesPageTemplate.TAL_CONDITION );
	}
	
	public TALContent getTALContent(ZPTDocument zptDocument) throws PageTemplateException {
		
		DynamicAttribute result = this.getTALAttribute(
				TwoPhasesPageTemplate.TAL_CONTENT, 
				zptDocument );
		
		if ( result == null ){
			return null;
		}
		
		if ( result instanceof TALContent ){
			return ( TALContent ) result;
		}
		
		throw generateAttributeInstanceException( TwoPhasesPageTemplate.TAL_CONTENT );
	}
	
	public TALDefine getTALDefine( ZPTDocument zptDocument ) throws PageTemplateException {
		
		DynamicAttribute result = this.getTALAttribute(
				TwoPhasesPageTemplate.TAL_DEFINE, 
				zptDocument );
		
		if ( result == null ){
			return null;
		}
		
		if ( result instanceof TALDefine ){
			return ( TALDefine ) result;
		}
		
		throw generateAttributeInstanceException( TwoPhasesPageTemplate.TAL_DEFINE );
	}
	
	public TALOmitTag getTALOmitTag( ZPTDocument zptDocument ) throws PageTemplateException {
		
		DynamicAttribute result = this.getTALAttribute(
				TwoPhasesPageTemplate.TAL_OMIT_TAG, 
				zptDocument );
		
		if ( result == null ){
			return null;
		}
		
		if ( result instanceof TALOmitTag ){
			return ( TALOmitTag ) result;
		}
		
		throw generateAttributeInstanceException( TwoPhasesPageTemplate.TAL_OMIT_TAG );
	}
	
	public TALOnError getTALOnError( ZPTDocument zptDocument ) throws PageTemplateException {
		
		DynamicAttribute result = this.getTALAttribute(
				TwoPhasesPageTemplate.TAL_ON_ERROR, 
				zptDocument );
		
		if ( result == null ){
			return null;
		}
		
		if ( result instanceof TALOnError ){
			return ( TALOnError ) result;
		}
		
		throw generateAttributeInstanceException( TwoPhasesPageTemplate.TAL_ON_ERROR );
	}
	
	public TALRepeat getTALRepeat( ZPTDocument zptDocument ) throws PageTemplateException {
		
		DynamicAttribute result = this.getTALAttribute(
				TwoPhasesPageTemplate.TAL_REPEAT, 
				zptDocument );
		
		if ( result == null ){
			return null;
		}
		
		if ( result instanceof TALRepeat ){
			return ( TALRepeat ) result;
		}
		
		throw generateAttributeInstanceException( TwoPhasesPageTemplate.TAL_REPEAT );
	}

	public TALTag getTALTag( ZPTDocument zptDocument ) throws PageTemplateException {
		
		DynamicAttribute result = this.getTALAttribute(
				TwoPhasesPageTemplate.TAL_TAG, 
				zptDocument );
		
		if ( result == null ){
			return null;
		}
		
		if ( result instanceof TALTag ){
			return ( TALTag ) result;
		}
		
		throw generateAttributeInstanceException( TwoPhasesPageTemplate.TAL_TAG );
	}
	
	public METALDefineMacro getMETALDefineMacro( ZPTDocument zptDocument ) throws PageTemplateException {
		
		DynamicAttribute result = this.getMETALAttribute(
				TwoPhasesPageTemplate.METAL_DEFINE_MACRO, 
				zptDocument );
		
		if ( result == null ){
			return null;
		}
		
		if ( result instanceof METALDefineMacro ){
			return ( METALDefineMacro ) result;
		}
		
		throw generateAttributeInstanceException( TwoPhasesPageTemplate.METAL_DEFINE_MACRO );
	}
	
	public METALDefineSlot getMETALDefineSlot( ZPTDocument zptDocument ) throws PageTemplateException {
		
		DynamicAttribute result = this.getMETALAttribute(
				TwoPhasesPageTemplate.METAL_DEFINE_SLOT, 
				zptDocument );
		
		if ( result == null ){
			return null;
		}
		
		if ( result instanceof METALDefineSlot ){
			return ( METALDefineSlot ) result;
		}
		
		throw generateAttributeInstanceException( TwoPhasesPageTemplate.METAL_DEFINE_SLOT );
	}
	
	public METALFillSlot getMETALFillSlot( ZPTDocument zptDocument ) throws PageTemplateException {
		
		DynamicAttribute result = this.getMETALAttribute(
				TwoPhasesPageTemplate.METAL_FILL_SLOT, 
				zptDocument );
		
		if ( result == null ){
			return null;
		}
		
		if ( result instanceof METALFillSlot ){
			return ( METALFillSlot ) result;
		}
		
		throw generateAttributeInstanceException( TwoPhasesPageTemplate.METAL_FILL_SLOT );
	}
	
	public METALUseMacro getMETALUseMacro(ZPTDocument zptDocument) throws PageTemplateException {
		
		DynamicAttribute result = this.getMETALAttribute(
				TwoPhasesPageTemplate.METAL_USE_MACRO, 
				zptDocument );
		
		if ( result == null ){
			return null;
		}
		
		if ( result instanceof METALUseMacro ){
			return ( METALUseMacro ) result;
		}
		
		throw generateAttributeInstanceException( TwoPhasesPageTemplate.METAL_USE_MACRO );
	}
	
	public I18NAttributes getI18NAttributes(ZPTDocument zptDocument) throws PageTemplateException {
		
		DynamicAttribute result = this.getI18nAttribute(
				TwoPhasesPageTemplate.I18N_ATTRIBUTES, 
				zptDocument );
		
		if ( result == null ){
			return null;
		}
		
		if ( result instanceof I18NAttributes ){
			return ( I18NAttributes ) result;
		}
		
		throw generateAttributeInstanceException( TwoPhasesPageTemplate.I18N_ATTRIBUTES );
	}
	
	public I18NContent getI18NContent( ZPTDocument zptDocument ) throws PageTemplateException {
		
		DynamicAttribute result = this.getI18nAttribute(
				TwoPhasesPageTemplate.I18N_CONTENT, 
				zptDocument );
		
		if ( result == null ){
			return null;
		}
		
		if ( result instanceof I18NContent ){
			return ( I18NContent ) result;
		}
		
		throw generateAttributeInstanceException( TwoPhasesPageTemplate.I18N_CONTENT );
	}
	
	public I18NDefine getI18NDefine(ZPTDocument zptDocument) throws PageTemplateException {
		
		DynamicAttribute result = this.getI18nAttribute(
				TwoPhasesPageTemplate.I18N_DEFINE, 
				zptDocument );
		
		if ( result == null ){
			return null;
		}
		
		if ( result instanceof I18NDefine ){
			return ( I18NDefine ) result;
		}
		
		throw generateAttributeInstanceException( TwoPhasesPageTemplate.I18N_DEFINE );
	}
	
	public I18NDomain getI18NDomain(ZPTDocument zptDocument) throws PageTemplateException {
		
		DynamicAttribute result = this.getI18nAttribute(
				TwoPhasesPageTemplate.I18N_DOMAIN, 
				zptDocument );
		
		if ( result == null ){
			return null;
		}
		
		if ( result instanceof I18NDomain ){
			return ( I18NDomain ) result;
		}
		
		throw generateAttributeInstanceException( TwoPhasesPageTemplate.I18N_DOMAIN );
	}
	
	public I18NOnError getI18NOnError( ZPTDocument zptDocument ) throws PageTemplateException {
		
		DynamicAttribute result = this.getI18nAttribute(
				TwoPhasesPageTemplate.I18N_ON_ERROR, 
				zptDocument );
		
		if ( result == null ){
			return null;
		}
		
		if ( result instanceof I18NOnError ){
			return ( I18NOnError ) result;
		}
		
		throw generateAttributeInstanceException( TwoPhasesPageTemplate.I18N_ON_ERROR );
	}
	
	public I18NParams getI18NParams( ZPTDocument zptDocument ) throws PageTemplateException {
		
		DynamicAttribute result = this.getI18nAttribute(
				TwoPhasesPageTemplate.I18N_PARAMS, 
				zptDocument );
		
		if ( result == null ){
			return null;
		}
		
		if ( result instanceof I18NParams ){
			return ( I18NParams ) result;
		}
		
		throw generateAttributeInstanceException( TwoPhasesPageTemplate.I18N_PARAMS );
	}
	
	static private PageTemplateException generateAttributeInstanceException( String attributeName ) 
			throws PageTemplateException {
		
		return new PageTemplateException( 
				"Attribute " + attributeName + " is not of type " + attributeName );
	}
	
	public void addNamespaceStaticAttribute( Namespace namespace ){
		
		this.addStaticAttribute(
				new StaticAttributeImpl(
						namespace.getPrefix(), 
						XMLNS, 
						namespace.getURI() ) );
	}
}
