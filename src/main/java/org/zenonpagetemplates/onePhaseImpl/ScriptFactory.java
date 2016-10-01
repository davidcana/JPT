package org.zenonpagetemplates.onePhaseImpl;

import org.zenonpagetemplates.common.AbstractZPTContext;
import org.zenonpagetemplates.common.scripting.AbstractScriptFactory;
import org.zenonpagetemplates.onePhaseImpl.ZPTContext;

/**
 * <p>
 *   Extends AbstractScriptFactory to implement getContext method.
 * </p>
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

public class ScriptFactory extends AbstractScriptFactory {
	
	private static ScriptFactory instance;
	
	private ScriptFactory(){
		super();
	}

	@Override
	protected AbstractZPTContext getContext() {
		return ZPTContext.getInstance();
	}
	
    public static ScriptFactory getInstance(){

        if (instance == null){
            instance = new ScriptFactory();
        }

        return instance;
    }
}
