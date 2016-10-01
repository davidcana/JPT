package org.zenonpagetemplates.twoPhasesImpl.model.attributes.TAL;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.helpers.AttributesImpl;
import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.JPTDocument;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.AttributesUtils;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.JPTAttributeImpl;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.KeyValuePair;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.JPTExpression;

/**
 * <p>
 *   Allows to set a list of attribute values.
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
 * @version $Revision: 1.1 $
 */
public class TALAttributes extends JPTAttributeImpl implements DynamicAttribute {

	private static final long serialVersionUID = -8025473144201537666L;
	
	private List<KeyValuePair<JPTExpression>> attributes = new ArrayList<KeyValuePair<JPTExpression>>();
	
	
	public TALAttributes(){}
	public TALAttributes( String namespaceUri, String expression ) throws PageTemplateException {
		super( namespaceUri );
		this.attributes = AttributesUtils.getDefinitions( expression );
	}

	
	public List<KeyValuePair<JPTExpression>> getAttributes() {
		return attributes;
	}

	public void setAttributes( List<KeyValuePair<JPTExpression>> attributes ) {
		this.attributes = attributes;
	}

	public void addAttribute( KeyValuePair<JPTExpression> attribute ){
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
	
	public void evaluate( EvaluationHelper evaluationHelper, AttributesImpl attributesImpl, JPTDocument jptDocument ) 
			throws EvaluationException {
		
		for ( KeyValuePair<JPTExpression> attribute : this.attributes ){
			
			JPTExpression jptExpression = null;
			try {
			    String qualifiedName = attribute.getKey();
			    jptExpression = attribute.getValue();
				Object value = jptExpression.evaluate( evaluationHelper );
			    
				AttributesUtils.addAttribute( qualifiedName, value, attributesImpl, jptDocument );
			
			} catch ( EvaluationException e ) {
				e.setInfo(
						jptExpression.getStringExpression(),
						this.getQualifiedName() );
				throw e;
				
			} catch ( Exception e ) {
				EvaluationException e2 = new EvaluationException( e );
				e2.setInfo(
						jptExpression.getStringExpression(),
						this.getQualifiedName() );
				throw e2;
			}
		}
	}
}
