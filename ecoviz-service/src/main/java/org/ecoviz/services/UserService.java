/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ecoviz.domain.User;
import org.ecoviz.domain.dto.JwtTokenDto;
import org.ecoviz.domain.dto.UserDto;
import org.ecoviz.helpers.JwtHelper;
import org.ecoviz.helpers.RandomHelper;
import org.ecoviz.repositories.UserRepository;
import org.jnosql.artemis.Database;
import org.jnosql.artemis.DatabaseType;

@ApplicationScoped
public class UserService {

	Logger logger = Logger.getLogger(UserService.class.getName());
	
	@Inject
	@Database(DatabaseType.DOCUMENT)
	private UserRepository userRepository;
	
	/**
	 * Logs a user in by generating a JWT
	 */
	public JwtTokenDto login(String username, String password) throws Exception {
		Optional<User> user = userRepository.findByUsernameAndPassword(username, cryptPassword(password));
	
		if(!user.isPresent()) {
			return new JwtTokenDto("", 0);
		}
		
		return new JwtTokenDto(JwtHelper.generateJWTString(user.get()), JwtHelper.TOKEN_DURATION_SEC);
	}

	/**
	 * Creates a new user
	 * @throws NoSuchAlgorithmException
	 */
	public User createUser(UserDto userDto) throws NoSuchAlgorithmException {
		String username = userDto.getUsername();
		if(userRepository.findByUsername(username).isPresent()) {
			throw new RuntimeException("Username already taken: " + username);
		}
		
		User user = new User();
		user.setId(RandomHelper.uuid());
		user.setPassword(cryptPassword(userDto.getPassword()));
		user.setUsername(username);
		user.setRoles(userDto.getRoles());

		return userRepository.save(user);
	}

	public List<User> getUsers() {
		List<User> users = userRepository.findAll();
		
		// Removes password...
		users.forEach(u -> u.setPassword(""));

		return users;
	}

	public void deleteUser(String userId) {
		userRepository.deleteById(userId);
	}

	public void setPassword(String username, String password) {
		Optional<User> optUser = userRepository.findByUsername(username);

		if(!optUser.isPresent()) { logger.info("User does not exists... " + username); return; }

		User user = optUser.get();
		user.setPassword(password);

		userRepository.save(user);
	}

	/** 
	 * Creates a default user with admin privilegies 
	 * if no user exists in DB
	 * @throws NoSuchAlgorithmException
	 */
	public void createDefaultUserIfNeeded() throws NoSuchAlgorithmException {

		// TODO: Use method count() but it gives a nullpointer..
		if(userRepository.findAll().size() == 0) { 
			String password = RandomHelper.uuid().substring(0, 10);

			UserDto userDto = new UserDto();
			userDto.setUsername("admin");
			userDto.setPassword(password);
			userDto.setRoles(Arrays.asList("user", "admin"));

			createUser(userDto);

			logger.info("\n===== EcoViz ====="+
						"\n\nCreating a default user\n"+ 
							  "> Username: admin\n"+
							  "> Password: "+password + 
						  "\n\n==================\n");
		}

	}

	/**
	 * Encrypts a password using SHA-256 algorithm
	 * @throws NoSuchAlgorithmException
	 */
	private static String cryptPassword(String password) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
        byte[] result = mDigest.digest(password.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
         
        return sb.toString();
	}

}
