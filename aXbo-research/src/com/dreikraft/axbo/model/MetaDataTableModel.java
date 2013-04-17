/*
 * © 2008 3kraft
 * $Id: MetaDataTableModel.java,v 1.16 2010-12-13 10:24:09 illetsch Exp $
 */
package com.dreikraft.axbo.model;

import com.dreikraft.events.ApplicationEventDispatcher;
import com.dreikraft.axbo.data.SleepData;
import com.dreikraft.axbo.events.SleepDataSave;
import com.dreikraft.axbo.util.BundleUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * $Id: MetaDataTableModel.java,v 1.16 2010-12-13 10:24:09 illetsch Exp $
 *
 * @author 3kraft - $Author: illetsch $
 * @version $Revision: 1.16 $
 */
public class MetaDataTableModel extends AbstractTableModel {
  // global constants

  public static Log log = LogFactory.getLog(MetaDataTableModel.class);

  // Column Headings
  enum Column {

    DATE("date", Date.class),
    ID("id", String.class),
    NAME("name", String.class),
    COMMENT("comment", String.class);
    private static final String HEADING_KEY_PREFIX =
        "metaDataTable.columnheading.";
    private String headingKey;
    private Class<?> type;

    private Column(final String headingKey, final Class<?> type) {
      this.headingKey = headingKey;
      this.type = type;
    }

    public String getLocalizedColumnHeading() {
      return BundleUtil.getMessage(HEADING_KEY_PREFIX + headingKey);
    }

    public Class<?> getType() {
      return type;
    }
  }
  private List<SleepData> data = new ArrayList<SleepData>();
  private List<SleepData> filteredData = new ArrayList<SleepData>();

  public MetaDataTableModel() {
    this.data = new ArrayList<SleepData>();
    filteredData = new ArrayList<SleepData>();
  }

  @Override
  public String getColumnName(int col) {
    return Column.values()[col].getLocalizedColumnHeading();
  }

  @Override
  public int getRowCount() {
    return filteredData.size();
  }

  @Override
  public int getColumnCount() {
    return Column.values().length;
  }

  @Override
  public Object getValueAt(int row, int col) {
    Object val = null;
    switch (col) {
      case 0:
        val = filteredData.get(row).calculateStartTime();
        break;
      case 1:
        val = filteredData.get(row).getId();
        break;
      case 2:
        val = filteredData.get(row).getName();
        break;
      case 3:
        val = filteredData.get(row).getComment();
        break;
    }
    return val;
  }

  @Override
  public Class<?> getColumnClass(int col) {
    return Column.values()[col].getType();
  }

  public SleepData getSleepDataAt(int row) {
    return filteredData.get(row);
  }

  public void filterData(final String name, final Date dateFrom, Date dateUntil) {
    final boolean searchName = (name != null && name.trim().length() > 0);
    final boolean searchDateFrom = (dateFrom != null);
    final boolean searchDateUntil = (dateUntil != null);

    // add 1 day to date until
    if (searchDateUntil) {
      GregorianCalendar cal = new GregorianCalendar();
      cal.setTime(dateUntil);
      cal.add(Calendar.DATE, 1);
      dateUntil = cal.getTime();
    }

    // are the search fields not all empty
    if (searchName || searchDateFrom || searchDateUntil) {
      filteredData = new ArrayList<SleepData>(data.size());
      for (SleepData record : data) {
        final String recName =
            (record.getName() != null ? record.getName() : "");
        final boolean matchesName = Pattern.compile("^.*" + name + ".*$",
            Pattern.CASE_INSENSITIVE).matcher(recName).matches();
        @SuppressWarnings({"null", "ConstantConditions"})
        final boolean matchesDateFrom = (searchDateFrom && dateFrom.before(
            record.
            calculateStartTime()));
        @SuppressWarnings({"null", "ConstantConditions"})
        final boolean matchesDateUntil = (searchDateUntil && dateUntil.after(
            record.
            calculateStartTime()));
        if ((matchesName || !searchName) && (matchesDateFrom || !searchDateFrom)
            && (matchesDateUntil
            || !searchDateUntil)) {
          filteredData.add(record);
        }
      }
    }

    // notify the TableSorter that the Tabel has changed
    this.fireTableDataChanged();
  }

  public List<SleepData> getData() {
    return data;
  }

  public void addSleepData(SleepData sleepData) {
    data.add(sleepData);
    filteredData.add(sleepData);
    this.fireTableDataChanged();
  }

  public void removeSleepData(SleepData sleepData) {
    data.remove(sleepData);
    filteredData.remove(sleepData);
    this.fireTableDataChanged();
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return columnIndex == 2 || columnIndex == 3;
  }

  @Override
  public void setValueAt(final Object aValue, int rowIndex, int columnIndex) {
    final SleepData sleepData = getSleepDataAt(rowIndex);
    if (columnIndex == 2) {
      sleepData.setName((String) aValue);
    }
    if (columnIndex == 3) {
      sleepData.setComment((String) aValue);
    }
    ApplicationEventDispatcher.getInstance().dispatchGUIEvent(new SleepDataSave(
        this, sleepData));
  }
}
