package model.user;

public class Account {
	private String name;
	private String lastName;
	private String email;
	private String password;
	private String profilePhoto;
	private long id;

	public Account() {

	}

	public Account(String name, String lastName, String email, String password, String profilePhoto) {
		this.name = name;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.profilePhoto = profilePhoto;
	}

	public Account(String name, String lastName, String email, String password, String profilePhoto, long id) {
		this.name = name;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.profilePhoto = profilePhoto;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProfilePhoto() {
		return profilePhoto;
	}

	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
