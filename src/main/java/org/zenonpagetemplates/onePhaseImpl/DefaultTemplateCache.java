package org.zenonpagetemplates.onePhaseImpl;

import java.util.Map;
import java.util.TreeMap;

import org.dom4j.Document;
import org.zenonpagetemplates.common.exceptions.PageTemplateException;

/**
 * <p>
 *   Default implementation of TemplateCache interface not for production 
 *   use (only for testing purposes).
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
 * @version $Revision: 1.1 $
 */

public class DefaultTemplateCache implements TemplateCache {
	
	private Map<String, Document> documents = new TreeMap<String, Document>();
	
	DefaultTemplateCache(){}
	
	@Override
	public void put( String id, Document document ) throws PageTemplateException {
		this.documents.put( id, document );
	}

	@Override
	public Document get( String id ) throws PageTemplateException {
		return this.documents.get( id );
	}
	
}
