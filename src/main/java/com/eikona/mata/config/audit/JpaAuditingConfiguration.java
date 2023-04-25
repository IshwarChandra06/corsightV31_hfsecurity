package com.eikona.mata.config.audit;


import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eikona.mata.constants.ApplicationConstants;

@Configuration
@EnableJpaAuditing(auditorAwareRef = ApplicationConstants.AUDITOR_PROVIDER)
public class JpaAuditingConfiguration implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		
		return (SecurityContextHolder.getContext().getAuthentication() == null)? Optional.of(ApplicationConstants.SYSTEM) :Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());
		
	}

    @Bean
    public AuditorAware<String> auditorProvider() {
		return () -> getCurrentAuditor();
    }
    
    /*
    if you are using spring security, you can get the currently logged username with following code segment.

    SecurityContextHolder.getContext().getAuthentication().getName()
    
   */
    

    
}
