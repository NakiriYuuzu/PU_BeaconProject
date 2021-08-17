package tw.edu.pu.pu_smart_campus_micro_positioning_service.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class check_ViewModel extends ViewModel {
    private final MutableLiveData<String> titleName = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalPPL = new MutableLiveData<>();
    private final MutableLiveData<Integer> locationImage = new MutableLiveData<>();

    public void setTitleName(String s) {titleName.setValue(s);}
    public LiveData<String> getTitleName() {return titleName;}

    public void setTotalPPL(int s) {totalPPL.setValue(s);}
    public LiveData<Integer> getTotalPPL() {return totalPPL;}

    public void setLocationImage(int s) {totalPPL.setValue(s);}
    public LiveData<Integer> getLocationImage() {return totalPPL;}
}
