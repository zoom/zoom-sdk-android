package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.zipow.videobox.fragment.SelectCountryCodeFragment;
import com.zipow.videobox.fragment.SimpleMessageDialog;
import com.zipow.videobox.util.ZMWebPageUtil;
import com.zipow.videobox.view.ZMVerifyCodeView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import us.zoom.androidlib.app.ZMActivity;
import us.zoom.androidlib.app.ZMDialogFragment;
import us.zoom.androidlib.util.CountryCodeUtil;
import us.zoom.androidlib.util.EventAction;
import us.zoom.androidlib.util.IUIElement;
import us.zoom.androidlib.util.PhoneNumberUtil;
import us.zoom.androidlib.util.StringUtil;
import us.zoom.androidlib.util.UIUtil;
import us.zoom.androidlib.widget.WaitingDialog;
import us.zoom.androidlib.widget.ZMAlertDialog;
import us.zoom.androidlib.widget.ZMSpanny;
import us.zoom.sdk.IZoomRetrieveSMSVerificationCodeHandler;
import us.zoom.sdk.IZoomVerifySMSVerificationCodeHandler;
import us.zoom.sdk.MobileRTCSMSVerificationError;
import us.zoom.sdk.SmsListener;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKCountryCode;

public class RealNameAuthDialog extends ZMDialogFragment implements View.OnClickListener, SmsListener, ZMVerifyCodeView.VerifyCodeCallBack {
    private static final String TAG = RealNameAuthDialog.class.getName();
    private static final int REQUEST_SELECT_COUNTRY_CODE = 10000;
    private Button mBtnCountryCode;
    private EditText mEdtNumber;
    private EditText mEdtCode;
    private Button mBtnVerify;
    private ZMVerifyCodeView mZMVerifyCodeView;
    private TextView mTxtSignInToJoin;
    private TextView mTxtPrivacy;
    @Nullable
    private SelectCountryCodeFragment.CountryCodeItem mSelectedCountryCode;

    private IZoomRetrieveSMSVerificationCodeHandler retrieveSMSVerificationCodeHandler;

    private IZoomVerifySMSVerificationCodeHandler verifySMSVerificationCodeHandler;

    public RealNameAuthDialog() {
    }

    public RealNameAuthDialog(IZoomRetrieveSMSVerificationCodeHandler retrieveSMSVerificationCodeHandler, IZoomVerifySMSVerificationCodeHandler verifySMSVerificationCodeHandler) {
        this.retrieveSMSVerificationCodeHandler = retrieveSMSVerificationCodeHandler;
        this.verifySMSVerificationCodeHandler = verifySMSVerificationCodeHandler;
    }

    public static RealNameAuthDialog show(@NonNull ZMActivity activity, IZoomRetrieveSMSVerificationCodeHandler handler) {
        FragmentManager fm = activity.getSupportFragmentManager();
        if (fm == null)
            return null;
        dismiss(fm);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        RealNameAuthDialog dlg = new RealNameAuthDialog(handler, null);
        dlg.show(fm, TAG);
        return dlg;
    }

    public static void dismiss(@NonNull ZMActivity activity, boolean resetScreenOrientation) {
        if (resetScreenOrientation) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        FragmentManager fm = activity.getSupportFragmentManager();
        if (fm == null)
            return;
        RealNameAuthDialog zmRealNameAuthDialog = (RealNameAuthDialog) fm.findFragmentByTag(TAG);
        if (zmRealNameAuthDialog != null)
            zmRealNameAuthDialog.dismiss();
    }

