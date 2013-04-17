/*
 * $Id: ContainerUtilities.java,v 1.1 2010-11-23 15:33:34 illetsch Exp $
 * © 3kraft GmbH & Co KG 2009
 */
package com.dreikraft.swing;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;

/**
 *
 * @author jan_solo
 * @author $Author
 * @version $Revision: 1.1 $
 */
public class ContainerUtilities
{
  private static Map<Container, List<JComponent>> containers =
      new HashMap<Container, List<JComponent>>();

  /**
   *  Convenience static method to disable all components of a given
   *  Container, including nested Containers.
   *
   *  @param container the Container containing Components to be disabled
   */
  public static void disable(Container container)
  {
    final List<JComponent> components = getDescendantsOfType(JComponent.class,
        container, true);
    containers.put(container, components);

    for (JComponent component : components)
    {
        component.setEnabled(false);
    }
    container.setEnabled(false);
  }

  /**
   *  Convenience static method to enable Components disabled by using
   *  the disable() method. Only Components disable by the disable()
   *  method will be enabled.
   *
   *  @param container a Container that has been previously disabled.
   */
  public static void enable(Container container)
  {
    List<JComponent> components = containers.get(container);

    if (components != null)
    {
      for (JComponent component : components)
      {
        component.setEnabled(true);
      }

      containers.remove(container);
    }
    container.setEnabled(true);
  }

  /**
   * Convenience method for searching below <code>container</code> in the
   * component hierarchy and return nested components that are instances of
   * class <code>clazz</code> it finds. Returns an empty list if no such
   * components exist in the container.
   * <P>
   * Invoking this method with a class parameter of JComponent.class
   * will return all nested components.
   *
   * @param clazz the class of components whose instances are to be found.
   * @param container the container at which to begin the search
   * @param nested true to list components nested within another listed
   * component, false otherwise
   * @return the List of components
   */
  public static <T extends Component> List<T> getDescendantsOfType(
      Class<T> clazz, Container container, boolean nested)
  {
    List<T> tList = new ArrayList<T>();
    for (Component component : container.getComponents())
    {
      if (clazz.isAssignableFrom(component.getClass()))
      {
        tList.add(clazz.cast(component));
      }
      if (nested || !clazz.isAssignableFrom(component.getClass()))
      {
        tList.addAll(getDescendantsOfType(clazz,
            (Container) component, nested));
      }
    }
    return tList;
  }

  public static void dumpComponents(final Container container,
      final String increment)
  {
    for (Component component : container.getComponents())
    {
      if (component instanceof Container)
      {
        System.out.println(increment + container.getClass());
        try
        {
          System.out.println(increment + component.getPreferredSize());
        }
        catch (NullPointerException ex)
        {
          ex.printStackTrace(System.out);
        }
        dumpComponents((Container) component, increment + ".");
      }
    }
  }
}
