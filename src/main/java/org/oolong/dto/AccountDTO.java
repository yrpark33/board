package org.oolong.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
public class AccountDTO extends BaseTimeDTO implements UserDetails {
	
	private String username;
	private String password;
	private String name;
	private String email;
	private List<AccountRole> roleNames;
	private boolean enabled;
	private LocalDateTime updatedAt;
	
	
	
	public void addRole(AccountRole role) {
		
		if(roleNames == null) {
			roleNames = new ArrayList<>();
		}
		
		roleNames.add(role);
	}
	
	public void clearRoles() {
		
		roleNames.clear();
	}
	
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		if(roleNames == null || roleNames.size() == 0) {
			
			return List.of();
			
		}
		
		return roleNames.stream()
				.map(accountRole -> new SimpleGrantedAuthority("ROLE_" + accountRole.name()))
				.collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

}
