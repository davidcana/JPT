package org.zenonpagetemplates.twoPhasesImpl.model.attributes;

import java.io.Serializable;

/**
 * <p>
 *   Simple class that implements a key / value pair. The type of key is
 *   String; the value is generic. 
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
 * @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 * @version $Revision: 1.0 $
 */
public class KeyValuePair<T> implements Serializable {

	private static final long serialVersionUID = 7648050722270108895L;
	
	private String key;
	private T value;
	
	public KeyValuePair(){}
	public KeyValuePair( String key, T value ){
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return this.key;
	}

	public void setKey( String key ) {
		this.key = key;
	}

	public T getValue() {
		return this.value;
	}

	public void setValue( T value ) {
		this.value = value;
	}
	
	public String toString( String delimiter ){
		return this.key + delimiter + this.value.toString();
	}
}
