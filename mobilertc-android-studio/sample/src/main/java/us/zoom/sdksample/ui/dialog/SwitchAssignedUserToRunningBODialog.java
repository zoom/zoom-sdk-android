package us.zoom.sdksample.ui.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import us.zoom.sdk.IBOAdmin;
import us.zoom.sdk.IBOCreator;
import us.zoom.sdk.IBOData;
import us.zoom.sdk.IBOMeeting;
import us.zoom.sdk.InMeetingBOController;
import us.zoom.sdk.InMeetingService;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.R;

public class SwitchAssignedUserToRunningBODialog extends BaseDialogFragment {
    private static final String ARG_BO_USER_ID = "ARG_BO_USER_ID";
    private static final String ARG_CURRENT_BO_ID = "ARG_CURRENT_BO_ID";

    private String mCurrentBoId;
    private String mBoUserId;

    private ListView mOtherBoLv;
    private List<IBOMeeting> mOtherBoList = new ArrayList<>();
    private OtherBoListAdapter mAdapter;

    private OnUserRemovedListener onUserRemovedListener;

    public static void show(@NonNull FragmentManager fm, String boUserId, String currentBoId, OnUserRemovedListener listener) {
        Bundle args = new Bundle();
        args.putString(ARG_BO_USER_ID, boUserId);
        args.putString(ARG_CURRENT_BO_ID, currentBoId);
        SwitchAssignedUserToRunningBODialog f = new SwitchAssignedUserToRunningBODialog();
        f.setArguments(args);
        if (listener != null)
            f.setOnUserRemovedListener(listener);

        f.show(fm, SwitchAssignedUserToRunningBODialog.class.getName());
    }

    public SwitchAssignedUserToRunningBODialog() {
        setCancelable(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_switch_assigned_user_to_running_bo, container, false);
        mOtherBoLv = view.findViewById(R.id.lv_bos);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCurrentBoId = bundle.getString(ARG_CURRENT_BO_ID);
            mBoUserId = bundle.getString(ARG_BO_USER_ID);
        }
        InMeetingService inMeetingService = ZoomSDK.getInstance().getInMeetingService();
        final InMeetingBOController boController = inMeetingService.getInMeetingBOController();
        IBOData boData = boController.getBODataHelper();
        List<String> bIds = (boData == null) ? new ArrayList<String>() : boData.getBOMeetingIDList();
        for (String id : bIds) {
            if (mCurrentBoId.equalsIgnoreCase(id))
                continue;
            IBOMeeting iboMeeting = boData.getBOMeetingByID(id);
            if (iboMeeting != null)
                mOtherBoList.add(iboMeeting);
        }
        mAdapter = new OtherBoListAdapter(getActivity(), mOtherBoList);
        mOtherBoLv.setAdapter(mAdapter);
        mOtherBoLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IBOMeeting boMeeting = mOtherBoList.get(position);
                IBOAdmin boAdmin = boController.getBOAdminHelper();
                IBOCreator iboCreator = boController.getBOCreatorHelper();
                boolean sucess = false;
                if (boController.isBOStarted()) {
                    if (boAdmin != null) {
                        sucess = boAdmin.switchAssignedUserToRunningBO(mBoUserId, boMeeting.getBoId());
                    }
                } else {
                    if (iboCreator != null) {
                        if(iboCreator.removeUserFromBO(mBoUserId, mCurrentBoId)) {
                            sucess = iboCreator.assignUserToBO(mBoUserId, boMeeting.getBoId());
                        }
                    }
                }

                Toast.makeText(getContext(),
                        sucess ? "move successfully" : "move failed",
                        Toast.LENGTH_SHORT).show();
                if(sucess) {
                    if(onUserRemovedListener != null)
                        onUserRemovedListener.onUserRemoved();
                }
                dismiss();
            }
        });
        return view;
    }

    public void setOnUserRemovedListener(OnUserRemovedListener onUserRemovedListener) {
        this.onUserRemovedListener = onUserRemovedListener;
    }

    public interface OnUserRemovedListener {
        void onUserRemoved();
    }

    public static class OtherBoListAdapter extends BaseAdapter {

        private Context context;
        private List<IBOMeeting> list;

        public OtherBoListAdapter(@NonNull Context context, @NonNull List<IBOMeeting> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        @Nullable
        public Object getItem(int position) {
            if (position >= 0)
                return list.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final String tag = "otherBoListItem";
            View view;
            if (convertView != null && tag.equals(convertView.getTag())) {
                view = convertView;
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.other_bo_list_item, parent, false);
                view.setTag(tag);
            }
            IBOMeeting boMeeting = list.get(position);
            TextView tv_bo_name = view.findViewById(R.id.tv_bo_name);
            tv_bo_name.setText(boMeeting.getBoName());
            return view;
        }
    }
}