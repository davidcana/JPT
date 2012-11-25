package org.javapagetemplates.twoPhasesImpl.model.attributes.TAL;

import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.twoPhasesImpl.HTMLFragment;
import org.javapagetemplates.twoPhasesImpl.NullContent;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.javapagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.javapagetemplates.twoPhasesImpl.model.attributes.JPTAttributeImpl;
import org.javapagetemplates.twoPhasesImpl.model.attributes.TextEscapableAttribute;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;

import bsh.Interpreter;

/**
 * <p>
 *   Allows to set the content of a tag from an expression.
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
public class TALContent extends JPTAttributeImpl implements DynamicAttribute, TextEscapableAttribute {

	private static final long serialVersionUID = -4354550333342649701L;

	static final NullContent NULL_CONTENT = new NullContent();
	
	private JPTExpression content;
	private boolean escapeOn = true;
	
	
	public TALContent(){}
	public TALContent(String namespaceUri, String expression) throws PageTemplateException {
		super(namespaceUri);
		configureTextEscapableAttribute( this, expression );
	}
	

	public JPTExpression getContent() {
		return this.content;
	}
	
	@Override
	public void setContent(JPTExpression content) {
		this.content = content;
	}
	
	public boolean isEscapeOn() {
		return this.escapeOn;
	}
	
	@Override
	public void setEscapeOn(boolean escapeOn) {
		this.escapeOn = escapeOn;
	}
	
	@Override
	public String getAttributeName() {
		return TwoPhasesPageTemplate.TAL_CONTENT;
	}
	
	@Override
	public String getValue() {
		return (this.escapeOn? "": TwoPhasesPageTemplate.STRING_EXPR_STRUCTURE)
				+ this.content.toString();
	}
	
	public Object evaluate( Interpreter beanShell ) throws ExpressionEvaluationException {
		
		try {
			Object result = this.content.evaluate(beanShell);
			
			if (result == null){
				return NULL_CONTENT;
			}
			
			// If escape is off return a HTMLFragment instance
			if (!this.escapeOn){
				return new HTMLFragment(result.toString());
			}
			
			// Escape is on
			
			// If result is a HTMLFragment escape it
			if (result instanceof HTMLFragment){
				return ((HTMLFragment)result).toString();
			}
			
			return result;
			/*
			return this.escapeOn? 
					result:
					new HTMLFragment(result.toString());*/
			
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
