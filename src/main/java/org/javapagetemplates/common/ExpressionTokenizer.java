package org.javapagetemplates.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.javapagetemplates.common.exceptions.ExpressionSyntaxException;

/**
 * <p>
 *   Divides an expression into simple tokens using a given 
 *   delimiter.
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
 * @version $Revision: 1.3 $
 */
public class ExpressionTokenizer {
    private String expression;

    private Iterator<Integer> iterator;
    private int currIndex = 0;
    private int delimiterCount = 0;
    
    public ExpressionTokenizer( String expression, char delimiter ) 
        throws ExpressionSyntaxException {
        this( expression, delimiter, false );
    }
    
    public ExpressionTokenizer( String exp, char delimiter, boolean escape ) 
        throws ExpressionSyntaxException {
    	
        String expression = new String ( exp );
        boolean avoidRepeatedSeparators = delimiter == ' ';
        
        // Go ahead and find delimiters, if any, at construction time
        List<Integer> delimiters = new ArrayList<Integer>( 10 );
        
        int parenLevel = 0;
        boolean inQuote = false;
        char previousCh = (char)0;
        
        // Scan for delimiters
        int length = expression.length();
        for ( int i = 0; i < length; i++ ) {
            char ch = expression.charAt(i);
            
            if ( ch == delimiter ) {
                // If delimiter is not buried in parentheses or a quote
                if ( parenLevel == 0 && ! inQuote  ) {
                	
                	if (avoidRepeatedSeparators && previousCh == delimiter){
                		continue;
                	}
                	
                    char nextCh = ( i + 1 < length ) ? expression.charAt( i + 1 ) : (char)0;
                    
                    // And if delimiter is not escaped
                    if ( ! ( escape && nextCh == delimiter ) ) {
                        this.delimiterCount++;
                        delimiters.add( i );
                    }
                    else {
                        // Somewhat inefficient way to pare the
                        // escaped delimiter down to a single
                        // character without breaking our stride
                        expression = expression.substring( 0, i + 1 ) +
                            expression.substring( i + 2 );
                        length--;
                    }
                }
            }
            
            // increment parenthesis level
            else if ( ch == '(' ) {
                parenLevel++;
            }
            
            // decrement parenthesis level
            else if ( ch == ')' ) {
                parenLevel--;
                // If unmatched right parenthesis
                if ( parenLevel < 0 ) {
                    throw new ExpressionSyntaxException
                        ( "syntax error: unmatched right parenthesis: " + expression );
                }
            }
            
            // start or end quote
            else if ( ch == '\'' ) {
                inQuote = ! inQuote;
            }
            
            previousCh = ch;
        }
        
        // If unmatched left parenthesis
        if ( parenLevel > 0 ) {
            throw new ExpressionSyntaxException
                ( "syntax error: unmatched left parenthesis: " + expression );
        }
        
        // If runaway quote
        if ( inQuote ) {
            throw new ExpressionSyntaxException
                ( "syntax error: runaway quotation: " + expression );
        }
        
        this.expression = expression;
        this.iterator = delimiters.iterator();
    }
    
    public boolean hasMoreTokens() {
        return this.currIndex < this.expression.length();
    }
    
    public String nextToken() {
    	
        if ( this.iterator.hasNext() ) {
            int delim = ((Integer)this.iterator.next()).intValue();
            String token = this.expression.substring( this.currIndex, delim ).trim();
            this.currIndex = delim + 1;
            this.delimiterCount--;
            
            return removeParenthesisIfAny(token);
        }
        
        String token = this.expression.substring( this.currIndex ).trim();
        this.currIndex = this.expression.length();
        
        return removeParenthesisIfAny(token);
    }
    
    static public String removeParenthesisIfAny(String token){
    	
		String effectiveToken = token.trim();
		
		if (effectiveToken.isEmpty()){
			return effectiveToken;
		}
		
		if (effectiveToken.charAt(0) == '('){
			return effectiveToken.substring(1, effectiveToken.lastIndexOf(')')).trim();		
			//return token.substring(1, token.length() - 1);
		}
		
		return effectiveToken;
    }
    
    public int countTokens() {
        if ( hasMoreTokens() ) {
            return this.delimiterCount + 1;
        }
        return 0;
    }
}
