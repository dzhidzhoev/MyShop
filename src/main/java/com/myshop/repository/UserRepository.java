package com.myshop.repository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.myshop.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	public Optional<User> findByEmailIgnoreCase(String email);
	public Optional<User> findByEmailIgnoreCaseAndPwdHashIgnoreCase(String email, String pwdHash);
	public List<User> findByEmailToken(String emailToken);
	public List<User> findByPwdChangeToken(String pwdChangeToken);
	
	public default String getPasswordHash(String password) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("No MD5 algorithm found");
		}
		md.update(password.getBytes());
		md.update("myshp82".getBytes());
		return DatatypeConverter.printHexBinary(md.digest());
	}
	
	public default Optional<User> logIn(String email, String password) {
		email = email.trim();
		password = password.trim();
		
		var res = findByEmailIgnoreCaseAndPwdHashIgnoreCase(email, getPasswordHash(password));
		if (res.isPresent() && res.get().isDeleted()) {
			return Optional.empty();
		}
		return res;
	}
	
	public default boolean orderingAvailable(int userID) {
		var userOpt = findById(userID);
		if (userOpt.isPresent()) {
			var user = userOpt.get();
			return user.getEmailToken() == null;
		} else {
			return false;
		}
	}
	
	public default void approveEmail(String token) {
		if (token == null || token.isEmpty()) {
			return;
		}
		findByEmailToken(token).stream().forEach(user -> {
			if (!user.isDeleted()) {
				user.setEmailToken(null);
				save(user);
			}
		});
		flush();
	}
	
	public default boolean isPasswordValid(@NonNull String newPwd) {
		for (int i = 0; i < newPwd.length(); i++) {
			char c = newPwd.charAt(i);
			if (c >= 0x21 && c <= 0x2f) continue;
			if (!Character.isDigit(c) && !Character.isAlphabetic(c)) {
				return false;
			}
		}
		return newPwd.length() >= 8;
	}
	
	public default void resetPassword(String token, String newPwd) {
		if (token == null || newPwd == null) {
			return;
		}
		if (isPasswordValid(newPwd)) {
			findByPwdChangeToken(token).forEach((user) -> {
				if (!user.isDeleted()) {
					user.setPwdHash(getPasswordHash(newPwd.trim()));
					save(user);
				}
			});
			flush();
		}
	}
	
	public default Pair<User, String> registerUser(String lastName, String firstName, String middleName, 
			String phoneNumber, String address, String email, String password) {
		if (lastName == null) {
			lastName = "";
		}
		if (firstName == null) {
			firstName = "";
		}
		if (email == null) {
			email = "";
		}
		if (password == null) {
			password = "";
		}
		lastName = lastName.trim();
		firstName = firstName.trim();
		email = email.trim();
		if (lastName.isEmpty()) {
			return Pair.of(null, "last name is empty");
		}
		if (firstName.isEmpty()) {
			return Pair.of(null, "first name is empty");
		}
		if (email.isEmpty()) {
			return Pair.of(null, "email is empty");
		}
		if (!email.contains("@") || email.length() < 4) {
			return Pair.of(null, "not an email");
		}
		if (password.isEmpty() || !isPasswordValid(password)) {
			return Pair.of(null, "password is empty");
		}
		if (findByEmailIgnoreCase(email).isPresent()) {
			return Pair.of(null, "email has been used already");
		}
		var user = new User().setLastName(lastName)
				.setFirstName(firstName)
				.setMiddleName(middleName)
				.setPhone(phoneNumber)
				.setAddress(address)
				.setEmail(email)
				.setPwdHash(getPasswordHash(password))
				.setEmailToken(UUID.randomUUID().toString());
		try {
			saveAndFlush(user);
		} catch (DataAccessException e) {
			return Pair.of(null, "unknown exception " + e.toString());
		}
		
		return Pair.of(user, "ok");
	}
	
	public default void removeUser(int userID) {
		findById(userID).ifPresent(user -> {
			user.setDeleted(true);
			saveAndFlush(user);
		});
	}
}
