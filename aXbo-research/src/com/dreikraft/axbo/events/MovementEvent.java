/*
 * © 2008 3kraft
 * $Id: MovementEvent.java,v 1.12 2010-12-16 23:13:53 illetsch Exp $
 */
package com.dreikraft.axbo.events;


import com.dreikraft.events.ApplicationEvent;
import com.dreikraft.axbo.data.MovementData;
import com.dreikraft.axbo.util.ReflectUtil;


/**
 * $Id: MovementEvent.java,v 1.12 2010-12-16 23:13:53 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.12 $
 */
public class MovementEvent extends ApplicationEvent implements
    Comparable<MovementEvent>
{
  private MovementData movementData;
  private String id;
  private String cmd;
      
  public MovementEvent(Object source, MovementData movementData, String id)
  {
    super(source);
    this.movementData = movementData;
    this.id = id;
  }
  
  public MovementEvent(Object source, MovementData movementData, String id, 
      String cmd)
  {
    this(source, movementData, id);
    this.cmd = cmd;
  }
  
  public MovementData getMovementData()
  {
    return this.movementData;
  }

  public String getId()
  {
    return this.id;
  }

  public String getCmd()
  {
    return cmd;
  }

  @Override
  public String toString()
  {
    return ReflectUtil.toString(this);
  }

  @Override
  public int compareTo(MovementEvent evt)
  {
    if (this.getMovementData().getTimestamp().equals(
        evt.getMovementData().getTimestamp()))
      return 0;
    if (this.getMovementData().getTimestamp().before(
        evt.getMovementData().getTimestamp()))
      return -1;
    else
      return 1;
  }
}
/*
$Log: MovementEvent.java,v $
Revision 1.12  2010-12-16 23:13:53  illetsch
fixed log data reading

Revision 1.11  2010-11-29 15:42:23  illetsch
refactored aXbo communication

Revision 1.10  2008/05/13 15:08:44  illetsch
licenses, div. japanese fixes

Revision 1.9  2007/12/03 14:40:26  illetsch
axbo research v2

Revision 1.8  2007/05/15 10:05:33  illetsch
working aXbo mobile prototype with gravity filter

Revision 1.7  2007/05/03 09:18:55  illetsch
no message

Revision 1.6  2006/05/19 12:45:03  illetsch
*** empty log message ***

Revision 1.5  2006/04/18 13:18:14  illetsch
merge axbo_consumer branch back into trunk

Revision 1.4.2.2  2006/03/23 23:04:31  illetsch
*** empty log message ***

Revision 1.4.2.1  2006/03/22 07:58:09  illetsch
added win32comm.dll, working configuration panel, saving of configuration on exit, loading and showing of single and multiple spw files, sending commands to the alarm clock, internal frames resize, selectable look and feels, updated commons libraries, new layout of the internal frames, serial interaces do not need installation any more, runs directly from directory, varioes fixes

Revision 1.4  2005/04/20 15:38:04  illetsch
*** empty log message ***

Revision 1.3  2005/04/18 22:11:13  illetsch
*** empty log message ***

Revision 1.2  2005/04/18 10:25:38  illetsch
*** empty log message ***

Revision 1.1  2005/04/13 10:25:53  illetsch
*** empty log message ***

 */
