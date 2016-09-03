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

import org.jdom.Attribute;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * contains a collection of zero or more Setting objects. Useful for simplifying settings
 * management, persistence, cloning and comparison.
 */
abstract public class Settings
        implements Cloneable
{
    private ArrayList/*<Setting>*/ settingsList;
    private HashMap/*<String, Setting>*/ settingsByName;
    private final ArrayList/*<ISettingsChangeListener>*/ changeListeners;

    public Settings()
    {
        this.settingsList = new ArrayList/*<Setting>*/(70);
        settingsByName = new HashMap/*<String, Setting>*/(70);
        changeListeners = new ArrayList/*<ISettingsChangeListener>*/();
    }

    final void addSetting(final Setting s)
    {
        s.setOwner(this);
        settingsList.add(s);
        settingsByName.put(s.settingName, s);
    }

    final Setting find(final String s)
    {
        return (Setting) settingsByName.get(s);
    }

    public final ListIterator/*<Setting>*/ getSettings()
    {
        return settingsList.listIterator();
    }

    /** Equality of Settings is true when the number of settings is equal, and each setting in
     * the first has the same value as that of the second.
     * @param obj
     * @return
     */
    public final boolean equals(final Object obj)
    {
        if (obj instanceof Settings)
        {
            final Settings s = (Settings) obj;
            if (this.settingsList.size() == s.settingsList.size())
            {
                final ListIterator/*<Setting>*/ i = settingsList.listIterator();
                while (i.hasNext())
                {
                    final Setting my_setting = (Setting) i.next();
                    final Setting their_setting = s.find(my_setting.settingName);
                    if (their_setting == null)
                    {
                        return false;
                    }
                    else if (!my_setting.equals(their_setting))
                    {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Give subclasses a chance to initialize any variables that may point to settings.
     */
    abstract protected void initialize();

    protected final Object clone() throws CloneNotSupportedException
    {
        final Settings result = (Settings) super.clone();
        result.settingsList = new ArrayList/*<Setting>*/(settingsList.size());
        result.settingsByName = new HashMap/*<String, Setting>*/(settingsList.size());
        // now duplicate settings list and map.
        final ListIterator/*<Setting>*/ i = settingsList.listIterator();
        while (i.hasNext())
        {
            final Setting setting = (Setting) i.next();
            result.addSetting((Setting) setting.clone());
        }
        result.initialize();
        return result;
    }

    public final Settings deepCopy()
    {
        try
        {
            return (Settings) this.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * For each setting, inspect the entry element for an attribute with a matching ALIGN_MODIFIERS.
     * If found, set the setting according to the attribute's value.
     *
     * @param entry               JDOM element which contains setting values as attributes.
     */
    public final void readExternal(final Element entry)
    {
        final Iterator i = settingsList.listIterator();
        while (i.hasNext())
        {
            final Setting s = (Setting) i.next();
            final Attribute attribute = entry.getAttribute(s.settingName);
            if (attribute != null)
            {
                final String value = attribute.getValue();
                s.readValue(value);
            }
        }
    }

    public final void writeExternal(final Element our_element)
    {
        final Iterator i = settingsList.listIterator();
        while (i.hasNext())
        {
            final Setting s = (Setting) i.next();
            our_element.setAttribute(s.settingName, s.writeValue());
        }
    }

    public final void notifyChangeListeners(final Setting setting)
    {
        final ListIterator/*<ISettingsChangeListener>*/ iterator = this.changeListeners.listIterator();
        while (iterator.hasNext())
        {
            final ISettingsChangeListener listener = (ISettingsChangeListener) iterator.next();
            listener.settingsChange(setting);
        }
    }

    public final void addChangeListener(final ISettingsChangeListener settingsChangeListener)
    {
        changeListeners.add(settingsChangeListener);
    }
}
