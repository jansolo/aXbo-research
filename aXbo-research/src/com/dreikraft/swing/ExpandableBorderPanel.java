/*
 * $Id: ExpandableBorderPanel.java,v 1.1 2010-11-23 15:33:34 illetsch Exp $
 * © 3kraft GmbH & Co KG 2009
 */
package com.dreikraft.swing;

import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * A Panel that can be shown and hidden when clicking on the border.
 * 
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.1 $
 */
public class ExpandableBorderPanel extends JPanel
{
  private String title;
  private boolean expanded = true;
  private boolean expandable = false;

  /** Creates new form ExpandableBorderPanel */
  public ExpandableBorderPanel()
  {
    initComponents();
  }

  @SuppressWarnings("unchecked")
  private void initComponents()
  {

    setBorder(javax.swing.BorderFactory.createTitledBorder(""));
    addMouseListener(new java.awt.event.MouseAdapter()
    {
      @Override
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        formMouseClicked(evt);
      }
    });
    setLayout(new java.awt.BorderLayout());
  }

  private void formMouseClicked(java.awt.event.MouseEvent evt)
  {
    if (getComponentCount() > 0 && expandable)
    {
      setExpanded(!isExpanded());
    }
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
    if (getBorder() instanceof TitledBorder)
    {
      if (isExpandable())
      {
        ((TitledBorder) getBorder()).setTitle(title + " " + (isExpanded()
            ? "\u02c4" : "\u02c5"));
      }
      else
      {
        ((TitledBorder) getBorder()).setTitle(title);
      }
    }
  }

  public boolean isExpanded()
  {
    return expanded;
  }

  public void setExpanded(boolean expanded)
  {
    this.expanded = expanded;
    for (Component component : getComponents())
    {
      component.setVisible(expanded);
    }
    if (getBorder() instanceof TitledBorder)
    {
      ((TitledBorder) getBorder()).setTitle(title + " " + (isExpanded()
          ? "\u02c4" : "\u02c5"));
    }
  }

  public boolean isExpandable()
  {
    return expandable;
  }

  public void setExpandable(boolean expandable)
  {
    this.expandable = expandable;
    if (getBorder() instanceof TitledBorder)
    {
      if (isExpandable())
      {
        ((TitledBorder) getBorder()).setTitle(title + " " + (isExpanded()
            ? "\u02c4" : "\u02c5"));
      }
      else
      {
        ((TitledBorder) getBorder()).setTitle(title);
      }
    }
  }
}
