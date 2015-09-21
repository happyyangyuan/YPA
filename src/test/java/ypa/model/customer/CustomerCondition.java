package ypa.model.customer;


import java.io.Serializable;

public class CustomerCondition implements Serializable {

	private Long id;

	private String name;

	private String phone;

	public Long getId() {
		return id;
	}

	public CustomerCondition setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public CustomerCondition setName(String name) {
		this.name = name;
		return this;
	}

	public String getPhone() {
		return phone;
	}

	public CustomerCondition setPhone(String phone) {
		this.phone = phone;
		return this;
	}

}
