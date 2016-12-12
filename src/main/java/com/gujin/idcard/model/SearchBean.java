package com.gujin.idcard.model;

public class SearchBean {
    public ResultEntity result;
    public int error_code;

    public static class ResultEntity {
        public String area;
        public String sex;
        public String birthday;
    }
}
