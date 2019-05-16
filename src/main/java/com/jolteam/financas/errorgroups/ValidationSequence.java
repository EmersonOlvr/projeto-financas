package com.jolteam.financas.errorgroups;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

import com.jolteam.financas.errorgroups.usuario.*;

@GroupSequence({NomePatternGroup.class, NomeSizeMinGroup.class, NomeSizeMaxGroup.class, 
				SobrenomePatternGroup.class, SobrenomeSizeMinGroup.class, SobrenomeSizeMaxGroup.class, 
				EmailPatternGroup.class, EmailSizeMaxGroup.class, 
				SenhaSizeMinGroup.class, SenhaNotBlankGroup.class, SenhaSizeMaxGroup.class, 
				SenhaRepetidaNotBlankGroup.class, 
				Default.class})
public interface ValidationSequence {

}
