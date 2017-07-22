package com.yj.sryx.view.im;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yj.sryx.R;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.AsmackModel;
import com.yj.sryx.model.AsmackModelImpl;
import com.yj.sryx.model.beans.Contact;

import org.jivesoftware.smack.RosterEntry;

import java.util.List;

/**
 * Created by zhangxutong .
 * Date: 16/08/28
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
//    private final int NEW_FRIENDS_POS = 0;
    private final int GROUP_CHAT_POS = 0;

    protected Context mContext;
    protected List<Contact> mDatas;
    protected LayoutInflater mInflater;
    private AsmackModel mAsmackModel;
    private OnItemClickListener mOnItemClickListener;

    interface OnItemClickListener{
        void OnNewFriendsClick();
        void OnGroupChatClick();
        void OnItemClick(int position);
    }

    public ContactsAdapter(Context context, List<Contact> datas) {
        this.mContext = context;
        this.mDatas = datas;
        mInflater = LayoutInflater.from(context);
        mAsmackModel = new AsmackModelImpl(context);
    }

    public List<Contact> getDatas() {
        return mDatas;
    }

    public ContactsAdapter setDatas(List<Contact> datas) {
        mDatas = datas;
        return this;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mOnItemClickListener = listener;
    }

    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_list_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(final ContactsAdapter.ViewHolder holder, final int position) {
        final Contact contact = mDatas.get(position);
        holder.tvName.setText(contact.getName());
        if(position == GROUP_CHAT_POS){
            holder.ivHeader.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.chat_group));
        }else {
            mAsmackModel.getHeaderPic(contact.getUser(), new HeaderPicSetListener(position, holder.ivHeader));
        }
        holder.llContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null){
                    switch (position){
//                        case NEW_FRIENDS_POS:
//                            mOnItemClickListener.OnNewFriendsClick();
//                            break;
                        case GROUP_CHAT_POS:
                            mOnItemClickListener.OnGroupChatClick();
                            break;
                        default:
                            mOnItemClickListener.OnItemClick(position);
                            break;
                    }

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivHeader;
        LinearLayout llContact;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivHeader = (ImageView) itemView.findViewById(R.id.iv_header);
            llContact = (LinearLayout) itemView.findViewById(R.id.ll_contact);
        }
    }

    class HeaderPicSetListener implements SubscriberOnNextListener<Drawable> {
        int position;
        ImageView imageHeaderPic;

        public HeaderPicSetListener(int position, ImageView imageView) {
            this.position = position;
            this.imageHeaderPic = imageView;
        }

        @Override
        public void onSuccess(Drawable drawable) {
            imageHeaderPic.setImageDrawable(drawable);
        }

        @Override
        public void onError(String msg) {

        }
    }
}
