package com.wrq.tabifier.formatter;

import java.util.LinkedList;
import java.util.List;

public class TabGroup
{
    private final List<TabStop> tabStops;
    private final TabStopManager manager;

    public TabGroup(TabStopManager manager)
    {
        tabStops = new LinkedList<>();
        this.manager = manager;
    }

}
