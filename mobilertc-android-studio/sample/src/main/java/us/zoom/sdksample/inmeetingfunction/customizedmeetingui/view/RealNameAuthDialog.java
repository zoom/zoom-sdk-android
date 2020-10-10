package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import us.zoom.sdk.IZoomRetrieveSMSVerificationCodeHandler;
import us.zoom.sdk.IZoomVerifySMSVerificationCodeHandler;
import us.zoom.sdk.MobileRTCSMSVerificationError;
import us.zoom.sdk.SmsListener;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKCountryCode;
import us.zoom.sdksample.R;
import us.zoom.sdksample.inmeetingfunction.customizedmeetingui.MyMeetingActivity;

public class RealNameAuthDialog extends Dialog implements View.OnClickListener, SmsListener {

    private static final String TAG = "RealNameAuthDialog";
    EditText edtNumber;
    EditText edtCode;
    EditText edtCountry;
    Button btnVerify;
    Button zm_btn_send;

    private IZoomRetrieveSMSVerificationCodeHandler senderHandler;

    public RealNameAuthDialog(@NonNull Context context) {
        super(context);
    }

    public RealNameAuthDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public RealNameAuthDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static void show(MyMeetingActivity myMeetingActivity, IZoomRetrieveSMSVerificationCodeHandler handler) {

        RealNameAuthDialog dialog = new RealNameAuthDialog(myMeetingActivity);
        dialog.senderHandler = handler;
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_verify_phone);
        edtNumber = findViewById(R.id.edtNumber);
        edtCode = findViewById(R.id.edtCode);
        btnVerify = findViewById(R.id.btnVerify);
        edtCountry = findViewById(R.id.edtCountry);
        btnVerify.setOnClickListener(this);
        zm_btn_send = findViewById(R.id.zm_btn_send);
        zm_btn_send.setOnClickListener(this);

        ZoomSDK.getInstance().getSmsService().addListener(this);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ZoomSDK.getInstance().getSmsService().removeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnVerify) {
            if (null == verifySMSVerificationCodeHandler) {
                verifySMSVerificationCodeHandler = ZoomSDK.getInstance().getSmsService().getReVerifySMSVerificationCodeHandler();
            }

            if (null != verifySMSVerificationCodeHandler) {
                verifySMSVerificationCodeHandler.verify(edtCountry.getText().toString().trim(),
                        edtNumber.getText().toString().trim()
                        , edtCode.getText().toString().trim());
            }
        } else if (v == zm_btn_send) {
            if (null == senderHandler) {
                senderHandler = ZoomSDK.getInstance().getSmsService().getResendSMSVerificationCodeHandler();
            }
            if (null != senderHandler) {
                senderHandler.retrieve(edtCountry.getText().toString().trim(),
                        edtNumber.getText().toString().trim());
            }
        }
    }

    @Override
    public void onNeedRealNameAuthMeetingNotification(List<ZoomSDKCountryCode> supportCountryList, String privacyUrl, IZoomRetrieveSMSVerificationCodeHandler handler) {
        if (null != handler) {
            senderHandler = handler;
        }
    }

    IZoomVerifySMSVerificationCodeHandler verifySMSVerificationCodeHandler;

    public void onRetrieveSMSVerificationCodeResultNotification(final MobileRTCSMSVerificationError result, IZoomVerifySMSVerificationCodeHandler handler) {
        verifySMSVerificationCodeHandler = handler;

        if (result == MobileRTCSMSVerificationError.SMSVerificationCodeErr_Success) {
            senderHandler = null;
        }
        sinkRequestRealNameAuthSMS(result);
        btnVerify.setEnabled(true);
    }

    private void sinkRequestRealNameAuthSMS(MobileRTCSMSVerificationError result) {
        Log.i(TAG, "sinkRequestRealNameAuthSMS, result=" + result.ordinal());

        if (result != MobileRTCSMSVerificationError.SMSVerificationCodeErr_Success) {
            int resId = us.zoom.videomeetings.R.string.zm_msg_verify_send_sms_failed_109213;
            if (result == MobileRTCSMSVerificationError.SMSVerificationCodeErr_Retrieve_InvalidPhoneNum) {
                resId = us.zoom.videomeetings.R.string.zm_msg_verify_invalid_phone_num_109213;
                //TODO enable Send button
            } else if (result == MobileRTCSMSVerificationError.SMSVerificationCodeErr_Retrieve_PhoneNumAlreadyBound) {
                resId = us.zoom.videomeetings.R.string.zm_msg_verify_phone_num_already_bound_109213;
                //TODO enable Send button
            } else if (result == MobileRTCSMSVerificationError.SMSVerificationCodeErr_Retrieve_PhoneNumSendTooFrequent) {
                resId = us.zoom.videomeetings.R.string.zm_msg_verify_phone_num_send_too_frequent_109213;
            }
            Toast.makeText(getContext(), getContext().getString(resId), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onVerifySMSVerificationCodeResultNotification(MobileRTCSMSVerificationError result) {
        sinkVerifyRealNameAuthResult(result);
        if (result == MobileRTCSMSVerificationError.SMSVerificationCodeErr_Success) {
            dismiss();
            verifySMSVerificationCodeHandler = null;
        }
    }

    private void sinkVerifyRealNameAuthResult(MobileRTCSMSVerificationError result) {
        Log.i(TAG, "sinkRequestRealNameAuthSMS, result=" + result);

        int msgId = -1;
        if (result == MobileRTCSMSVerificationError.SMSVerificationCodeErr_Verify_CodeIncorrect) {
            msgId = us.zoom.videomeetings.R.string.zm_msg_error_verification_code_109213;
        } else if (result == MobileRTCSMSVerificationError.SMSVerificationCodeErr_Verify_CodeExpired) {
            msgId = us.zoom.videomeetings.R.string.zm_msg_expired_verification_code_109213;
        } else if (result == MobileRTCSMSVerificationError.SMSVerificationCodeErr_Verify_UnknownError || result == MobileRTCSMSVerificationError.SMSVerificationCodeErr_Success) {
            return;
        }
        Toast.makeText(getContext(), getContext().getString(msgId), Toast.LENGTH_LONG).show();
    }
}
