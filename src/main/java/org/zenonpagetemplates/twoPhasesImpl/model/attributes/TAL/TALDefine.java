package org.zenonpagetemplates.twoPhasesImpl.model.attributes.TAL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplateImpl;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.AttributesUtils;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.ZPTAttributeImpl;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.KeyValuePair;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpression;

/**
 * <p>
 *   Allows to set a list of variable definitions. The scope of these
 *   variables is this tag and its children.
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
public class TALDefine extends ZPTAttributeImpl implements DynamicAttribute {

	private static final long serialVersionUID = 1099824857590914745L;
	
	private List<KeyValuePair<ZPTExpression>> definitions = new ArrayList<KeyValuePair<ZPTExpression>>();
	
	
	public TALDefine(){}
	public TALDefine(String namespaceURI, String expression) throws PageTemplateException {
		super( namespaceURI );
		this.definitions = AttributesUtils.getDefinitions( expression );
	}
	
	
	public List<KeyValuePair<ZPTExpression>> getDefinitions() {
		return this.definitions;
	}

	public void setDefinitions(List<KeyValuePair<ZPTExpression>> definitions) {
		this.definitions = definitions;
	}
	
	public void addDefinition(KeyValuePair<ZPTExpression> definition){
		this.definitions.add( definition );
	}
	
	@Override
	public String getAttributeName() {
		return TwoPhasesPageTemplate.TAL_DEFINE;
	}
	
	@Override
	public String getValue() {
		return AttributesUtils.getStringFromDefinitions( this.definitions );
	}
	
	public void process( EvaluationHelper evaluationHelper, 
			List<String> varsToUnset, Map<String, Object> varsToSet ) throws EvaluationException {
		
		ZPTExpression expression = null;
		try {
			
			for ( KeyValuePair<ZPTExpression> definition: this.definitions ){
				expression = definition.getValue();
				TwoPhasesPageTemplateImpl.setVar(
						evaluationHelper, 
						varsToUnset, 
						varsToSet, 
						definition.getKey(), 
						expression.evaluate( evaluationHelper ) );
			}
			
		} catch ( EvaluationException e ) {
			e.setInfo(
					expression == null? null: expression.getStringExpression(),
					this.getQualifiedName() );
			throw e;
			
		} catch ( Exception e ) {
			EvaluationException e2 = new EvaluationException( e );
			e2.setInfo(
					expression == null? null: expression.getStringExpression(),
					this.getQualifiedName() );
			throw e2;
		}
	}
}
