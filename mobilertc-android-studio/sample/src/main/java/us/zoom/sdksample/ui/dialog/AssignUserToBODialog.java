package us.zoom.sdksample.ui.dialog;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.List;

import us.zoom.androidlib.widget.ZMSimpleMenuItem;
import us.zoom.sdk.IBOCreator;
import us.zoom.sdk.IBOData;
import us.zoom.sdk.InMeetingBOController;
import us.zoom.sdk.InMeetingService;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.R;

public class AssignUserToBODialog extends BaseDialogFragment {
    private static final String ARG_BO_ID = "ARG_BO_ID";

    private String mBoId;

    private ListView mUserListView;
    private List<String> mUserList;
    private UserListAdapter mAdapter;

    private OnItemSelectedListener onItemSelectedListener;

    public static void show(@NonNull FragmentManager fm, String boId, OnItemSelectedListener listener) {
        Bundle args = new Bundle();
        args.putString(ARG_BO_ID, boId);
        AssignUserToBODialog f = new AssignUserToBODialog();
        f.setArguments(args);
        if (listener != null)
            f.setOnItemSelectedListener(listener);

        f.show(fm, AssignUserToBODialog.class.getName());
    }

    public AssignUserToBODialog() {
        setCancelable(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_assign_user_to_bo, container, false);
        mUserListView = view.findViewById(R.id.lv_users);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mBoId = bundle.getString(ARG_BO_ID);
        }
        InMeetingService inMeetingService = ZoomSDK.getInstance().getInMeetingService();
        InMeetingBOController boController = inMeetingService.getInMeetingBOController();
        IBOData boData = boController.getBODataHelper();
        mUserList = boData.getUnassginedUserList();
        mAdapter = new UserListAdapter(getActivity(), mUserList, boController, mBoId);
        mUserListView.setAdapter(mAdapter);
        return view;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected();
    }

    public static class UserListAdapter extends BaseAdapter {

        private Context context;
        private List<String> list;
        private InMeetingBOController boController;
        private String boId;

        public UserListAdapter(@NonNull Context context, @NonNull List<String> list, InMeetingBOController boController, String boId) {
            this.context = context;
            this.list = list;
            this.boController = boController;
            this.boId = boId;
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
            final String tag = "userListItem";
            View view;
            if (convertView != null && tag.equals(convertView.getTag())) {
                view = convertView;
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.bo_unassigned_user_list_item, parent, false);
                view.setTag(tag);
            }
            TextView tv_user_name = view.findViewById(R.id.tv_user_name);
            CheckBox check_select = view.findViewById(R.id.check_select);
            final String userId = list.get(position);
            IBOData boData = boController.getBODataHelper();
            if (boData != null)
                tv_user_name.setText(boData.getBOUserName(userId));
            check_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    IBOCreator iboCreator = boController.getBOCreatorHelper();
                    if (iboCreator != null) {
                        if (isChecked) {
                            Toast.makeText(context,
                                    iboCreator.assignUserToBO(userId, boId) ? "assign successfully" : "assign failed",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context,
                                    iboCreator.removeUserFromBO(userId, boId) ? "remove successfully" : "remove failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            return view;
        }
    }
}