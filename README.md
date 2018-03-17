# Flank

![Logo](https://raw.githubusercontent.com/UBCMint/MuseAndroidApp/master/images/flank.png)

Flank is an Android App that can provide users with a collection of metrics regarding their attentional state, by connecting to a Muse EEG headset and analyzing signal during some behavioural testing.

_Disclaimer: The UBC MiNT App 'Flank' is a prototype tool for exploring attention data with a Muse headset.
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

_Initial screen of the Application - either start a run, or view previous results._
![Initial Screen](https://raw.githubusercontent.com/UBCMint/MuseAndroidApp/master/images/initial.jpg)

On the first run, no historic data is available, so it is recommended to run the Flanker task. Make sure bluetooth is enabled and the Muse device is turned on, and select "Begin testing"

1 START 
The app will attempt to connect with the EEG headset. Connection status is shown top right by four indicator lights corresponding to the four sensors on the headset. Once all four are bright green, proceed to start the trial. 

Start Screen|
:------:|
![Start Screen](https://raw.githubusercontent.com/UBCMint/MuseAndroidApp/master/images/startScreen.png)|
* Note that the Android device running the app should be held in landscape mode for this section, ideally with thumbs or fingers near both sides of screen to allow ease of response.

2 Flanker Test
The test consists of 38 trials, each trial begins with a cue, then followed by a stimulus. 

The cues are a pair of left and right triangles colored in either yellow or blue. If one triangle is yellow, it indicates the stimulus that follows is _likely_ (but not guaranteed) to be pointing in that direction. 

_Example of a cues indicating that the following stimulus **may** require a tap on the **left**_
Cue|
:---:|
![Cue Left](https://raw.githubusercontent.com/UBCMint/MuseAndroidApp/master/images/cueLeft.jpg)|

Triangle pairs can be both yellow or blue. Two blue triangles do not give directional hints. Two yellow triangles hints that the middle arrow in the stimulus is pointing to the opposite direction of the side arrows. When the cues are present, no actions are required.

The stimulus is consisted of five characters of arrows or plus signs. The four characters on the side will always match each other, whereas the centre character may differ from the side characters.
* Congruent Stimulus: side characters match the centre character
    * ">>>>>"
    * "<<<<<"
* Incongruent Stimulus: side characters contradicts the centre character
    * ">><>>"
    * "<<><<"
* Neutral Stimulus: side characters are plus signs
    * "\++>\++"
    * "\++<\++"
* Catch Stimulus: all characters are plus signs
    * "+++++"    

When the stimulus is shown, the user must **tap on the side of the screen indicated by the middle character**. The direction of the side arrows are to be ignored. In the case of a **Catch Trial**, the user must tap on the direction indicated by the yellow cue previously shown. 

_Example of a Congruent stimulus: the user should tap on the *right*, as the center character points **rightwards**._
Congruent Stimulus |
:------: |
![Tap Right with Right Flankers](https://raw.githubusercontent.com/UBCMint/MuseAndroidApp/master/images/tapRightFlankRight.jpg)|

_Example of a Incongruent stimulus: despite the side arrows points to the right the user should tap on the **left**, as the **centre arrow** points to the **left**._
Incongruent Stimulus |
:------: |
![Tap Left with Right Flankers](https://raw.githubusercontent.com/UBCMint/MuseAndroidApp/master/images/tapLeftFlankRight.jpg)|

_Example of a Catch stimulus: the cue previoiusly shown has a yellow triangle pointing to the left, the user should remember this direction and tap on left the side when a catch stimulus is shown._

Cue          |  Catch Stimulus
:-------------------------:|:-------------------------:
![Cue Left](https://raw.githubusercontent.com/UBCMint/MuseAndroidApp/master/images/cueLeft.jpg)  |  ![Catch Stimulus](https://raw.githubusercontent.com/UBCMint/MuseAndroidApp/master/images/catchTrial.png)

3 Relax Period
After every stimulus is shown the border of the screen will turn green. During this period the user can relax their eyes. Once the border turns white, the next trial begin shortly. 

Relax Stage | Trial Stage
:-------------------------:|:-------------------------:
![Relax Stage](https://raw.githubusercontent.com/UBCMint/MuseAndroidApp/master/images/relaxStage.png)  |  ![Trial Stage](https://raw.githubusercontent.com/UBCMint/MuseAndroidApp/master/images/trialStage.png)



Throughout the experiment, raw alpha and beta frequency strengths are collected. Upon completion of the 38 cue/stimulus trials, this data is processed, and ERPs are shown for the two periods described in the background section. Average alpha- and beta-suppression is calculated and displayed, along with average reaction time and accuracy at tapping the correct side of screen (or not tapping when required).

_Results after a trial, showing tap accuracy, timing, and suppression epochs with average decrease._
![Trial Results](https://raw.githubusercontent.com/UBCMint/MuseAndroidApp/master/images/oneResult.jpg)

Finally, once data is recorded through successful completion of the task, it can be viewed from the main screen. Selecting the "View past results" option will display historic results, which can be selected to view in finer detail.

_List of historic results, tapping one will show the result screen as above._
![Historic Results](https://raw.githubusercontent.com/UBCMint/MuseAndroidApp/master/images/allResults.jpg)

### About Us

Flank was created by the [(Medical Innovation in NeuroTechnology](https://ubcmint.github.io/) (MiNT) team of undergraduate students, part of the group of [Biomedical Engineering Student Teams](http://www.ubcbest.com/) at the University of British Columbia in Vancouver, Canada.

Flank was submitted as a project for the Open category of the [NeuroTechX 2018 Student Club competition](https://neurotechx.github.io/studentclubs/competition/).