package org.javapagetemplates.twoPhasesImpl;

import bsh.Interpreter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.javapagetemplates.common.PageTemplate;
import org.javapagetemplates.common.exceptions.ExpressionEvaluationException;
import org.javapagetemplates.twoPhasesImpl.model.attributes.TAL.TALRepeat;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;
import org.javapagetemplates.twoPhasesImpl.model.expressions.path.ArrayExpression;

/**
 * <p>
 *   Makes it easy to iterate through JPT elements in a loop.
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
 *
 * @author <a href="mailto:chris@christophermrossi.com">Chris Rossi</a>
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.1 $
 */
public class Loop {
	private boolean forceExit = false;
	private boolean once = false;
	private String variableName;
	@SuppressWarnings("rawtypes")
	private Iterator iterator;
	private int index = -1;
	private int length = -1; 
	private TALRepeat talRepeat;


	@SuppressWarnings("rawtypes")
	public Loop( TALRepeat talRepeat, Interpreter beanShell, List<String> varsToUnset, Map<String, Object> varsToSet )
			throws ExpressionEvaluationException {

		this.talRepeat = talRepeat;
		JPTExpression expression = null;
		try {
			expression = this.talRepeat.getRepeat().getValue();

			if ( expression == null ) {
				throw new ExpressionEvaluationException("null expression in loop");   
			}

			this.variableName = talRepeat.getRepeat().getKey();
			Object loop = expression.evaluate( beanShell );

			if ( loop == null){
				this.forceExit = true;
				return;

			} else if ( loop instanceof Iterator ) {
				this.iterator = (Iterator)loop;

			} else if ( loop instanceof Collection ) {
				this.iterator = ((Collection)loop).iterator();
				this.length = ((Collection)loop).size();

			} else if ( loop.getClass().isArray() ) {
				if ( loop.getClass().getComponentType().isPrimitive() ) {
					loop = ArrayExpression.convertPrimitiveArray( loop );
				}
				this.iterator = Arrays.asList((Object[])loop).iterator();
				this.length = ((Object[])loop).length;

			} else {
				throw new ClassCastException
				( "result of repeat expression must evaluate to an array, java.util.Iterator " +
						"or java.util.Collection: " +
						expression + ": evaluates to " + loop.getClass().getName() );
			}

			this.registerVars(beanShell, varsToUnset, varsToSet);

		} catch (ExpressionEvaluationException e) {
			e.setInfo(
					expression == null? null: expression.getStringExpression(),
							talRepeat.getQualifiedName());
			throw e;

		} catch (Exception e) {
			ExpressionEvaluationException e2 = new ExpressionEvaluationException(e);
			e2.setInfo(
					expression == null? null: expression.getStringExpression(),
							talRepeat.getQualifiedName());
			throw e2;
		}
	}

	private void registerVars(Interpreter beanShell, List<String> varsToUnset,
			Map<String, Object> varsToSet) throws ExpressionEvaluationException {

		try {
			TwoPhasesPageTemplateImpl.setVar(
					beanShell, 
					varsToUnset, 
					varsToSet, 
					this.variableName, 
					null);

			TwoPhasesPageTemplateImpl.setVar(
					beanShell, 
					varsToUnset, 
					varsToSet, 
					PageTemplate.REPEAT_VAR_NAME, 
					null);

		} catch (Exception e) {
			throw new ExpressionEvaluationException(e);
		}
	}

	public boolean repeat( Interpreter beanShell ) throws ExpressionEvaluationException {

		try {
			if (this.forceExit){
				return false;
			}

			if ( this.iterator == null ) {
				if ( ! this.once ) {
					this.once = true;
					return true;
				}
				return false;

			} else if ( this.iterator.hasNext() ) {
				this.index++;
				beanShell.set( this.variableName, this.iterator.next() );
				beanShell.set( PageTemplate.REPEAT_VAR_NAME, this );
				return true;

			} else {
				return false;
			}

		} catch (Exception e) {
			ExpressionEvaluationException e2 = new ExpressionEvaluationException(e);
			e2.setInfo(
					this.talRepeat.getValue(),
					this.talRepeat.getQualifiedName());
			throw e2;
		}
	}

	public int getIndex() {
		return this.index;
	}

	public int getNumber() {
		return this.index + 1;
	}

	public boolean isEven() {
		return this.index % 2 == 0;
	}

	public boolean isOdd() {
		return this.index % 2 == 1;
	}

	public boolean isStart() {
		return this.index == 0;
	}

	public boolean isEnd() {
		return ! this.iterator.hasNext();
	}

	/**
	 * will be undefined (-1) if expression evaluates to Iterator
	 */
	 public int getLength() {
		 return this.length;
	 }

	public String getLetter() {
		return formatLetter( this.index, 'a' );
	}

	public String getCapitalLetter() {
		return formatLetter( this.index, 'A' );
	}

	static String formatLetter( int n ) {
		return formatLetter( n - 1, 'a' );
	}

	static String formatCapitalLetter( int n ) {
		return formatLetter( n - 1, 'A' );
	}

	private static String formatLetter( int ind, char start ) {
		int index = ind;
		StringBuffer buffer = new StringBuffer( 2 );
		int digit = index % 26;
		buffer.append( (char)(start + digit) );
		while( index > 25 ) {
			index /= 26;
			digit = (index - 1 ) % 26;
			buffer.append( (char)(start + digit) );
		}
		return buffer.reverse().toString();
	}

	public String getRoman() {
		return formatRoman( this.index + 1, 0 );
	}

	public String getCapitalRoman() {
		return formatRoman( this.index + 1, 1 );
	}

	static String formatRoman( int n ) {
		return formatRoman( n, 0 );
	}

	static String formatCapitalRoman( int n ) {
		return formatRoman( n, 1 );
	}

	static String formatRoman( int nn, int capital ) {

		int n = nn;

		// Can't represent any number 4000 or greater
		if ( n >= 4000 ) {
			return "<overflow>";
		}

		StringBuffer buf = new StringBuffer(12);
		for ( int decade = 0; n != 0; decade ++ ) {
			int digit = n % 10;
			if ( digit > 0 ) {
				digit--;
				buf.append( roman[decade][digit][capital] );
			}
			n /= 10;
		}

		buf.reverse();
		return buf.toString();
	}

	static final String[][][] roman = {
		/* One's place */
		{
			{ "i", "I" },
			{ "ii", "II" }, 
			{ "iii", "III" },
			{ "vi", "VI" },
			{ "v", "V" },
			{ "iv", "IV" },
			{ "iiv", "IIV" },
			{ "iiiv", "IIIV" },
			{ "xi", "XI" },
		},

		/* 10's place */
		{
			{ "x", "X" },
			{ "xx", "XX" },
			{ "xxx", "XXX" },
			{ "lx", "LX" },
			{ "l", "L" },
			{ "xl", "XL" },
			{ "xxl", "XXL" },
			{ "xxxl", "XXXL" },
			{ "cx", "CX" },
		},

		/* 100's place */
		{
			{ "c", "C" },
			{ "cc", "CC" },
			{ "ccc", "CCC" },
			{ "dc", "DC" },
			{ "d", "D" },
			{ "cd", "CD" },
			{ "ccd", "CCD" },
			{ "cccd", "CCCD" },
			{ "mc", "MC" },
		},

		/* 1000's place */
		{
			{ "m", "M" },
			{ "mm", "MM" },
			{ "mmm", "MMM" }
		}
	};
}

