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
 * Superclass for all individual settings. Every setting has a ALIGN_MODIFIERS and a value. The
 * ALIGN_MODIFIERS is used in the external (serialized) version of the setting as an XML attribute
 * ALIGN_MODIFIERS.
 */
abstract public class Setting
        implements Cloneable
{
    final String settingName;
    private Settings settings; // settings to which this setting belongs

    public Setting(final String settingName)
    {
        this.settingName = settingName;
        settings = null;
    }

    final void setOwner(final Settings settings)
    {
        this.settings = settings;
    }

    final void notifyChangeListeners()
    {
        /**
         * settings owner may be null if this Setting is just now being constructed.  Nobody is listening
         * at that point.
         */
        if (settings != null)
        {
            settings.notifyChangeListeners(this);
        }
    }

    abstract protected Object clone() throws CloneNotSupportedException;

    /**
     * read setting's value from a string in externalized form.
     */
    abstract void readValue(String s);

    /**
     * generate externalizable string representation of the setting's value.
     */
    abstract String writeValue();

    public String toString()
    {
        return settingName;
    }
}
