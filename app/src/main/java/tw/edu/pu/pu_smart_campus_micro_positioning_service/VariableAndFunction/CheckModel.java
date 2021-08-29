package tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction;

public class CheckModel {
    private String titleNames, imageUrl, numPeople;

    public CheckModel(String titleNames) {
        this.titleNames = titleNames;
    }

    public CheckModel(String titleNames, String numPeople) {
        this.titleNames = titleNames;
        this.numPeople = numPeople;
    }

    public CheckModel(String titleNames, String imageUrl, String numPeople) {
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

    public String getNumPeople() {
        return numPeople;
    }

    public void setNumPeople(String numPeople) {
        this.numPeople = numPeople;
    }

    @Override
    public String toString() {
        return "CheckModel{" +
                "titleNames='" + titleNames + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", numPeople=" + numPeople +
                '}';
    }
}
