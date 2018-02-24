package ca.ubc.best.mint.museandroidapp.vm;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** Stimuli that can be to displayed for the task. */
enum FlankerStimulus {
  RIGHTCONGRUENT(">>>>>"),
  LEFTCONGRUENT("<<<<<"),
  RIGHTNEUTRAL("++>++"),
  LEFTNEUTRAL("++<++"),
  RIGHTINCONGRUENT("<<><<"),
  LEFTINCONGRUENT(">><>>"),
  CATCH ("+++++");

  private final String textVersion;

  FlankerStimulus(String textVersion) {
    this.textVersion = textVersion;
  }


  /** Gets the text to display for this stimulus. */
  public String asText() {
    return this.textVersion;
  }

  /** Create an array of neutral stimuli **/
  public static List<FlankerStimulus> createNeutralStimuli (int numNeturalSimuli) {
    List<FlankerStimulus> neutralstimuliArray = new ArrayList<>();
    for(int i = 0; i < numNeturalSimuli/2; i++) {
      //half LEFTCONGRUENT
      neutralstimuliArray.add(FlankerStimulus.LEFTCONGRUENT);
    }

    for(int i = numNeturalSimuli/2; i < numNeturalSimuli; i++) {
      //half RIGHTCONGRUENT
      neutralstimuliArray.add(FlankerStimulus.RIGHTCONGRUENT);
    }
    return neutralstimuliArray;
  }

  /** Create an array of congruent stimuli **/
  public static List<FlankerStimulus> createCongStimuli (int numCongSimuli) {
    List<FlankerStimulus> congstimuliArray = new ArrayList<>();
    for(int i = 0; i < numCongSimuli/2; i++) {
      //half LEFTCONGRUENT
      congstimuliArray.add(FlankerStimulus.LEFTCONGRUENT);
    }

    for(int i = numCongSimuli/2; i < numCongSimuli; i++) {
      //half RIGHTCONGRUENT
      congstimuliArray.add(FlankerStimulus.RIGHTCONGRUENT);
    }
    return congstimuliArray;
  }

  /** Create an array of incongruent stimuli **/
  public static List<FlankerStimulus> createIncongStimuli (int numIncongSimuli) {
    List<FlankerStimulus> IncongstimuliArray = new ArrayList<>();
    for(int i = 0; i < numIncongSimuli/2; i++) {
      //half LEFTCONGRUENT
      IncongstimuliArray.add(FlankerStimulus.LEFTCONGRUENT);
    }

    for(int i = numIncongSimuli/2; i < numIncongSimuli; i++) {
      //half RIGHTCONGRUENT
      IncongstimuliArray.add(FlankerStimulus.RIGHTCONGRUENT);
    }
    return IncongstimuliArray;
  }




  /** @return An ordering of stimuli to use for a given number of trials. */
 /*
  public static List<FlankerStimulus> createStimuli(int totalTrials) {
    // congPercent = 0.289;
    // neutralPercent = 0.289;
    // incongPercent = 0.421;
    // totalTrials = 10;
    int numCong = Math.round(0.289f * totalTrials);
    int numNeut = Math.round(0.289f * totalTrials);
    List<FlankerStimulus> stimulusArray = new ArrayList<>();
    for(int i = 0; i < numCong/2; i++) {
      //half LEFTCONGRUENT
      stimulusArray.add(FlankerStimulus.LEFTCONGRUENT);
    }

    for(int i = numCong/2; i < numCong; i++) {
      //half RIGHTCONGRUENT
      stimulusArray.add(FlankerStimulus.RIGHTCONGRUENT);
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
*/

  private static final List<FlankerStimulus> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
  private static final int SIZE = VALUES.size();
  private static final Random RANDOM = new Random();

  public static FlankerStimulus randomStimulusCue()  {
    return VALUES.get(RANDOM.nextInt(SIZE));
  }
}
