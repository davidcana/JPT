package org.zenonpagetemplates.common.scripting.beanShell;

import java.util.HashMap;
import java.util.Map;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;

import bsh.BshClassManager;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;

/**
 * <p>
 *   Simple class that implements EvaluationHelper for Bean Shell.
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

public class BeanShellEvaluationHelper implements EvaluationHelper {
	
	private static final String GLOBAL = "global";
	
	private static BshClassManager bshClassManager;
	
	private Interpreter beanShell;
	
	BeanShellEvaluationHelper(){
		this.beanShell = createInterpreter();
	}
	
	public BeanShellEvaluationHelper( Interpreter beanShell ){
		this.beanShell = beanShell;
	}

	public Interpreter getBeanShell() {
		return this.beanShell;
	}
	

	static private Interpreter createInterpreter(){
		
		Interpreter result = new Interpreter();
		
        NameSpace globalNameSpace = new NameSpace( getBshClassManager(), GLOBAL );
        result.setNameSpace( globalNameSpace );
        
        return result;
	}
	
	
	static private BshClassManager getBshClassManager() {
    	
    	if ( bshClassManager == null ){
    		bshClassManager = BshClassManager.createClassManager( null );
    	}
    	
		return bshClassManager;
	}
	
	
	@Override
	public void unset( String name ) throws EvaluationException {
		
		try {
			this.beanShell.unset( name );
			
		} catch ( EvalError e ) {
			throw new EvaluationException( e );
		}
	}
	

	@Override
	public void set( String name, Object value ) throws EvaluationException {
		
		try {
			this.beanShell.set( name, value );
			
		} catch ( EvalError e ) {
			throw new EvaluationException( e );
		}
	}
	
	
	@Override
	public Object get( String name ) throws EvaluationException {
		
		try {
			return this.beanShell.get( name );
			
		} catch ( EvalError e ) {
			throw new EvaluationException( e );
		}
	}
	
	
	@Override
	public boolean has( String name ) {
		
		try {
			return this.get( name ) != null;
			
		} catch ( Exception e ) {
			return false;
		}
	}
	
	
	@Override
	public Map<String, Object> getVariables() throws EvaluationException {
		
		try {
			String[] variables = ( String[] ) this.beanShell.get( "this.variables" );
			
			Map<String, Object> result = new HashMap<String, Object>();
			
			for ( String name : variables ){
				result.put( 
						name, 
						this.beanShell.get( name ) );
			}
			
			return result;
			
		} catch ( Exception e ) {
			throw new EvaluationException( e );
		}
	}
	

	@SuppressWarnings("rawtypes")
	@Override
	public Class getClassType() {
		return Interpreter.class;
	}

}
