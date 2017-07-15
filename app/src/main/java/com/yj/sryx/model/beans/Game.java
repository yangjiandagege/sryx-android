package com.yj.sryx.model.beans;

import com.yj.sryx.utils.DateUtils;

import java.io.Serializable;

public class Game implements Serializable{
    private static final long serialVersionUID = 1L;
    
	private Integer gameId;
	private String 	gameOwnerId;
	private String 	gameOwnerAvatarUrl;
	private String 	gameOwnerNickName;
	private String 	inviteCode;
	private Integer killerNum;
	private Integer policeNum;
	private Integer citizenNum;
	private Integer	state;
	private String	startTime;
	private String	endTime;
	private Integer result;
	private String 	createTime;

	public Integer getGameId() {
		return gameId;
	}


	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}


	public String getGameOwnerId() {
		return gameOwnerId;
	}


	public void setGameOwnerId(String gameOwnerId) {
		this.gameOwnerId = gameOwnerId;
	}


	public String getGameOwnerAvatarUrl() {
		return gameOwnerAvatarUrl;
	}


	public void setGameOwnerAvatarUrl(String gameOwnerAvatarUrl) {
		this.gameOwnerAvatarUrl = gameOwnerAvatarUrl;
	}

	public String getGameOwnerNickName() {
		return gameOwnerNickName;
	}


	public void setGameOwnerNickName(String gameOwnerNickName) {
		this.gameOwnerNickName = gameOwnerNickName;
	}


	public String getInviteCode() {
		return inviteCode;
	}


	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}


	public Integer getKillerNum() {
		return killerNum;
	}


	public void setKillerNum(Integer killerNum) {
		this.killerNum = killerNum;
	}


	public Integer getPoliceNum() {
		return policeNum;
	}


	public void setPoliceNum(Integer policeNum) {
		this.policeNum = policeNum;
	}


	public Integer getCitizenNum() {
		return citizenNum;
	}


	public void setCitizenNum(Integer citizenNum) {
		this.citizenNum = citizenNum;
	}


	public Integer getState() {
		return state;
	}

    public String getGameDate(){
        return DateUtils.convertTimeFormat(startTime, "yyyy-MM-dd HH:mm:ss.S", "yyyy-MM-dd");
    }

	public void setState(Integer state) {
		this.state = state;
	}


	public String getStartTime() {
        return DateUtils.convertTimeFormat(startTime, "yyyy-MM-dd HH:mm:ss.S", "HH:mm");
	}


	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}


	public String getEndTime() {
        return DateUtils.convertTimeFormat(endTime, "yyyy-MM-dd HH:mm:ss.S", "HH:mm");
	}


	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}


	public Integer getResult() {
		return result;
	}


	public void setResult(Integer result) {
		this.result = result;
	}


	public String getCreateTime() {
		return createTime;
	}


	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}


	@Override
	public String toString() {
		return "Game [gameId=" + gameId + ", gameOwnerId=" + gameOwnerId
				+ ", gameOwnerAvatarUrl=" + gameOwnerAvatarUrl
				+ ", gameOwnerNickName=" + gameOwnerNickName + ", inviteCode="
				+ inviteCode + ", killerNum=" + killerNum + ", policeNum="
				+ policeNum + ", citizenNum=" + citizenNum + ", state=" + state
				+ ", startTime=" + startTime + ", endTime=" + endTime
				+ ", result=" + result + ", createTime=" + createTime + "]";
	}
}
