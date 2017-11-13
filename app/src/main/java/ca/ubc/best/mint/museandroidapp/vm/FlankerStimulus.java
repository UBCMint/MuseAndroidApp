package ca.ubc.best.mint.museandroidapp.vm;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Stimuli that can be to displayed for the task. */
enum FlankerStimulus {
  CONGRUENT(">>>>>"),
  NEUTRAL("+++++"),
  INCONGRUENT(">><>>");

  private final String textVersion;

  FlankerStimulus(String textVersion) {
    this.textVersion = textVersion;
  }

  /** Gets the text to display for this stimulus. */
  public String asText() {
    return this.textVersion;
  }

  /** @return An ordering of stimuli to use for a given number of trials. */
  public static List<FlankerStimulus> createStimuli(int totalTrials) {
    // congPercent = 0.289;
    // neutralPercent = 0.289;
    // incongPercent = 0.421;
    // totalTrials = 10;
    int numCong = Math.round(0.289f * totalTrials);
    int numNeut = Math.round(0.289f * totalTrials);
    List<FlankerStimulus> stimulusArray = new ArrayList<>();
    for(int i = 0; i < numCong; i++) {
      stimulusArray.add(FlankerStimulus.CONGRUENT);
    }
    for(int i = 0; i < numNeut; i++) {
      stimulusArray.add(FlankerStimulus.NEUTRAL);
    }
    while (stimulusArray.size() < totalTrials) {
      stimulusArray.add(FlankerStimulus.INCONGRUENT);
    }
    Collections.shuffle(stimulusArray); //randomize the array
    Log.d("MINT", "Stimulus array: " + stimulusArray.toString());
    return stimulusArray;
  }
}
