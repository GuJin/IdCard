package com.gujin.idcard.presenter;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.gujin.idcard.BuildConfig;
import com.gujin.idcard.R;
import com.gujin.idcard.application.MyApplication;
import com.gujin.idcard.model.LeakBean;
import com.gujin.idcard.model.LossBean;
import com.gujin.idcard.model.SearchBean;
import com.gujin.idcard.net.IdcardService;
import com.gujin.idcard.view.ISearchView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.gujin.idcard.presenter.PresenterImpl.Mode.SEARCH;

public class PresenterImpl implements IPresenter {

    private SearchBeanCallBack mSearchBeanCallBack;
    private LeakBeanCallBack mLeakBeanCallBack;
    private LossBeanCallBack mLossBeanCallBack;

    private final ISearchView mSearchView;

    private static final IdcardService sIdcardService;
    private Snackbar mSnackbar;

    private Mode mMode = SEARCH;

    public enum Mode {
        SEARCH,
        LEAK,
        LOSS
    }

    static {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://apis.juhe.cn/idcard/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        sIdcardService = retrofit.create(IdcardService.class);
    }

    public PresenterImpl(ISearchView searchView) {
        mSearchView = searchView;
    }

    @Override
    public void setMode(Mode mode) {
        if (mMode == mode) {
            return;
        }
        mMode = mode;
    }

