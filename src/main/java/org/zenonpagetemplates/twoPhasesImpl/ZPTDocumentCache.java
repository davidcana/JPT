package org.zenonpagetemplates.twoPhasesImpl;

import java.net.URI;

import org.zenonpagetemplates.common.exceptions.PageTemplateException;
import org.zenonpagetemplates.twoPhasesImpl.model.ZPTDocument;

/**
 * <p>
 *   Interface to cache ZPTDocument instances.
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
public interface ZPTDocumentCache {
	
	/**
	 * Put a ZPTDocument into the cache
	 * 
	 * @param uri
	 * @param zptDocument
	 * @throws PageTemplateException
	 */
	public void put( URI uri, ZPTDocument zptDocument ) throws PageTemplateException;
	
	
	/**
	 * Get a ZPTDocument from the cache
	 * 
	 * @param uri
	 * @return a ZPTDocument from the cache
	 * @throws PageTemplateException
	 */
	public ZPTDocument get( URI uri ) throws PageTemplateException;
}
