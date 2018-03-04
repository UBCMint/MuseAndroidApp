package ca.ubc.best.mint.museandroidapp;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import ca.ubc.best.mint.museandroidapp.databinding.ResultItemBinding;
import ca.ubc.best.mint.museandroidapp.analysis.HistoricResults;
import eeg.useit.today.eegtoolkit.view.CustomViewHolder;

/**
 * Adapter that allows binding a list of devices to a list view,
 * by binding each device separately to its own single-device view.
 */
public class ResultListAdapter extends RecyclerView.Adapter<CustomViewHolder<ResultItemBinding>> {
  /** Context this adapter lives within. */
  private final Context ctx;

  /** Viewmodel of the list of devices that this is displaying. */
  private final HistoricResults viewModel;

  public ResultListAdapter(final Context ctx, HistoricResults viewModel) {
    this.ctx = ctx;
    this.viewModel = viewModel;
  }

  @Override
  public CustomViewHolder<ResultItemBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
    ResultItemBinding binding = DataBindingUtil.inflate(
        LayoutInflater.from(this.ctx), R.layout.result_item, parent, false);
    return new CustomViewHolder<>(binding);
  }

  @Override
  public void onBindViewHolder(CustomViewHolder<ResultItemBinding> holder, int position) {
    // View is created, so bind the view to the device it shows.
    holder.getBinding().setContext(ctx);
    holder.getBinding().setResult(this.viewModel.getResult(position));
    holder.getBinding().setResultList(this.viewModel);
  }

  @Override
  public int getItemCount() {
    return this.viewModel.size();
  }
}