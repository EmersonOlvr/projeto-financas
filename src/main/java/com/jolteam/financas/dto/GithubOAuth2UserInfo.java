package com.jolteam.financas.dto;

import java.util.Map;

import org.apache.logging.log4j.util.Strings;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {

	private String nomeCompleto;
	
    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
    	super(attributes);
    	this.nomeCompleto = (String) attributes.get("name").toString().trim().replace("  ", " ");
    }

    @Override
    public String getId() {
        return ((Integer) this.attributes.get("id")).toString();
    }
    @Override
    public String getName() {
    	String nome = "";
    	if (!Strings.isBlank(this.nomeCompleto)) {
    		int indexSpace = this.nomeCompleto.contains(" ") ? this.nomeCompleto.indexOf(" ") : 1;
    		nome = this.nomeCompleto.substring(0, indexSpace);
    	}
        return nome;
    }
    @Override
    public String getEmail() {
        return (String) this.attributes.get("email");
    }
    @Override
    public String getImageUrl() {
        return (String) this.attributes.get("avatar_url");
    }
    
    public String getFullName() {
    	return (String) this.attributes.get("name");
    }
    public String getSobrenome() {
    	String sobrenome = "";
    	if (!Strings.isBlank(this.nomeCompleto)) {
    		int indexSpace = this.nomeCompleto.contains(" ") ? this.nomeCompleto.lastIndexOf(" ") : 0;
    		int inicioSobrenome = this.nomeCompleto.length() > 1 ? indexSpace + 1 : 0;
    		int fimSobrenome = indexSpace != 0 ? this.nomeCompleto.length() : 0;
    		sobrenome = this.nomeCompleto.substring(inicioSobrenome, fimSobrenome);
    	}
        return sobrenome;
    }

	@Override
	public String toString() {
		return "GithubOAuth2UserInfo [nomeCompleto=" + nomeCompleto + ", getId()=" + getId() + ", getName()=" + getName()
				+ ", getEmail()=" + getEmail() + ", getImageUrl()=" + getImageUrl() + ", getFullName()=" + getFullName()
				+ ", getSobrenome()=" + getSobrenome() + "]";
	}
    
}
