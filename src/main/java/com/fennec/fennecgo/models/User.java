package com.fennec.fennecgo.models;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users", 
    uniqueConstraints = { 
      @UniqueConstraint(columnNames = "email"),
      @UniqueConstraint(columnNames = "phone") // ✅ Only phone is unique now
    })
public class User {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 20)
  private String username; // ❌ Removed unique constraint here

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(max = 20)
  private String phone;  // ✅ Unique phone field

  @NotBlank
  @Size(max = 120)
  private String password;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_roles", 
             joinColumns = @JoinColumn(name = "user_id"), 
             inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

//This field can store a URL to the profile photo.
 @Column(name = "profile_photo", nullable = true)
 private String profilePhoto;

 // Optional gender field (could also be an enum for better type safety)
 @Column(name = "gender", nullable = true)
 private String gender;
 
  public User() {}

  // ✅ Constructor with Phone
  public User(String username, String email, String phone, String password) {
    this.username = username;
    this.email = email;
    this.phone = phone;
    this.password = password;
  }

  // ✅ Getters & Setters

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }
  
  public String getProfilePhoto() {
	    return profilePhoto;
	  }

  public void setProfilePhoto(String profilePhoto) {
	    this.profilePhoto = profilePhoto;
	  }

	  public String getGender() {
	    return gender;
	  }

	  public void setGender(String gender) {
	    this.gender = gender;
	  }
}
