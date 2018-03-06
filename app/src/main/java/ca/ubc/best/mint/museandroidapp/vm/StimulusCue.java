package ca.ubc.best.mint.museandroidapp.vm;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Carolfa on 2018-02-23.
 */

public class StimulusCue {
    public FlankerStimulus stimulus;
    public FlankerCue cue;
    public StimulusCue (FlankerStimulus stimulus, FlankerCue cue) {
        this.stimulus = stimulus;
        this.cue = cue;
    }

    public static List<StimulusCue> createNeutralStimuliCueList(List<FlankerStimulus> neutralStimuliList) {
        /** Neutral Stimuli are preceded by:
            76.4% NULL cues
            23.6% RP cues, out of all RP cues 84% are the correct cue, 16% are the wrong cue.
            For the correct RP cues, 27.7% are followed by a catch stimuli "+++++",
               users are instructed to tap on the side indicated by the RP cue
         **/
        List<StimulusCue> neutralStimulusCueSet = new ArrayList<>();

        int arraySize = neutralStimuliList.size();
        int numNullCues = Math.round(0.764f * arraySize);
        int numRPCues = arraySize - numNullCues; // All cues are null or RP
        int numWrongRPCues = Math.round(0.16f * numRPCues);
        int numRightRPCues = numRPCues - numWrongRPCues;
        int numCorNCatRPCues = Math.round(0.723f * numRightRPCues);
        int numCorCatchRPCues = numRightRPCues - numCorNCatRPCues;
        assert numNullCues + numWrongRPCues + numCorNCatRPCues + numCorCatchRPCues == arraySize;

        /**Add null cues**/
        int index = 0;

        //half left neutral stimulus
        for(int i = 0; i < numNullCues; i++) {
            neutralStimulusCueSet.add(new StimulusCue(neutralStimuliList.get(index), FlankerCue.NULL));
            index ++;
        }

        //add wrong rp cues
        for(int i=0;i<numWrongRPCues; i++) {
            if(neutralStimuliList.get(index) == FlankerStimulus.LEFTNEUTRAL){
                // add Right RP cue if the stimulus is Left Neutral
                neutralStimulusCueSet.add(new StimulusCue(neutralStimuliList.get(index), FlankerCue.RRP));
            }
            else {
                // else add Left RP cue
                neutralStimulusCueSet.add(new StimulusCue(neutralStimuliList.get(index), FlankerCue.LRP));
            }
            index ++;
        }

        //add CORRECT non-catch rp cues
        for(int i=0; i < numCorNCatRPCues; i++) {
            if(neutralStimuliList.get(index) == FlankerStimulus.LEFTNEUTRAL){
                // add Left RP cue if the stimulus is Left Neutral
                neutralStimulusCueSet.add(new StimulusCue(neutralStimuliList.get(index), FlankerCue.LRP));
            }
            else {
                // else add Right RP cue
                neutralStimulusCueSet.add(new StimulusCue(neutralStimuliList.get(index), FlankerCue.RRP));
            }
            index ++;
        }

        //add CORRECT Catch rp cues

        //half has LRP cues
        for(int i=0; i<numCorCatchRPCues/2; i++) {
            neutralStimulusCueSet.add(new StimulusCue(FlankerStimulus.CATCH, FlankerCue.LRP));
            index ++;
        }
        //half has RRP cues
        for(int i=0;i<neutralStimuliList.size(); i++) {
            neutralStimulusCueSet.add(new StimulusCue(FlankerStimulus.CATCH, FlankerCue.RRP));
            index ++;
        }

        Collections.shuffle(neutralStimulusCueSet); //randomize the array
        Log.d("MINT", "Created Neutral Stimulus Cue Set: " + neutralStimulusCueSet.toString());
        return neutralStimulusCueSet;
    }


