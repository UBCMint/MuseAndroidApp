package ca.ubc.best.mint.museandroidapp;

import java.text.SimpleDateFormat;

public class Constants {
  /**
   * Muse send frequency data back at this rate:
   * http://developer.choosemuse.com/tools/available-data#Absolute_Band_Powers
   */
  public static final int FREQUENCY_HZ = 10;

  /** Location of file for historic results. */
  public static final String HISTORIC_RESULTS_FILE = "historicResults.dat";
  public static final String BETA_RESULTS_FILE = "betaResults.dat";
  public static final String ALPHA_RESULTS_FILE = "alphaResults.txt";

  /** How to format dates to string. */
  public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MMM d, yyyy");
}
