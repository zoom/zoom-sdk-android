package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view.share;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import androidx.fragment.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import us.zoom.androidlib.utils.ZmAccessibilityUtils;
import us.zoom.androidlib.utils.ZmKeyboardUtils;
import us.zoom.androidlib.utils.ZmUIUtils;
import us.zoom.sdk.InMeetingRemoteController;
import us.zoom.sdk.InMeetingService;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.R;

public class RCFloatView extends LinearLayout implements View.OnClickListener {

    private static final String TAG = "RCFloatView";

    public interface IRemoteControlButtonStatusListener {
        void onEnabledRC(boolean enabled);
    }

    private ImageView mIvRCControl;
    private ImageView mIvKeyboard;
    private ImageView mIvQuestion;
    private EditText mHiddenEditText;
    private ViewGroup mVGContentSpan;
    private ViewGroup mVGFloatPanel;

    private boolean mbPosChanged = false;

    private boolean mCleanHiddenEditText = false;

    private IRemoteControlButtonStatusListener iEnabledRemoteControlListener;

    private FragmentActivity mActivity;

    private Dialog mHelpDialog;

    private Handler mHandler = new Handler();

    InMeetingService meetingService;

    Runnable mFirstFocusRunnabel = new Runnable() {
        @Override
        public void run() {
            ZmAccessibilityUtils.sendAccessibilityFocusEvent(mIvRCControl);
        }
    };

    public RCFloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RCFloatView(Context context) {
        super(context);
        init(context);
    }

    public void setRemoteControlButtonStatusListener(IRemoteControlButtonStatusListener iEnabledRemoteControlListener) {
        this.iEnabledRemoteControlListener = iEnabledRemoteControlListener;
    }

    private void init(Context context) {
        meetingService = ZoomSDK.getInstance().getInMeetingService();
        mActivity = (FragmentActivity) context;
        mHelpDialog = new Dialog(context);
        mHelpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mHelpDialog.setContentView(R.layout.zm_rc_fingers_question);
        mHelpDialog.setCanceledOnTouchOutside(true);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.zm_rc_float_view, this);
        mIvRCControl = (ImageView) view.findViewById(R.id.rc_control);
        mIvKeyboard = (ImageView) view.findViewById(R.id.rc_keyboard);
        mIvQuestion = (ImageView) view.findViewById(R.id.rc_question);
        mVGContentSpan = (ViewGroup) view.findViewById(R.id.rc_content_span);
        mHiddenEditText = (EditText) view.findViewById(R.id.rc_hidden_edit);
        mVGFloatPanel = (ViewGroup) view.findViewById(R.id.rc_float_panel);

        mIvRCControl.setOnClickListener(this);
        mIvRCControl.setImageResource(R.drawable.zm_rc_control);
        mIvQuestion.setOnClickListener(this);
        mVGContentSpan.setVisibility(View.INVISIBLE);
        //mIvRCControl.setOnTouchListener(this);
        mIvKeyboard.setOnClickListener(this);

