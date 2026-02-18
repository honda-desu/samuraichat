package com.example.samuraichat.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.samuraichat.entity.Role;
import com.example.samuraichat.entity.User;
import com.example.samuraichat.form.SignupForm;
import com.example.samuraichat.repository.RoleRepository;
import com.example.samuraichat.repository.UserRepository;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public User createUser(SignupForm signupForm) {
		User user = new User();
		Role role = roleRepository.findByName("ROLE_GENERAL");

		user.setName(signupForm.getName());
		user.setFurigana(signupForm.getFurigana());
		user.setEmail(signupForm.getEmail());
		user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
		user.setRole(role);
		user.setEnabled(false);

		return userRepository.save(user);
	}

	// メールアドレスが登録済みかどうかをチェックする
	public boolean isEmailRegistered(String email) {
		User user = userRepository.findByEmail(email);
		return user != null;
	}

	// パスワードとパスワード（確認用）の入力値が一致するかどうかをチェックする
	public boolean isSamePassword(String password, String passwordConfirmation) {
		return password.equals(passwordConfirmation);
	}

	// ユーザーを有効にする
	@Transactional
	public void enableUser(User user) {
		user.setEnabled(true);
		userRepository.save(user);
	}
	
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	//プロフィールを編集する
	@Transactional
	public void updateProfile(String email, String name, String furigana, String profileText, MultipartFile profileImage) {
		User user = userRepository.findByEmail(email);
		
		user.setName(name);
		user.setFurigana(furigana);
		user.setProfileText(profileText);
		
		//画像がアップロードされた場合のみ処理
		if(!profileImage.isEmpty()) {
			// 画像がアップロードされた場合のみ処理
		    if (!profileImage.isEmpty()) {
		        try {
		            String fileName = UUID.randomUUID().toString() + "_" + profileImage.getOriginalFilename();
		            Path uploadPath = Paths.get("uploads");

		            if (!Files.exists(uploadPath)) {
		                Files.createDirectories(uploadPath);
		            }

		            Path filePath = uploadPath.resolve(fileName);
		            Files.copy(profileImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		            // DB に保存するのはファイル名だけでOK
		            user.setProfileImage(fileName);

		        } catch (IOException e) {
		            throw new RuntimeException("画像の保存に失敗しました", e);
		        }
		    }

		}
		
		userRepository.save(user);
				
	}
	
	public Page<User> getUserPage(int page) {
	    Pageable pageable = PageRequest.of(page, 10, Sort.by("id").ascending());
	    return userRepository.findAll(pageable);
	}
	
}
