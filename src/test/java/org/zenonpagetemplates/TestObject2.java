package org.zenonpagetemplates;

import java.util.HashMap;
import java.util.Map;

/**
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
 *  @author <a href="mailto:rossi+wzcommon@webslingerZ.com">Chris Rossi</a>
 *  @author <a href="mailto:david.javapagetemplates@gmail.com">David Cana</a>
 *  @version $Revision: 1.0 $
 */
public class TestObject2 {
    Map<String, Object> map = new HashMap<String, Object>();

    TestObject2() {
        map.put( "friend", "kevin" );
        map.put( "enemy", "mc2" );
        map.put( "hello", new Long(99l) );
    }

    public int getNumber() {
        return 9;
    }

    public long getZero() {
        return 0l;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public String toString() {
        return "Albert";
    }
}