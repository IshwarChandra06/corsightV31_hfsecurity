//package com.eikona.mata.repository;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.Assert.assertNotNull;
//
//import java.util.List;
//
//import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//
//import com.eikona.mata.entity.User;
//
//@SpringBootTest
//@TestMethodOrder(OrderAnnotation.class)
//@ActiveProfiles("test")
//public class UserRepositoryTest {
//
//	@Autowired
//	private UserRepository userRepository;
//
//	@Autowired
//	private PasswordEncoder passwordEncoder;
//
//	@Test
//	@Order(1)
//	public void testCreateUser() {
//		User user = new User("Pradeep", "pradeep.thapa@eikona.tech", passwordEncoder.encode("eikona@2020"), false);
//		User saved = userRepository.save(user);
//		assertNotNull(saved);
//	}
//
//	@Test
//	@Order(2)
//	public void testfindUserByEmail() {
//		String email = "pradeep.thapa@eikona.tech";
//		User user = userRepository.findByEmailAndIsDeletedFalse(email);
//		assertThat(user.getEmail().equals(email));
//
//	}
//
//	@Test
//	@Order(3)
//	public void testGetListOfUser() {
//		List<User> listUser = (List<User>) userRepository.findAllByIsDeletedFalse();
//		assertThat(listUser.size()).isGreaterThan(0);
//	}
//
//	@Test
//	@Order(4)
//	public void testUpdateUser() {
//		String email = "thapa.pradeep@eikona.tech";
//		User user = userRepository.findByEmailAndIsDeletedFalse("pradeep.thapa@eikona.tech");
//		user.setEmail(email);
//		User updated = userRepository.save(user);
//		assertThat(updated.getEmail()).isEqualTo(email);
//
//	}
//
//	@Test
//	@Order(5)
//	public void testDeleteUser() {
//		User user = userRepository.findByEmailAndIsDeletedFalse("pradeep.thapa@eikona.tech");
//		userRepository.delete(user);
//		User deletedUser = userRepository.findByEmailAndIsDeletedFalse("pradeep.thapa@eikona.tech");
//
//		assertThat(deletedUser).isNull();
//	}
//}
