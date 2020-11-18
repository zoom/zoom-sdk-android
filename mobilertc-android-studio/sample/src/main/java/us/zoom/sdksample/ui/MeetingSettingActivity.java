package us.zoom.sdksample.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import us.zoom.sdk.InviteOptions;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.MeetingViewsOptions;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;
import us.zoom.sdk.ZoomSDKRawDataMemoryMode;
import us.zoom.sdksample.R;
import us.zoom.sdksample.initsdk.AuthConstants;
import us.zoom.sdksample.initsdk.InitAuthSDKHelper;
import us.zoom.sdksample.inmeetingfunction.zoommeetingui.ZoomMeetingUISettingHelper;

public class MeetingSettingActivity extends FragmentActivity implements CompoundButton.OnCheckedChangeListener {

    LinearLayout settingContain;

    LinearLayout rawDataSettingContain;

    EditText edit_participant_id;
    EditText webinar_token;
    EditText edit_custom_meeting_id;

    private static final String TAG = MeetingSettingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_setting);

        Switch btnCustomUI = ((Switch) findViewById(R.id.btn_custom_ui));

        boolean isCustomUI = ZoomSDK.getInstance().getMeetingSettingsHelper().isCustomizedMeetingUIEnabled();

        btnCustomUI.setChecked(isCustomUI);

        settingContain = findViewById(R.id.settings_contain);
        settingContain.setVisibility(isCustomUI ? View.GONE : View.VISIBLE);

        boolean hasRawDataLicense = ZoomSDK.getInstance().hasRawDataLicense();
        Switch btnVideoSource = (Switch) findViewById(R.id.btn_external_video_source);
        btnVideoSource.setVisibility(hasRawDataLicense ? View.VISIBLE : View.GONE);
        btnVideoSource.setChecked(ZoomMeetingUISettingHelper.useExternalVideoSource);


        btnVideoSource.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ZoomMeetingUISettingHelper.changeVideoSource(isChecked);
            }
        });




        boolean hasLicense = ZoomSDK.getInstance().hasRawDataLicense();
        rawDataSettingContain = findViewById(R.id.rawdata_settings_contain);
        rawDataSettingContain.setVisibility(isCustomUI && hasLicense ? View.VISIBLE : View.GONE);

        for (int i = 0, count = settingContain.getChildCount(); i < count; i++) {
            View child = settingContain.getChildAt(i);
            if (null != child && child instanceof Switch) {
                ((Switch) child).setOnCheckedChangeListener(this);
                initCheck((Switch) child);
            }
        }

        initInvite();

        final JoinMeetingOptions options = ZoomMeetingUISettingHelper.getMeetingOptions();

        edit_participant_id = findViewById(R.id.edit_participant_id);
        webinar_token = findViewById(R.id.webinar_token);
        edit_custom_meeting_id = findViewById(R.id.edit_custom_meeting_id);

        edit_participant_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                options.participant_id = edit_participant_id.getText().toString();
            }
        });

        webinar_token.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                options.webinar_token = webinar_token.getText().toString();
            }
        });

        edit_custom_meeting_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                options.custom_meeting_id = edit_custom_meeting_id.getText().toString();
            }
        });


        edit_participant_id.setText(options.participant_id);
        webinar_token.setText(options.webinar_token);
        edit_custom_meeting_id.setText(options.custom_meeting_id);

        ((Switch) findViewById(R.id.btn_switch_domain)).setOnCheckedChangeListener(this);

        Switch btnRawData = findViewById(R.id.btn_raw_data);
        SharedPreferences sharedPreferences = getSharedPreferences("UI_Setting", Context.MODE_PRIVATE);
        boolean enable = sharedPreferences.getBoolean("enable_rawdata", false);
        btnRawData.setChecked(enable);
        btnRawData.setOnCheckedChangeListener(this);


        btnCustomUI.setOnCheckedChangeListener(this);

        LinearLayout linearLayout = findViewById(R.id.view_option_contain);

        for (int index = 0, count = linearLayout.getChildCount(); index < count; index++) {
            LinearLayout layout = (LinearLayout) linearLayout.getChildAt(index);
            initMeetingViewOption(layout);
        }

    }

    void initMeetingViewOption(ViewGroup contain) {
        JoinMeetingOptions options = ZoomMeetingUISettingHelper.getMeetingOptions();
        for (int index = 0, count = contain.getChildCount(); index < count; index++) {
            CheckBox checkBox = (CheckBox) contain.getChildAt(index);
            switch (checkBox.getId()) {
                case R.id.no_btn_video: {
                    checkBox.setChecked((options.meeting_views_options & MeetingViewsOptions.NO_BUTTON_VIDEO) != 0);
                    break;
                }
                case R.id.no_btn_audio: {
                    checkBox.setChecked((options.meeting_views_options & MeetingViewsOptions.NO_BUTTON_AUDIO) != 0);
                    break;
                }
                case R.id.no_btn_share: {
                    checkBox.setChecked((options.meeting_views_options & MeetingViewsOptions.NO_BUTTON_SHARE) != 0);
                    break;
                }
                case R.id.no_btn_participants: {
                    checkBox.setChecked((options.meeting_views_options & MeetingViewsOptions.NO_BUTTON_PARTICIPANTS) != 0);
                    break;
                }
                case R.id.no_btn_more: {
                    checkBox.setChecked((options.meeting_views_options & MeetingViewsOptions.NO_BUTTON_MORE) != 0);
                    break;
                }
                case R.id.no_text_meeting_id: {
                    checkBox.setChecked((options.meeting_views_options & MeetingViewsOptions.NO_TEXT_MEETING_ID) != 0);
                    break;
                }
                case R.id.no_text_password: {
                    checkBox.setChecked((options.meeting_views_options & MeetingViewsOptions.NO_TEXT_PASSWORD) != 0);
                    break;
                }
                case R.id.no_btn_leave: {
                    checkBox.setChecked((options.meeting_views_options & MeetingViewsOptions.NO_BUTTON_LEAVE) != 0);
                    break;
                }
                case R.id.no_btn_switch_camera: {
                    checkBox.setChecked((options.meeting_views_options & MeetingViewsOptions.NO_BUTTON_SWITCH_CAMERA) != 0);
                    break;
                }
                case R.id.no_btn_switch_audio_source: {
                    checkBox.setChecked((options.meeting_views_options & MeetingViewsOptions.NO_BUTTON_SWITCH_AUDIO_SOURCE) != 0);
                    break;
                }
            }

            setMeetingViewOption(checkBox);
        }
    }

    void initInvite() {
        final JoinMeetingOptions options = ZoomMeetingUISettingHelper.getMeetingOptions();


        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.invite_disable_all: {
                        options.invite_options = isChecked ? options.invite_options & InviteOptions.INVITE_DISABLE_ALL :
                                options.invite_options | InviteOptions.INVITE_DISABLE_ALL;
                        break;
                    }
                    case R.id.invite_enable_all: {
                        options.invite_options = isChecked ? options.invite_options | InviteOptions.INVITE_ENABLE_ALL :
                                options.invite_options & InviteOptions.INVITE_ENABLE_ALL;
                        break;
                    }
                    case R.id.invite_via_sms: {
                        options.invite_options = isChecked ? options.invite_options | InviteOptions.INVITE_VIA_SMS :
                                options.invite_options & InviteOptions.INVITE_VIA_SMS;
                        break;
                    }
                    case R.id.invite_via_email: {
                        options.invite_options = isChecked ? options.invite_options | InviteOptions.INVITE_VIA_EMAIL :
                                options.invite_options & InviteOptions.INVITE_VIA_EMAIL;
                        break;
                    }
                    case R.id.invite_copy_url: {
                        options.invite_options = isChecked ? options.invite_options | InviteOptions.INVITE_COPY_URL :
                                options.invite_options & InviteOptions.INVITE_COPY_URL;
                        break;
                    }
                }

            }
        };

        CheckBox checkBox = findViewById(R.id.invite_disable_all);
        if ((options.invite_options | InviteOptions.INVITE_DISABLE_ALL) == 0) {
            checkBox.setChecked(true);
        }
        checkBox.setOnCheckedChangeListener(listener);

        checkBox = findViewById(R.id.invite_enable_all);
        if ((options.invite_options & InviteOptions.INVITE_ENABLE_ALL) == InviteOptions.INVITE_ENABLE_ALL) {
            checkBox.setChecked(true);
        }
        checkBox.setOnCheckedChangeListener(listener);

        checkBox = findViewById(R.id.invite_via_sms);
        if ((options.invite_options & InviteOptions.INVITE_VIA_SMS) != 0) {
            checkBox.setChecked(true);
        }
        checkBox.setOnCheckedChangeListener(listener);

        checkBox = findViewById(R.id.invite_via_email);
        if ((options.invite_options & InviteOptions.INVITE_VIA_EMAIL) != 0) {
            checkBox.setChecked(true);
        }
        checkBox.setOnCheckedChangeListener(listener);

        checkBox = findViewById(R.id.invite_copy_url);
        if ((options.invite_options & InviteOptions.INVITE_COPY_URL) != 0) {
            checkBox.setChecked(true);
        }
        checkBox.setOnCheckedChangeListener(listener);

    }

    void setMeetingViewOption(CheckBox checkBox) {
        final JoinMeetingOptions options = ZoomMeetingUISettingHelper.getMeetingOptions();
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                switch (buttonView.getId()) {
                    case R.id.no_btn_video: {
                        options.meeting_views_options = isChecked ? options.meeting_views_options | MeetingViewsOptions.NO_BUTTON_VIDEO :
                                options.meeting_views_options & MeetingViewsOptions.NO_BUTTON_VIDEO;
                        break;
                    }
                    case R.id.no_btn_audio: {
                        options.meeting_views_options = isChecked ? options.meeting_views_options | MeetingViewsOptions.NO_BUTTON_AUDIO :
                                options.meeting_views_options & MeetingViewsOptions.NO_BUTTON_AUDIO;
                        break;
                    }
                    case R.id.no_btn_share: {

                        options.meeting_views_options = isChecked ? options.meeting_views_options | MeetingViewsOptions.NO_BUTTON_SHARE :
                                options.meeting_views_options & MeetingViewsOptions.NO_BUTTON_SHARE;
                        break;
                    }
                    case R.id.no_btn_participants: {
                        options.meeting_views_options = isChecked ? options.meeting_views_options | MeetingViewsOptions.NO_BUTTON_PARTICIPANTS :
                                options.meeting_views_options & MeetingViewsOptions.NO_BUTTON_PARTICIPANTS;
                        break;
                    }
                    case R.id.no_btn_more: {
                        options.meeting_views_options = isChecked ? options.meeting_views_options | MeetingViewsOptions.NO_BUTTON_MORE :
                                options.meeting_views_options & MeetingViewsOptions.NO_BUTTON_MORE;
                        break;
                    }
                    case R.id.no_text_meeting_id: {
                        options.meeting_views_options = isChecked ? options.meeting_views_options | MeetingViewsOptions.NO_TEXT_MEETING_ID :
                                options.meeting_views_options & MeetingViewsOptions.NO_TEXT_MEETING_ID;
                        break;
                    }
                    case R.id.no_text_password: {
                        options.meeting_views_options = isChecked ? options.meeting_views_options | MeetingViewsOptions.NO_TEXT_PASSWORD :
                                options.meeting_views_options & MeetingViewsOptions.NO_TEXT_PASSWORD;
                        break;
                    }
                    case R.id.no_btn_leave: {
                        options.meeting_views_options = isChecked ? options.meeting_views_options | MeetingViewsOptions.NO_BUTTON_LEAVE :
                                options.meeting_views_options & MeetingViewsOptions.NO_BUTTON_LEAVE;
                        break;
                    }
                    case R.id.no_btn_switch_camera: {
                        options.meeting_views_options = isChecked ? options.meeting_views_options | MeetingViewsOptions.NO_BUTTON_SWITCH_CAMERA :
                                options.meeting_views_options & MeetingViewsOptions.NO_BUTTON_SWITCH_CAMERA;
                        break;
                    }
                    case R.id.no_btn_switch_audio_source: {
                        options.meeting_views_options = isChecked ? options.meeting_views_options | MeetingViewsOptions.NO_BUTTON_SWITCH_AUDIO_SOURCE :
                                options.meeting_views_options & MeetingViewsOptions.NO_BUTTON_SWITCH_AUDIO_SOURCE;
                        break;
                    }
                }

            }
        });
    }


    private void initCheck(Switch view) {
        switch (view.getId()) {
            case R.id.btn_auto_connect_audio: {
                view.setChecked(ZoomSDK.getInstance().getMeetingSettingsHelper().isAutoConnectVoIPWhenJoinMeetingEnabled());
                break;
            }
            case R.id.btn_mute_my_mic: {
                view.setChecked(ZoomSDK.getInstance().getMeetingSettingsHelper().isMuteMyMicrophoneWhenJoinMeetingEnabled());
                break;
            }
            case R.id.btn_turn_off_video: {
                view.setChecked(ZoomSDK.getInstance().getMeetingSettingsHelper().isTurnOffMyVideoWhenJoinMeetingEnabled());
                break;
            }
            case R.id.btn_hide_no_video_user: {
                view.setChecked(ZoomSDK.getInstance().getMeetingSettingsHelper().isHideNoVideoUsersEnabled());
                break;
            }
            case R.id.btn_auto_switch_video: {
//                view.setChecked(ZoomSDK.getInstance().getMeetingSettingsHelper().isSwitchVideoLayoutAccordingToUserCountEnabled());
                break;
            }
            case R.id.btn_gallery_video: {
                view.setChecked(ZoomSDK.getInstance().getMeetingSettingsHelper().isGalleryVideoViewDisabled());
                break;
            }
            case R.id.btn_show_tool_bar: {
                view.setChecked(ZoomSDK.getInstance().getMeetingSettingsHelper().isAlwaysShowMeetingToolbarEnabled());
                break;
            }
            case R.id.btn_show_larger_share: {
                view.setChecked(ZoomSDK.getInstance().getMeetingSettingsHelper().isLargeShareVideoSceneEnabled());
                break;
            }

            case R.id.btn_no_video_title_share: {
                view.setChecked(ZoomSDK.getInstance().getMeetingSettingsHelper().isNoVideoTileOnShareScreenEnabled());
                break;
            }

            case R.id.btn_no_leave_btn: {
                view.setChecked(ZoomSDK.getInstance().getMeetingSettingsHelper().isNoLeaveMeetingButtonForHostEnabled());
                break;
            }

            case R.id.btn_no_tips_user_event: {
                view.setChecked(ZoomSDK.getInstance().getMeetingSettingsHelper().isNoUserJoinOrLeaveTipEnabled());
                break;
            }
            case R.id.btn_no_drive_mode: {
                view.setChecked(ZoomMeetingUISettingHelper.getMeetingOptions().no_driving_mode);
                break;
            }
            case R.id.btn_no_end_dialog: {
                view.setChecked(ZoomMeetingUISettingHelper.getMeetingOptions().no_meeting_end_message);
                break;
            }
            case R.id.btn_hidden_title_bar: {
                view.setChecked(ZoomMeetingUISettingHelper.getMeetingOptions().no_titlebar);
                break;
            }
            case R.id.btn_hidden_invite: {
                view.setChecked(ZoomMeetingUISettingHelper.getMeetingOptions().no_invite);
                break;
            }
            case R.id.btn_hidden_bottom_bar: {
                view.setChecked(ZoomMeetingUISettingHelper.getMeetingOptions().no_bottom_toolbar);
                break;
            }

            case R.id.btn_hidden_dial_in_via_phone: {
                view.setChecked(ZoomMeetingUISettingHelper.getMeetingOptions().no_dial_in_via_phone);
                break;
            }
            case R.id.btn_hidden_dial_out_via_phone: {
                view.setChecked(ZoomMeetingUISettingHelper.getMeetingOptions().no_dial_out_to_phone);
                break;
            }

            case R.id.btn_no_share: {
                view.setChecked(ZoomMeetingUISettingHelper.getMeetingOptions().no_share);
                break;
            }
            case R.id.btn_no_video: {
                view.setChecked(ZoomMeetingUISettingHelper.getMeetingOptions().no_video);
                break;
            }
            case R.id.btn_no_audio: {
                view.setChecked(ZoomMeetingUISettingHelper.getMeetingOptions().no_audio);
                break;
            }
            case R.id.btn_no_meeting_error_message: {
                view.setChecked(ZoomMeetingUISettingHelper.getMeetingOptions().no_meeting_error_message);
                break;
            }
            case R.id.btn_hide_screen_share_toolbar_annotation:
                view.setChecked(ZoomSDK.getInstance().getMeetingSettingsHelper().isHideAnnotationInScreenShareToolbar());
                break;
            case R.id.btn_hide_screen_share_toolbar_stopshare:
                view.setChecked(ZoomSDK.getInstance().getMeetingSettingsHelper().isHideStopShareInScreenShareToolbar());
                break;
            case R.id.btn_hidden_disconnect_audio: {
                view.setChecked(ZoomMeetingUISettingHelper.getMeetingOptions().no_disconnect_audio);
                break;
            }
            case R.id.no_unmute_confirm_dialog: {
                view.setChecked(ZoomMeetingUISettingHelper.getMeetingOptions().no_unmute_confirm_dialog);
                break;
            }
            case R.id.no_webinar_register_dialog: {
                view.setChecked(ZoomMeetingUISettingHelper.getMeetingOptions().no_webinar_register_dialog);
                break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.btn_custom_ui: {
                ZoomSDK.getInstance().getMeetingSettingsHelper().setCustomizedMeetingUIEnabled(isChecked);
                settingContain.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                boolean hasLicense = ZoomSDK.getInstance().hasRawDataLicense();
                rawDataSettingContain.setVisibility(isChecked && hasLicense ? View.VISIBLE : View.GONE);
                break;
            }
            case R.id.btn_auto_connect_audio: {
                ZoomSDK.getInstance().getMeetingSettingsHelper().setAutoConnectVoIPWhenJoinMeeting(isChecked);
                break;
            }
            case R.id.btn_mute_my_mic: {
                ZoomSDK.getInstance().getMeetingSettingsHelper().setMuteMyMicrophoneWhenJoinMeeting(isChecked);
                break;
            }
            case R.id.btn_turn_off_video: {
                ZoomSDK.getInstance().getMeetingSettingsHelper().setTurnOffMyVideoWhenJoinMeeting(isChecked);
                break;
            }
            case R.id.btn_hide_no_video_user: {
                ZoomSDK.getInstance().getMeetingSettingsHelper().setHideNoVideoUsersEnabled(isChecked);
                break;
            }
            case R.id.btn_auto_switch_video: {
//                ZoomSDK.getInstance().getMeetingSettingsHelper().setSwitchVideoLayoutAccordingToUserCountEnabled(isChecked);
                break;
            }
            case R.id.btn_gallery_video: {
                ZoomSDK.getInstance().getMeetingSettingsHelper().setGalleryVideoViewDisabled(!isChecked);
                break;
            }
            case R.id.btn_show_tool_bar: {
                ZoomSDK.getInstance().getMeetingSettingsHelper().setAlwaysShowMeetingToolbarEnabled(isChecked);
                break;
            }
            case R.id.btn_show_larger_share: {
                ZoomSDK.getInstance().getMeetingSettingsHelper().setLargeShareVideoSceneEnabled(isChecked);
                break;
            }

            case R.id.btn_no_video_title_share: {
                ZoomSDK.getInstance().getMeetingSettingsHelper().setNoVideoTileOnShareScreenEnabled(isChecked);
                break;
            }

            case R.id.btn_no_leave_btn: {
                ZoomSDK.getInstance().getMeetingSettingsHelper().setNoLeaveMeetingButtonForHostEnabled(isChecked);
                break;
            }

            case R.id.btn_no_tips_user_event: {
                ZoomSDK.getInstance().getMeetingSettingsHelper().setNoUserJoinOrLeaveTipEnabled(isChecked);
                break;
            }
            case R.id.btn_no_drive_mode: {
                ZoomMeetingUISettingHelper.getMeetingOptions().no_driving_mode = isChecked;
                break;
            }
            case R.id.btn_no_end_dialog: {
                ZoomMeetingUISettingHelper.getMeetingOptions().no_meeting_end_message = isChecked;
                break;
            }
            case R.id.btn_hidden_title_bar: {
                ZoomMeetingUISettingHelper.getMeetingOptions().no_titlebar = isChecked;
                break;
            }
            case R.id.btn_hidden_invite: {
                ZoomMeetingUISettingHelper.getMeetingOptions().no_invite = isChecked;
                break;
            }
            case R.id.btn_hidden_bottom_bar: {
                ZoomMeetingUISettingHelper.getMeetingOptions().no_bottom_toolbar = isChecked;
                break;
            }

            case R.id.btn_hidden_dial_in_via_phone: {
                ZoomMeetingUISettingHelper.getMeetingOptions().no_dial_in_via_phone = isChecked;
                break;
            }
            case R.id.btn_hidden_dial_out_via_phone: {
                ZoomMeetingUISettingHelper.getMeetingOptions().no_dial_out_to_phone = isChecked;
                break;
            }
            case R.id.btn_hidden_disconnect_audio: {
                ZoomMeetingUISettingHelper.getMeetingOptions().no_disconnect_audio = isChecked;
                break;
            }
            case R.id.btn_no_share: {
                ZoomMeetingUISettingHelper.getMeetingOptions().no_share = isChecked;
                break;
            }
            case R.id.btn_no_video: {
                ZoomMeetingUISettingHelper.getMeetingOptions().no_video = isChecked;
                break;
            }
            case R.id.btn_no_audio: {
                ZoomMeetingUISettingHelper.getMeetingOptions().no_audio = isChecked;
                break;
            }
            case R.id.btn_no_meeting_error_message: {
                ZoomMeetingUISettingHelper.getMeetingOptions().no_meeting_error_message = isChecked;
                break;
            }
            case R.id.no_unmute_confirm_dialog: {
                ZoomMeetingUISettingHelper.getMeetingOptions().no_unmute_confirm_dialog = isChecked;
                break;
            }
            case R.id.no_webinar_register_dialog: {
                ZoomMeetingUISettingHelper.getMeetingOptions().no_webinar_register_dialog = isChecked;
                break;
            }
            case R.id.btn_force_start_video: {
                ZoomSDK.getInstance().getMeetingSettingsHelper().enableForceAutoStartMyVideoWhenJoinMeeting(isChecked);
                break;
            }
            case R.id.btn_force_stop_video: {
                ZoomSDK.getInstance().getMeetingSettingsHelper().enableForceAutoStopMyVideoWhenJoinMeeting(isChecked);
                break;
            }
            case R.id.btn_show_audio_select_dialog: {
                ZoomSDK.getInstance().getMeetingSettingsHelper().disableAutoShowSelectJoinAudioDlgWhenJoinMeeting(isChecked);
                break;
            }

            case R.id.btn_raw_data: {
                SharedPreferences sharedPreferences = getSharedPreferences("UI_Setting", Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("enable_rawdata", isChecked).commit();
                break;
            }
            case R.id.btn_hide_screen_share_toolbar_annotation:
                ZoomSDK.getInstance().getMeetingSettingsHelper().hideAnnotationInScreenShareToolbar(isChecked);
                break;
            case R.id.btn_hide_screen_share_toolbar_stopshare:
                ZoomSDK.getInstance().getMeetingSettingsHelper().hideStopShareInScreenShareToolbar(isChecked);
                break;
            case R.id.btn_switch_domain: {
                boolean success = ZoomSDK.getInstance().switchDomain("https://www.zoomus.cn", true);
                Log.d(TAG, "switchDomain:" + success);
                if (success) {
                    ZoomSDKInitParams initParams = new ZoomSDKInitParams();
                    initParams.appKey = "";
                    initParams.appSecret = "";
                    initParams.enableLog = true;
                    initParams.logSize = 50;
                    initParams.domain = "https://www.zoomus.cn";
                    initParams.videoRawDataMemoryMode = ZoomSDKRawDataMemoryMode.ZoomSDKRawDataMemoryModeStack;
                    if (TextUtils.isEmpty(initParams.appKey) || TextUtils.isEmpty(initParams.appSecret)) {
                        return;
                    }

                    ZoomSDK.getInstance().initialize(this, new ZoomSDKInitializeListener() {
                        @Override
                        public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
                            Log.d(TAG, "onZoomSDKInitializeResult:" + errorCode + ":" + internalErrorCode);
                        }

                        @Override
                        public void onZoomAuthIdentityExpired() {
                            Log.d(TAG, "onZoomAuthIdentityExpired:");
                        }
                    }, initParams);
                }
                break;
            }
        }
    }
}