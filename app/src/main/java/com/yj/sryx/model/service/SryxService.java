package com.yj.sryx.model.service;

import com.yj.sryx.model.beans.Game;
import com.yj.sryx.model.beans.HttpResult;
import com.yj.sryx.model.beans.Player;
import com.yj.sryx.model.beans.Role;
import com.yj.sryx.model.beans.WxGetTokenRes;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by eason.yang on 2017/7/10.
 */

public interface SryxService {
    @GET("sryx/updateplayer")
    Observable<HttpResult<String>> updatePlayer(
            @Query("playerId") String playerId,
            @Query("avatarUrl") String avatarUrl,
            @Query("nickName") String nickName,
            @Query("gender") Integer gender,
            @Query("language") String language,
            @Query("country") String country,
            @Query("province") String province,
            @Query("city") String city);

    @GET("sryx/creategame")
    Observable<HttpResult<String>> createGame(
            @Query("gameOwnerId") String gameOwnerId,
            @Query("gameOwnerAvatarUrl") String gameOwnerAvatarUrl,
            @Query("gameOwnerNickName") String gameOwnerNickName,
            @Query("killerNum") Integer killerNum,
            @Query("policeNum") Integer policeNum,
            @Query("citizenNum") Integer citizenNum);

    @GET("sryx/getgamebyid")
    Observable<HttpResult<Game>> getGameById(@Query("gameId") String gameId);

    @GET("sryx/getrolelistingame")
    Observable<HttpResult<List<Role>>> getRolesInGame(@Query("gameId") String gameId);

    @GET("sryx/getgamebyinvitecode")
    Observable<HttpResult<Game>> getGameByInviteCode(@Query("inviteCode") String inviteCode);

    @GET("sryx/updaterole")
    Observable<HttpResult<String>> joinGameById(
            @Query("gameId") Integer gameId,
            @Query("playerId") String playerId,
            @Query("playerNickName") String playerNickName,
            @Query("playerAvatarUrl") String playerAvatarUrl);

    @GET("sryx/updategamestate")
    Observable<HttpResult<String>> cancleGame(
            @Query("state") Integer state,
            @Query("gameId") Integer gameId);


    @GET("sryx/updateroledeathstate")
    Observable<HttpResult<String>> setRoleOut(
            @Query("death") Integer state,
            @Query("roleId") Integer roleId,
            @Query("gameId") Integer gameId);

    @GET("sryx/getmygamerecords")
    Observable<HttpResult<List<Role>>> getMyGameRecordList(
            @Query("playerId") String playerId);

    @GET("sryx/getmyroleingame")
    Observable<HttpResult<Role>> getMyRoleInGame(
            @Query("playerId") String playerId,
            @Query("gameId") Integer gameId);

    @GET("sryx/getplayerbyid")
    Observable<HttpResult<Player>> getPlayer(
            @Query("playerId") String playerId);
}
