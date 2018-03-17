# NTX Open Challenge submission

![Logo](https://raw.githubusercontent.com/UBCMint/MuseAndroidApp/master/images/flank.png)

Flank, a submission by UBC MiNT team to the 2018 NTX student challenge, Open category.

_Disclaimer: The UBC MiNT App 'Flank' is a prototype tool for exploring attention data with a Muse headset.
It is not clinically approved, so the results may not be medically accurate and should not be interpreted as such._

## Instructions to run:

Flank is an Android app that uses a Muse EEG headset, so will require both an Android device with bluetooth, and a Muse EEG headset. Once these two are obtained, there are two options for running the submission:

1) Download Flank.apk from this folder and [install it on your phone](https://www.wikihow.tech/Install-APK-Files-on-Android). The app is then run by clicking an Icon that should look like the image above.
2) Check out this repository, and open the MuseAndroidApp project in Android studio. This can be used to inspect the code, and then build the app and [run it on the Android device via USB](https://developer.android.com/studio/run/device.html). Note that this depends on the included [EEGToolkit library](https://github.com/padster/Muse-EEG-Toolkit/), written by a member of UBC MiNT for processing and visualising data live from a Muse. This transitively also depends on the [Muse Android library](http://developer.choosemuse.com/sdk/android) for streaming data from a device.

Once the App is installed, see our repository readme for instructions on how to use the app. Note that the device it connects to will be the first Muse headset found in the area, so it is recommended having only a single worn headset turned on at the time of connection.


## Technical details

#### App
This was an Android app implemented in Java, as most developers on the project had Android phones and the cost/ease of development on the platform was preferred to iOS. Additionally, we had some experience using Muse's own Android SDK.

The Android code itself uses fairly standard Android design; the initial screen, result list screen and final results screen are all static layout pages showing data and letting users click through to other screens. The screen that performs the Flanker task is more complicated - it uses the EEGToolkit library to display Muse connection status in realtime, plus is backed by a state machine that uses Timed tasks to transition between phases of the test. While doing this, it is also buffering live alpha and beta values from the muse, and using these to collect epochs at appropriate times after cues. The best place to start in code for seeing how this is implemented would be: [FlankerViewModel](https://github.com/UBCMint/MuseAndroidApp/blob/master/app/src/main/java/ca/ubc/best/mint/museandroidapp/vm/FlankerViewModel.java)

All screens use [Android Data Binding](https://developer.android.com/topic/libraries/data-binding/index.html) to make showing and updating values simple, plus responding to user actions like tapping.

#### Flanker Cues & Stimuli
Implementation for the Flanker task itself was primary based off the [supplementary information](https://www.sciencedirect.com/science/article/pii/S0006322313007762?via%3Dihub#s0135) provided with the Mazaheri et al. (2014) paper.

There are four types of [Cues](https://github.com/UBCMint/MuseAndroidApp/blob/master/app/src/main/java/ca/ubc/best/mint/museandroidapp/vm/FlankerCue.java): 
* NULL - two blue, no side indication
* LRP (Left Response Preparation) - left yellow, right blue, indicating likely having to tap left next.
* RRP (Right Response Preparation) - same as LRP, but right instead of left.
* WARN - two yellow, indicates that the next stimulus will likely have the middle character different to the side ones.
 
Additionally there are seven types of [Stimuli](https://github.com/UBCMint/MuseAndroidApp/blob/master/app/src/main/java/ca/ubc/best/mint/museandroidapp/vm/FlankerStimulus.java):
* LEFT/RIGHTCONGRUENT, where the side flanking characters are the same as the middle (<<<<< and >>>>>)
* LEFT/RIGHTNEUTRAL, where the side flanking characters are neutral (\++<\++ and \++>\++)
* LEFT/RIGHTINCONGRUENT, where the side flanking characters are the opposite (>><>> and <<><<)
* CATCH, where all arrows are neutral and the tap should be on the side indicated by the cue (+++++)

These are then combined into stimulus-cue pairs within [StimulusCue](https://github.com/UBCMint/MuseAndroidApp/blob/master/app/src/main/java/ca/ubc/best/mint/museandroidapp/vm/StimulusCue.java), using the frequencies provided in the paper. 

#### Flanker Data recording and processing

During each cue-stimulus tasks, recorders are running that write average raw alpha and beta channel values to circular array buffers. The Muse SDK writes [raw frequency data at 10Hz](http://developer.choosemuse.com/tools/available-data#Absolute_Band_Powers) - i.e. one sample every 100ms - so the following timings are used: 
* 800ms to 300ms (8 to 3 samples) **before** cue, to calculate the baseline power.
* 200ms to 1200ms (2 to 12 samples) **after** cue, to calculate average alpha power.
* 800ms to 1300ms (8 to 13 samples) **after** cue, to calculate average beta power.
See [FlankerLiveRecorder.java](https://github.com/UBCMint/MuseAndroidApp/blob/master/app/src/main/java/ca/ubc/best/mint/museandroidapp/vm/FlankerLiveRecorder.java) for implementation details

For alpha and beta cutoff points, each were chosen at times during the most power difference as observed in the paper, rounded to the nearest sample. After performing baseline correction, the suppression is calculated as the average value during the target time range, then negated to get a suppression value. The code powering this processing is available at [ResultsPostProcessing](https://github.com/UBCMint/MuseAndroidApp/blob/master/app/src/main/java/ca/ubc/best/mint/museandroidapp/analysis/ResultsPostProcessing.java).




