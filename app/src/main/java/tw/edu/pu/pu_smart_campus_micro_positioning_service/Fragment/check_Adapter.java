package tw.edu.pu.pu_smart_campus_micro_positioning_service.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.RecycleView_Variable;

public class check_Adapter extends RecyclerView.Adapter<check_Adapter.check_ViewHolder> {

    ArrayList<RecycleView_Variable> checkList;

    public check_Adapter(ArrayList<RecycleView_Variable> checkList) {
        this.checkList = checkList;
    }

    @NonNull
    @Override
    public check_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.check_detail, parent, false);
        return new check_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull check_ViewHolder holder, int position) {
        RecycleView_Variable checkVariable = checkList.get(position);
        holder.spotNames.setText(checkVariable.getTitleNames());
        //holder.spotPeople.setText(checkVariable.getNumPeople());
    }

    @Override
    public int getItemCount() {
        if (checkList != null) {
            return checkList.size();
        } else {
            return 0;
        }
    }

    public static class check_ViewHolder extends RecyclerView.ViewHolder {

        ShapeableImageView spotImage;
        MaterialTextView spotNames, spotPeople;

        public check_ViewHolder(@NonNull View itemView) {
            super(itemView);
            spotNames = itemView.findViewById(R.id.tv_check_recycleView_titleName);
            spotPeople = itemView.findViewById(R.id.tv_check_recycleView_numPeople);
            spotImage = itemView.findViewById(R.id.iv_check_recycleView_Image);
        }
    }
}
