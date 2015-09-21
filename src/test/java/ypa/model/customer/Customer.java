package ypa.model.customer;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
public class Customer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String code;

	private String name;

	private String phone;

	/**
	 * 中文地址
	 */
	private String address;
	/**
	 * 英文地址
	 */
	private String addressInEnglish;

	private String postCode;

	private String fax;

	private String contactMan;

	private String email;
	/**
	 * 认证码
	 */
	private String authenticationNo;
	/**
	 * 营业执照
	 */
	private String icbLicensePath;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getContactMan() {
		return contactMan;
	}

	public void setContactMan(String contactMan) {
		this.contactMan = contactMan;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAuthenticationNo() {
		return authenticationNo;
	}

	public void setAuthenticationNo(String authenticationNo) {
		this.authenticationNo = authenticationNo;
	}

	public String getIcbLicensePath() {
		return icbLicensePath;
	}

	public void setIcbLicensePath(String icbLicensePath) {
		this.icbLicensePath = icbLicensePath;
	}

	public String getAddressInEnglish() {
		return addressInEnglish;
	}

	public void setAddressInEnglish(String addressInEnglish) {
		this.addressInEnglish = addressInEnglish;
	}

}
