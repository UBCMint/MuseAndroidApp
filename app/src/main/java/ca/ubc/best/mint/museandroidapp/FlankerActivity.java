package ca.ubc.best.mint.museandroidapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import ca.ubc.best.mint.museandroidapp.databinding.ActivityFlankerBinding;
import ca.ubc.best.mint.museandroidapp.vm.FlankerStage;
import ca.ubc.best.mint.museandroidapp.vm.FlankerViewModel;

/** Activity to draw the stimuli for the Flanker task, and record results. */
public class FlankerActivity extends AppCompatActivity
    implements FlankerViewModel.CompletionHandler, View.OnTouchListener {
  /** Initial delay for animating going full-screen. */
  private static final int UI_ANIMATION_DELAY = 100;

  private final Handler hideHandler = new Handler();
  private final FlankerViewModel viewModel = new FlankerViewModel(this);

  private View rootView; // Root of the view heirarchy, used for switching to fullscreen.

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Bind VM to view, saving the root for later.
    ActivityFlankerBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_flanker);
    binding.setFlankerVM(viewModel);
    rootView = findViewById(R.id.fullscreenContent);
    rootView.setOnTouchListener(this);
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    hideHandler.postDelayed(new Runnable() {
      @Override public void run() {
        hideDelayed();
      }
    }, UI_ANIMATION_DELAY);
  }

  private void hideDelayed() {
    // Hide UI first
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.hide();
    }

    // Schedule a runnable to remove the status and navigation bar after a delay
    hideHandler.postDelayed(new Runnable() {
      @SuppressLint("InlinedApi")
      @Override public void run() {
        // Delayed removal of status and navigation bar
        rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        // Actually start the first trial!
        viewModel.beginStage(FlankerStage.PRE_CUE);
      }
    }, UI_ANIMATION_DELAY);
  }

  @Override
  public void onComplete(FlankerViewModel viewModel) {
    // TODO: Analyses EEG data and tap data from the viewModel...
    Intent finishedIntent = new Intent(this, TaskCompleteActivity.class);
    startActivity(finishedIntent);
  }

  @Override
  public boolean onTouch(View rootView, MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      boolean isOnLeft = event.getX() < (rootView.getWidth() / 2);
      return viewModel.handleScreenTap(isOnLeft);
    }
    return false;
  }
}