    @Override
    public void search() {
        final String cardNo = mSearchView.getCardNo();
        int cardNumberValidStatus = getCardNumberValidStatus(cardNo);
        if (cardNumberValidStatus != 0) {
            showErrorHint(cardNumberValidStatus);
            return;
        }

        mSearchView.setProgressBarVisiblity(View.VISIBLE);

        switch (mMode) {
            case SEARCH:
                Call<SearchBean> search = sIdcardService.search(BuildConfig.appKey, cardNo, "json");
                search.enqueue(getSearchBeanCallBack(cardNo));
                break;
            case LEAK:
                Call<LeakBean> leak = sIdcardService.leak(BuildConfig.appKey, cardNo, "json");
                leak.enqueue(getLeakBeanCallBack(cardNo));
                break;
            case LOSS:
                Call<LossBean> loss = sIdcardService.loss(BuildConfig.appKey, cardNo, "json");
                loss.enqueue(getLossBeanCallBack(cardNo));
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private SearchBeanCallBack getSearchBeanCallBack(String cardNo) {
        if (mSearchBeanCallBack == null) {
            mSearchBeanCallBack = new SearchBeanCallBack();
        }
        mSearchBeanCallBack.setCardno(cardNo);
        return mSearchBeanCallBack;
    }

    private LeakBeanCallBack getLeakBeanCallBack(String cardNo) {
        if (mLeakBeanCallBack == null) {
            mLeakBeanCallBack = new LeakBeanCallBack();
        }
        mLeakBeanCallBack.setCardno(cardNo);
        return mLeakBeanCallBack;
    }

    private LossBeanCallBack getLossBeanCallBack(String cardNo) {
        if (mLossBeanCallBack == null) {
            mLossBeanCallBack = new LossBeanCallBack();
        }
        mLossBeanCallBack.setCardno(cardNo);
        return mLossBeanCallBack;
    }

    private class LeakBeanCallBack implements Callback<LeakBean> {

        private String mCardno;

        @Override
        public void onResponse(Call<LeakBean> call, Response<LeakBean> response) {
            mSearchView.setProgressBarVisiblity(View.GONE);

            Context context = MyApplication.getInstance().getApplicationContext();
            LeakBean leakBean = response.body();
            if (!response.isSuccessful() || String.valueOf(leakBean.error_code).startsWith("1") || leakBean.error_code == 203804) {
                showSnackBar(R.string.search_fail);
                return;
            }

            StringBuilder sb = new StringBuilder();

            if (leakBean.error_code != 0) {
                switch (leakBean.error_code) {
                    case 203802:
                        showSnackBar(R.string.error_code_203802);
                        break;
                    case 203803:
                        showSnackBar(R.string.error_code_203803);
                        break;
                }
            }

            sb.append(context.getString(R.string.result_id_number)).append(mCardno).append("\n");
            String tips;
            switch (leakBean.result.res) {
                case "1":
                    tips = context.getString(R.string.result_leak_res_1);
                    break;
                case "2":
                    tips = context.getString(R.string.result_leak_res_2);
                    break;
                default:
                    tips = context.getString(R.string.result_unknow);
                    break;
            }
            sb.append(context.getString(R.string.result_tips)).append(tips);
            mSearchView.setRestlt(sb.toString());
            mSearchView.clearInput();
        }

        @Override
        public void onFailure(Call<LeakBean> call, Throwable t) {
            showSnackBar(R.string.search_fail);
            mSearchView.setProgressBarVisiblity(View.GONE);
        }

        private void setCardno(String cardno) {
            mCardno = cardno;
        }
    }

    private class LossBeanCallBack implements Callback<LossBean> {

        private String mCardno;

        @Override
        public void onResponse(Call<LossBean> call, Response<LossBean> response) {
            mSearchView.setProgressBarVisiblity(View.GONE);

            Context context = MyApplication.getInstance().getApplicationContext();
            LossBean lossBean = response.body();
            if (!response.isSuccessful() || String.valueOf(lossBean.error_code).startsWith("1") || lossBean.error_code == 203804) {
                showSnackBar(R.string.search_fail);
                return;
            }

            StringBuilder sb = new StringBuilder();

            if (lossBean.error_code != 0) {
                switch (lossBean.error_code) {
                    case 203802:
                        showSnackBar(R.string.error_code_203802);
                        break;
                    case 203803:
                        showSnackBar(R.string.error_code_203803);
                        break;
                }
            }

            sb.append(context.getString(R.string.result_id_number)).append(mCardno).append("\n");
            String tips;
            switch (lossBean.result.res) {
                case "1":
                    tips = context.getString(R.string.result_loss_res_1);
                    break;
                case "2":
                    tips = context.getString(R.string.result_loss_res_2);
                    break;
                default:
                    tips = context.getString(R.string.result_unknow);
                    break;
            }
            sb.append(context.getString(R.string.result_tips)).append(tips);
            mSearchView.setRestlt(sb.toString());
            mSearchView.clearInput();
        }

        @Override
        public void onFailure(Call<LossBean> call, Throwable t) {
            showSnackBar(R.string.search_fail);
            mSearchView.setProgressBarVisiblity(View.GONE);
        }

        private void setCardno(String cardno) {
            mCardno = cardno;
        }
    }

    private class SearchBeanCallBack implements Callback<SearchBean> {

        private String mCardno;

        private void setCardno(String cardno) {
            mCardno = cardno;
        }

        @Override
        public void onResponse(Call<SearchBean> call, Response<SearchBean> response) {
            mSearchView.setProgressBarVisiblity(View.GONE);

            Context context = MyApplication.getInstance().getApplicationContext();
            SearchBean searchBean = response.body();
            if (!response.isSuccessful() || String.valueOf(searchBean.error_code).startsWith("1") || searchBean.error_code == 203804) {
                showSnackBar(R.string.search_fail);
                return;
            }

            StringBuilder sb = new StringBuilder();

            if (searchBean.error_code != 0) {
                switch (searchBean.error_code) {
                    case 203802:
                        showSnackBar(R.string.error_code_203802);
                        break;
                    case 203803:
                        showSnackBar(R.string.error_code_203803);
                        break;
                }
            }

            sb.append(context.getString(R.string.result_id_number)).append(mCardno).append("\n");
            sb.append(context.getString(R.string.result_birthday)).append(searchBean.result.birthday).append("\n");
            sb.append(context.getString(R.string.result_area)).append(searchBean.result.area).append("\n");
            sb.append(context.getString(R.string.result_sex)).append(searchBean.result.sex);
            mSearchView.setRestlt(sb.toString());
            mSearchView.clearInput();
        }

        @Override
        public void onFailure(Call<SearchBean> call, Throwable t) {
            showSnackBar(R.string.search_fail);
            mSearchView.setProgressBarVisiblity(View.GONE);
        }
    }

    private void showErrorHint(int cardNumberValidStatus) {
        Context applicationContext = MyApplication.getInstance().getApplicationContext();
        if (cardNumberValidStatus == 1) {
            mSearchView.setErrorHint(applicationContext.getString(R.string.error_hint_exceed_18));
            return;
        }
        mSearchView.setErrorHint(applicationContext.getString(R.string.error_hint_subter_18));
    }

    private int getCardNumberValidStatus(String cardNo) {
        int length = cardNo.length();
        if (length == 18) {
            return 0;
        }
        if (length < 18) {
            return -1;
        }
        return 1;
    }

    private void showSnackBar(@StringRes int testId) {
        showSnackBar(MyApplication.getInstance().getApplicationContext().getString(testId));
    }

    private void showSnackBar(String text) {
        if (mSnackbar == null) {
            mSnackbar = Snackbar.make(mSearchView.getSnackBarView(), text, Snackbar.LENGTH_INDEFINITE);
            mSnackbar.setAction(R.string.i_know, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }
        mSnackbar.setText(text);
        mSnackbar.show();
    }


    @Override
    public void clearResult() {
        mSearchView.clearResult();
    }
}
