package org.zenonpagetemplates.common.scripting.groovy;

import java.util.Map;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;

import groovy.lang.Binding;
import groovy.lang.MissingPropertyException;

/**
 * <p>
 *   Simple class that implements EvaluationHelper for Groovy.
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
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.2 $
 */

public class GroovyEvaluationHelper implements EvaluationHelper {
	
	private Binding binding;
	
	GroovyEvaluationHelper(){
		this.binding = new Binding();
	}
	
	public GroovyEvaluationHelper( Binding binding ){
		this.binding = binding;
	}

	public Binding getBinding() {
		return this.binding;
	}

	@Override
	public void unset( String name ) throws EvaluationException {
		
		try {
			this.binding.getVariables().remove( name );
			
		} catch ( Exception e ) {
			throw new EvaluationException( "Error trying to unset variable '" + name + "'." );
		}
	}

	@Override
	public void set( String name, Object value ) throws EvaluationException {
		
		try {
			this.binding.setVariable( name, value );
			
		} catch ( Exception e ) {
			throw new EvaluationException( "Error trying to set variable '" + name + "' with value '" +  value + "'." );
		}
	}
	

	@Override
	public Object get( String name ) throws EvaluationException {
		
		try {
			return this.binding.getVariable( name );
			
		} catch ( MissingPropertyException e ) {	
			return null;
			
		} catch ( Exception e ) {
			throw new EvaluationException( "Error trying to get variable '" + name + "'." );
		}
	}
	
	@Override
	public boolean has( String name ) {
		return this.binding.hasVariable( name );
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getVariables() throws EvaluationException {
		
		try {
			return this.binding.getVariables();
			
		} catch ( Exception e ) {
			throw new EvaluationException( e );
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Class getClassType() {
		return Binding.class;
	}
}
