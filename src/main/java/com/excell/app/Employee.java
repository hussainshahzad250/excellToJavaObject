package com.excell.app;

import com.poiji.annotation.ExcelCell;

public class Employee {

	@ExcelCell(0)
	private long employeeId;

	@ExcelCell(1)
	private String name;

	@ExcelCell(2)
	private String surname;

	@ExcelCell(3)
	private int age;

	@ExcelCell(4)
	private boolean single;

	@ExcelCell(5)
	private String birthday;

	public Employee() {
	}
	// no need getters/setters to map excel cells to fields

	public Employee(long employeeId, String name, String surname, int age, boolean single, String birthday) {
		this.employeeId = employeeId;
		this.name = name;
		this.surname = surname;
		this.age = age;
		this.single = single;
		this.birthday = birthday;
	}

	@Override
	public String toString() {
		return "Employee{" + "employeeId=" + employeeId + ", name='" + name + '\'' + ", surname='" + surname + '\''
				+ ", age=" + age + ", single=" + single + ", birthday='" + birthday + '\'' + '}';
	}

	public long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(long employeeId) {
		this.employeeId = employeeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public boolean isSingle() {
		return single;
	}

	public void setSingle(boolean single) {
		this.single = single;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
}