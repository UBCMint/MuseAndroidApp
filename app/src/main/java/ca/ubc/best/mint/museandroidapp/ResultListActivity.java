package ca.ubc.best.mint.museandroidapp;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ca.ubc.best.mint.museandroidapp.analysis.HistoricResults;
import ca.ubc.best.mint.museandroidapp.databinding.ActivityResultListBinding;

public class ResultListActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ActivityResultListBinding binding =
        DataBindingUtil.setContentView(this, R.layout.activity_result_list);
    // TODO: add stuff to binding? Currently nothing is used in activity_result_list.xml

    HistoricResults results = HistoricResults.loadFromFile(this);


    // Hook up the binding for the list of devices.
    RecyclerView listView = (RecyclerView) findViewById(R.id.resultList);
    listView.setLayoutManager(new LinearLayoutManager(this));
    listView.setAdapter(new ResultListAdapter(this, results));
  }

}