        mHiddenEditText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                        InMeetingRemoteController remoteController = meetingService.getInMeetingRemoteController();
                        if (null == remoteController) {
                            return false;
                        }
                        remoteController.remoteControlKeyInput(InMeetingRemoteController.MobileRTCRemoteControlInputType.MobileRTCRemoteControl_Del);
                        return true;
                    }
                }
                return false;
            }
        });

        mHiddenEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (mCleanHiddenEditText) {
                    return;
                }
                InMeetingRemoteController remoteController = meetingService.getInMeetingRemoteController();
                if (null == remoteController) {
                    return;
                }
                if (count > 0) { //change text
                    for (int i = 0; i < before; i++) {
                        remoteController.remoteControlKeyInput(InMeetingRemoteController.MobileRTCRemoteControlInputType.MobileRTCRemoteControl_Del);
                    }
                }

                CharSequence ss = s.subSequence(start, s.length());
                if (count == 1 && ss.equals("\n")) {
                    remoteController.remoteControlKeyInput(InMeetingRemoteController.MobileRTCRemoteControlInputType.MobileRTCRemoteControl_Return);
                } else {
                    remoteController.remoteControlCharInput(ss.toString());
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 100) {
                    mCleanHiddenEditText = true;
                    mHiddenEditText.setText("");
                } else {
                    mCleanHiddenEditText = false;
                }
            }
        });
        String message = getMessage();
        if (!TextUtils.isEmpty(message))
            mIvRCControl.setContentDescription(message);
    }


    private void enableRC(boolean enable) {
        Log.d("RCFloatView", "enableRC:" + enable);
        if (enable) {
            mVGContentSpan.setVisibility(View.VISIBLE);
            mIvRCControl.setImageResource(R.drawable.zm_rc_control_reverse_bg);
            mVGFloatPanel.setBackgroundResource(R.drawable.zm_rc_drawer);
            if (iEnabledRemoteControlListener != null) {
                iEnabledRemoteControlListener.onEnabledRC(true);
            }
            String message = getMessage();
            if (!TextUtils.isEmpty(message))
                mIvRCControl.setContentDescription(message);
        } else {
            mVGContentSpan.setVisibility(View.INVISIBLE);
            mIvRCControl.setImageResource(R.drawable.zm_rc_control);
            mVGFloatPanel.setBackgroundResource(0);
            showKeyboard(false);
            if (mHelpDialog.isShowing())
                mHelpDialog.dismiss();
            if (iEnabledRemoteControlListener != null) {
                iEnabledRemoteControlListener.onEnabledRC(false);
            }
        }
    }

    private void showRCTapMessageTip() {
        String message = getMessage();
        if (!TextUtils.isEmpty(message)) {
            mIvRCControl.setContentDescription(message);
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }

    }

    public void showRCFloatView(boolean show, boolean isFirst, boolean isController) {
        Log.d(TAG, "showRCFloatView :" + show + ":" + isFirst + " mbPosChanged:" + mbPosChanged);
        if (show) {
            View parent = (View) getParent();
            if (parent == null) {
                return;
            }
            if (!mbPosChanged) {
//                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.getLayoutParams();
//                if (params != null) {
//                    params.topMargin = parent.getHeight() - UIUtil.dip2px(mActivity, 150);
//                    params.leftMargin = UIUtil.dip2px(mActivity, 50);
//                    this.setLayoutParams(params);
//                }
            }
            this.setVisibility(View.VISIBLE);
            if (isFirst) {
                showRCTapMessageTip();
            }
        } else {
            this.setVisibility(View.GONE);
            if (mHelpDialog.isShowing())
                mHelpDialog.dismiss();
        }
        enableRC(isController);
        showKeyboard(false);

        if (isFirst && show) {
            mHandler.removeCallbacks(mFirstFocusRunnabel);
            mHandler.postDelayed(mFirstFocusRunnabel, ZmAccessibilityUtils.DELAY_SEND_FOCUS_EVENT);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    @Override
    public void onClick(View v) {
        if (v == mIvRCControl) {
            InMeetingService service = ZoomSDK.getInstance().getInMeetingService();
            if (service == null) return;
            InMeetingRemoteController remoteController = service.getInMeetingRemoteController();
            if (null == remoteController) {
                return;
            }
            boolean isRc = remoteController.isRemoteController();
            if (isRc) {
                enableRC(!(mVGContentSpan.getVisibility() == View.VISIBLE));
            } else {
                remoteController.grabRemoteControl();
            }
        } else if (v == mIvKeyboard) {
            showKeyboard(true);
        } else if (v == mIvQuestion) {
            mHelpDialog.show();
        }
    }


    private void showKeyboard(boolean show) {
        if (show)
            ZmKeyboardUtils.openSoftKeyboard(getContext(), mHiddenEditText);
        else
            ZmKeyboardUtils.closeSoftKeyboard(getContext(), mHiddenEditText);
    }

    private String getMessage() {
        InMeetingService service = ZoomSDK.getInstance().getInMeetingService();
        if (null == service) {
            return null;
        }
        long userID = service.activeShareUserID();

        String shareUsername = "" + userID;
        //TODO
        //user.getScreenName();
        String messageFormat = mActivity.getString(R.string.zm_rc_tap_notice);
        return String.format(messageFormat, shareUsername);
    }


}

