package org.javapagetemplates.twoPhasesImpl;

import java.net.URI;

import org.javapagetemplates.common.exceptions.PageTemplateException;
import org.javapagetemplates.twoPhasesImpl.model.JPTDocument;

/**
 * <p>
 *   Interface to cache JPTDocument instances.
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
public interface JPTDocumentCache {
	
	/**
	 * Put a JPTDocument into the cache
	 * 
	 * @param uri
	 * @param jptDocument
	 * @throws PageTemplateException
	 */
	public void put( URI uri, JPTDocument jptDocument ) throws PageTemplateException;
	
	
	/**
	 * Get a JPTDocument from the cache
	 * 
	 * @param uri
	 * @return
	 * @throws PageTemplateException
	 */
	public JPTDocument get( URI uri ) throws PageTemplateException;
}
