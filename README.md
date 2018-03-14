# Flank

**TODO: Flank logo**

Flank is an Android App that can provide users with a collection of metrics regarding their attentional state, by connecting to a Muse EEG headset and analyzing signal during some behavioural testing.

_Disclaimer: Flank is a prototype tool for exploring attention data with a Muse headset.
It is not clinically approved, so the results may not be medically accurate and should not be interpreted as such._

### Background

Flank is named after the Erikson flanker task, a behavioural test where participants select a region according to a central stimulus, while it is flanked by distractor stimuli that may point in the same (congruent) or opposite (incongruent) direction, or even no direction at all (neutral).

In their paper "[Differential Oscillatory Electroencephalogram Between Attention-Deficit/Hyperactivity Disorder Subtypes and Typically Developing Adolescents](https://doi.org/10.1016/j.biopsych.2013.08.023)", Mazaher et al (2014) were able to pair the Flanker task with preceding cue stimuli, and find two frequency-based indicators of ADHD: 
* _Reduced_ suppression of Alpha frequencies between 200ms and 1200ms after cue presented. During this period, a  participant is determining whether the cue actually corresponds to which side they should select.
* _Reduced_ suppression of Beta frequencies between 800ms and 1300ms after cue presented. During this period, a participant is sending motor commands to tap the correct side of the screen.

The Flank app implements the experiment protocol as closely as possible on an Android device using a Muse consumer EEG headset. Users can periodically test themselves using the phone-based Flanker tast, and get historic trends of their alpha- and beta-suppression, as well as overall reaction time and accuracy.

## Usage

First, the app must be installed on an Android device - either by loading the provided .apk, or by building the 'app' project with Android Studio and running that on their phone. A Muse EEG headset is also required (2016 version preferred, but 2014 works too).

The first screen presents the user two options: Either connect to a device and perform a Flanker task trial, or view past results.
**TODO - image**

On the first run, no historic data is available, so it is recommended to run the Flanker task. Make sure bluetooth is enabled and the Muse device is turned on, and select **TODO - button name**

The trial will start, and attempt to connect to the EEG headset. Connection status is shown top right by four indicator lights corresponding to the four sensors on the headset. Once all four are bright green, it is ok to start the trial. Note that the Android device running the app should be held in landscape mode for this section, ideally with thumbs or fingers near both sides of screen to allow ease of response.

The trial consists of 30 cue/stimulus pairs. The cues are a left and right arrow, with yellow indicating the stimulus is _likely_ (but not guaranteed) to be pointing in that direction. Arrow pairs which are both yellow or both blue are also possible, which do not give directional hints. Which the cues are present, no actions are required.

After the cues, a stimulus of five characters is shown, and the user must **tap on the side of the screen indicated by the middle character**. This will indicate left ('<'), right ('>'), or to abstain from tapping ('+'). As a distractor, the four flanking stimuli (two left, two right) will all be the same value, one of '<', '>' or '+'. The value of these flanking stimuli is to be ignored, they do not indicate which side should be tapped.

**TODO - image of tapping right with right flankers.**
**TODO - image of tapping right with '+' flankers.**

Throughout the experiment, raw alpha and beta frequency strengths are collected. Upon completion of the 30 cue/stimulus pairs, this data is processed, and ERPs are shown for the two periods described in the background section. Average alpha- and beta-suppression is calculated and displayed, along with average reaction time and accuracy at tapping the correct side of screen (or not tapping when required).
 
**TODO - image of results page**

Finally, once data is recorded through successful completion of the task, it can be viewed from the main screen. Selecting the **TODO - button name** option will display historic results, which can be selected to view in finer detail.

### About Us

Flank was created by the [(Medical Innovation in NeuroTechnology](https://ubcmint.github.io/) (MiNT) team of undergraduate students, part of the group of [Biomedical Engineering Student Teams](http://www.ubcbest.com/) at the University of British Columbia in Vancouver, Canada. 

Flank was submitted as a project for the Open category of the [NeuroTechX 2018 Student Club competition](https://neurotechx.github.io/studentclubs/competition/).
