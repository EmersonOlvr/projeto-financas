package com.jolteam.financas.errorgroups.usuario;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({NomeNotBlankGroup.class, NomePatternGroup.class, NomeSizeGroup.class, 
				SobrenomeNotBlankGroup.class, SobrenomePatternGroup.class, SobrenomeSizeGroup.class, 
				EmailNotBlankGroup.class, EmailPatternGroup.class, EmailSizeMaxGroup.class, 
				SenhaSizeMinGroup.class, SenhaNotBlankGroup.class, SenhaSizeMaxGroup.class, 
				SenhaRepetidaNotBlankGroup.class, 
				Default.class})
public interface UsuarioValidationSequence {

}
