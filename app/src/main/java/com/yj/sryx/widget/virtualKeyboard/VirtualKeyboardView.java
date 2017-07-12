package com.yj.sryx.widget.virtualKeyboard;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;


import com.yj.sryx.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 虚拟键盘
 */
public class VirtualKeyboardView extends RelativeLayout {

    private Context mContext;
    private GridView mGridView;    //用GrideView布局键盘，其实并不是真正的键盘，只是模拟键盘的功能
    private ArrayList<Map<String, String>> mValueList;
    //因为要用Adapter中适配，用数组不能往adapter中填充
    private RelativeLayout mLayoutBack;
    private EditText mEditText;
    private View mView;
    private Animation mEnterAnim;
    private Animation mExitAnim;
    private OnTextInputListener mInputListener;

    public VirtualKeyboardView(Context context) {
        this(context, null);
    }

    public VirtualKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mView = View.inflate(context, R.layout.layout_virtual_keyboard, null);
        mLayoutBack = (RelativeLayout) mView.findViewById(R.id.layoutBack);
        mGridView = (GridView) mView.findViewById(R.id.gv_keybord);

        initValueList();
        setupView();
        addView(mView);      //必须要，不然不显示控件
        initAnim();
    }

    /**
     * 数字键盘显示动画
     */
    private void initAnim() {
        mEnterAnim = AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_in);
        mExitAnim = AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_out);
    }

    public void bindKeyboard(EditText editText, Window window, OnTextInputListener listener){
        mEditText = editText;
        // 设置不调用系统键盘
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            mEditText.setInputType(InputType.TYPE_NULL);
        } else {
            window.setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus",
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(mEditText, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mLayoutBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.startAnimation(mExitAnim);
                mView.setVisibility(View.GONE);
            }
        });

        mEditText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.setFocusable(true);
                mView.setFocusableInTouchMode(true);

                mView.startAnimation(mEnterAnim);
                mView.setVisibility(View.VISIBLE);
            }
        });
        mInputListener = listener;
        mGridView.setOnItemClickListener(onItemClickListener);
    }

    private void initValueList() {
        mValueList = new ArrayList<>();
        // 初始化按钮上应该显示的数字
        for (int i = 1; i < 13; i++) {
            Map<String, String> map = new HashMap<>();
            if (i < 10) {
                map.put("name", String.valueOf(i));
            } else if (i == 10) {
                map.put("name", ".");
            } else if (i == 11) {
                map.put("name", String.valueOf(0));
            } else if (i == 12) {
                map.put("name", "");
            }
            mValueList.add(map);
        }
    }

    private void setupView() {
        KeyBoardAdapter keyBoardAdapter = new KeyBoardAdapter(mContext, mValueList);
        mGridView.setAdapter(keyBoardAdapter);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            if (position < 11 && position != 9) {    //点击0~9按钮
                String amount = mEditText.getText().toString().trim();
                amount = amount + mValueList.get(position).get("name");
                mEditText.setText(amount);
                Editable ea = mEditText.getText();
                mEditText.setSelection(ea.length());
                mInputListener.onTextInput(ea.toString());
            } else {
                if (position == 9) {      //点击退格键
                    String amount = mEditText.getText().toString().trim();
                    if (!amount.contains(".")) {
                        amount = amount + mValueList.get(position).get("name");
                        mEditText.setText(amount);

                        Editable ea = mEditText.getText();
                        mEditText.setSelection(ea.length());
                    }
                }

                if (position == 11) {      //点击退格键
                    String amount = mEditText.getText().toString().trim();
                    if (amount.length() > 0) {
                        amount = amount.substring(0, amount.length() - 1);
                        mEditText.setText(amount);

                        Editable ea = mEditText.getText();
                        mEditText.setSelection(ea.length());
                    }
                }
            }
        }
    };
}
