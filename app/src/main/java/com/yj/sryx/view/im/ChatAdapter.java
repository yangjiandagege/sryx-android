package com.yj.sryx.view.im;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yj.sryx.R;
import com.yj.sryx.SryxApp;
import com.yj.sryx.model.beans.ChatMessage;
import com.yj.sryx.utils.LogUtils;

import org.jivesoftware.smack.packet.Message;

import java.util.List;

/**
 * Created by eason.yang on 2017/7/19.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static final int ITEM_ME = 1;
    public static final int ITEM_OTHER = 2;

    private List<ChatMessage> mMessageList;
    private Context mContext;
    private LayoutInflater mInflater;
    private Drawable mMeHeaderDrawable;
    private Drawable mOtherHeaderDrawable;

    public ChatAdapter(Context context, List<ChatMessage> messageList, Drawable me, Drawable other) {
        mContext = context;
        mMessageList = messageList;
        mMeHeaderDrawable = me;
        mOtherHeaderDrawable = other;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mInflater = LayoutInflater.from(mContext);
        RecyclerView.ViewHolder holder = null;
        if(viewType == ITEM_ME){
            View view = mInflater.inflate(R.layout.item_me_message,parent,false);
            holder = new MeViewHolder(view);
        }else {
            View view = mInflater.inflate(R.layout.item_other_message,parent,false);
            holder = new OtherViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MeViewHolder){
            ((MeViewHolder) holder).header.setImageDrawable(mMeHeaderDrawable);
            ((MeViewHolder) holder).content.setText(mMessageList.get(position).getBody());
        }else {
            ((OtherViewHolder) holder).header.setImageDrawable(mOtherHeaderDrawable);
            ((OtherViewHolder) holder).content.setText(mMessageList.get(position).getBody());
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMessageList.get(position).getFrom().contains(SryxApp.sWxUser.getOpenid())?ITEM_ME:ITEM_OTHER;
    }

    public class MeViewHolder extends RecyclerView.ViewHolder {
        ImageView header;
        TextView  content;
        public MeViewHolder(View itemView) {
            super(itemView);
            header = (ImageView) itemView.findViewById(R.id.iv_header);
            content = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }

    public class OtherViewHolder extends RecyclerView.ViewHolder {
        ImageView header;
        TextView  content;
        public OtherViewHolder(View itemView) {
            super(itemView);
            header = (ImageView) itemView.findViewById(R.id.iv_header);
            content = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }
}
