import javax.swing.*;

public class GroupTest1
{
    void method()
    {
        final JFrame             frame       = new JFrame("SwingApplication");
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor    = GridBagConstraints.NORTHWEST;
        constraints.fill      = GridBagConstraints.BOTH;
        constraints.weightx   = 1.0d;
        constraints.weighty   = 1.0d;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
    }
}
