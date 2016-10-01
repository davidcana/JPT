package org.zenonpagetemplates.common;

import java.util.Iterator;
import java.util.List;

import org.xnap.commons.i18n.I18n;

/**
 * <p>
 *   Simple implementation of Translator interface.
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
 * @author <a href="mailto:chris@christophermrossi.com">Chris Rossi</a>
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.2 $
 */
public class TranslatorImpl implements Translator {

	
	public TranslatorImpl(){ }
	
	
	@Override
	public String tr( List<I18n> i18nList, String i18nTranslate ) {
		
        String result = null;
        boolean done = false;
        Iterator<I18n> i = i18nList.iterator();
        
        while ( ! done && i.hasNext() ){
            I18n i18n = i.next();
            result = i18n.tr( i18nTranslate );
            done = ! i18nTranslate.equals( result );
        }
        
        return result;
	}
	
	@Override
	public String tr( List<I18n> i18nList, String i18nTranslate,
			Object[] i18nParams ) {
		
        if ( i18nParams == null ){
            return tr( i18nList, i18nTranslate );
        }
        
        String result = null;
        boolean done = false;
        Iterator<I18n> i = i18nList.iterator();
        
        while ( ! done && i.hasNext() ){
            I18n i18n = i.next();
            result = i18n.tr( i18nTranslate, i18nParams );
            done = ! i18nTranslate.equals( result );
        }
        
        return result;
	}

}
