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
            For the correct RP cues, 27.7% are followed by a catch stimuli "+++++", users are instructed to tap on the side indicated by the RP cue
         **/
        List<StimulusCue> neutralStimulusCueSet = new ArrayList<>();

        int arraySize = neutralStimuliList.size();
        int numNullCues = Math.round(0.764f * arraySize);
        int numWrongRPCues = Math.round(0.236f * 0.16f * arraySize);
        int numCorNCatRPCues = Math.round(0.236f * 0.84f * 0.723f * arraySize);
        int numCorCatchRPCues =  Math.round(0.236f * 0.84f * 0.277f * arraySize);


        /**Add null cues**/
        int index = 0;

        //half left neutral stimulus
        while( index < numNullCues) {
            neutralStimulusCueSet.add(new StimulusCue(neutralStimuliList.get(index), FlankerCue.NULL));
            index ++;
        }

        //add wrong rp cues
        while( index < numWrongRPCues) {
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
        while( index < numCorNCatRPCues) {
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
        while( index < numCorCatchRPCues/2) {
            neutralStimulusCueSet.add(new StimulusCue(FlankerStimulus.CATCH, FlankerCue.LRP));
            index ++;
        }
        //half has RRP cues
        while(index < neutralStimuliList.size()) {
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
         For the correct RP cues, 27.7% are followed by a catch stimuli "+++++", users are instructed to tap on the side indicated by the RP cue
         **/
        List<StimulusCue> congStimulusCueSet = new ArrayList<>();

        int arraySize = congStimuliList.size();
        int numNullCues = Math.round(0.764f * arraySize);
        int numWrongRPCues = Math.round(0.236f * 0.16f * arraySize);
        int numCorNCatRPCues = Math.round(0.236f * 0.84f * 0.723f * arraySize);
        int numCorCatchRPCues =  Math.round(0.236f * 0.84f * 0.277f * arraySize);


        /**Add null cues**/
        int index = 0;

        //half left neutral stimulus
        while( index < numNullCues) {
            congStimulusCueSet.add(new StimulusCue(congStimuliList.get(index), FlankerCue.NULL));
            index ++;
        }

        //add wrong rp cues
        while( index < numWrongRPCues) {
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
        while( index < numCorNCatRPCues) {
            if(congStimuliList.get(index) == FlankerStimulus.LEFTCONGRUENT){
                // add Left RP cue if the stimulus is Left Congruent
                congStimulusCueSet.add(new StimulusCue(congStimuliList.get(index), FlankerCue.LRP));
            }
            else {
                // else add Right RP cue
                congStimulusCueSet.add(new StimulusCue(congStimuliList.get(index), FlankerCue.RRP));
            }
            index ++;
        }

        //add CORRECT Catch rp cues

        //half has LRP cues
        while( index < numCorCatchRPCues/2) {
            congStimulusCueSet.add(new StimulusCue(FlankerStimulus.CATCH, FlankerCue.LRP));
            index ++;
        }
        //half has RRP cues
        while(index < congStimuliList.size()) {
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
        int numWrongRPCues = Math.round(0.2625f * 0.16f * arraySize);
        int numCorNCatRPCues = Math.round(0.2625f * 0.84f * 0.723f * arraySize);
        int numCorCatchRPCues =  Math.round(0.2625f * 0.84f * 0.277f * arraySize);



        int index = 0;

        /**Add null cues**/
        while( index < numNullCues) {
            incongStimulusCueSet.add(new StimulusCue(incongStimuliList.get(index), FlankerCue.NULL));
            index ++;
        }

        /** Add Warning cues **/
        while( index < numWarnCues) {
            incongStimulusCueSet.add(new StimulusCue(incongStimuliList.get(index), FlankerCue.WARN));
            index++;
        }


        /** Add RP cues **/

        //add wrong rp cues
        while( index < numWrongRPCues) {
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
        while( index < numCorNCatRPCues) {
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
        while( index < numCorCatchRPCues/2) {
            incongStimulusCueSet.add(new StimulusCue(FlankerStimulus.CATCH, FlankerCue.LRP));
            index ++;
        }
        //half has RRP cues
        while(index < incongStimuliList.size()) {
            incongStimulusCueSet.add(new StimulusCue(FlankerStimulus.CATCH, FlankerCue.RRP));
            index ++;
        }

        Collections.shuffle(incongStimulusCueSet); //randomize the array
        Log.d("MINT", "Created Incongruent Stimulus Cue Set: " + incongStimulusCueSet.toString());
        return incongStimulusCueSet;
    }




}
