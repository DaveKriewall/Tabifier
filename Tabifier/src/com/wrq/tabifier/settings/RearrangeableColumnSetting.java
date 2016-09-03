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
 * Represents a column whose tokens can be rearranged before rendering, such as the column containing modifiers
 * such as "public" and "final".
 */
public final class RearrangeableColumnSetting
        extends ColumnSetting
{
    private boolean rearrange;

    public RearrangeableColumnSetting(final boolean initialValue, final int nCharacters, final String name, final boolean subAlign)
    {
        super(initialValue, name);
        setCharacters(nCharacters);
        setTabs(false);
        this.rearrange = subAlign;
    }

    private RearrangeableColumnSetting(final RearrangeableColumnSetting columnSetting, final String settingName)
    {
        super(columnSetting.get(), columnSetting.getCharacters(), settingName);
        setTabs(columnSetting.isTabs());
        setRearrange(columnSetting.isRearrange());
    }


    public boolean isRearrange()
    {
        return rearrange;
    }

    public void setRearrange(final boolean rearrange)
    {
        this.rearrange = rearrange;
        notifyChangeListeners();
    }

    public final boolean equals(final Object obj)
    {
        if (obj instanceof RearrangeableColumnSetting)
        {
            final RearrangeableColumnSetting cs = (RearrangeableColumnSetting) obj;
            return (super.equals(obj) &&
                    cs.getCharacters() == getCharacters() &&
                    cs.isTabs() == isTabs() &&
                    cs.rearrange == rearrange);
        }
        else
            return false;
    }

    protected void readValue(String s)
    {
        super.readValue(s);    //To change body of overridden methods use File | Settings | File Templates.
        rearrange = s.indexOf("rearrange") >= 0;
    }

    protected String writeValue()
    {
        String s = super.writeValue();
        if (rearrange) {
            s += " rearrange";
        }
        return s;
    }
 
    protected final Object clone() throws CloneNotSupportedException
    {
        return new RearrangeableColumnSetting(this, this.settingName);
    }

    public final String toString()
    {
        return super.toString() + ", rearrange=" + rearrange;
    }
}
