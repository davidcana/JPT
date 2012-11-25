package org.javapagetemplates.twoPhasesImpl.model.attributes.TAL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplateImpl;
import org.javapagetemplates.twoPhasesImpl.model.attributes.AttributesUtils;
import org.javapagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.javapagetemplates.twoPhasesImpl.model.attributes.JPTAttributeImpl;
import org.javapagetemplates.twoPhasesImpl.model.attributes.KeyValuePair;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;

import bsh.Interpreter;

/**
 * <p>
 *   Allows to set a list of variable definitions. The scope of these
 *   variables is this tag and its children.
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
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.0 $
 */
public class TALDefine extends JPTAttributeImpl implements DynamicAttribute {

	private static final long serialVersionUID = 1099824857590914745L;
	
	private List<KeyValuePair<JPTExpression>> definitions = new ArrayList<KeyValuePair<JPTExpression>>();
	
	
	public TALDefine(){}
	public TALDefine(String namespaceUri, String expression) throws PageTemplateException {
		super(namespaceUri);
		this.definitions = AttributesUtils.getDefinitions(expression);
	}
	
	
	public List<KeyValuePair<JPTExpression>> getDefinitions() {
		return this.definitions;
	}

	public void setDefinitions(List<KeyValuePair<JPTExpression>> definitions) {
		this.definitions = definitions;
	}
	
	public void addDefinition(KeyValuePair<JPTExpression> definition){
		this.definitions.add(definition);
	}
	
	@Override
	public String getAttributeName() {
		return TwoPhasesPageTemplate.TAL_DEFINE;
	}
	
	@Override
	public String getValue() {
		return AttributesUtils.getStringFromDefinitions(this.definitions);
	}
	
	public void process( Interpreter beanShell, 
			List<String> varsToUnset, Map<String, Object> varsToSet ) throws ExpressionEvaluationException {
		
		JPTExpression expression = null;
		try {
			
			for ( KeyValuePair<JPTExpression> definition: this.definitions ){
				expression = definition.getValue();
				TwoPhasesPageTemplateImpl.setVar(
						beanShell, 
						varsToUnset, 
						varsToSet, 
						definition.getKey(), 
						expression.evaluate(beanShell));
			}
			
		} catch (ExpressionEvaluationException e) {
			e.setInfo(
					expression == null? null: expression.getStringExpression(),
					this.getQualifiedName());
			throw e;
			
		} catch (Exception e) {
			ExpressionEvaluationException e2 = new ExpressionEvaluationException(e);
			e2.setInfo(
					expression == null? null: expression.getStringExpression(),
					this.getQualifiedName());
			throw e2;
		}
	}
}
