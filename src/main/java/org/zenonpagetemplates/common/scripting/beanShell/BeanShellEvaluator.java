package org.zenonpagetemplates.common.scripting.beanShell;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map.Entry;

import org.zenonpagetemplates.common.exceptions.EvaluationException;
import org.zenonpagetemplates.common.scripting.EvaluationHelper;
import org.zenonpagetemplates.common.scripting.Evaluator;
import org.zenonpagetemplates.common.scripting.Script;

import bsh.Interpreter;

/**
 * <p>
 *   Simple class that implements Evaluator for Bean Shell.
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

public class BeanShellEvaluator implements Evaluator {
	
	static private BeanShellEvaluator instance;
	 
	private BeanShellEvaluator(){}
	
	
	@Override
	public Object evaluate( Script script, EvaluationHelper helper )
			throws EvaluationException {
		
		return this.evaluate( script.getScript(), helper );	
	}
	
	
	@Override
	public Object evaluate( String expression, EvaluationHelper helper ) throws EvaluationException {
    	
    	try {
    		return this.getBeanShell( helper ).eval( expression );
    		
		} catch ( EvaluationException e ) {
			throw e;
			
		} catch ( Exception e ) {
			throw new EvaluationException( e, expression );
		}
	}
	
	
	private Interpreter getBeanShell( EvaluationHelper helper ) throws EvaluationException {
		
		if ( helper instanceof BeanShellEvaluationHelper ){
			BeanShellEvaluationHelper bsHelper = ( BeanShellEvaluationHelper ) helper;
    		return bsHelper.getBeanShell();
		}
		
		try {
			Interpreter result = new Interpreter();
			
			for ( Entry<String, Object> entry : helper.getVariables().entrySet() ){
				String name = entry.getKey();
				Object value = entry.getValue();
				result.set ( name, value );
			}
			
			return result;
			
		} catch ( Exception e ) {
			throw new EvaluationException( e );
		}
	}
	
	
	@Override
	public Script createScript(URL resource) throws IOException {
		
		return new BeanShellScript( 
        		new InputStreamReader( resource.openStream() ) );
	}
	

	@Override
	public EvaluationHelper createEvaluationHelper() throws EvaluationException {
		return new BeanShellEvaluationHelper();
	}

    
    public static BeanShellEvaluator getInstance(){

        if ( instance == null ){
            instance = new BeanShellEvaluator();
        }

        return instance;
    }
}
