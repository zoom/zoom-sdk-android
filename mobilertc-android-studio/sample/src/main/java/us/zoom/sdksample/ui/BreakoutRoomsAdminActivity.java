package us.zoom.sdksample.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import us.zoom.androidlib.app.ZMActivity;
import us.zoom.sdk.IBOAdmin;
import us.zoom.sdk.IBOAssistant;
import us.zoom.sdk.IBOAttendee;
import us.zoom.sdk.IBOCreator;
import us.zoom.sdk.IBOData;
import us.zoom.sdk.IBODataEvent;
import us.zoom.sdk.IBOMeeting;
import us.zoom.sdk.InMeetingBOController;
import us.zoom.sdk.InMeetingBOControllerListener;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.R;
import us.zoom.sdksample.ui.dialog.AssignNewUserToRunningBODialog;
import us.zoom.sdksample.ui.dialog.AssignUserToBODialog;

public class BreakoutRoomsAdminActivity extends ZMActivity implements InMeetingBOControllerListener {
    public static final int REQUEST_ID_BO_EDIT = 1;
    final String TAG = BreakoutRoomsAdminActivity.class.getSimpleName();
    InMeetingBOController mBoController;

    ListView mBoLv;
    ListView mUnassignedUsersLv;
    Button mBtnOpenBo;
    Button mBtnAddBo;
    BoListAdapter mAdapter;
    BoNewUserListAdapter mNewUsersAdapter;
    int mBoCount = 0;

