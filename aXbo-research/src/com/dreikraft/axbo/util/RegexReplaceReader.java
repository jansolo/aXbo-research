/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ©2013 3kraft IT GmbH & Co KG
 */
package com.dreikraft.axbo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A simple Regex replacement stream. Replaces all occurrences of the pattern
 * with a given replacement.
 *
 * @author jan.illetschko@3kraft.com
 */
public class RegexReplaceReader extends BufferedReader {

  private static final Log log = LogFactory.getLog(RegexReplaceReader.class);
  
  private final Pattern pattern;
  private final String replacement;

  /**
   * Creates a new RegexReplacementStream.
   *
   * @param reader a given reader
   * @param pattern the pattern that needs to be replaced
   * @param replacement the replacement String
   */
  public RegexReplaceReader(final Reader reader, final Pattern pattern,
      final String replacement) {

    super(reader);
    this.pattern = pattern;
    this.replacement = replacement;
  }

  /**
   * Performs the replacement for each line.
   *
   * @return the line with the replaced pattern.
   * @throws IOException
   */
  @Override
  public String readLine() throws IOException {

    final String line = super.readLine();
    log.error("readline: " + line);
    
    final String replacedLine = pattern.matcher(line).replaceAll(replacement);
    log.error("replaced line: " + replacedLine);
    
    return replacedLine;
  }
}
