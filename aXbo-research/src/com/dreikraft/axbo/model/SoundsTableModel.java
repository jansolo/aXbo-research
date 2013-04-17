/*
 * © 2008 3kraft
 * $Id: SoundsTableModel.java,v 1.2 2008-05-13 15:08:44 illetsch Exp $
 */
package com.dreikraft.axbo.model;

import com.dreikraft.axbo.sound.Sound;
import com.dreikraft.axbo.util.BundleUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * $Id: SoundsTableModel.java,v 1.2 2008-05-13 15:08:44 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.2 $
 */
public class SoundsTableModel extends AbstractTableModel
{
  // global constants
  public static Log log = LogFactory.getLog(MetaDataTableModel.class);

  // Column Headings
  enum Column
  {
    PLAYS("plays"), ID("id"), NAME("name");
    private static final String HEADING_KEY_PREFIX =
        "soundsTable.columnheading.";
    private String headingKey;

    private Column(String headingKey)
    {
      this.headingKey = headingKey;
    }

    public String getLocalizedColumnHeading()
    {
      return BundleUtil.getMessage(HEADING_KEY_PREFIX + headingKey);
    }
  }
  private List<Sound> sounds;

  /** Creates a new instance of SleepDataTableModel */
  public SoundsTableModel()
  {
    sounds = new ArrayList<Sound>();
  }

  @SuppressWarnings("unchecked")
  public SoundsTableModel(List<Sound> sounds)
  {
    this.sounds = sounds;
  }

  @Override
  public String getColumnName(int col)
  {
    return Column.values()[col].getLocalizedColumnHeading();
  }

  @Override
  public int getRowCount()
  {
    return getSounds().size();
  }

  @Override
  public int getColumnCount()
  {
    return Column.values().length;
  }

  @Override
  public Object getValueAt(int row, int col)
  {
    Object val = null;
    switch (col)
    {
      case 0:
        val = getSounds().get(row).isPlaying() ? "x" : "";
        break;
      case 1:
        val = getSounds().get(row).getId();
        break;
      case 2:
        val = getSounds().get(row).getName();
        break;
    }
    return val;
  }

  @Override
  public Class<?> getColumnClass(int c)
  {
    return getValueAt(0, c).getClass();
  }

  public Sound getSoundAt(int row)
  {
    return getSounds().get(row);
  }

  public List<Sound> getSounds()
  {
    return sounds;
  }

  public void setSounds(List<Sound> sounds)
  {
    this.sounds = sounds;
  }

  public void addSound(Sound sound)
  {
    sounds.add(sound);
    this.fireTableDataChanged();
  }

  public void removeSound(Sound sound)
  {
    sounds.remove(sound);
    this.fireTableDataChanged();
  }
}
