import javax.swing.*;
import java.awt.event.KeyEvent;

public class AnonymousInnerClassTest
{
    abstract class IconBox
    {
        AnonymousInnerClassTest t;

        protected IconBox(AnonymousInnerClassTest t)
        {
            this.t = t;
        }

        abstract boolean getSetting();
        abstract void    setSetting(boolean value);
        abstract Icon    getIcon();
        abstract String  getToolTipText();
        abstract int     getShortcut();
        JComponent       getIconBox() {
            return new JPanel();
        }
    }
    void m()
    {
        final JComponent showTypesBox =
                new IconBox(this)
                {
                    boolean getSetting(             ) { return true;                            }
                    void    setSetting(boolean value) { t = value ? null : t;                   }
                    Icon    getIcon(                ) { return new ImageIcon("ShowParamTypes"); }
                    String  getToolTipText(         ) { return "Show parameter types";          }
                    int     getShortcut(            ) { return KeyEvent.VK_T;                   }
                }.getIconBox();
    }
}