/*
 * © 2014 3kraft IT GmbH & Co KG
 */
package com.dreikraft.axbo.model;

import com.dreikraft.axbo.Axbo;
import com.dreikraft.axbo.util.BundleUtil;

/**
 * Supported UI languages.
 * @author jan.illetschko@3kraft.com
 */
public enum SupportedLanguage {
  en,
  de,
  fr,
  ja,
  ru;
  
  public String getLocalizedName() {
    return BundleUtil.getMessage(Axbo.LANGUAGE_PREF);
  }
}
