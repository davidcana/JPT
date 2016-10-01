package org.zenonpagetemplates.twoPhasesImpl.model.attributes.TAL;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.helpers.AttributesImpl;
import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.ZPTDocument;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.AttributesUtils;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.ZPTAttributeImpl;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.KeyValuePair;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpression;

/**
 * <p>
 *   Allows to set a list of attribute values.
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
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.1 $
 */
public class TALAttributes extends ZPTAttributeImpl implements DynamicAttribute {

	private static final long serialVersionUID = -8025473144201537666L;
	
	private List<KeyValuePair<ZPTExpression>> attributes = new ArrayList<KeyValuePair<ZPTExpression>>();
	
	
	public TALAttributes(){}
	public TALAttributes( String namespaceURI, String expression ) throws PageTemplateException {
		super( namespaceURI );
		this.attributes = AttributesUtils.getDefinitions( expression );
	}

	
	public List<KeyValuePair<ZPTExpression>> getAttributes() {
		return attributes;
	}

	public void setAttributes( List<KeyValuePair<ZPTExpression>> attributes ) {
		this.attributes = attributes;
	}

	public void addAttribute( KeyValuePair<ZPTExpression> attribute ){
		this.attributes.add( attribute );
	}
	
	@Override
	public String getAttributeName() {
		return TwoPhasesPageTemplate.TAL_ATTRIBUTES;
	}
	
	@Override
	public String getValue() {
		return AttributesUtils.getStringFromDefinitions( this.attributes );
	}
	
	public void evaluate( EvaluationHelper evaluationHelper, AttributesImpl attributesImpl, ZPTDocument zptDocument ) 
			throws EvaluationException {
		
		for ( KeyValuePair<ZPTExpression> attribute : this.attributes ){
			
			ZPTExpression zptExpression = null;
			try {
			    String qualifiedName = attribute.getKey();
			    zptExpression = attribute.getValue();
				Object value = zptExpression.evaluate( evaluationHelper );
			    
				AttributesUtils.addAttribute( qualifiedName, value, attributesImpl, zptDocument );
			
			} catch ( EvaluationException e ) {
				e.setInfo(
						zptExpression.getStringExpression(),
						this.getQualifiedName() );
				throw e;
				
			} catch ( Exception e ) {
				EvaluationException e2 = new EvaluationException( e );
				e2.setInfo(
						zptExpression.getStringExpression(),
						this.getQualifiedName() );
				throw e2;
			}
		}
	}
}
