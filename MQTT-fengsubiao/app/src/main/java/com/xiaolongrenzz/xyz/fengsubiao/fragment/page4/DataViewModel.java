package com.xiaolongrenzz.xyz.fengsubiao.fragment.page4;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class DataViewModel extends ViewModel {

    private MutableLiveData<List<ListData>> allListData;
    private List<ListData> listData;

    public MutableLiveData<List<ListData>> getAllListData() {
        if(allListData==null){
            allListData=new MutableLiveData<>();
        }
        return allListData;
    }

    public void setAllListData(List<ListData> listData1) {
        allListData.postValue(listData1);
    }
}
