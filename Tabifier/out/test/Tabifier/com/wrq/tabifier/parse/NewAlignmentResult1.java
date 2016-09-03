package testData.com.wrq.tabifier.parse;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class NewAlignmentTest1
{
    public static void main(String args[])
    {
        JCheckBox box  = new JCheckBox("box"  );
        JCheckBox box3 = new JCheckBox("box 3");
        box.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                int       i = 3;
                JCheckBox s = (JCheckBox) e.getSource();
            }
        });
    }
}
