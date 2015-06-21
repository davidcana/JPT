package org.javapagetemplates.twoPhasesImpl.model.attributes.TAL;

import org.javapagetemplates.common.exceptions.EvaluationException;
import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.common.scripting.EvaluationHelper;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.javapagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.javapagetemplates.twoPhasesImpl.model.attributes.JPTAttributeImpl;
import org.javapagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;
import org.javapagetemplates.twoPhasesImpl.model.expressions.path.literals.BooleanLiteralExpression;

/**
 * <p>
 *   Allows to set an expression as condition. If it evaluates to 
 *   <code>false</code> the tag will be omitted, but its contents will
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
public class TALOmitTag extends JPTAttributeImpl implements DynamicAttribute {

	private static final long serialVersionUID = -7076944821961746650L;
	
	private JPTExpression condition;
	
	
	public TALOmitTag(){}
	public TALOmitTag(String namespaceUri, String expression) throws PageTemplateException {
		super( namespaceUri );
	
		this.condition = expression.isEmpty()?
				new BooleanLiteralExpression( true ):
				ExpressionUtils.generate( expression );
	}
	

	public JPTExpression getCondition() {
		return this.condition;
	}

	public void setCondition(JPTExpression condition) {
		this.condition = condition;
	}
	
	@Override
	public String getAttributeName() {
		return TwoPhasesPageTemplate.TAL_OMIT_TAG;
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
					this.getQualifiedName());
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
