package io.github.kheynov.bicyclepark;

import com.example.smart_bike_park.R;

class ParkPlaceListItem {
    public enum parkPlaceStates{
        OPENED,
        CLOSED
    }
    private int id;

    private int LockImageId;
    private parkPlaceStates status;

    public ParkPlaceListItem(int id, parkPlaceStates status) {
        this.id = id;
        this.status = status;
        if (status == parkPlaceStates.OPENED){
            LockImageId = R.drawable.ic_lock_open_65dp;
        }else {
            LockImageId = R.drawable.ic_lock_close_65dp;
        }
    }

    public int getLockImageId() {
        return LockImageId;
    }

    public parkPlaceStates getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }
}
