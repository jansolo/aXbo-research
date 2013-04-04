/*
 * $Id: DisablePanel.java,v 1.1 2010-11-23 15:33:34 illetsch Exp $
 * © 3kraft GmbH & Co KG 2009
 */
package com.dreikraft.swing;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author jan_solo
 * @author $Author
 * @version $Revision: 1.1 $
 */
public class DisablePanel extends JPanel
{
  private List<JComponent> enabledComponents = new ArrayList<JComponent>();

  @Override
  public void setEnabled(boolean enabled)
  {
    super.setEnabled(enabled);

    if (enabled)
    {
      for (JComponent component : enabledComponents)
      {
        component.setEnabled(enabled);
      }
      enabledComponents.clear();
    }
    else
    {
      for (Component component : getComponents())
      {
        if (JComponent.class.isAssignableFrom(component.getClass()))
        {
          final JComponent jComponent = (JComponent) component;
          if (jComponent.isEnabled())
          {
            enabledComponents.add(jComponent);
            jComponent.setEnabled(enabled);
          }
        }
      }
    }
  }
}
