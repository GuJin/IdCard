package com.gujin.idcard.view;

import android.view.View;

public interface ISearchView {
    String getCardNo();

    void clearInput();

    void setProgressBarVisiblity(int visiblity);

    void setRestlt(String restlt);

    void clearResult();

    void setErrorHint(String errorHint);

    View getSnackBarView();
}
