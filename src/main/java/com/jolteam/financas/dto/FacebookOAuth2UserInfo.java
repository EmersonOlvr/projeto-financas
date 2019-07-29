package com.jolteam.financas.dto;

import java.util.Map;

public class FacebookOAuth2UserInfo extends OAuth2UserInfo {
	
    public FacebookOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) this.attributes.get("id");
    }
    @Override
    public String getName() {
        return (String) this.attributes.get("first_name");
    }
    @Override
    public String getEmail() {
        return (String) this.attributes.get("email");
    }
    @Override
    public String getImageUrl() {
        if (this.attributes.containsKey("picture")) {
            @SuppressWarnings("unchecked")
			Map<String, Object> pictureObj = (Map<String, Object>) this.attributes.get("picture");
            
            if (pictureObj.containsKey("data")) {
                @SuppressWarnings("unchecked")
				Map<String, Object>  dataObj = (Map<String, Object>) pictureObj.get("data");
                
                if (dataObj.containsKey("url")) {
                    return (String) dataObj.get("url");
                }
            }
        }
        return null;
    }
    public String getLastName() {
    	return (String) this.attributes.get("last_name");
    }
    
	@Override
	public String toString() {
		return "FacebookOAuth2UserInfo [getId()=" + getId() + ", getName()=" + getName() + ", getEmail()=" + getEmail()
				+ ", getImageUrl()=" + getImageUrl() + "]";
	}
    
}
