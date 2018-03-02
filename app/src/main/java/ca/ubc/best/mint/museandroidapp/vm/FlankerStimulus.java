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
      //half LEFTNEUTRAL
      neutralstimuliArray.add(FlankerStimulus.LEFTNEUTRAL);
    }

    for(int i = numNeturalSimuli/2; i < numNeturalSimuli; i++) {
      //half RIGHTNEUTRAL
      neutralstimuliArray.add(FlankerStimulus.RIGHTNEUTRAL);
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
  public static List<FlankerStimulus> createIncongStimuli(int numIncongSimuli) {
    List<FlankerStimulus> incongstimuliArray = new ArrayList<>();
    for(int i = 0; i < numIncongSimuli/2; i++) {
      //half LEFTINCONGRUENT
      incongstimuliArray.add(FlankerStimulus.LEFTINCONGRUENT);
    }

    for(int i = numIncongSimuli/2; i < numIncongSimuli; i++) {
      //half RIGHTINCONGRUENT
      incongstimuliArray.add(FlankerStimulus.RIGHTINCONGRUENT);
    }
    return incongstimuliArray;
  }


}