    List<IBOMeeting> mList = new ArrayList<>();
    List<String> mNewUserList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bo_admin);
        mBoLv = (ListView) findViewById(R.id.lv_bo);
        mUnassignedUsersLv = (ListView) findViewById(R.id.lv_unassigned_users);
        mBtnOpenBo = (Button) findViewById(R.id.btn_open_bo);
        mBtnAddBo = (Button) findViewById(R.id.btn_add_bo);

        mBoController = ZoomSDK.getInstance().getInMeetingService().getInMeetingBOController();
        mBoController.addListener(this);

        mAdapter = new BoListAdapter(this, mList, mBoController);
        mNewUsersAdapter = new BoNewUserListAdapter(this, mNewUserList, mBoController);
        IBOData iboData = mBoController.getBODataHelper();
        if (iboData != null) {
            List<String> bIds = iboData.getBOMeetingIDList();
            if (bIds == null || bIds.size() == 0) {
                Log.d(TAG, "first create break room");
                createBO();
            } else {
                Log.d(TAG, "break rooms already exists");
                mBoCount = bIds.size();
                for (String id : bIds) {
                    IBOMeeting iboMeeting=iboData.getBOMeetingByID(id);
                    if(null!=iboMeeting)
                    {
                        mList.add(iboMeeting);
                    }
                }
            }
        }
        mBoLv.setAdapter(mAdapter);
        mUnassignedUsersLv.setAdapter(mNewUsersAdapter);

        mBoLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(BreakoutRoomsAdminActivity.this, BoEditActivity.class);
                intent.putExtra(BoEditActivity.ARG_BO_ID, mList.get(position).getBoId());
                startActivityForResult(intent, REQUEST_ID_BO_EDIT);
            }
        });
        boolean isBOStarted = mBoController.isBOStarted();
        mBtnOpenBo.setText(isBOStarted ? "Close All BO" : "Open All BO");
        mBtnAddBo.setEnabled(!isBOStarted);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registBoDataEvent();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegistBoDataEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBoController.removeListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ID_BO_EDIT:
                IBOData iboData = mBoController.getBODataHelper();
                if (iboData != null) {
                    mList.clear();
                    List<String> bIds = iboData.getBOMeetingIDList();
                    if (bIds != null && bIds.size() > 0) {
                        mBoCount = bIds.size();
                        for (String id : bIds) {
                            IBOMeeting iboMeeting=iboData.getBOMeetingByID(id);
                            if(null!=iboMeeting)
                            {
                                mList.add(iboMeeting);
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    mBtnOpenBo.setEnabled(mList.size() > 0);
                }
                break;
        }
    }

    private void createBO() {
        IBOCreator iboCreator = mBoController.getBOCreatorHelper();
        if (iboCreator != null) {
            mBoCount++;
            String bId = iboCreator.createBO("Breakout Room " + mBoCount);
            IBOData iboData = mBoController.getBODataHelper();
            if (iboData != null)
            {
                IBOMeeting iboMeeting=iboData.getBOMeetingByID(bId);
                if(null!=iboMeeting)
                {
                    mList.add(iboMeeting);
                }
            }

            mAdapter.notifyDataSetChanged();
            mBtnOpenBo.setEnabled(mList.size() > 0);
        }
    }

    public void onClose(View view) {
        finish();
    }

    public void onClickAddBO(View view) {
        createBO();
    }

    public void onClickStartBO(View view) {
        IBOAdmin boAdmin = mBoController.getBOAdminHelper();
        if (boAdmin != null) {
            if (mBoController.isBOStarted()) {
                if (boAdmin.stopBO()) {
                    mBtnOpenBo.setText("Open All BO");
                    mBtnAddBo.setEnabled(true);
                    mAdapter.notifyDataSetChanged();
                }
            } else {
                if (boAdmin.startBO()) {
                    mBtnOpenBo.setText("Close All BO");
                    mBtnAddBo.setEnabled(false);
                    mAdapter.notifyDataSetChanged();
                }
            }
            refreshUnassignedNewUsersList();
        }
    }

    private void refreshUnassignedNewUsersList() {
        mNewUserList.clear();
        if (!mBoController.isBOStarted()) {
            mUnassignedUsersLv.setVisibility(View.GONE);
            mNewUsersAdapter.notifyDataSetChanged();
            return;
        }
        IBOData boData = mBoController.getBODataHelper();
        List<String> list = boData == null ? null : boData.getUnassginedUserList();
        if (list != null && list.size() > 0) {
            mUnassignedUsersLv.setVisibility(View.VISIBLE);
            mNewUserList.addAll(list);
        } else {
            mUnassignedUsersLv.setVisibility(View.GONE);
        }
        mNewUsersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHasCreatorRightsNotification(IBOCreator iboCreator) {
        Log.d(TAG, "onHasCreatorRightsNotification");
        mList.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHasAdminRightsNotification(IBOAdmin iboAdmin) {
        Log.d(TAG, "onHasAdminRightsNotification");
    }

    @Override
    public void onHasAssistantRightsNotification(IBOAssistant iboAssistant) {
        Log.d(TAG, "onHasAssistantRightsNotification");
    }

    @Override
    public void onHasAttendeeRightsNotification(IBOAttendee iboAttendee) {
        Log.d(TAG, "onHasAttendeeRightsNotification");
    }

    @Override
    public void onHasDataHelperRightsNotification(IBOData iboData) {
        Log.d(TAG, "onHasDataHelperRightsNotification");
    }

    @Override
    public void onLostCreatorRightsNotification() {
        Log.d(TAG, "onLostCreatorRightsNotification");
    }

    @Override
    public void onLostAdminRightsNotification() {
        Log.d(TAG, "onLostAdminRightsNotification");
    }

    @Override
    public void onLostAssistantRightsNotification() {
        Log.d(TAG, "onLostAssistantRightsNotification");
    }

    @Override
    public void onLostAttendeeRightsNotification() {
        Log.d(TAG, "onLostAttendeeRightsNotification");
    }

    @Override
    public void onLostDataHelperRightsNotification() {
        Log.d(TAG, "onLostDataHelperRightsNotification");
    }

    public static class BoListAdapter extends BaseAdapter {
        private Context context;
        private List<IBOMeeting> list;
        private InMeetingBOController boController;

        public BoListAdapter(@NonNull Context context, @NonNull List<IBOMeeting> list, InMeetingBOController boController) {
            this.context = context;
            this.list = list;
            this.boController = boController;
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
            final String tag = "boListItem";
            View view;
            if (convertView != null && tag.equals(convertView.getTag())) {
                view = convertView;
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.bo_list_item, parent, false);
                view.setTag(tag);
            }
            final IBOMeeting boMeeting = list.get(position);
            TextView tv_bo_name = (TextView) view.findViewById(R.id.tv_bo_name);
            Button btn_assign = (Button) view.findViewById(R.id.btn_assign);
            tv_bo_name.setText(boMeeting.getBoName());
            if (boController.isBOStarted()) {
                btn_assign.setText("Join");
            } else {
                btn_assign.setText("Assign");
            }
            btn_assign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (boController.isBOStarted()) {
                        IBOAssistant iboAssistant = boController.getBOAssistantHelper();
                        if (iboAssistant != null) {
                            boolean success = iboAssistant.joinBO(boMeeting.getBoId());
                            Toast.makeText(context, success ? "Join successfully" : "Join failed", Toast.LENGTH_SHORT).show();
                            if(success) {
                                if(context instanceof BreakoutRoomsAdminActivity) {
                                    ((BreakoutRoomsAdminActivity)context).finish();
                                }
                            }
                        }
                    } else {
                        IBOData boData = boController.getBODataHelper();
                        if (boData != null) {
                            List<String> unassignedUsers = boData.getUnassginedUserList();
                            if (unassignedUsers == null || unassignedUsers.size() == 0) {
                                Toast.makeText(context, "All participants have been assigned to Breakout Rooms.", Toast.LENGTH_SHORT).show();
                            } else {
                                if (context instanceof ZMActivity) {
                                    FragmentManager fm = ((ZMActivity) context).getSupportFragmentManager();
                                    AssignUserToBODialog.show(fm, boMeeting.getBoId(), null);
                                }
                            }
                        }
                    }
                }
            });
            return view;
        }
    }

    public static class BoNewUserListAdapter extends BaseAdapter {
        private Context context;
        private List<String> list;
        private InMeetingBOController boController;

        public BoNewUserListAdapter(@NonNull Context context, @NonNull List<String> list, InMeetingBOController boController) {
            this.context = context;
            this.list = list;
            this.boController = boController;
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
            final String tag = "boNewUserListItem";
            View view;
            if (convertView != null && tag.equals(convertView.getTag())) {
                view = convertView;
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.bo_new_user_list_item, parent, false);
                view.setTag(tag);
            }
            TextView tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
            Button btn_assign_to = (Button) view.findViewById(R.id.btn_assign_to);
            IBOData iboData = boController.getBODataHelper();
            if (iboData != null)
                tv_user_name.setText(iboData.getBOUserName(list.get(position)));
            btn_assign_to.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof ZMActivity) {
                        FragmentManager fm = ((ZMActivity)context).getSupportFragmentManager();
                        AssignNewUserToRunningBODialog.show(fm, list.get(position), null);
                    }
                }
            });
            return view;
        }
    }

    private void registBoDataEvent(){
        IBOData iboData = mBoController.getBODataHelper();
        if(iboData != null) {
            iboData.setEvent(iboDataEvent);
        }
    }

    private void unRegistBoDataEvent(){
        IBOData iboData = mBoController.getBODataHelper();
        if(iboData != null) {
            iboData.setEvent(null);
        }
    }

    private IBODataEvent iboDataEvent = new IBODataEvent() {
        @Override
        public void onBOInfoUpdated(String strBOID) {
            refreshUnassignedNewUsersList();
        }

        @Override
        public void onUnAssignedUserUpdated() {
            refreshUnassignedNewUsersList();
        }
    };
}
