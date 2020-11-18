package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;
import java.util.List;

import us.zoom.sdk.IInterpretationLanguage;
import us.zoom.sdk.InMeetingInterpretationController;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.R;

public class MeetingInterpretationDialog extends Dialog {

    private ListView listView;

    public MeetingInterpretationDialog(@NonNull Context context) {
        super(context);
    }

    public MeetingInterpretationDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public MeetingInterpretationDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public static void show(Context context) {

        MeetingInterpretationDialog dialog = new MeetingInterpretationDialog(context);
        dialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.dialog_meeting_interpretation);
        listView = findViewById(R.id.languageList);

        List<String> list = new ArrayList<>();
        list.add("original Audio");

        final InMeetingInterpretationController interpretationController = ZoomSDK.getInstance().getInMeetingService().getInMeetingInterpretationController();

        final List<IInterpretationLanguage> languageList = interpretationController.getAvailableLanguageList();


        if (null != languageList) {
            for (IInterpretationLanguage interpretationLanguage : languageList) {
                list.add(interpretationLanguage.getLanguageName());
            }
        }

        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_checked, list));
        int index = 0;
        int id = interpretationController.getJoinedLanguageID();
        if (null != languageList) {
            for (int i = 0; i < languageList.size(); i++) {
                IInterpretationLanguage interpretationLanguage = languageList.get(i);
                if (interpretationLanguage.getLanguageID() == id) {
                    index = i + 1;
                    break;
                }
            }
        }

        final Switch btn_mute_original = findViewById(R.id.btn_mute_original);
        btn_mute_original.setChecked(interpretationController.isMajorAudioTurnOff());
        if (index == 0) {
            btn_mute_original.setVisibility(View.GONE);
        } else {
            btn_mute_original.setVisibility(View.VISIBLE);
        }

        btn_mute_original.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    interpretationController.turnOffMajorAudio();
                } else {
                    interpretationController.turnOnMajorAudio();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    btn_mute_original.setVisibility(View.GONE);
                } else {
                    btn_mute_original.setVisibility(View.VISIBLE);
                }
            }
        });

        listView.setItemChecked(index, true);

        findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = listView.getCheckedItemPosition();
                if (position == 0) {
                    interpretationController.joinLanguageChannel(-1);
                } else {
                    interpretationController.joinLanguageChannel(languageList.get(position - 1).getLanguageID());
                }
                dismiss();
            }
        });


        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
