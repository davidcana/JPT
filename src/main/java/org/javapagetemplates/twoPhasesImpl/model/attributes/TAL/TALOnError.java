package org.javapagetemplates.twoPhasesImpl.model.attributes.TAL;

import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.twoPhasesImpl.HTMLFragment;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.javapagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.javapagetemplates.twoPhasesImpl.model.attributes.JPTAttributeImpl;
import org.javapagetemplates.twoPhasesImpl.model.attributes.TextEscapableAttribute;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;

import bsh.Interpreter;

/**
 * <p>
 *   Allows to set an expression that will be evaluated if the contents
 *   of the tag throws an exception.
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
public class TALOnError extends JPTAttributeImpl implements DynamicAttribute, TextEscapableAttribute {

	private static final long serialVersionUID = 4194311049933172978L;
	private JPTExpression content;
	private boolean escapeOn = true;
	
	
	public TALOnError(){}
	public TALOnError(String namespaceUri, String expression) throws PageTemplateException {
		super(namespaceUri);
		configureTextEscapableAttribute( this, expression );
	}
	
	
	public JPTExpression getContent() {
		return content;
	}

	@Override
	public void setContent(JPTExpression content) {
		this.content = content;
	}
	
	@Override
	public String getAttributeName() {
		return TwoPhasesPageTemplate.TAL_ON_ERROR;
	}
	
	@Override
	public String getValue() {
		return this.content.toString();
	}
	
	@Override
	public void setEscapeOn(boolean escapeOn) {
		this.escapeOn = escapeOn;
	}
	
	public boolean isEscapeOn() {
		return escapeOn;
	}
	
	public Object evaluate( Interpreter beanShell ) throws ExpressionEvaluationException {
		
		try {
			Object result = this.content.evaluate(beanShell);
			
			//if (result == null){
			//	return TALContent.NULL_CONTENT;
			//}
			
			return this.escapeOn? 
					result:
					new HTMLFragment(result.toString());
			
		} catch (ExpressionEvaluationException e) {
			e.setInfo(
					this.content.getStringExpression(),
					this.getQualifiedName());
			throw e;
			
		} catch (Exception e) {
			ExpressionEvaluationException e2 = new ExpressionEvaluationException(e);
			e2.setInfo(
					this.content.getStringExpression(),
					this.getQualifiedName());
			throw e2;
		}
	}
}
