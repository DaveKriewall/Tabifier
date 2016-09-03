package com.wrq.tabifier.formatter;

import com.wrq.tabifier.parse.AlignableToken;

/**
 * A TabStop represents an offset in a block of lines of text, generally associated with the beginning of a token which is to be aligned
 * with other tokens on other lines.  Such related tabStops (which are to be aligned by inserting spaces as necessary to make the tabStops
 * align vertically) belong to a TabGroup which is responsible for identifying the related tabStops.
 * <p/>
 * All TabStops for a given block of lines are registered with a TabStopManager, which ensures that tab stop offsets are correctly
 * maintained when space is inserted anywhere in the block.
 */
public class TabStop
{
    private       StringBuilder  sb;
    private       int            tab;
    private final TabStopManager tabStopManager;
    private       AlignableToken token; // for debugging - the token that's at this tab stop

    public TabStop(StringBuilder sb, TabStopManager tabStopManager)
    {
        this.sb             = sb;
        tab                 = sb.length();
        this.tabStopManager = tabStopManager;
    }

    public int getTabPosition()
    {
        return tab;
    }

    public void setTabPosition(int tab)
    {
        this.tab = tab;
    }

    /**
     *
     * @return offset of this tab stop from the beginning of line.
     */
    public int getColumn()
    {
        int newLineIndex = sb.lastIndexOf(tabStopManager.newLineString, tab);
        return (newLineIndex < 0) ? tab : tab - (newLineIndex + tabStopManager.newLineString.length());
    }

    @Override
    public String toString()
    {
        return "TabStop " + tab;
    }
}
