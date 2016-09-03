package com.wrq.tabifier.formatter;

import com.wrq.tabifier.parse.ColumnSequence;
import com.wrq.tabifier.parse.Line;

import java.util.ArrayList;

public class ColumnNodeTabifier
{
    private final ColumnSequence parentNode;
    private final ArrayList<Line> linesToAlign;

    public ColumnNodeTabifier(ColumnSequence parentNode, ArrayList<Line> linesToAlign)
    {
        this.parentNode =  parentNode;
        this.linesToAlign = linesToAlign;
    }

    public void align()
    {

    }
}
