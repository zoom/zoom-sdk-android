package us.zoom.sdksample.inmeetingfunction.customizedmeetingui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import us.zoom.internal.InterpreterNative;
import us.zoom.sdk.IInterpretationLanguage;
import us.zoom.sdk.IInterpreter;
import us.zoom.sdk.InMeetingInterpretationController;
import us.zoom.sdk.InMeetingUserInfo;
import us.zoom.sdk.MobileRTCSDKError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.R;

public class MeetingInterpretationAdminDialog extends Dialog implements View.OnClickListener {


    LinearLayout item_contain;

    private List<UserItemInfo> userList = new ArrayList<>();

    public MeetingInterpretationAdminDialog(@NonNull Context context) {
        super(context);
    }

    public MeetingInterpretationAdminDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public MeetingInterpretationAdminDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static void show(Context context) {
        MeetingInterpretationAdminDialog dialog = new MeetingInterpretationAdminDialog(context);
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_interpretaion_admin);

        item_contain = findViewById(R.id.item_contain);

        findViewById(R.id.btn_update).setOnClickListener(this);
        findViewById(R.id.btn_add).setOnClickListener(this);


        Button end = findViewById(R.id.btn_end);
        end.setOnClickListener(this);

        InMeetingInterpretationController controller = ZoomSDK.getInstance().getInMeetingService()
                .getInMeetingInterpretationController();
        if (controller.isInterpretationStarted()) {
            end.setText("End");
        } else {
            findViewById(R.id.btn_update).setVisibility(View.GONE);
            end.setText("Start");
        }

        loadUser();

