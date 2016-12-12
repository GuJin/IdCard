package com.gujin.idcard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.gujin.idcard.R;
import com.gujin.idcard.presenter.PresenterImpl;
import com.gujin.idcard.view.ISearchView;

public class MainActivity extends Activity implements ISearchView, View.OnClickListener {

    private PresenterImpl mPresenter;

    private Toolbar mToolbar;
    private TextInputLayout textInputLayoutIdNo;
    private Button btnMainSearch;
    private ProgressBar progressBar;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        btnMainSearch.setOnClickListener(this);
    }

    private void initData() {
        mPresenter = new PresenterImpl(this);

        EditText editText = textInputLayoutIdNo.getEditText();
        if (editText != null) {
            editText.addTextChangedListener(new ErrorHintTextWatcher());
        }
    }

    private class ErrorHintTextWatcher implements TextWatcher {

        @Override
        public void afterTextChanged(Editable editable) {
            if (textInputLayoutIdNo.isErrorEnabled()) {
                textInputLayoutIdNo.setErrorEnabled(false);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
        }
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        textInputLayoutIdNo = (TextInputLayout) findViewById(R.id.textInputLayout_id_no);
        btnMainSearch = (Button) findViewById(R.id.btn_main_search);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvResult = (TextView) findViewById(R.id.tv_result);

        //TextInputLayout在最下方有padding，会造成右边的Button无法对齐
        EditText editText = textInputLayoutIdNo.getEditText();
        if (editText != null) {
            int paddingBottom = editText.getCompoundPaddingTop();
            findViewById(R.id.fl_button).setPadding(0, 0, 0, paddingBottom);
        }

        setActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                mToolbar.setTitle(R.string.search_info);
                mPresenter.setMode(PresenterImpl.Mode.SEARCH);
                break;
            case R.id.leak:
                mToolbar.setTitle(R.string.search_leak);
                mPresenter.setMode(PresenterImpl.Mode.LEAK);
                break;
            case R.id.loss:
                mToolbar.setTitle(R.string.search_loss);
                mPresenter.setMode(PresenterImpl.Mode.LOSS);
                break;
        }
        return true;
    }

    @Override
    public String getCardNo() {
        EditText editText = textInputLayoutIdNo.getEditText();
        if (editText == null) {
            return "";
        }
        Editable text = editText.getText();
        return text.toString();
    }

    @Override
    public void clearInput() {
        EditText editText = textInputLayoutIdNo.getEditText();
        if (editText != null) {
            editText.getText().clear();
        }
    }

    @Override
    public void setProgressBarVisiblity(int visiblity) {
        progressBar.setVisibility(visiblity);
        if (visiblity == View.VISIBLE) {
            btnMainSearch.setText("");
            btnMainSearch.setEnabled(false);
        } else {
            btnMainSearch.setText(R.string.btn_search);
            btnMainSearch.setEnabled(true);
        }
    }

    @Override
    public void setRestlt(String restlt) {
        tvResult.setVisibility(View.VISIBLE);
        tvResult.setText(restlt);
    }

    @Override
    public void clearResult() {
        tvResult.setVisibility(View.GONE);
        tvResult.setText("");
    }

    @Override
    public void setErrorHint(String errorHint) {
        textInputLayoutIdNo.setErrorEnabled(true);
        textInputLayoutIdNo.setError(errorHint);
    }

    @Override
    public View getSnackBarView() {
        return tvResult;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_main_search:
                mPresenter.search();
                mPresenter.clearResult();
                break;
        }
    }
}
