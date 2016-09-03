package testData.com.wrq.tabifier.parse;

import javax.swing.*;
import java.awt.*;

public class MethodCallAlignmentTest10
{
    void method()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder   (4, 0, 0, 0),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder (Color.gray),
                        BorderFactory.createEmptyBorder(0, 0, 3, 3))));
    }
}
