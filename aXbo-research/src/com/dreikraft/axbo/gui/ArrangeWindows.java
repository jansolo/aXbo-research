/*
 * © 2008 3kraft
 * $Id: ArrangeWindows.java,v 1.3 2008-05-13 15:08:43 illetsch Exp $
 */
package com.dreikraft.axbo.gui;
import javax.swing.*;

/**
 * $Id: ArrangeWindows.java,v 1.3 2008-05-13 15:08:43 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.3 $
 */
public class ArrangeWindows
{
  
  /** Creates a new instance of ArangeWindows */
  public ArrangeWindows()
  {
  }
  
  // Cascade Windows
  public static void cascadeWindows(JDesktopPane desktop)
  {
    JInternalFrame[] frames = null; 
    try 
    {
      frames = desktop.getAllFrames();
    }
    catch (NullPointerException ex) 
    {
      return;
    }
    
    int x = 0;
    int y = 0;
    int frameDistance = 20;  // 20 pixels between frames
    
    // count frames that aren't iconized
    int frameCount = 0;
    for (int i = 0; i < frames.length; i++)
    {
      if (!frames[i].isIcon())
        frameCount++;
    }
    if (frameCount == 0)
      return;
    
    // width and height of instances which will fill available space
    int width = desktop.getWidth() - ((frameCount-1) * frameDistance);
    int height = desktop.getHeight() - ((frameCount-1) * frameDistance);
    
    for (int i = 0; i < frames.length; i++)
    {
      if (!frames[i].isIcon())
      {
        try
        {
          frames[i].setMaximum(false);
          frames[i].reshape(x, y, width, height);
          
          x += frameDistance;
          y += frameDistance;
          // wrap around at the desktop edge
          if (x + width > desktop.getWidth()) x = 0;
          if (y + height > desktop.getHeight()) y = 0;
        }
        catch(Exception e)
        {}
      }
    }
  }
  
  static void tileWindowsVertical(JDesktopPane desktop)
  {
    JInternalFrame[] frames = null; 
    try 
    {
      frames = desktop.getAllFrames();
    }
    catch (NullPointerException ex) 
    {
      return;
    }
    
    // count frames that aren't iconized
    int frameCount = 0;
    for (int i = 0; i < frames.length; i++)
    {
      if (!frames[i].isIcon())
        frameCount++;
    }
    if (frameCount == 0)
      return;
    
    int rows = (int)Math.sqrt(frameCount);
    int cols = frameCount / rows;
    int extra = frameCount % rows;
    // number of columns with an extra row
    
    int width = desktop.getWidth() / cols;
    int height = desktop.getHeight() / rows;
    int r = 0;
    int c = 0;
    for (int i = 0; i < frames.length; i++)
    {
      if (!frames[i].isIcon())
      {
        try
        {
          frames[i].setMaximum(false);
          frames[i].reshape(c * width, r * height, width, height);
          r++;
          if (r == rows)
          {
            r = 0;
            c++;
            if (c == cols - extra)
            {
              // start adding an extra row
              rows++;
              height = desktop.getHeight() / rows;
            }
          }
        }
        catch(Exception e)
        {}
      }
    }
  }
  
  static void tileWindowsHorizontal(JDesktopPane desktop)
  {
    JInternalFrame[] frames = null; 
    try 
    {
      frames = desktop.getAllFrames();
    }
    catch (NullPointerException ex) 
    {
      return;
    }
    
    // count frames that aren't iconized
    int frameCount = 0;
    for (int i = 0; i < frames.length; i++)
    {
      if (!frames[i].isIcon())
        frameCount++;
    }
    if (frameCount == 0)
      return;
    
    int rows = frameCount;
    int cols = 1;
    int extra = frameCount % rows;
    // number of columns with an extra row
    
    int width = desktop.getWidth() / cols;
    int height = desktop.getHeight() / rows;
    int r = 0;
    int c = 0;
    for (int i = 0; i < frames.length; i++)
    {
      if (!frames[i].isIcon())
      {
        try
        {
          frames[i].setMaximum(false);
          frames[i].reshape(c * width,
              r * height, width, height);
          r++;
        }
        catch(Exception e)
        {}
      }
    }
  }
  
  public void selectNextWindow(JDesktopPane desktop)
  {
    JInternalFrame[] frames = desktop.getAllFrames();
    for (int i = 0; i < frames.length; i++)
    {
      if (frames[i].isSelected())
      {
        // find next frame that isn't an icon and can be selected
        try
        {
          int next = i + 1;
          while (next != i && frames[next].isIcon())
            next++;
          if (next == i) return;
          // all other frames are icons or veto selection
          frames[next].setSelected(true);
          frames[next].toFront();
          return;
        }
        catch(Exception e)
        {}
      }
    }
  }
  
}
