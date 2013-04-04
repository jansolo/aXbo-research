/*
 * © 2008 3kraft
 * $Id: SoundPackagesTableModel.java,v 1.4 2008-05-13 15:08:44 illetsch Exp $
 */
package com.dreikraft.axbo.model;

import com.dreikraft.axbo.sound.SoundPackage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * $Id: SoundPackagesTableModel.java,v 1.4 2008-05-13 15:08:44 illetsch Exp $
 * 
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.4 $
 */
public class SoundPackagesTableModel extends AbstractTableModel
{
  // global constants
  public static Log log = LogFactory.getLog(SoundPackagesTableModel.class);

  private List<SoundPackage> packages = new ArrayList<SoundPackage>();


  public SoundPackagesTableModel()
  {
    packages = new ArrayList<SoundPackage>();
  }

  public SoundPackagesTableModel(List<SoundPackage> packages)
  {
    this.packages = packages;
  }

  @Override
  public String getColumnName(int col)
  {
    return "Name";
  }

public int getRowCount()
  {
    return getPackages().size();
  }

  public int getColumnCount()
  {
    return 1;
  }

  public Object getValueAt(int row, int col)
  {
    Object val = null;
    switch (col)
    {
      case 0:
        val = getPackages().get(row).getName();
        break;
    }
    return val;
  }

  public SoundPackage getSoundPackageAt(int row)
  {
    return getPackages().get(row);
  }

  public void addSoundPackage(SoundPackage soundPackage)
  {
    packages.add(soundPackage);
    this.fireTableDataChanged();
  }

  public void removeSoundPackage(SoundPackage soundPackage)
  {
    packages.remove(soundPackage);
    this.fireTableDataChanged();
  }

  public void removeAllSoundPackages()
  {
    packages = new ArrayList<SoundPackage>();
    this.fireTableDataChanged();
  }

  public List<SoundPackage> getPackages()
  {
    return packages;
  }

  public void setPackages(List<SoundPackage> packages)
  {
    this.packages = packages;
    this.fireTableDataChanged();
  }
}
