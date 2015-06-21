package org.javapagetemplates.twoPhasesImpl.model.expressions.path;

import java.util.ArrayList;
import java.util.List;

import org.javapagetemplates.common.exceptions.EvaluationException;
import org.javapagetemplates.common.exceptions.ExpressionSyntaxException;
import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.common.scripting.EvaluationHelper;
import org.javapagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpressionImpl;
import org.javapagetemplates.twoPhasesImpl.model.expressions.StringExpression;

/**
 * <p>
 *   Defines an array access to an expression of array type.
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
 * @version $Revision: 1.0 $
 */
public class ArrayExpression extends JPTExpressionImpl implements FirstPathToken, NextPathToken {

	private static final long serialVersionUID = 5624721214284460380L;
	
	private JPTExpression arrayBase;
	private List<JPTExpression> indexes = new ArrayList<JPTExpression>();
	
	public ArrayExpression(){}

	public ArrayExpression( String stringExpression, JPTExpression arrayBase ){
		super( stringExpression );
		
		this.arrayBase = arrayBase;
	}

	public List<JPTExpression> getIndexes() {
		return this.indexes;
	}

	public void setIndexes( List<JPTExpression> indexes ) {
		this.indexes = indexes;
	}

	public void addIndex( JPTExpression index ){
		this.indexes.add( index );
	}
	
	@Override
	public Object evaluate( EvaluationHelper evaluationHelper ) throws EvaluationException {
		return evaluate( this.arrayBase, this.indexes, evaluationHelper );
	}
	
	static public Object evaluate( JPTExpression arrayBase, List<JPTExpression> indexes, EvaluationHelper evaluationHelper ) 
			throws EvaluationException {
		
		try {
			Object object = arrayBase.evaluate( evaluationHelper );
    		
			if ( object == null ){
				return null;
			}
			
			if ( ! object.getClass().isArray() ) {
    			throw new EvaluationException( 
    					arrayBase.getStringExpression() + " is not an array: " + object.getClass() );
    		}
			
			return evaluateArrayItem( object, indexes, evaluationHelper );

		} catch ( Exception e ) {
			throw new EvaluationException( e );
		}
	}
	
	static private Object evaluateArrayItem( Object object, List<JPTExpression> indexExpressions, EvaluationHelper evaluationHelper ) 
			throws PageTemplateException {
		
		Integer index = ExpressionUtils.evaluateToNumber(
				indexExpressions.get( 0 ), 
				evaluationHelper ).intValue();
		
		if ( ! object.getClass().isArray() ) {
			throw new EvaluationException( 
					"Element in '" + object.toString() + "' is not an array: " + object.getClass() );
		}
		
		if ( object.getClass().getComponentType().isPrimitive() ) {
			object = convertPrimitiveArray( object );
		}
		
		Object[] arrayInstance = ( Object[] ) object;
		
		if ( indexExpressions.size() == 1 ){	
			return arrayInstance[ index ];
		}
		
		List<JPTExpression> newIndexExpressions = new ArrayList<JPTExpression>( indexExpressions );
		newIndexExpressions.remove( 0 );
		
		return evaluateArrayItem (
				arrayInstance[ index ], 
				newIndexExpressions,
				evaluationHelper );
	}
	
    static public final Object evaluate( String tok, Object res, String acc, EvaluationHelper evaluationHelper ) 
    	throws ExpressionSyntaxException, EvaluationException {
    	
    	Object result = res;
    	String token = tok;
    	String accessor = acc;
    	try {
    		// Array accessor must begin and end with brackets
    		int close = accessor.indexOf( ']' );
    		if ( accessor.charAt(0) != '[' || close == -1 ) {
    			throw new ExpressionSyntaxException( 
    					"Bad array accessor for " + token + ": "  + accessor );
    		}

    		// Array accessor must operate on an array
    		if ( !  result.getClass().isArray() ) {
    			throw new EvaluationException( 
    					token + " is not an array: " + result.getClass() );
    		}

    		if ( result.getClass().getComponentType().isPrimitive() ) {
    			result = convertPrimitiveArray( result );
    		}
    		Object[] array = (Object[])result;
    		Object index = StringExpression.evaluate( 
    				accessor.substring( 1, close ), 
    				evaluationHelper );
    		if ( ! ( index instanceof Integer ) ) {
    			throw new EvaluationException( "Array index must be an integer" );
    		}
    		result = array[ ( ( Integer) index ).intValue() ];

    		// Continue evaluating array access for multidimensional arrays
    		close++;
    		if ( accessor.length() > close ) {
    			token += accessor.substring( 0, close );
    			accessor = accessor.substring( close );
    			result = evaluate( token, result, accessor, evaluationHelper );
    		}
    		return result;
    		
    	} catch ( ArrayIndexOutOfBoundsException e ) {
    		throw new EvaluationException( e );
    	}
    }

	
    private static final int SCANNING = 0;
    private static final int IN_PAREN = 1;
    private static final int IN_QUOTE = 2;
    public static final int findArrayAccessor( String token ) {
        int length = token.length();
        int state = SCANNING;
        int parenDepth = 0;
        for ( int i = 0; i < length; i++ ) {
            char ch = token.charAt( i );
            switch( state ) {
                case IN_PAREN:
                    if ( ch == ')' ) {
                        parenDepth--;
                        if ( parenDepth == 0 ) {
                            state = SCANNING;
                        }
                    }
                    else if ( ch == '(' ) {
                        parenDepth++;
                    }
                    break;
                    
                case IN_QUOTE:
                    if ( ch == '\'' ) {
                        state = SCANNING;
                    }
                    break;
                    
                case SCANNING:
                    if ( ch == '\'' ) {
                        state = IN_QUOTE;
                    }
                    else if ( ch == '(' ) {
                        parenDepth++;
                        state = IN_PAREN;
                    }
                    else if ( ch == '[' ) {
                        return i;
                    }
            }
        }
        return -1;
    }
    
