/*******************************************************************************
 * Qantiqa : Decentralized microblogging platform
 * Copyright (C) 2010 Dario (i@dario.im) 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/

package im.dario.qantiqa.common.utils;

/**
 * Reference to the result of an asynchronous operation.
 * 
 * We use this class as immutable, so once execute
 * {@link AsyncResult#set(Object)}, we don't allow to modify the value again.
 * 
 * @author Dario
 */
public class AsyncResult<V> {

    private V value;

    @SuppressWarnings("unchecked")
    public void set(Object o) {
        if (value == null) {
            value = (V) o;
        }
    }

    /**
     * Get the set value with {@link AsyncResult#set(Object)}. If not it is set,
     * it waits in a no busy-wait way.
     * 
     * @return Result value
     */
    public synchronized V get() {
        while (value == null) {
            try {
                this.wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return value;
    }
}
