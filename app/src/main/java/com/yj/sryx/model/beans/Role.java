package com.yj.sryx.model.beans;

import com.yj.sryx.utils.DateUtils;
import com.yj.sryx.utils.TimeUtils;

import java.io.Serializable;

public class Role implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private Integer roleId;
    private String	playerId;
    private Integer	gameId;
    private Integer roleType;
    private String	playerNickName;
    private String	playerAvatarUrl;
    private Integer victorySide;
    private Integer death;
    private String 	deathTime;
    private String 	createTime;
    private String 	remark;

	public String getDate(){
        return DateUtils.convertTimeFormat(createTime, "yy-MM-dd HH:mm:ss.S", "MM月dd日");
    }

    public String getTime() {
        return DateUtils.convertTimeFormat(createTime, "yy-MM-dd HH:mm:ss.S", "HH:mm");
    }

	public String getRoleName() {
		String roleName = null;
		switch(roleType){
			case 0:
				roleName = "杀手";
				break;
			case 1:
				roleName = "警察";
				break;
			case 2:
				roleName = "平民";
				break;
			case 3:
				roleName = "裁判";
				break;
		}
		return roleName;
	}

    public String getGameResult(){
        String gameResult = null;
        switch(victorySide){
            case 0:
                gameResult = "杀手集团获胜";
                break;
            case 1:
                gameResult = "正义联盟获胜";
                break;
            case 2:
                gameResult = "平局";
                break;
        }
        return gameResult;
    }

	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public Integer getGameId() {
		return gameId;
	}
	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
	public Integer getRoleType() {
		return roleType;
	}
	public void setRoleType(Integer roleType) {
		this.roleType = roleType;
	}
	public String getPlayerNickName() {
		return playerNickName;
	}
	public void setPlayerNickName(String playerNickName) {
		this.playerNickName = playerNickName;
	}
	public String getPlayerAvatarUrl() {
		return playerAvatarUrl;
	}
	public void setPlayerAvatarUrl(String playerAvatarUrl) {
		this.playerAvatarUrl = playerAvatarUrl;
	}
	public Integer getVictorySide() {
		return victorySide;
	}
	public void setVictorySide(Integer victorySide) {
		this.victorySide = victorySide;
	}
	public Integer getDeath() {
		return death;
	}
	public void setDeath(Integer death) {
		this.death = death;
	}
	public String getDeathTime() {
		return deathTime;
	}
	public void setDeathTime(String deathTime) {
		this.deathTime = deathTime;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Override
	public String toString() {
		return "Role [roleId=" + roleId + ", playerId=" + playerId
				+ ", gameId=" + gameId + ", roleType=" + roleType
				+ ", playerNickName=" + playerNickName + ", playerAvatarUrl="
				+ playerAvatarUrl + ", victorySide=" + victorySide + ", death="
				+ death + ", deathTime=" + deathTime + ", createTime="
				+ createTime + ", remark=" + remark + "]";
	}

}
