package com.yj.sryx.model;

import com.yj.sryx.manager.httpRequest.subscribers.SubscriberOnNextListener;
import com.yj.sryx.model.beans.Game;
import com.yj.sryx.model.beans.Role;
import com.yj.sryx.model.beans.WxUser;

import java.util.List;

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

    void getGameById(String gameId, SubscriberOnNextListener<Game> callback);

    void getRolesInGame(String gameId, SubscriberOnNextListener<List<Role>> callback);

    void joinGameByCode(String gameCode, String playerId, String playerNickName, String playerAvatarUrl, SubscriberOnNextListener<String> callback);

    void getRoleByCode(String gameCode, String playerId, SubscriberOnNextListener<Role> callback);

    void cancleGame(Integer gameId, SubscriberOnNextListener<String> callback);

    void setRoleOut(Integer death, Integer roleId, Integer gameId, SubscriberOnNextListener<String> callback);

    void getMyGameRecordList(String playerId, SubscriberOnNextListener<List<Role>> callback);

    void getMyLastGame(String playerId, SubscriberOnNextListener<Game> callback);
}
