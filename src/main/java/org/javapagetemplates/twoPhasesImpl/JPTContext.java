package org.javapagetemplates.twoPhasesImpl;

import org.javapagetemplates.common.AbstractJPTContext;

/**
 * <p>
 *   Extends AbstractJPTContext class with methods to get and set 
 *   the JPTDocumentCache instance.
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
 * @version $Revision: 1.0 $
 */
public class JPTContext extends AbstractJPTContext {
	
	private JPTDocumentCache jptDocumentCache = new DefaultJPTDocumentCache();
    
    private static JPTContext instance;

    private JPTContext(){ }

    
	public JPTDocumentCache getJptDocumentCache() {
		return this.jptDocumentCache;
	}

	public void setJptDocumentCache( JPTDocumentCache jptDocumentCache ) {
		
		if ( jptDocumentCache == null ){
			throw new IllegalArgumentException( "Unable to set jptDocumentCache to null" );
		}
		
		this.jptDocumentCache = jptDocumentCache;
	}

    public static JPTContext getInstance(){

        if (instance == null){
            instance = new JPTContext();
        }

        return instance;
    }

}
