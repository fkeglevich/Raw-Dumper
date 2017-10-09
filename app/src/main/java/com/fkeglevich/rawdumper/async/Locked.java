/*
 * Copyright 2017, Flávio Keglevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fkeglevich.rawdumper.async;

import com.fkeglevich.rawdumper.util.ThreadUtil;

/**
 * A locked object it's an object that will throw an unchecked exception when you tries to
 * access it outside a proper synchronized block.
 *
 * Even though it's quite trivial to leak the inner object, the main purpose of this class
 * is to remember the programmer that the object is shared among threads and MUST NOT be
 * accessed outside a synchronized block.
 *
 * Created by Flávio Keglevich on 02/08/2017.
 */

public class Locked<T>
{
    private T object;
    private final Object lock;

    /**
     * Creates a new Locked object.
     *
     * @param object    The object to be locked.
     * @param lock      The object used to implement the lock.
     */
    public Locked(T object, Object lock)
    {
        this.object = object;
        this.lock = lock;
    }

    /**
     * Creates a new Locked object without using a specific lock object.
     *
     * @param object    The object to be locked.
     */
    public Locked(T object)
    {
        this.object = object;
        this.lock = this;
    }

    /**
     * Gets the locked object.
     * Throws an unchecked exception if getting outside a proper synchronized block.
     *
     * @return  The T object
     */
    public T get()
    {
        ThreadUtil.checkIfSynchronized(getLock());
        return object;
    }

    /**
     * Sets the locked object.
     * Throws an unchecked exception if setting outside a proper synchronized block.
     *
     * @param object    The T object
     */
    public void set(T object)
    {
        ThreadUtil.checkIfSynchronized(getLock());
        this.object = object;
    }

    /**
     * Gets whether the locked object is null.
     * Throws an unchecked exception if getting outside a proper synchronized block.
     *
     * @return  A boolean value
     */
    public boolean isNull()
    {
        ThreadUtil.checkIfSynchronized(getLock());
        return object == null;
    }

    /**
     * Gets the object used as synchronization lock.
     *
     * @return  An object
     */
    public Object getLock()
    {
        return lock;
    }
}
