package org.zenonpagetemplates.common;
/**
 * <p>
 *   Implementation of error type from  
 *   <a href="http://docs.zope.org/zope2/zope2book/AppendixC.html">ZPT specification</a>
 *   as described in it.
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
 * @version $Revision: 1.2 $
 */
public class TemplateError {
	
	private String type;
	private Exception value;
	private StackTraceElement[] traceback;
	
	public TemplateError( Exception exception ){
		
		this.type = exception.getClass().getName();
		this.value = exception;
		this.traceback = exception.getStackTrace();
	}

	public String getType() {
		return this.type;
	}

	public Exception getValue() {
		return this.value;
	}

	public StackTraceElement[] getTraceback() {
		return this.traceback;
	}
}