    public static List<StimulusCue> createCongStimuliCueList(List<FlankerStimulus> congStimuliList) {
        /** Congruent Stimuli are preceded by:
         76.4% NULL cues
         23.6% RP cues, out of all RP cues 84% are the correct cue, 16% are the wrong cue.
         For the correct RP cues, 27.7% are followed by a catch stimuli "+++++",
            users are instructed to tap on the side indicated by the RP cue
         **/
        List<StimulusCue> congStimulusCueSet = new ArrayList<>();

        int arraySize = congStimuliList.size();
        int numNullCues = Math.round(0.764f * arraySize);
        int numRPCues = arraySize - numNullCues; // All cues are null or RP
        int numWrongRPCues = Math.round(0.16f * numRPCues);
        int numRightRPCues = numRPCues - numWrongRPCues;
        int numCorNCatRPCues = Math.round(0.723f * numRightRPCues);
        int numCorCatchRPCues = numRightRPCues - numCorNCatRPCues;
        assert numNullCues + numWrongRPCues + numCorNCatRPCues + numCorCatchRPCues == arraySize;


        /**Add null cues**/
        int index = 0;

        //half left neutral stimulus
        for(int i=0;i<numNullCues;i++) {
            congStimulusCueSet.add(new StimulusCue(congStimuliList.get(index), FlankerCue.NULL));
            index ++;
        }

        //add wrong rp cues
        for(int i=0; i<numWrongRPCues; i++) {
            if(congStimuliList.get(index) == FlankerStimulus.LEFTCONGRUENT){
                // add Right RP cue if the stimulus is Left Congruent
                congStimulusCueSet.add(new StimulusCue(congStimuliList.get(index), FlankerCue.RRP));
            }
            else {
                // else add Left RP cue
                congStimulusCueSet.add(new StimulusCue(congStimuliList.get(index), FlankerCue.LRP));
            }
            index ++;
        }

        //add CORRECT non-catch rp cues
        for(int i=0; i<numCorNCatRPCues; i++) {
            if(congStimuliList.get(index) == FlankerStimulus.LEFTCONGRUENT){
                // add Left RP cue if the stimulus is Left Congruent
                congStimulusCueSet.add(new StimulusCue(congStimuliList.get(index), FlankerCue.LRP));
            }
            else {
                // else add Right RP cue
                congStimulusCueSet.add(new StimulusCue(congStimuliList.get(index), FlankerCue.RRP));
            }
            index++;
        }

        //add CORRECT Catch rp cues

        //half has LRP cues
        for(int i=0; i<numCorCatchRPCues/2; i++) {
            congStimulusCueSet.add(new StimulusCue(FlankerStimulus.CATCH, FlankerCue.LRP));
            index ++;
        }
        //half has RRP cues
        for(int i=0; i<congStimuliList.size(); i++) {
            congStimulusCueSet.add(new StimulusCue(FlankerStimulus.CATCH, FlankerCue.RRP));
            index ++;
        }

        Collections.shuffle(congStimulusCueSet); //randomize the array
        Log.d("MINT", "Created Congruent Stimulus Cue Set: " + congStimulusCueSet.toString());
        return congStimulusCueSet;
    }


    public static List<StimulusCue> createIncongStimuliCueList(List<FlankerStimulus> incongStimuliList) {
        /** Incongruent Stimuli are preceded by:
         47.5% NULL cues
         26.25% WARN cues
         26.25% RP cues, out of all RP cues 84% are the correct cue, 16% are the wrong cue.
         For the correct RP cues, 27.7% are followed by a catch stimuli "+++++", users are instructed to tap on the side indicated by the RP cue

         **/
        List<StimulusCue> incongStimulusCueSet = new ArrayList<>();

        int arraySize = incongStimuliList.size();
        int numNullCues = Math.round(0.475f * arraySize);
        int numWarnCues = Math.round(0.2625f * arraySize);
        int numRPCues = arraySize - numNullCues - numWarnCues;
        int numWrongRPCues = Math.round(0.16f * numRPCues);
        int numRightRPCues = numRPCues - numWrongRPCues;
        int numCorNCatRPCues = Math.round(0.723f * numRightRPCues);
        int numCorCatchRPCues = numRightRPCues - numCorNCatRPCues;
        assert numNullCues + numWarnCues + numWrongRPCues + numCorNCatRPCues + numCorCatchRPCues == arraySize;

        int index = 0;

        /**Add null cues**/
        for(int i=0; i<numNullCues; i++) {
            incongStimulusCueSet.add(new StimulusCue(incongStimuliList.get(index), FlankerCue.NULL));
            index ++;
        }

        /** Add Warning cues **/
        for(int i=0; i<numWarnCues; i++) {
            incongStimulusCueSet.add(new StimulusCue(incongStimuliList.get(index), FlankerCue.WARN));
            index++;
        }


        /** Add RP cues **/

        //add wrong rp cues
        for(int i=0;i<numWrongRPCues; i++) {
            if(incongStimuliList.get(index) == FlankerStimulus.LEFTINCONGRUENT){
                // add Right RP cue if the stimulus is Left Incongruent
                incongStimulusCueSet.add(new StimulusCue(incongStimuliList.get(index), FlankerCue.RRP));
            }
            else {
                // else add Left RP cue
                incongStimulusCueSet.add(new StimulusCue(incongStimuliList.get(index), FlankerCue.LRP));
            }
            index ++;
        }

        //add CORRECT non-catch rp cues
        for(int i=0;i<numCorNCatRPCues; i++) {
            if(incongStimuliList.get(index) == FlankerStimulus.LEFTINCONGRUENT){
                // add Left RP cue if the stimulus is Left Incongruent
                incongStimulusCueSet.add(new StimulusCue(incongStimuliList.get(index), FlankerCue.LRP));
            }
            else {
                // else add Right RP cue
                incongStimulusCueSet.add(new StimulusCue(incongStimuliList.get(index), FlankerCue.RRP));
            }
            index ++;
        }

        //add CORRECT Catch rp cues

        //half has LRP cues
        for(int i=0; i<numCorCatchRPCues/2; i++) {
            incongStimulusCueSet.add(new StimulusCue(FlankerStimulus.CATCH, FlankerCue.LRP));
            index ++;
        }
        //half has RRP cues
        for(int i=0;i< incongStimuliList.size(); i++) {
            incongStimulusCueSet.add(new StimulusCue(FlankerStimulus.CATCH, FlankerCue.RRP));
            index ++;
        }

        Collections.shuffle(incongStimulusCueSet); //randomize the array
        Log.d("MINT", "Created Incongruent Stimulus Cue Set: " + incongStimulusCueSet.toString());
        return incongStimulusCueSet;
    }
}
