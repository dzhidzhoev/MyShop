package com.myshop.repository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.xml.bind.DatatypeConverter;

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
	
	public default String getPasswordHash(@NotNull String password) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("No MD5 algorithm found");
		}
		md.update(password.getBytes());
		md.update("myshp82".getBytes());
		return DatatypeConverter.printHexBinary(md.digest()).toLowerCase();
	}
	
	public default Optional<User> logIn(String email, String password) {
		if (email == null || password == null) {
			return Optional.empty();
		}
		email = email.trim();
		
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
					user.setPwdChangeToken(null);
					user.setPwdHash(getPasswordHash(newPwd.trim()));
					save(user);
				}
			});
			flush();
		}
	}
	
	public default Pair<Optional<User>, String> registerUser(Integer id, String lastName, String firstName, String middleName, 
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
		if (middleName != null) {
			middleName = middleName.trim();
		}
		email = email.trim();
		if (lastName.isEmpty()) {
			return Pair.of(Optional.empty(), "last name is empty");
		}
		if (firstName.isEmpty()) {
			return Pair.of(Optional.empty(), "first name is empty");
		}
		if (email.isEmpty()) {
			return Pair.of(Optional.empty(), "email is empty");
		}
		if (!email.contains("@") || email.length() < 4) {
			return Pair.of(Optional.empty(), "not an email");
		}
		var old = findByEmailIgnoreCase(email);
		if (old.isPresent() && (id == null || old.get().getId() != id)) {
			return Pair.of(Optional.empty(), "email has been used already");
		}
		String pwdHash = getPasswordHash(password);
		if (!old.isPresent() && id != null && !password.isEmpty()) {
			var oldId = findById(id);
			if (oldId.isPresent() && !oldId.get().getPwdHash().equals(pwdHash)) {
				return Pair.of(Optional.empty(), "can't change login and password simultaneously");
			}
		}
		if (password.isEmpty() && id != null) {
			pwdHash = findById(id).get().getPwdHash();
		}
		if ((password.isEmpty() && id == null) || (!isPasswordValid(password) && !password.isEmpty())) {
			return Pair.of(Optional.empty(), "password is invalid");
		}
		var user = new User().setLastName(lastName)
				.setFirstName(firstName)
				.setMiddleName(middleName)
				.setPhone(phoneNumber)
				.setAddress(address)
				.setEmail(email)
				.setPwdHash(pwdHash)
				.setAdmin(id != null ? findById(id).get().isAdminOrNull() : null)
				.setEmailToken(UUID.randomUUID().toString());
		if (id != null) {
			user.setId(id);
		}
		
		user = saveAndFlush(user);
		
		return Pair.of(Optional.of(user), "ok");
	}
	
	public default void removeUser(int userID) {
		findById(userID).ifPresent(user -> {
			user.setDeleted(true);
			saveAndFlush(user);
		});
	}
}
