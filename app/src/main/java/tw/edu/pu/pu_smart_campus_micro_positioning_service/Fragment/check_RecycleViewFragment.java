package tw.edu.pu.pu_smart_campus_micro_positioning_service.Fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.CheckModel;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.ShareData;

public class check_RecycleViewFragment extends Fragment {

    ArrayList<CheckModel> viewList;

    View view;
    RecyclerView recyclerView;
    check_Adapter checkAdapter;

    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.e("updateUi", "UI is update.");
            refreshRecycleView();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_check__recycle_view, container, false);

        importData();
        initRecycleView();
        timer.schedule(timerTask, 10250, 10250);

        return view;
    }

    private void initRecycleView() {
        recyclerView = view.findViewById(R.id.check_recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        checkAdapter = new check_Adapter(viewList, this);
        recyclerView.setAdapter(checkAdapter);
    }

    private void refreshRecycleView() {
        new Thread(() -> {
            importData();
            updateUI();
        }).start();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUI() {
        if(getActivity() != null){
            getActivity().runOnUiThread(() -> {
                checkAdapter = new check_Adapter(viewList, check_RecycleViewFragment.this);
                recyclerView.setAdapter(checkAdapter);
                checkAdapter.notifyDataSetChanged();
            });
        }
    }

    private void importData() {
        ShareData shareData = new ShareData(getContext(), viewList);
        viewList = shareData.loadData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer.purge();
    }
}