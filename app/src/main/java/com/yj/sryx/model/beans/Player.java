package com.yj.sryx.model.beans;

import java.io.Serializable;

public class Player implements Serializable{
    private static final long serialVersionUID = 1L;
    
	private String 	playerId;
	private String 	avatarUrl;
	private String 	nickName;
	private Integer gender;
	private String 	language;
	private String 	country;
	private String 	province;
	private String 	city;
	private Integer lastGameId;
	private String 	createTime;
	
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Integer getLastGameId() {
		return lastGameId;
	}
	public void setLastGameId(Integer lastGameId) {
		this.lastGameId = lastGameId;
	}
	
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	@Override
	public String toString() {
		return "Player [playerId=" + playerId + ", avatarUrl=" + avatarUrl
				+ ", nickName=" + nickName + ", gender=" + gender
				+ ", language=" + language + ", country=" + country
				+ ", province=" + province + ", city=" + city + ", lastGameId="
				+ lastGameId + ", createTime=" + createTime + "]";
	}
}
