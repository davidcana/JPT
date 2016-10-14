package org.zenonpagetemplates.twoPhasesImpl.model.expressions.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.zenonpagetemplates.common.ExpressionTokenizer;
import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.exceptions.ExpressionSyntaxException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.zenonpagetemplates.twoPhasesImpl.model.expressions.ZPTExpressionImpl;

/**
 * <p>
 *   Evaluates a list.
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
 *
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.2 $
 */
public class ListExpression extends ZPTExpressionImpl implements FirstPathToken {

	private static final long serialVersionUID = -772976815563014592L;
	private List<ListExpressionItem> items = new ArrayList<ListExpressionItem>();
	
	public ListExpression(){}

	public ListExpression( String stringExpression, List<ListExpressionItem> items ){
		super( stringExpression );
		
		this.items = items;
	}


	public List<ListExpressionItem> getItems() {
		return items;
	}

	public void setItems(List<ListExpressionItem> items) {
		this.items = items;
	}

	@Override
	public Object evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		return evaluate( this.items, evaluationHelper );
	}
	
	@SuppressWarnings("unchecked")
	static public Object evaluate( List<ListExpressionItem> items, EvaluationHelper evaluationHelper ) throws EvaluationException {
		
		List<Object> result = new ArrayList<Object>();
		
		for ( ListExpressionItem item : items ){
			Object evaluate = item.evaluate( evaluationHelper );
			if ( evaluate instanceof List ){
				result.addAll( ( Collection<? extends Object> ) evaluate );
			} else {
				result.add( evaluate );
			}
		}
		
		return result;
	}
	
	static public ListExpression generate( String stringExpression ) throws ExpressionSyntaxException {
		
        if ( stringExpression.isEmpty() || stringExpression.charAt( 0 ) != '[' || stringExpression.charAt( stringExpression.length() - 1 ) != ']' ) {
            return null;
        }

        String listExp = stringExpression.substring( 1, stringExpression.length() - 1 ).trim();
        List<ListExpressionItem> items = new ArrayList<ListExpressionItem>();
        ExpressionTokenizer segments = new ExpressionTokenizer( listExp, TwoPhasesPageTemplate.EXPRESSION_DELIMITER );
        
        while ( segments.hasMoreTokens() ) {
            String segment = segments.nextToken().trim();
            RangeExpression rangeExpression = RangeExpression.generate( segment );
            
            if ( rangeExpression != null ){
                items.add( rangeExpression );
            } else {
            	items.add(
            			ExpressionUtils.generate( segment ) );
            }
        }
        
		return new ListExpression( stringExpression, items );
	}

}
