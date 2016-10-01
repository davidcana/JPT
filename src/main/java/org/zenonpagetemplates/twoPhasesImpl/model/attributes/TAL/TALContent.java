package org.zenonpagetemplates.twoPhasesImpl.model.attributes.TAL;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.HTMLFragment;
import org.zenonpagetemplates.twoPhasesImpl.NullContent;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.ZPTAttributeImpl;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.TextEscapableAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpression;

/**
 * <p>
 *   Allows to set the content of a tag from an expression.
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
public class TALContent extends ZPTAttributeImpl implements DynamicAttribute, TextEscapableAttribute {

	private static final long serialVersionUID = -4354550333342649701L;

	static final NullContent NULL_CONTENT = new NullContent();
	
	private ZPTExpression content;
	private boolean escapeOn = true;
	
	
	public TALContent(){}
	public TALContent(String namespaceURI, String expression) throws PageTemplateException {
		super( namespaceURI );
		configureTextEscapableAttribute( this, expression );
	}
	

	public ZPTExpression getContent() {
		return this.content;
	}
	
	@Override
	public void setContent(ZPTExpression content) {
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
		return ( this.escapeOn? "": TwoPhasesPageTemplate.STRING_EXPR_STRUCTURE )
				+ this.content.toString();
	}
	
	public Object evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		
		try {
			Object result = this.content.evaluate( evaluationHelper );
			
			if ( result == null ){
				return NULL_CONTENT;
			}
			
			// If escape is off return a HTMLFragment instance
			if ( ! this.escapeOn ){
				return new HTMLFragment( result.toString() );
			}
			
			// Escape is on
			
			// If result is a HTMLFragment escape it
			if ( result instanceof HTMLFragment ){
				return ( ( HTMLFragment ) result ).toString();
			}
			
			return result;
			/*
			return this.escapeOn? 
					result:
					new HTMLFragment(result.toString());*/
			
		} catch ( EvaluationException e ) {
			e.setInfo(
					this.content.getStringExpression(),
					this.getQualifiedName() );
			throw e;
			
		} catch ( Exception e ) {
			EvaluationException e2 = new EvaluationException( e );
			e2.setInfo(
					this.content.getStringExpression(),
					this.getQualifiedName() );
			throw e2;
		}
	}
}
