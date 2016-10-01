package org.zenonpagetemplates.twoPhasesImpl;

import org.zenonpagetemplates.common.AbstractZPTContext;

/**
 * <p>
 *   Extends AbstractZPTContext class with methods to get and set 
 *   the ZPTDocumentCache instance.
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
 * @version $Revision: 1.0 $
 */
public class ZPTContext extends AbstractZPTContext {
	
	private ZPTDocumentCache zptDocumentCache = new DefaultZPTDocumentCache();
    
    private static ZPTContext instance;

    private ZPTContext(){ }

    
	public ZPTDocumentCache getZPTDocumentCache() {
		return this.zptDocumentCache;
	}

	public void setZPTDocumentCache( ZPTDocumentCache zptDocumentCache ) {
		
		if ( zptDocumentCache == null ){
			throw new IllegalArgumentException( "Unable to set zptDocumentCache to null" );
		}
		
		this.zptDocumentCache = zptDocumentCache;
	}

    public static ZPTContext getInstance(){

        if (instance == null){
            instance = new ZPTContext();
        }

        return instance;
    }

}
