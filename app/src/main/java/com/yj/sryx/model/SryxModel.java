package com.yj.sryx.model;

import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.beans.WxUser;

/**
 * Created by eason.yang on 2017/7/11.
 */

public interface SryxModel {
    void createGame(String gameOwnerId,
                    String gameOwnerAvatarUrl,
                    String gameOwnerNickName,
                    Integer killerNum,
                    Integer policeNum,
                    Integer citizenNum,
                    SubscriberOnNextListener<String> callback);

    void updatePlayer(WxUser user, SubscriberOnNextListener<String> callback);
}