    private static void dismiss(FragmentManager fm) {
        RealNameAuthDialog zmRealNameAuthDialog = (RealNameAuthDialog) fm.findFragmentByTag(TAG);
        if (zmRealNameAuthDialog != null)
            zmRealNameAuthDialog.dismiss();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, us.zoom.videomeetings.R.style.ZMDialog_NoTitle);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setCancelable(false);
        View view = LayoutInflater.from(getActivity()).inflate(us.zoom.videomeetings.R.layout.zm_verify_phone_dialog, null, false);
        view.findViewById(us.zoom.videomeetings.R.id.btnClose).setOnClickListener(this);
        mZMVerifyCodeView = view.findViewById(us.zoom.videomeetings.R.id.zmVerifyCodeView);
        mBtnCountryCode = view.findViewById(us.zoom.videomeetings.R.id.btnCountryCode);
        mBtnCountryCode.setOnClickListener(this);
        mEdtNumber = view.findViewById(us.zoom.videomeetings.R.id.edtNumber);
        mEdtCode = view.findViewById(us.zoom.videomeetings.R.id.edtCode);
        mBtnVerify = view.findViewById(us.zoom.videomeetings.R.id.btnVerify);
        mBtnVerify.setOnClickListener(this);
        mTxtSignInToJoin = view.findViewById(us.zoom.videomeetings.R.id.txtSignInToJoin);
        mTxtPrivacy = view.findViewById(us.zoom.videomeetings.R.id.txtPrivacy);
        if (savedInstanceState == null) {
            loadDefaultNumber();
        } else {
            mSelectedCountryCode = (SelectCountryCodeFragment.CountryCodeItem) savedInstanceState.get("mSelectedCountryCode");
            if (mSelectedCountryCode == null) {
                loadDefaultNumber();
            } else {
                updateSelectedCountry();
            }

        }
        setUpView();
        mZMVerifyCodeView.setmVerifyCodeCallBack(this);
        ZoomSDK.getInstance().getSmsService().addListener(this);
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("mSelectedCountryCode", mSelectedCountryCode);
    }

    @Override
    public void onDestroyView() {
        ZoomSDK.getInstance().getSmsService().removeListener(this);
        if (mZMVerifyCodeView != null)
            mZMVerifyCodeView.setmVerifyCodeCallBack(null);
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_COUNTRY_CODE && resultCode == Activity.RESULT_OK && data != null) {
            SelectCountryCodeFragment.CountryCodeItem item = (SelectCountryCodeFragment.CountryCodeItem) data.getSerializableExtra(SelectCountryCodeFragment.RESULT_ARG_COUNTRY_CODE);
            if (item != null) {
                mSelectedCountryCode = item;
                updateSelectedCountry();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == us.zoom.videomeetings.R.id.btnClose) {
            onClickClose();
        } else if (id == us.zoom.videomeetings.R.id.btnVerify) {
            onClickVerify();
        } else if (id == us.zoom.videomeetings.R.id.btnCountryCode) {
            onClickCountryCode();
        }
    }

    @Override
    public void onClickSendCode() {
        if (mSelectedCountryCode == null)
            return;
        String countryCode = mSelectedCountryCode.countryCode;
        String number = PhoneNumberUtil.getPhoneNumber(mEdtNumber.getText().toString());
        if (StringUtil.isEmptyOrNull(countryCode) || StringUtil.isEmptyOrNull(number))
            return;
        if (null == retrieveSMSVerificationCodeHandler) {
            retrieveSMSVerificationCodeHandler = ZoomSDK.getInstance().getSmsService().getResendSMSVerificationCodeHandler();
        }
        if (null != retrieveSMSVerificationCodeHandler && retrieveSMSVerificationCodeHandler.retrieve(countryCode, number)) {
            WaitingDialog dialog = WaitingDialog.newInstance(us.zoom.videomeetings.R.string.zm_msg_waiting);
            dialog.show(getFragmentManager(), WaitingDialog.class.getName());
        } else {
            SimpleMessageDialog dialog = SimpleMessageDialog.newInstance(us.zoom.videomeetings.R.string.zm_msg_verify_phone_number_failed);
            dialog.show(getFragmentManager(), SimpleMessageDialog.class.getName());
        }
        retrieveSMSVerificationCodeHandler = null;
    }

    @Override
    public void onNeedRealNameAuthMeetingNotification(List<ZoomSDKCountryCode> supportCountryList, String privacyUrl, IZoomRetrieveSMSVerificationCodeHandler handler) {

    }

    public void onVerifySMSVerificationCodeResultNotification(final MobileRTCSMSVerificationError result) {

        getNonNullEventTaskManagerOrThrowException().push(new EventAction("onRequestRealNameAuthSMS") {
            @Override
            public void run(@NonNull IUIElement ui) {
                sinkVerifyRealNameAuthResult(result);
            }
        });
    }

    public void onRetrieveSMSVerificationCodeResultNotification(final MobileRTCSMSVerificationError result, IZoomVerifySMSVerificationCodeHandler handler) {
        verifySMSVerificationCodeHandler = handler;
        getNonNullEventTaskManagerOrThrowException().push(new EventAction("onRequestRealNameAuthSMS") {
            @Override
            public void run(@NonNull IUIElement ui) {
                sinkRequestRealNameAuthSMS(result);
            }
        });
    }


    private void sinkRequestRealNameAuthSMS(MobileRTCSMSVerificationError result) {
        Log.i(TAG, "sinkRequestRealNameAuthSMS, result=" + result.ordinal());
        FragmentManager fm = getFragmentManager();
        if (fm == null)
            return;
        WaitingDialog dialog = (WaitingDialog) fm.findFragmentByTag(WaitingDialog.class.getName());
        if (dialog != null)
            dialog.dismiss();
        if (result != MobileRTCSMSVerificationError.SMSVerificationCodeErr_Success) {
            int resId = us.zoom.videomeetings.R.string.zm_msg_verify_send_sms_failed_109213;
            if (result == MobileRTCSMSVerificationError.SMSVerificationCodeErr_Retrieve_InvalidPhoneNum) {
                resId = us.zoom.videomeetings.R.string.zm_msg_verify_invalid_phone_num_109213;
                mZMVerifyCodeView.forceEnableSendCode();
            } else if (result == MobileRTCSMSVerificationError.SMSVerificationCodeErr_Retrieve_PhoneNumAlreadyBound) {
                resId = us.zoom.videomeetings.R.string.zm_msg_verify_phone_num_already_bound_109213;
                mZMVerifyCodeView.forceEnableSendCode();
            } else if (result == MobileRTCSMSVerificationError.SMSVerificationCodeErr_Retrieve_PhoneNumSendTooFrequent)
                resId = us.zoom.videomeetings.R.string.zm_msg_verify_phone_num_send_too_frequent_109213;
            SimpleMessageDialog simpleMessageDialog = SimpleMessageDialog.newInstance(resId);
            simpleMessageDialog.show(getFragmentManager(), SimpleMessageDialog.class.getName());
        }

    }

    private void closeDialog() {
        ZMActivity activity = (ZMActivity) getActivity();
        if (activity != null) {
            UIUtil.closeSoftKeyboard(getActivity(), getView());
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        dismiss();

    }

    private void sinkVerifyRealNameAuthResult(MobileRTCSMSVerificationError result) {
        Log.i(TAG, "sinkRequestRealNameAuthSMS, result=" + result);
        FragmentManager fm = getFragmentManager();
        if (fm == null)
            return;
        WaitingDialog dialog = (WaitingDialog) fm.findFragmentByTag(WaitingDialog.class.getName());
        if (dialog != null)
            dialog.dismiss();
        ZMActivity activity = (ZMActivity) getActivity();
        if (activity == null)
            return;
        int msgId = -1;
        if (result == MobileRTCSMSVerificationError.SMSVerificationCodeErr_Verify_CodeIncorrect) {
            msgId = us.zoom.videomeetings.R.string.zm_msg_error_verification_code_109213;
        } else if (result == MobileRTCSMSVerificationError.SMSVerificationCodeErr_Verify_CodeExpired) {
            msgId = us.zoom.videomeetings.R.string.zm_msg_expired_verification_code_109213;
        } else if (result == MobileRTCSMSVerificationError.SMSVerificationCodeErr_Verify_UnknownError || result == MobileRTCSMSVerificationError.SMSVerificationCodeErr_Success) {
            closeDialog();
            return;
        }
        if (msgId != -1) {
            ZMAlertDialog alertDialog = new ZMAlertDialog.Builder(activity)
                    .setMessage(msgId)
                    .setCancelable(true)
                    .setPositiveButton(us.zoom.videomeetings.R.string.zm_btn_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                        }
                    })
                    .create();
            alertDialog.show();
        }

    }

    private void onClickVerify() {
        if (mSelectedCountryCode == null)
            return;
        String countryCode = mSelectedCountryCode.countryCode;
        String number = PhoneNumberUtil.getPhoneNumber(mEdtNumber.getText().toString());
        String code = mEdtCode.getText().toString();
        if (StringUtil.isEmptyOrNull(countryCode) || StringUtil.isEmptyOrNull(number) || StringUtil.isEmptyOrNull(code))
            return;
        Activity activity = getActivity();

        if (activity != null) {
            UIUtil.closeSoftKeyboard(getActivity(), getView());
        }

        WaitingDialog dialog = WaitingDialog.newInstance(us.zoom.videomeetings.R.string.zm_msg_waiting);
        dialog.show(getFragmentManager(), WaitingDialog.class.getName());

        if(null==verifySMSVerificationCodeHandler)
        {
            verifySMSVerificationCodeHandler=  ZoomSDK.getInstance().getSmsService().getReVerifySMSVerificationCodeHandler();
        }
        if (null != verifySMSVerificationCodeHandler) {
            verifySMSVerificationCodeHandler.verify(countryCode, number, code);
        }
        verifySMSVerificationCodeHandler=null;
    }

    private void onClickCountryCode() {
        List<ZoomSDKCountryCode> countryCodeList = ZoomSDK.getInstance().getSmsService().getSupportPhoneNumberCountryList();
        if (countryCodeList == null)
            return;
        if (countryCodeList == null || countryCodeList.isEmpty())
            return;
        Activity activity = getActivity();
        if (activity != null) {
            UIUtil.closeSoftKeyboard(getActivity(), getView());
        }

        ArrayList<SelectCountryCodeFragment.CountryCodeItem> filterCountryCodes = new ArrayList<>();
        String strCode;
        for (ZoomSDKCountryCode countryCode : countryCodeList) {
            if (countryCode == null)
                continue;
            strCode = countryCode.getCode();
            if (strCode.startsWith("+")) {
                strCode = strCode.substring(1);
            }
            filterCountryCodes.add(new SelectCountryCodeFragment.CountryCodeItem(strCode, countryCode.getId(), countryCode.getName()));
        }

        SelectCountryCodeFragment.showAsActivity(this, filterCountryCodes, true, REQUEST_SELECT_COUNTRY_CODE);
    }

    private void onClickPrivacy() {
        String privacyUrl = ZoomSDK.getInstance().getSmsService().getRealNameAuthPrivacyURL();
        Log.i(TAG, "onClickPrivacy, privacyUrl=" + privacyUrl);
        if (StringUtil.isEmptyOrNull(privacyUrl))
            return;

        ZMWebPageUtil.startWebPage(this, privacyUrl, getString(us.zoom.videomeetings.R.string.zm_title_privacy_policy));
    }

    private void onClickClose() {
        dismiss(getFragmentManager());
        ZoomSDK.getInstance().getMeetingService().leaveCurrentMeeting(true);
        getActivity().finish();
    }

    private void setUpView() {
        ZMSpanny spanny;
        String tempStr = getString(us.zoom.videomeetings.R.string.zm_title_privacy_policy);
        spanny = new ZMSpanny(getString(us.zoom.videomeetings.R.string.zm_lbl_cn_join_meeting_privacy_109213, tempStr));
        spanny.setSpans(tempStr, new StyleSpan(Typeface.BOLD),
                new ForegroundColorSpan(getResources().getColor(us.zoom.videomeetings.R.color.zm_ui_kit_color_blue_0E71EB)),
                new RelativeSizeSpan(1.2f), new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        onClickPrivacy();
                    }
                });
        mTxtPrivacy.setText(spanny);
        mTxtPrivacy.setMovementMethod(LinkMovementMethod.getInstance());

        mTxtSignInToJoin.setVisibility(View.GONE);

        mEdtNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onPhoneNumberChanged();
            }
        });

        mEdtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onCodeChanged();
            }
        });
    }

    private void onPhoneNumberChanged() {
        if (mEdtCode == null || mEdtNumber == null || mZMVerifyCodeView == null || mBtnVerify == null)
            return;
        String number = PhoneNumberUtil.getPhoneNumber(mEdtNumber.getText().toString());
        String code = mEdtCode.getText().toString();
        boolean readyForNumber = number.length() > 4;
        boolean readyForCode = (code.length() == 6);
        mZMVerifyCodeView.enableSendCode(readyForNumber);
        mBtnVerify.setEnabled(readyForNumber && readyForCode);


    }

    private void onCodeChanged() {
        if (mEdtCode == null || mEdtNumber == null || mZMVerifyCodeView == null || mBtnVerify == null)
            return;
        String number = PhoneNumberUtil.getPhoneNumber(mEdtNumber.getText().toString());
        String code = mEdtCode.getText().toString();
        mBtnVerify.setEnabled(number.length() > 4 && (code.length() == 6));

    }

    private void loadDefaultNumber() {
        Activity activity = getActivity();
        if (activity == null)
            return;
        String isoCountryCode = CountryCodeUtil.CN_ISO_COUNTRY_CODE;
        String phoneCountryCode = CountryCodeUtil.isoCountryCode2PhoneCountryCode(isoCountryCode);
        Locale locale = new Locale("", isoCountryCode.toLowerCase(Locale.US));
        String countryName = locale.getDisplayCountry();
        mSelectedCountryCode = new SelectCountryCodeFragment.CountryCodeItem(phoneCountryCode, isoCountryCode, countryName);
        updateSelectedCountry();
    }

    private void updateSelectedCountry() {
        if (mSelectedCountryCode == null)
            return;

        mBtnCountryCode.setText("+" + mSelectedCountryCode.countryCode);
    }
}
