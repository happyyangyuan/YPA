package ypa.model.customer;


import java.io.Serializable;

public class CustomerCondition_1 implements Serializable {

	private Long id;

	private String name;

	private String phone;

	public Long getId() {
		return id;
	}

	public CustomerCondition_1 setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public CustomerCondition_1 setName(String name) {
		this.name = name;
		return this;
	}

	public String getPhone() {
		return phone;
	}

	public CustomerCondition_1 setPhone(String phone) {
		this.phone = phone;
		return this;
	}

}
