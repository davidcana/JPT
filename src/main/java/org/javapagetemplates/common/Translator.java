package org.javapagetemplates.common;

import java.util.List;

import org.xnap.commons.i18n.I18n;

/**
 * <p>
 *   Interface to translate strings to different languages.
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
 * @version $Revision: 1.5 $
 */
public interface Translator {
	
    /**
     * Return the translation using the given key iterating through the list of
     * <code>I18n</code> instances until it is found
     * 
     * @param i18nList the <code>I18n</code> list
     * @param i18nTranslate the key
     * @return the translation using the given key iterating through the list of
     * <code>I18n</code> instances until it is found
     */
	public String tr( List<I18n> i18nList, String i18nTranslate );
	
	
    /**
     * Return the translation using the given key and params iterating through the list of
     * <code>I18n</code> instances until it is found
     * 
     * @param i18nList the <code>I18n</code> list
     * @param i18nTranslate the key
     * @param i18nParams the translation params
     * @return the translation using the given key and params iterating through the list of
     * <code>I18n</code> instances until it is found
     */
	public String tr( List<I18n> i18nList, String i18nTranslate, Object[] i18nParams );
}