        loadInterpretation();
    }

    private void loadUser() {
        List<Long> list = ZoomSDK.getInstance().getInMeetingService().getInMeetingUserList();
        userList.clear();
        for (Long userId : list) {
            InMeetingUserInfo userInfo = ZoomSDK.getInstance().getInMeetingService().getUserInfoById(userId);
            userList.add(new UserItemInfo(userInfo));
        }
    }

    private void loadInterpretation() {
        InMeetingInterpretationController controller = ZoomSDK.getInstance().getInMeetingService()
                .getInMeetingInterpretationController();

        List<IInterpreter> interpreters = controller.getInterpreterList();

        if (null != interpreters) {
            for (IInterpreter interpreter : interpreters) {
                if (!interpreter.isAvailable()) {
                    userList.add(new UserItemInfo(interpreter));
                }
                addItem(interpreter);
            }
        }
    }

    private void addItem(final IInterpreter interpreter) {

        long selectedUserId = 0;
        if (null != interpreter) {
            selectedUserId = interpreter.getUserID();
        }

        final InMeetingInterpretationController controller = ZoomSDK.getInstance().getInMeetingService()
                .getInMeetingInterpretationController();

        final View item = LayoutInflater.from(getContext()).inflate(R.layout.layout_interpretaion_item, item_contain, false);
        Spinner spinner = item.findViewById(R.id.interpreter);

        spinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, userList));

        for (int index = 0, size = userList.size(); index < size; index++) {
            UserItemInfo itemInfo = userList.get(index);
            if (itemInfo.getId() == selectedUserId) {
                spinner.setSelection(index, true);
                break;
            }
        }

        Spinner spinner_lan1 = item.findViewById(R.id.lan_1);
        Spinner spinner_lan2 = item.findViewById(R.id.lan_2);


        List<IInterpretationLanguage> languageList = controller.getAllLanguageList();
        List<InterpretationLanguageInfo> infoList = new ArrayList<>();

        for (int index = 0, size = languageList.size(); index < size; index++) {
            IInterpretationLanguage language = languageList.get(index);
            infoList.add(new InterpretationLanguageInfo(language));
        }

        spinner_lan1.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, infoList));
        spinner_lan2.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, infoList));

        if (null == interpreter) {
            spinner_lan1.setSelection(0, true);
            spinner_lan2.setSelection(1, true);
        } else {
            for (InterpretationLanguageInfo language : infoList) {
                if (language.getId() == interpreter.getLanguageID1()) {
                    spinner_lan1.setSelection(infoList.indexOf(language), true);
                    break;
                }
            }
            for (InterpretationLanguageInfo language : infoList) {
                if (language.getId() == interpreter.getLanguageID2()) {
                    spinner_lan2.setSelection(infoList.indexOf(language), true);
                    break;
                }
            }
        }

        item.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller.isInterpretationStarted()) {
                    if (null != interpreter) {
//                        if (!interpreter.isAvailable()) {
//                            return;
//                        }
                        boolean success = controller.removeInterpreter(interpreter.getUserID());
                        if (success) {
                            item_contain.removeView(item);
                            Toast.makeText(getContext(), "Delete Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Delete fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });

        item_contain.addView(item);
    }

    class UserItemInfo {
        InMeetingUserInfo userInfo;
        //not joined user
        IInterpreter interpreter;

        public UserItemInfo(InMeetingUserInfo userInfo) {
            this.userInfo = userInfo;
        }

        public UserItemInfo(IInterpreter interpreter) {
            this.interpreter = interpreter;
        }

        public String toString() {
            if (null == userInfo) {
                return "*****(not joined)";
            }
            return userInfo.getUserName();
        }

        long getId() {
            if (null != userInfo) {
                return userInfo.getUserId();
            } else {
                return interpreter.getUserID();
            }
        }
    }

    class InterpretationLanguageInfo {

        private IInterpretationLanguage info;

        public InterpretationLanguageInfo(IInterpretationLanguage info) {
            this.info = info;
        }

        int getId(){
            return info.getLanguageID();
        }

        @Override
        public String toString() {
            return info.getLanguageName();
        }
    }

    class InterpreterItem {
        private long userId;
        private int languageID1;
        private int languageID2;

        public InterpreterItem(long userId, int languageID1, int languageID2) {
            this.userId = userId;
            this.languageID1 = languageID1;
            this.languageID2 = languageID2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InterpreterItem that = (InterpreterItem) o;
            return userId == that.userId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId);
        }
    }

    @Override
    public void onClick(View v) {
        InMeetingInterpretationController controller = ZoomSDK.getInstance().getInMeetingService()
                .getInMeetingInterpretationController();

        switch (v.getId()) {
            case R.id.btn_update: {
                updateInterpretation();
                break;
            }
            case R.id.btn_add: {
                addItem(null);
                break;
            }
            case R.id.btn_end: {
                MobileRTCSDKError error;
                if (controller.isInterpretationStarted()) {
                    error = controller.stopInterpretation();
                    Toast.makeText(getContext(), "StopInterpretation " + error, Toast.LENGTH_LONG).show();
                } else {
                    updateInterpretation();
                    error = controller.startInterpretation();
                    Toast.makeText(getContext(), "StartInterpretation " + error, Toast.LENGTH_LONG).show();
                }
                dismiss();
                break;
            }
        }

    }

    private void updateInterpretation() {
        InMeetingInterpretationController controller = ZoomSDK.getInstance().getInMeetingService()
                .getInMeetingInterpretationController();

        List<IInterpreter> allList = controller.getInterpreterList();
        Map<Long,IInterpreter> allMap = new HashMap<>();


        List<InterpreterItem> oldList = new ArrayList<>();
        for (IInterpreter interpreter : allList) {
            InterpreterItem interpreterItem = new InterpreterItem(interpreter.getUserID(), interpreter.getLanguageID1(), interpreter.getLanguageID2());
            oldList.add(interpreterItem);
            allMap.put(interpreter.getUserID(),interpreter);
        }
        //newList
        List<InterpreterItem> newInterpreterList = new ArrayList<>();
        for (int i = 0, count = item_contain.getChildCount(); i < count; i++) {
            View item = item_contain.getChildAt(i);
            Spinner spinner = item.findViewById(R.id.interpreter);
            UserItemInfo userItemInfo = (UserItemInfo) spinner.getSelectedItem();

            Spinner spinner_lan1 = item.findViewById(R.id.lan_1);
            Spinner spinner_lan2 = item.findViewById(R.id.lan_2);

            InterpretationLanguageInfo languageInfo1 = (InterpretationLanguageInfo) spinner_lan1.getSelectedItem();
            InterpretationLanguageInfo languageInfo2 = (InterpretationLanguageInfo) spinner_lan2.getSelectedItem();
            if (languageInfo1 != languageInfo2) {
                InterpreterItem interpreterItem = new InterpreterItem(userItemInfo.getId(), languageInfo1.info.getLanguageID(), languageInfo2.info.getLanguageID());
                newInterpreterList.add(interpreterItem);
            }
        }

        List<InterpreterItem> removeList = new ArrayList<>();
        removeList.addAll(oldList);
        removeList.removeAll(newInterpreterList);


        List<InterpreterItem> addList = new ArrayList<>();
        addList.addAll(newInterpreterList);
        addList.removeAll(oldList);


        List<InterpreterItem> updateList = new ArrayList<>();
        List<InterpreterItem> tempUpdateList = new ArrayList<>();
        tempUpdateList.addAll(newInterpreterList);
        tempUpdateList.removeAll(addList);
        tempUpdateList.removeAll(removeList);
        for (InterpreterItem item : tempUpdateList) {
            IInterpreter interpreter = allMap.get(item.userId);
            if (interpreter.getLanguageID1() != item.languageID1 || interpreter.getLanguageID2() != item.languageID2) {
                updateList.add(item);
            }
        }

        for (InterpreterItem item : addList) {
            boolean ret = controller.addInterpreter(item.userId, item.languageID1, item.languageID2);
            Toast.makeText(getContext(), "addInterpreter:" + ret, Toast.LENGTH_SHORT).show();
        }

        for (InterpreterItem item : updateList) {
            boolean ret = controller.modifyInterpreter(item.userId, item.languageID1, item.languageID2);
            Toast.makeText(getContext(), "modifyInterpreter:" + ret, Toast.LENGTH_SHORT).show();
        }

        for (InterpreterItem item : removeList) {
            boolean ret = controller.removeInterpreter(item.userId);
            Toast.makeText(getContext(), "removeInterpreter:" + ret, Toast.LENGTH_SHORT).show();
        }
    }
}
