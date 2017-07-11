package com.yj.sryx.model.beans;

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
