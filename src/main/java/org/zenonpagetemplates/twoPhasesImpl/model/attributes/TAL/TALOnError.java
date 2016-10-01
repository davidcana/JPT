package org.zenonpagetemplates.twoPhasesImpl.model.attributes.TAL;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.HTMLFragment;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.ZPTAttributeImpl;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.TextEscapableAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpression;

/**
 * <p>
 *   Allows to set an expression that will be evaluated if the contents
 *   of the tag throws an exception.
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
public class TALOnError extends ZPTAttributeImpl implements DynamicAttribute, TextEscapableAttribute {

	private static final long serialVersionUID = 4194311049933172978L;
	private ZPTExpression content;
	private boolean escapeOn = true;
	
	
	public TALOnError(){}
	public TALOnError(String namespaceURI, String expression) throws PageTemplateException {
		super( namespaceURI );
		configureTextEscapableAttribute( this, expression );
	}
	
	
	public ZPTExpression getContent() {
		return content;
	}

	@Override
	public void setContent(ZPTExpression content) {
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
	
	public Object evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		
		try {
			Object result = this.content.evaluate( evaluationHelper );
			
			//if (result == null){
			//	return TALContent.NULL_CONTENT;
			//}
			
			return this.escapeOn? 
					result:
					new HTMLFragment( result.toString() );
			
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
