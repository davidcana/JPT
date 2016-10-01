package org.zenonpagetemplates.twoPhasesImpl.model.attributes.I18N;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xnap.commons.i18n.I18n;
import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplateImpl;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.AttributesUtils;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.DynamicAttribute;
import org.zenonpagetemplates.twoPhasesImpl.model.attributes.ZPTAttributeImpl;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpression;

/**
 * <p>
 *   Defines a list of expressions to evaluate as a <code>List&lt;I18n&gt;</code>
 *   instance. This list will be used at the child tags to get the i18n strings.
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
public class I18NDomain extends ZPTAttributeImpl implements DynamicAttribute {

	private static final long serialVersionUID = 7815056548268774549L;
	
	private List<ZPTExpression> expressions = new ArrayList<ZPTExpression>();
	
	
	public I18NDomain(){}
	public I18NDomain( String namespaceURI, String expression ) throws PageTemplateException {
		super( namespaceURI );
		this.expressions = AttributesUtils.getExpressions( expression );
	}

	public List<ZPTExpression> getExpressions() {
		return this.expressions;
	}

	public void setExpressions( List<ZPTExpression> expressions ) {
		this.expressions = expressions;
	}

	public void addExpressions( ZPTExpression expressions ){
		this.expressions.add( expressions );
	}
	
	@Override
	public String getAttributeName() {
		return TwoPhasesPageTemplate.I18N_DOMAIN;
	}
	
	@Override
	public String getValue() {
		return AttributesUtils.getStringFromExpressions( this.expressions );
	}
	
	public void process( EvaluationHelper evaluationHelper, 
			List<String> varsToUnset, Map<String, Object> varsToSet ) throws EvaluationException {
		
		ZPTExpression expression = null;
        try {
            List<I18n> i18nList = new ArrayList<I18n>();
            for ( ZPTExpression zptExpression: this.expressions ){
            	expression = zptExpression;
            	i18nList.add( (I18n) zptExpression.evaluate( evaluationHelper ) );
            }
            
            TwoPhasesPageTemplateImpl.setVar(
            		evaluationHelper, 
            		varsToUnset, 
            		varsToSet, 
            		TwoPhasesPageTemplateImpl.I18N_DOMAIN_VAR_NAME, 
            		i18nList );
            
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
