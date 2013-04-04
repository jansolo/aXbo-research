/*
 * $Id: ScrollablePanel.java,v 1.1 2010-11-23 15:33:34 illetsch Exp $
 * © 3kraft GmbH & Co KG 2009
 */
package com.dreikraft.swing;

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;

/**
 *
 * @author jan_solo
 * @author $Author
 * @version $Revision: 1.1 $
 */
public class ScrollablePanel extends JPanel implements Scrollable {

  @Override
  public Dimension getPreferredScrollableViewportSize()
  {
    return getPreferredSize();
  }

  @Override
  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation,
      int direction)
  {
    return 1;
  }

  @Override
  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation,
      int direction)
  {
    return 10;
  }

  @Override
  public boolean getScrollableTracksViewportWidth()
  {
    if (getParent() instanceof JViewport)
		{
		    return (((JViewport)getParent()).getWidth() > getMinimumSize().width);
		}
    return false;
  }

  @Override
  public boolean getScrollableTracksViewportHeight()
  {
    if (getParent() instanceof JViewport)
		{
		    return (((JViewport)getParent()).getHeight() > getPreferredSize().height);
		}
    return false;
  }

}
