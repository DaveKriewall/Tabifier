/*
 * Copyright (c) 2003, 2010, Dave Kriewall
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1) Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 *
 * 2) Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.wrq.tabifier.settings;

/**
 * Represents a boolean configuration setting.  Done this way so that a reference to this
 * class can be passed into a generic "checkbox constructor" which will save the reference in the
 * button action callback, which allows it to set the value to true or false.  The Boolean class is
 * immutable, and the reference has to change when the value changes.
 */
public class BooleanSetting
        extends Setting
        implements Cloneable
{
    private boolean value;

    public BooleanSetting(final boolean initialValue, final String name)
    {
        super(name);
        value = initialValue;
    }

    public boolean equals(final Object obj)
    {
        if (obj instanceof BooleanSetting)
        {
            return (((BooleanSetting) obj).value == value);
        }
        else
            return false;
    }

    protected Object clone() throws CloneNotSupportedException
    {
        return new BooleanSetting(value, settingName);
    }

    void readValue(final String s)
    {
        value = "true".equals(s);
    }

    String writeValue()
    {
        return Boolean.toString(value);
    }

    public final boolean get()
    {
        return value;
    }

    public final void set(final boolean b)
    {
        final boolean changed = (value != b);
        value = b;
        if (changed)
        {
            notifyChangeListeners();
        }
    }

    public String toString()
    {
        return settingName + ": value=" + value;
    }
}