    /**
     * Oh curse Java!
     */
    static public final Object[] convertPrimitiveArray( Object o ) {
        Object[] newArray = null;
        if ( o instanceof int[] ) {
            int[] oldArray = ( int[] ) o;
            newArray = new Integer[ oldArray.length ];
            for ( int i = 0; i < oldArray.length; i++ ) {
                newArray[ i ] = new Integer( oldArray[ i ] );
            }
        }
        else if ( o instanceof long[] ) {
            long[] oldArray = ( long[] ) o;
            newArray = new Long[ oldArray.length ];
            for ( int i = 0; i < oldArray.length; i++ ) {
                newArray[ i ] = new Long( oldArray[ i ] );
            }
        }
        else if ( o instanceof boolean[] ) {
            boolean[] oldArray = ( boolean[] ) o;
            newArray = new Boolean[ oldArray.length ];
            for ( int i = 0; i < oldArray.length; i++ ) {
                newArray[ i ] = new Boolean( oldArray[ i ] );
            }
        }
        else if ( o instanceof char[] ) {
            char[] oldArray = ( char[] ) o;
            newArray = new Character[ oldArray.length ];
            for ( int i = 0; i < oldArray.length; i++ ) {
                newArray[ i ] = new Character( oldArray[i] );
            }
        }
        else if ( o instanceof byte[] ) {
            byte[] oldArray = ( byte[] ) o;
            newArray = new Byte[ oldArray.length ];
            for ( int i = 0; i < oldArray.length; i++ ) {
                newArray[ i ] = new Byte( oldArray[ i ] );
            }
        }
        else if ( o instanceof float[] ) {
            float[] oldArray = ( float[] ) o;
            newArray = new Float[ oldArray.length ];
            for ( int i = 0; i < oldArray.length; i++ ) {
                newArray[ i ] = new Float( oldArray[ i ] );
            }
        }
        else if ( o instanceof double[] ) {
            double[] oldArray = ( double[] ) o;
            newArray = new Double[ oldArray.length ];
            for ( int i = 0; i < oldArray.length; i++ ) {
                newArray[ i ] = new Double( oldArray[ i ] );
            }
        }
        else if ( o instanceof short[] ) {
            short[] oldArray = ( short[] ) o;
            newArray = new Short[ oldArray.length ];
            for ( int i = 0; i < oldArray.length; i++ ) {
                newArray[ i ] = new Short( oldArray[ i ] );
            }
        }
        return newArray;
    }

	@Override
	public Object evaluate( Object parent, EvaluationHelper evaluationHelper ) throws EvaluationException {
		
		try {
			String token = new String(this.stringExpression);
			
			// Separate identifier from any array accessors
			String arrayAccessor = null;
			int bracket = ArrayExpression.findArrayAccessor( token );
			if ( bracket != -1 ) {
			    arrayAccessor = token.substring( bracket ).trim();
			    token = token.substring( 0, bracket ).trim();
			}

			return evaluate( 
					token, parent, arrayAccessor, evaluationHelper ) ;
			
		} catch ( ExpressionSyntaxException e ) {
			throw new EvaluationException( e );
		}
	}

    static public final ArrayExpression generate( String tok, JPTExpression arrayBase, String acc ) throws ExpressionSyntaxException {
    	
    	String accessor = new String( acc );
    	String token = new String( tok );
    	try {
    		ArrayExpression result = new ArrayExpression( token, arrayBase );
    		
    		boolean done = false;
    		
    		while ( ! done ){
    			
        		// Array accessor must begin and end with brackets
        		int close = accessor.indexOf( ']' );
        		if ( accessor.charAt(0) != '[' || close == -1 ) {
        			throw new ExpressionSyntaxException( 
        					"bad array accessor for " + token + ": "  + accessor );
        		}

        		// Get index JPTExpression
        		JPTExpression index = ExpressionUtils.generate( 
        				accessor.substring( 1, close ) );
        		result.addIndex( index );
        		
        		// continue processing array access for multidimensional arrays
        		close++;
        		if ( accessor.length() > close ) {
        			token += accessor.substring( 0, close );
        			accessor = accessor.substring( close );
        		} else {
        			done = true;
        		}
    		}

    		return result;
    		
    	} catch ( ArrayIndexOutOfBoundsException e ) {
    		throw new ExpressionSyntaxException( e );
    	}
    }
    
    @Override
    public String toString(){
    	return this.stringExpression;
    }
}
