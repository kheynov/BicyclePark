package io.github.kheynov.bicyclepark;

import java.util.ArrayList;

class ServerResponse {
    private String type = "";
    private String msg = "";
    private String status = "";
    private ArrayList<String> parksState = new ArrayList<>();

    void clear() {
        type = "";
        msg = "";
        status = "";
        parksState.clear();
    }

    public int getParks_count() {
        return parksState.size();
    }


    ArrayList<String> getParksState() {
        return parksState;
    }

    public void setParksState(ArrayList<String> parksState) {
        this.parksState = parksState;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
