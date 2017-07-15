package com.yj.sryx.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;
import com.yj.sryx.R;
import com.yj.sryx.model.beans.Role;

import java.util.List;

public class GameRecordJudgeStickyGridAdapter extends BaseAdapter implements
		StickyGridHeadersSimpleAdapter {
	private List<Role> mRoleList;
	private LayoutInflater mInflater;
    private Context mContext;

	public GameRecordJudgeStickyGridAdapter(Context context, List<Role> list) {
		this.mRoleList = list;
		mInflater = LayoutInflater.from(context);
        mContext = context;
	}

	@Override
	public int getCount() {
		return mRoleList.size();
	}

	@Override
	public Role getItem(int position) {
		return mRoleList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
            viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_list_record, parent, false);
            viewHolder.tvRoom = (TextView) convertView.findViewById(R.id.tv_room);
            viewHolder.tvConfig = (TextView) convertView.findViewById(R.id.tv_config);
            viewHolder.tvResult = (TextView) convertView.findViewById(R.id.tv_result);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
			convertView.setTag(viewHolder);
		} else {
            viewHolder = (ViewHolder) convertView.getTag();
		}
		Role role = getItem(position);
        viewHolder.tvRoom.setText(role.getRoleName());
		viewHolder.tvConfig.setText(role.getRemark());
        switch (role.getVictorySide()){
            case 0:
                viewHolder.tvResult.setText("杀手集团");
                viewHolder.tvResult.setTextColor(ContextCompat.getColor(mContext, R.color.color_red_500));
                viewHolder.tvResult.setBackground(ContextCompat.getDrawable(mContext,R.drawable.fail_label_bg));
                break;
            case 1:
                viewHolder.tvResult.setText("正义联盟");
                viewHolder.tvResult.setTextColor(ContextCompat.getColor(mContext, R.color.color_green_500));
                viewHolder.tvResult.setBackground(ContextCompat.getDrawable(mContext,R.drawable.victory_label_bg));
                break;
            case 2:
                viewHolder.tvResult.setText("平局");
                break;
        }
        viewHolder.tvTime.setText(role.getTime());
		return convertView;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder headerHolder;
		if (convertView == null) {
            headerHolder = new HeaderViewHolder();
			convertView = mInflater.inflate(R.layout.item_list_record_header, parent, false);
            headerHolder.tvHeaderName = (TextView) convertView
					.findViewById(R.id.tv_header);
			convertView.setTag(headerHolder);
		} else {
            headerHolder = (HeaderViewHolder) convertView.getTag();
		}
        Role role = getItem(position);
        headerHolder.tvHeaderName.setText(role.getDate());
		return convertView;
	}

	public static class ViewHolder {
		public TextView tvRoom;
        public TextView tvConfig;
        public TextView tvResult;
        public TextView tvTime;
	}

	public static class HeaderViewHolder {
		public TextView tvHeaderName;
	}

	@Override
	public long getHeaderId(int position) {
		return getItem(position).getDate().hashCode();
	}
}
