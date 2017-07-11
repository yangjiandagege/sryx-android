package com.yj.sryx.model;

import android.content.Context;

import com.yj.sryx.manager.httpRequest.HttpResultFunc;
import com.yj.sryx.manager.httpRequest.RetrofitSingleton;
import com.yj.sryx.manager.httpRequest.subscribers.ProgressSubscriber;
import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.beans.WxUser;
import com.yj.sryx.model.service.SryxService;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by eason.yang on 2017/7/11.
 */

public class SryxModelImpl implements SryxModel {
    private Context mContext;
    private SryxService mService;

    public SryxModelImpl(Context context) {
        mContext = context;
        mService = RetrofitSingleton.getInstance().create(SryxService.class);
    }

    /**
     * 更新玩家信息
     * @param user
     * @param callback
     */
    public void updatePlayer(WxUser user, final SubscriberOnNextListener<String> callback){
        mService.updatePlayer(user.getOpenid(),user.getHeadimgurl(),
                user.getNickname(), user.getSex(), user.getLanguage(),
                user.getCountry(), user.getProvince(), user.getCity())
                .map(new HttpResultFunc<String>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ProgressSubscriber(callback, mContext));
    }

    /**
     * 创建游戏
     * @param gameOwnerId
     * @param gameOwnerAvatarUrl
     * @param gameOwnerNickName
     * @param killerNum
     * @param policeNum
     * @param citizenNum
     * @param callback
     */
    @Override
    public void createGame(String gameOwnerId,
                           String gameOwnerAvatarUrl,
                           String gameOwnerNickName,
                           Integer killerNum,
                           Integer policeNum,
                           Integer citizenNum,
                           final SubscriberOnNextListener<String> callback) {
        mService.createGame(gameOwnerId, gameOwnerAvatarUrl, gameOwnerNickName, killerNum, policeNum, citizenNum)
                .map(new HttpResultFunc<String>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ProgressSubscriber(callback, mContext));
    }
}