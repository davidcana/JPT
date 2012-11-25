package org.javapagetemplates.twoPhasesImpl.model.attributes.TAL;

import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.javapagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.javapagetemplates.twoPhasesImpl.model.attributes.JPTAttributeImpl;
import org.javapagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;

import bsh.Interpreter;

/**
 * <p>
 *   Allows to set an expression as a condition. If it evaluates to 
 *   <code>false</code> the tag, attributes and contents will not 
 *   be generated.
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
public class TALCondition extends JPTAttributeImpl implements DynamicAttribute {

	private static final long serialVersionUID = 4626938652290394973L;
	
	private JPTExpression condition;
	
	
	public TALCondition(){}
	public TALCondition(String namespaceUri, String expression) throws PageTemplateException {
		super(namespaceUri);
		this.condition = ExpressionUtils.generate(expression);
	}
	

	public JPTExpression getCondition() {
		return this.condition;
	}

	public void setCondition(JPTExpression condition) {
		this.condition = condition;
	}
	
	@Override
	public String getAttributeName() {
		return TwoPhasesPageTemplate.TAL_CONDITION;
	}
	
	@Override
	public String getValue() {
		return this.condition.toString();
	}
	
	public boolean evaluate( Interpreter beanShell ) throws ExpressionEvaluationException {
		
		try {
			return ExpressionUtils.evaluateToBoolean(
					this.condition, 
					beanShell);
			
		} catch (ExpressionEvaluationException e) {
			e.setInfo(
					this.condition.getStringExpression(),
					this.getQualifiedName());
			throw e;
			
		} catch (Exception e) {
			ExpressionEvaluationException e2 = new ExpressionEvaluationException(e);
			e2.setInfo(
					this.condition.getStringExpression(),
					this.getQualifiedName());
			throw e2;
		}
	}
}
