package org.zenonpagetemplates.common.scripting;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.zenonpagetemplates.common.exceptions.EvaluationException;

/**
 * <p>
 *   Abstract and default implementation of Script interface.
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
abstract public class ScriptImpl implements Serializable, Script {
	
    private static final int BLOCK_SIZE = 10000;
	private static final long serialVersionUID = 4430712254615344334L;
	
	protected String script;
    
    /**
     * Constructor that creates a new instance from a String
     * 
     * @param script
     */
    public ScriptImpl( String script ) {
        this.script = script;
    }

    /**
     * Constructor that creates a new instance from any Reader, such as a file
     * 
     * @param reader
     * @throws IOException
     */
    public ScriptImpl( Reader reader ) throws IOException {
    	
        List<char[]> blocks = null;
        int size = 0;
        
        char[] block = new char[ BLOCK_SIZE ];
        int blockSize = 0;
        while ( true ) {
            int read = reader.read( block, blockSize, BLOCK_SIZE - blockSize );
            if ( read == -1 ) {
                if ( blocks != null ) {
                    blocks.add( block );
                }
                break;
            }
            size += read;
            blockSize += read;
            if ( blockSize == BLOCK_SIZE ) {
                if ( blocks == null ) {
                    blocks = new LinkedList<char[]>();
                }
                blocks.add( block );
                block = new char[ BLOCK_SIZE ];
                blockSize = 0;
            }
        }
        
        if ( blocks == null ) {
            this.script = new String( block, 0, blockSize );
        } else {
            StringBuffer buffer = new StringBuffer( size );
            for ( Iterator<char[]> i = blocks.iterator(); i.hasNext(); ) {
                block = i.next();
                buffer.append( block, 0, Math.min( BLOCK_SIZE, size ) );
                size -= BLOCK_SIZE;
            }
            this.script = buffer.toString();
        }
    }
    
    @Override
    public String getScript() {
        return this.script;
    }
    
    @Override
    public String toString() {
        return this.script;
    }

	@Override
	public Object evaluate(EvaluationHelper helper) throws EvaluationException {
		return this.getEvaluator().evaluate( this, helper );
	}
    
}

