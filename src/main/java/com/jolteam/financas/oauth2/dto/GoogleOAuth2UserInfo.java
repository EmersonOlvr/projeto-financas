package com.jolteam.financas.oauth2.dto;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

	public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
	}
	
	@Override
    public String getId() {
        return (String) this.attributes.get("sub");
    }
    @Override
    public String getName() {
        return (String) this.attributes.get("given_name");
    }
    @Override
    public String getEmail() {
        return (String) this.attributes.get("email");
    }
    @Override
    public String getImageUrl() {
        return (String) this.attributes.get("picture");
    }
    
    public String getFamilyName() {
    	return (String) this.attributes.get("family_name");
    }

	@Override
	public String toString() {
		return "GoogleOAuth2UserInfo [getId()=" + getId() + ", getName()=" + getName() + ", getEmail()=" + getEmail()
				+ ", getImageUrl()=" + getImageUrl() + ", getFamilyName()=" + getFamilyName() + "]";
	}
    
}
