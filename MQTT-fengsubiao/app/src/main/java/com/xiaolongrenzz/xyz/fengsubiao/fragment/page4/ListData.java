package com.xiaolongrenzz.xyz.fengsubiao.fragment.page4;

public class ListData {
    private Object press;
    private String time;

    public ListData(Object press,String time) {
        this.time = time;
        this.press = press;
    }

    public String getTime() {
        return time;
    }

    public Object getPress() {
        return press;
    }

}
