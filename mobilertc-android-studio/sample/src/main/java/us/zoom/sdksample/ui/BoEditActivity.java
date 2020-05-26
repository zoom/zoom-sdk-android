package us.zoom.sdksample.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

import us.zoom.androidlib.app.ZMActivity;
import us.zoom.sdk.IBOCreator;
import us.zoom.sdk.IBOData;
import us.zoom.sdk.IBODataEvent;
import us.zoom.sdk.InMeetingBOController;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdksample.R;
import us.zoom.sdksample.ui.dialog.SwitchAssignedUserToRunningBODialog;

public class BoEditActivity extends ZMActivity {
    public static final String ARG_BO_ID = "ARG_BO_ID";
    final String TAG = BoEditActivity.class.getSimpleName();
    InMeetingBOController mBoController;

    EditText mEdtxBoName;
    ListView mBoUserLv;
    BoUserListAdapter mAdapter;
    String mBoId = "";
    Button mBtnSaveBoName, mBtnDeleteBo;
    List<String> mList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bo_edit);
        mEdtxBoName = (EditText) findViewById(R.id.edtx_bo_name);
        mBtnDeleteBo = (Button) findViewById(R.id.btn_delete_bo);
        mBtnSaveBoName = (Button) findViewById(R.id.btn_save_bo_name);
        mBoUserLv = (ListView) findViewById(R.id.lv_bo_users);

        mBoId = getIntent().getStringExtra(ARG_BO_ID);
        mBoController = ZoomSDK.getInstance().getInMeetingService().getInMeetingBOController();
        IBOData iboData = mBoController.getBODataHelper();
        if(iboData != null) {
            mEdtxBoName.setText(iboData.getBOMeetingByID(mBoId).getBoName());
            List<String> bIds = iboData.getBOMeetingIDList();
            mAdapter = new BoUserListAdapter(this, mList, mBoController, mBoId, bIds != null && bIds.size() > 1);
        }

        mBoUserLv.setAdapter(mAdapter);

        refreshBoUserList();

        boolean isBOStarted = mBoController.isBOStarted();
        mBtnDeleteBo.setEnabled(!isBOStarted);
        mBtnSaveBoName.setEnabled(!isBOStarted);
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

    public void onClose(View view) {
        finishWithResult();
    }

    public void onClickDeleteBO(View view) {
        IBOCreator iboCreator = mBoController.getBOCreatorHelper();
        if (iboCreator != null && iboCreator.removeBO(mBoId)) {
            finishWithResult();
        }
    }

    public void onClickSaveBoName(View view) {
        IBOCreator iboCreator = mBoController.getBOCreatorHelper();
        if (iboCreator != null && iboCreator.updateBOName(mBoId, mEdtxBoName.getText().toString()))
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
    }

    private void finishWithResult(){
        Intent intent = new Intent(this, BreakoutRoomsAdminActivity.class);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static class BoUserListAdapter extends BaseAdapter {
        private Context context;
        private List<String> list;
        private InMeetingBOController boController;
        private boolean canMove;
        private String boId;

        public BoUserListAdapter(@NonNull Context context, @NonNull List<String> list, InMeetingBOController boController, String boId, boolean canMove) {
            this.context = context;
            this.list = list;
            this.boController = boController;
            this.boId = boId;
            this.canMove = canMove;
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
            final String tag = "boUserListItem";
            View view;
            if (convertView != null && tag.equals(convertView.getTag())) {
                view = convertView;
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.bo_user_list_item, parent, false);
                view.setTag(tag);
            }
            TextView tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
            Button btn_move_to = (Button) view.findViewById(R.id.btn_move_to);
            btn_move_to.setVisibility(canMove ? View.VISIBLE : View.GONE);
            IBOData iboData = boController.getBODataHelper();
            if (iboData != null)
                tv_user_name.setText(iboData.getBOUserName(list.get(position)));
            btn_move_to.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof ZMActivity) {
                        FragmentManager fm = ((ZMActivity)context).getSupportFragmentManager();
                        SwitchAssignedUserToRunningBODialog.show(fm, list.get(position), boId, null);
                    }
                }
            });
            return view;
        }
    }

    private void refreshBoUserList(){
        IBOData iboData = mBoController.getBODataHelper();
        mList.clear();
        if(iboData != null) {
            List<String> users = iboData.getBOMeetingByID(mBoId).getBoUserList();
            if (users != null && users.size() > 0)
                mList.addAll(users);
        }
        mAdapter.notifyDataSetChanged();
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
            if(mBoId.equalsIgnoreCase(strBOID)){
                refreshBoUserList();
            }
        }

        @Override
        public void onUnAssignedUserUpdated() {
        }
    };
}
