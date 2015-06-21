package org.javapagetemplates.twoPhasesImpl.model.attributes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.javapagetemplates.common.ExpressionTokenizer;
import org.javapagetemplates.common.exceptions.ExpressionSyntaxException;
import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.common.scripting.EvaluationHelper;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplate;
import org.javapagetemplates.twoPhasesImpl.TwoPhasesPageTemplateImpl;
import org.javapagetemplates.twoPhasesImpl.model.JPTDocument;
import org.javapagetemplates.twoPhasesImpl.model.expressions.ExpressionUtils;
import org.javapagetemplates.twoPhasesImpl.model.expressions.JPTExpression;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <p>
 *   Utility methods for working with XML attributes.
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
public class AttributesUtils {
	
	static public List<JPTExpression> getExpressions( String string ) throws PageTemplateException {
		
		List<JPTExpression> result = new ArrayList<JPTExpression>();
		
		String element = string.trim();
		
        ExpressionTokenizer tokens = new ExpressionTokenizer( 
        		element, 
        		TwoPhasesPageTemplate.IN_DEFINE_DELIMITER, true );
        
        while( tokens.hasMoreTokens() ) {
            String expression = tokens.nextToken().trim();
            result.add(
            		ExpressionUtils.generate( expression ) );
        }
		
		return result;
	}
	
	static public String getStringFromExpressions( List<JPTExpression> expressions ) {
		
		StringBuilder result = new StringBuilder();
		
		Iterator<JPTExpression> i = expressions.iterator();
		
		while ( i.hasNext() ){
			JPTExpression jptExpression = i.next();
			result.append( jptExpression.toString() );
			if ( i.hasNext() ){
				result.append( '\n' );
			}
		}
		
		return result.toString();
	}
	
	static public KeyValuePair<JPTExpression> getDefinition( String string ) throws PageTemplateException {
		
        String variable = string.trim();
        int space = variable.indexOf( TwoPhasesPageTemplate.IN_DEFINE_DELIMITER );
        if ( space == -1 ) {
            throw new ExpressionSyntaxException( "bad expression: " + variable );
        }
        
        String name = variable.substring( 0, space );
        String expression = variable.substring( space + 1 );
        
        return new KeyValuePair<JPTExpression>(
        		name,
        		ExpressionUtils.generate( expression ) );
	}
	
	static public List<KeyValuePair<JPTExpression>> getDefinitions( String string ) throws PageTemplateException {
		
		List<KeyValuePair<JPTExpression>> result = new ArrayList<KeyValuePair<JPTExpression>>();
		
        ExpressionTokenizer tokens = new ExpressionTokenizer( 
        		string, 
        		TwoPhasesPageTemplate.DEFINE_DELIMITER, true );
        
        while ( tokens.hasMoreTokens() ) {
            String variable = tokens.nextToken().trim();
            int space = variable.indexOf( TwoPhasesPageTemplate.IN_DEFINE_DELIMITER );
            if ( space == -1 ) {
                throw new ExpressionSyntaxException( 
                		"Bad variable definition: " + variable + " in template");
            }

            String name = variable.substring( 0, space );
            String expression = variable.substring( space + 1 ).trim();
            
            KeyValuePair<JPTExpression> keyValuePair = new KeyValuePair<JPTExpression>(
            		name,
            		ExpressionUtils.generate( expression ) );
            result.add( keyValuePair );
        }
		
		return result;
	}
	
	static public <T> String getStringFromDefinitions( List<KeyValuePair<T>> definitions ){
		
		StringBuilder result = new StringBuilder();
		Iterator<KeyValuePair<T>> i = definitions.iterator();
		
		while ( i.hasNext() ){
			KeyValuePair<T> keyValuePair = i.next();
			result.append( keyValuePair.toString(" ") );
			if ( i.hasNext() ){
				result.append( TwoPhasesPageTemplate.DEFINE_DELIMITER );
				result.append( '\n' );
			}
		}
		
		return result.toString();
	}
	
	static public <T> String getStringFromDefinition( KeyValuePair<T> definition ){
		return definition.toString( " " );
	}
	
	static public List<KeyValuePair<String>> getDefinitionsFromString( String string ) throws PageTemplateException {
		
		List<KeyValuePair<String>> result = new ArrayList<KeyValuePair<String>>();
		
        ExpressionTokenizer tokens = new ExpressionTokenizer( 
        		string, 
        		TwoPhasesPageTemplate.DEFINE_DELIMITER, true );
        
        while ( tokens.hasMoreTokens() ) {
            String variable = tokens.nextToken().trim();
            int space = variable.indexOf( TwoPhasesPageTemplate.IN_DEFINE_DELIMITER );
            if ( space == -1 ) {
                throw new ExpressionSyntaxException( 
                		"Bad variable definition: " + variable + " in template");
            }

            String name = variable.substring( 0, space );
            String expression = variable.substring( space + 1 ).trim();
            
            KeyValuePair<String> keyValuePair = new KeyValuePair<String>(
            		name,
            		expression );
            result.add( keyValuePair );
        }
		
		return result;
	}

	static public void processStaticAttributes( List<StaticAttribute> staticAttributes,
			EvaluationHelper evaluationHelper, AttributesImpl attributesImpl , JPTDocument jptDocument )
			throws PageTemplateException {
		
		for ( StaticAttribute attribute: staticAttributes ){
            String qualifiedName = attribute.getQualifiedName();
            Object value = attribute.getValue();
            
            addAttribute( qualifiedName, value, attributesImpl, jptDocument );
    	}
	}
	
	static public void addAttribute( String qualifiedName, Object value,
			AttributesImpl attributesImpl, JPTDocument jptDocument ) {
		
		removeAttribute( attributesImpl, qualifiedName );
		
		if ( value != null ) {
		    String name = TwoPhasesPageTemplateImpl.VOID_STRING;
		    String uri = TwoPhasesPageTemplateImpl.VOID_STRING;
            int colon = qualifiedName.indexOf( ':' );
            if ( colon != -1 ) {
                String prefix = qualifiedName.substring( 0, colon );
                name = qualifiedName.substring( colon + 1 );
                uri = jptDocument.getNamespace( prefix );
            }
		    attributesImpl.addAttribute( 
		    		uri, 
		    		name, 
		    		qualifiedName, 
		    		TwoPhasesPageTemplateImpl.CDATA, 
		    		String.valueOf( value ) );
		}
	}
	
	static private void removeAttribute( AttributesImpl attributes, String qualifiedName ) {
        int index = attributes.getIndex( qualifiedName );
        if ( index != -1 ) {
            attributes.removeAttribute( index );
        }
    }

}
