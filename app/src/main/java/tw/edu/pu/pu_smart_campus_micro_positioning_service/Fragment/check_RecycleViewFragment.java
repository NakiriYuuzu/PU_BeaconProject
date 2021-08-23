package tw.edu.pu.pu_smart_campus_micro_positioning_service.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.RecycleView_Variable;

public class check_RecycleViewFragment extends Fragment {

    ArrayList<RecycleView_Variable> list = new ArrayList<>();

    View view;
    RecyclerView recyclerView;
    check_Adapter checkAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_check__recycle_view, container, false);

        importData();
        initRecycleView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initRecycleView() {
        recyclerView = view.findViewById(R.id.check_recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        checkAdapter = new check_Adapter(list);
        recyclerView.setAdapter(checkAdapter);
    }


    private void importData() {
        list.add(new RecycleView_Variable("a"));
        list.add(new RecycleView_Variable("b"));
        list.add(new RecycleView_Variable("c"));
        list.add(new RecycleView_Variable("d"));
        list.add(new RecycleView_Variable("e"));
        list.add(new RecycleView_Variable("f"));
        list.add(new RecycleView_Variable("g"));
    }
}