package com.spd.bus.entity;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
//交通部折扣
@Table(name = "citycode", id = "Id")
public class CityCodeTriff extends Model {
	@Column(name = "issuer_code")
	private String issuer_code;
	@Column(name = "is_available")
	private String is_available;
	@Column(name = "city")
	private String city;
	@Column(name = "created_at")
	private String created_at;
	@Column(name = "updated_at")
	private String updated_at;
	public String getIssuer_code() {
		return issuer_code;
	}
	public void setIssuer_code(String issuer_code) {
		this.issuer_code = issuer_code;
	}
	public String getIs_available() {
		return is_available;
	}
	public void setIs_available(String is_available) {
		this.is_available = is_available;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	
}
