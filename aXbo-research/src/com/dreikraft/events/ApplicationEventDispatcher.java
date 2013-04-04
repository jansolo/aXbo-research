/*
 * $Id: ApplicationEventDispatcher.java,v 1.2 2010-11-29 15:42:24 illetsch Exp $
 * © 3kraft GmbH & Co KG 2009
 */
package com.dreikraft.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Dispatches events on the swing event queue.
 * 
 * @author jan_solo
 * @author $Author: illetsch $
 * @version $Revision: 1.2 $
 */
public class ApplicationEventDispatcher
{

  private static final Log log = LogFactory.getLog(
      ApplicationEventDispatcher.class);
  private static final String HANDLER_METHOD_PREFIX = "handle";
  private static final ApplicationEventDispatcher dispatcher =
      new ApplicationEventDispatcher();
  private Map<Class<? extends ApplicationEvent>, List<ApplicationEventEnabled>> eventTypeReceivers;

  // Private constructor prevents instantiation from other classes
  private ApplicationEventDispatcher()
  {
    eventTypeReceivers =
        Collections.synchronizedMap(
        new HashMap<Class<? extends ApplicationEvent>, List<ApplicationEventEnabled>>());
  }

  /**
   * Returns a singleton instance.
   *
   * @return a singleton Dispatcher instance
   */
  public static ApplicationEventDispatcher getInstance()
  {
    return dispatcher;
  }

  /**
   * Registers an ApplicationEvent with a handler. The handler must implement
   * the ApplicationEventEnabled interface.
   *
   * @param appEventType an application event type class
   * @param eventHandler the handler
   */
  public void registerApplicationEventHandler(
      final Class<? extends ApplicationEvent> appEventType,
      final ApplicationEventEnabled eventHandler)
  {
    // lookup handlers for given event type
    List<ApplicationEventEnabled> handlers = eventTypeReceivers.get(
        appEventType);
    if (handlers == null)
    {
      handlers = Collections.synchronizedList(
          new ArrayList<ApplicationEventEnabled>());
      eventTypeReceivers.put(appEventType, handlers);
    }

    // add handler, if not already added
    if (!handlers.contains(eventHandler))
    {
      handlers.add(eventHandler);
    }
  }

  /**
   * Deregisters a handler for given event type.
   *
   * @param appEventType an application event type class
   * @param eventHandler the handler
   */
  public void deregisterApplicationEventHandler(
      final Class<? extends ApplicationEvent> appEventType,
      final ApplicationEventEnabled eventHandler)
  {
    // lookup handlers for given event type
    List<ApplicationEventEnabled> handlers = eventTypeReceivers.get(
        appEventType);
    if (handlers == null)
    {
      handlers = Collections.synchronizedList(
          new ArrayList<ApplicationEventEnabled>());
      eventTypeReceivers.put(appEventType, handlers);
    }

    if (handlers.contains(eventHandler))
    {
      handlers.remove(eventHandler);
    }
  }

  /**
   * Dispatches events on all registered handlers.The handler method will be be 
   * invoked with SwingUtilities.invokeLater(). This guarantees that view
   * updates will be processed correctly (eg. the progressbar).
   *
   * @param appEvent the application event to dispatch
   */
  public void dispatchGUIEvent(final ApplicationEvent appEvent)
  {
    SwingUtilities.invokeLater(new Runnable()
    {

      @Override
      public void run()
      {
        dispatchEvent(appEvent);
      }
    });
  }

  /**
   * The event will be dispatched directly from whatever thread you currently in.
   * Typically will be called from awt event thread to avoid synchronization
   * issues.
   *
   * Invoke this method if you need to recieve exceptions during processing of
   * the current event.
   *
   * @param appEvent the application event stopped
   */
  public void dispatchEvent(final ApplicationEvent appEvent)
  {
    log.debug("dispatching event: " + appEvent);
    final List<? extends ApplicationEventEnabled> handlers = eventTypeReceivers.
        get(appEvent.getClass());
    if (handlers != null)
    {
      synchronized (handlers)
      {
        for (final ApplicationEventEnabled handler : handlers)
        {
          try
          {
            final Method handlerMethod = handler.getClass().getMethod(
                HANDLER_METHOD_PREFIX, appEvent.getClass());
            handlerMethod.invoke(handler, appEvent);
          }
          catch (InvocationTargetException ex)
          {
            final Throwable t = ex.getCause();
            if (t instanceof CancelEventException)
            {
              log.info(appEvent + " cancelled");
              throw (CancelEventException) t;
            }
            else
            {
              final String msg = new StringBuffer("failed to dispatch event "
                  + appEvent + " to handler " + handler.getClass()).toString();
              log.error(msg, ex);
            }
          }
          catch (IllegalAccessException ex)
          {
            final String msg = new StringBuffer("failed to dispatch event "
                + appEvent + " to handler " + handler.getClass()).toString();
            log.error(msg, ex);
          }
          catch (IllegalArgumentException ex)
          {
            final String msg = new StringBuffer("failed to dispatch event "
                + appEvent + " to handler " + handler.getClass()).toString();
            log.error(msg, ex);
          }
          catch (NoSuchMethodException ex)
          {
            final String msg = new StringBuffer("failed to dispatch event "
                + appEvent + " to handler " + handler.getClass()).toString();
            log.error(msg, ex);
          }
          catch (SecurityException ex)
          {
            final String msg = new StringBuffer("failed to dispatch event "
                + appEvent + " to handler " + handler.getClass()).toString();
            log.error(msg, ex);
          }
        }
      }
    }
  }

  /**
   * This is a singleton.
   *
   * @return
   * @throws CloneNotSupportedException
   */
  @Override
  public Object clone() throws CloneNotSupportedException
  {
    throw new CloneNotSupportedException();
  }
}
