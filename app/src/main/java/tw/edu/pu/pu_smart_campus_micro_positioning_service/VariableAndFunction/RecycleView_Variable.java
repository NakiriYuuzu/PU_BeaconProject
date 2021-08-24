package tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction;

public class RecycleView_Variable {
    private String titleNames, imageUrl;
    private int numPeople;

    public RecycleView_Variable(String titleNames) {
        this.titleNames = titleNames;
    }

    public RecycleView_Variable(String titleNames, String imageUrl, int numPeople) {
        this.titleNames = titleNames;
        this.imageUrl = imageUrl;
        this.numPeople = numPeople;
    }

    public String getTitleNames() {
        return titleNames;
    }

    public void setTitleNames(String titleNames) {
        this.titleNames = titleNames;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getNumPeople() {
        return numPeople;
    }

    public void setNumPeople(int numPeople) {
        this.numPeople = numPeople;
    }

    @Override
    public String toString() {
        return "RecycleView_Variable{" +
                "titleNames='" + titleNames + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", numPeople=" + numPeople +
                '}';
    }
}
