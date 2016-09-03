package com.wrq.tabifier.formatter;

import java.util.*;

public class TabStopManager
{
    private final List<TabStop> masterList    = new LinkedList<>();
    private final StringBuilder sb;
    public  final String        newLineString = "\n"; // System.getProperty("line.separator");

    public TabStopManager(StringBuilder sb)
    {
        this.sb = sb;
    }

    public TabStop createTab()
    {
        TabStop result = new TabStop(sb, this);
        masterList.add(result);
        return result;
    }

    public void align(List<TabStop> tabStopList)
    {
        // find the highest column of all the tab stops.
        // then go back and align them all by inserting spaces to bring them up to the desired column.
        // update the master list accordingly.
        int maxColumn = Integer.MIN_VALUE;
        for (TabStop tabStop : tabStopList)
        {
            maxColumn = Math.max(maxColumn, tabStop.getColumn());
        }
        for (TabStop tabStop : tabStopList)
        {
            int insertionAmount = maxColumn - tabStop.getColumn();
            int insertionPoint = tabStop.getTabPosition();
            while (insertionAmount-- > 0) sb.insert(insertionPoint, ' ');
            // now update the position of every tab stop in the master list that follows what was just inserted,
            // to reflect the inserted space.
            insertionAmount = maxColumn - tabStop.getColumn();
            for (TabStop aTab : masterList)
            {
                if (aTab.getTabPosition() >= insertionPoint)
                {
                    aTab.setTabPosition(aTab.getTabPosition() + insertionAmount);
                }
            }
        }
    }
}
