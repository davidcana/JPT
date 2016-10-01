package org.zenonpagetemplates.twoPhasesImpl.model.attributes.TAL;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.ZPTAttributeImpl;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpression;

/**
 * <p>
 *   Allows to set an expression as a condition. If it evaluates to 
 *   <code>false</code> the tag, attributes and contents will not 
 *   be generated.
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
public class TALCondition extends ZPTAttributeImpl implements DynamicAttribute {

	private static final long serialVersionUID = 4626938652290394973L;
	
	private ZPTExpression condition;
	
	
	public TALCondition(){}
	public TALCondition( String namespaceURI, String expression ) throws PageTemplateException {
		super( namespaceURI );
		this.condition = ExpressionUtils.generate( expression );
	}
	

	public ZPTExpression getCondition() {
		return this.condition;
	}

	public void setCondition( ZPTExpression condition ) {
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
	
	public boolean evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		
		try {
			return ExpressionUtils.evaluateToBoolean(
					this.condition, 
					evaluationHelper );
			
		} catch ( EvaluationException e ) {
			e.setInfo(
					this.condition.getStringExpression(),
					this.getQualifiedName() );
			throw e;
			
		} catch ( Exception e ) {
			EvaluationException e2 = new EvaluationException( e );
			e2.setInfo(
					this.condition.getStringExpression(),
					this.getQualifiedName() );
			throw e2;
		}
	}
}
