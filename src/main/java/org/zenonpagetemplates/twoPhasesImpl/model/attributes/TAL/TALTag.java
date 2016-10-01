package org.zenonpagetemplates.twoPhasesImpl.model.attributes.TAL;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.JPTAttributeImpl;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.JPTExpression;

/**
 * <p>
 *   Allows to set an expression as the tag name ignoring the tag name 
 *   in the template, unless it evaluates to <code>null</code>.
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
public class TALTag extends JPTAttributeImpl implements DynamicAttribute {
	
	private static final long serialVersionUID = -3886897632750397602L;
	
	private JPTExpression expression;
	
	
	public TALTag(){}
	public TALTag( String namespaceUri, String expression ) throws PageTemplateException {
		super( namespaceUri );
		this.expression = ExpressionUtils.generate( expression );
	}
	

	public JPTExpression getExpression() {
		return this.expression;
	}

	public void setExpression( JPTExpression condition ) {
		this.expression = condition;
	}
	
	@Override
	public String getAttributeName() {
		return TwoPhasesPageTemplate.TAL_TAG;
	}
	
	@Override
	public String getValue() {
		return this.expression.toString();
	}
	
	public String evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		
		try {
			return ( String ) this.expression.evaluate( evaluationHelper );
			
		} catch ( EvaluationException e ) {
			e.setInfo(
					this.expression.getStringExpression(),
					this.getQualifiedName() );
			throw e;
			
		} catch ( Exception e ) {
			EvaluationException e2 = new EvaluationException( e );
			e2.setInfo(
					this.expression.getStringExpression(),
					this.getQualifiedName() );
			throw e2;
		}
	}
}
