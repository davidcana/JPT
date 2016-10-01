package org.zenonpagetemplates.common.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>
 *   Simple class to make it easy to format dates.
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
 * @author <a href="mailto:chris@christophermrossi.com">Chris Rossi</a>
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.1 $
 */

public class DateHelper {

	static final Map<String, SimpleDateFormat> dateFormats = new TreeMap<String, SimpleDateFormat>();
    

	public static final String format( String format, Date date ) {
		
        SimpleDateFormat dateFormat = dateFormats.get( format );
        
        if ( dateFormat == null ) {
            dateFormat = new SimpleDateFormat( format );
            dateFormats.put( format, dateFormat );
        }
        
        return dateFormat.format( date );
    }
}