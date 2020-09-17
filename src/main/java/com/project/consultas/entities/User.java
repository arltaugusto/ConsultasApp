package com.project.consultas.entities;

import java.time.LocalDateTime;
import java.util.Set;

/**/
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.project.consultas.dto.UserDTO;
import com.project.consultas.utils.IdGenerator;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class User {

	@Id
    @GeneratedValue(generator = IdGenerator.generatorName)
    @GenericGenerator(name = IdGenerator.generatorName, strategy = "com.project.consultas.utils.IdGenerator")
	private String id;
    private String legajo;
    private String email;
    private String name;
    private String surname;
    private String role;
    private String mobile;
    private String profileImagePath;
    private boolean showMobile = false;
    
    @JsonProperty(access = Access.WRITE_ONLY)
    private String deviceToken;

    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;

    public User() {
    }

    public User(String legajo, String email, String name, String role, String password, String mobile, String deviceToken, String surname) {
        this.legajo = legajo;
        this.email = email;
        this.name = name;
        this.role = role;
        this.password = password;
        this.mobile = mobile;
        this.deviceToken = deviceToken;
        this.surname = surname;
    }

    
    public boolean isShowMobile() {
		return showMobile;
	}

	public void setShowMobile(boolean showMobile) {
		this.showMobile = showMobile;
	}

	@JsonIgnore
    public String getPassword() {
        return password;
    }

    public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public void setPassword(String password) {
        this.password = password;
    }
    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @JsonIgnore
    public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}
	
	
	public String getProfileImagePath() {
		return profileImagePath;
	}

	public void setProfileImagePath(String profileImagePath) {
		this.profileImagePath = profileImagePath;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void updateData(UserDTO user) {
        String newEmail = user.getEmail();
        String newName = user.getName();
        String newMobile = user.getMobile();
        String surname = user.getSurname();
        if (StringUtils.isNotBlank(newEmail))
            setEmail(newEmail);
        if (StringUtils.isNotBlank(newName))
            setName(newName);
        if (StringUtils.isNotBlank(newMobile))
        	setMobile(newMobile);
        if (StringUtils.isNotBlank(surname))
        	setSurname(surname);
    }
	
	public void updatePassword(String newPassword) {
	    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	    setPassword(passwordEncoder.encode(newPassword));
    }
	
	@JsonIgnore
	public abstract Set<String> getSubscriptions();

    public boolean checkAvailability(Set<Clase> clases, LocalDateTime newStartTime, LocalDateTime newEndTime) {
        for (Clase existing : clases) {
            LocalDateTime currentStartTime = existing.getInitTime();
            LocalDateTime currentEndTime = existing.getEndTime();
            if (!((newStartTime.compareTo(currentStartTime) < 0 && newEndTime.compareTo(currentStartTime) < 0)
                    || (newStartTime.compareTo(currentEndTime) > 0 && newEndTime.compareTo(currentEndTime) > 0))) {
                return false;
            }
        }
        return true;
    }
}


