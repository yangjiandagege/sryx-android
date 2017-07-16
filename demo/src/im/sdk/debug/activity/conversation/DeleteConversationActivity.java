package im.sdk.debug.activity.conversation;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import im.sdk.debug.R;

public class DeleteConversationActivity extends Activity implements View.OnClickListener {

    private EditText mEt_username;
    private TextView mTv_info;
    private EditText mEt_group_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_delete_conversation);

        mEt_username = (EditText) findViewById(R.id.et_username);
        mTv_info = (TextView) findViewById(R.id.tv_info);
        mEt_group_id = (EditText) findViewById(R.id.et_group_id);
        Button bt_deleteMessage = (Button) findViewById(R.id.bt_delete_message);
        Button bt_singleDelete = (Button) findViewById(R.id.bt_single_delete);
        Button bt_groupDelete = (Button) findViewById(R.id.bt_group_delete);
        Button bt_getConversation = (Button) findViewById(R.id.bt_get_conversation);
        Button bt_getMessage = (Button) findViewById(R.id.bt_get_message);

        bt_deleteMessage.setOnClickListener(this);
        bt_singleDelete.setOnClickListener(this);
        bt_groupDelete.setOnClickListener(this);
        bt_getConversation.setOnClickListener(this);
        bt_getMessage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_delete_message:
                mTv_info.setText("");
                String deleteMessage = mEt_username.getText().toString();
                String deleteMessageId = mEt_group_id.getText().toString();
                if (!TextUtils.isEmpty(deleteMessage) && TextUtils.isEmpty(deleteMessageId)) {
                    Conversation singleConversation = JMessageClient.getSingleConversation(deleteMessage);
                    if (singleConversation == null) {
                        Toast.makeText(getApplicationContext(), "会话为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    boolean b = singleConversation.deleteAllMessage();
                    Toast.makeText(getApplicationContext(), "删除" + b, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(deleteMessage) && !TextUtils.isEmpty(deleteMessageId)) {
                    long deleteId = Long.parseLong(deleteMessageId);
                    Conversation groupConversation = JMessageClient.getGroupConversation(deleteId);
                    if (groupConversation == null) {
                        Toast.makeText(getApplicationContext(), "会话为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    boolean b = groupConversation.deleteAllMessage();
                    Toast.makeText(getApplicationContext(), "删除" + b, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "输入参数错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case R.id.bt_single_delete:
                mTv_info.setText("");
                String deleteSingle = mEt_username.getText().toString();
                if (TextUtils.isEmpty(deleteSingle)) {
                    Toast.makeText(getApplicationContext(), "请输入userName", Toast.LENGTH_SHORT).show();
                    return;
                }
                JMessageClient.deleteSingleConversation(deleteSingle);
                Toast.makeText(getApplicationContext(), "删除单聊会话成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_group_delete:
                mTv_info.setText("");
                String deleteGroup = mEt_group_id.getText().toString();
                if (TextUtils.isEmpty(deleteGroup)) {
                    Toast.makeText(getApplicationContext(), "请输入群组id", Toast.LENGTH_SHORT).show();
                    return;
                }
                long gid = Long.parseLong(deleteGroup);
                JMessageClient.deleteGroupConversation(gid);
                Toast.makeText(getApplicationContext(), "删除群聊会话成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_get_conversation:
                String getConversationId = mEt_group_id.getText().toString();
                String getConversation = mEt_username.getText().toString();
                Conversation conversation = null;
                if (!TextUtils.isEmpty(getConversation) && TextUtils.isEmpty(getConversationId)) {
                    conversation = JMessageClient.getSingleConversation(getConversation);
                } else if (TextUtils.isEmpty(getConversation) && !TextUtils.isEmpty(getConversationId)) {
                    long groupId = Long.parseLong(getConversationId);
                    conversation = JMessageClient.getGroupConversation(groupId);
                } else {
                    Toast.makeText(getApplicationContext(), "输入userName或groupID", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (conversation != null) {
                    mTv_info.setText("");
                    mTv_info.append("getType = " + conversation.getType() + "\ngetId = " + conversation.getTargetId());
                } else {
                    mTv_info.setText("");
                    mTv_info.append("会话为null");
                }
                break;
            case R.id.bt_get_message:
                String getMessage = mEt_username.getText().toString();
                String getGroup = mEt_group_id.getText().toString();
                if (!TextUtils.isEmpty(getMessage) && TextUtils.isEmpty(getGroup)) {
                    Conversation getMessageConversation = JMessageClient.getSingleConversation(getMessage);
                    getAllMessage(getMessageConversation);
                } else if (TextUtils.isEmpty(getMessage) && !TextUtils.isEmpty(getGroup)) {
                    long groupId = Long.parseLong(getGroup);
                    Conversation getMessageConversation = JMessageClient.getGroupConversation(groupId);
                    getAllMessage(getMessageConversation);
                } else {
                    Toast.makeText(getApplicationContext(), "输入相关参数有误", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            default:
                break;
        }
    }

    private void getAllMessage(Conversation getMessageConversation) {
        if (getMessageConversation == null) {
            Toast.makeText(getApplicationContext(), "会话为空", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Message> allMessage = getMessageConversation.getAllMessage();
        if (allMessage != null) {
            mTv_info.setText("");
            StringBuilder sb = new StringBuilder();
            for (Message msg : allMessage) {
                sb.append("消息ID = " + msg.getId());
                sb.append("~~~消息类型 = " + msg.getContentType());
                sb.append("\n");
            }
            mTv_info.append("getAllMessage = " + "\n" + sb.toString());
        }else {
            Toast.makeText(DeleteConversationActivity.this, "未能获取到消息", Toast.LENGTH_SHORT).show();
        }
    }

}
