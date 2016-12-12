package com.gujin.idcard.presenter;

interface IPresenter {
    void setMode(PresenterImpl.Mode mode);

    void search();

    void clearResult();
}
