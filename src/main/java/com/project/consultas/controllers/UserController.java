package com.project.consultas.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import com.project.consultas.dto.*;
import com.project.consultas.entities.*;
import com.project.consultas.repositories.StudentRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.firebase.messaging.FirebaseMessaging;
import com.project.consultas.exceptions.InvalidCredentialsException;
import com.project.consultas.notifications.EmailNotificationService;
import com.project.consultas.notifications.FCMService;
import com.project.consultas.notifications.NewProfessorPasswordEmailNotification;
import com.project.consultas.repositories.ProfessorRepository;
import com.project.consultas.repositories.UserRepository;
import com.project.consultas.security.JwtUtils;
import com.project.consultas.security.MyUserDetailsService;
import com.project.consultas.utils.BasicEntityUtils;
import com.project.consultas.utils.BucketName;
import com.project.consultas.utils.FileManager;

@CrossOrigin
@RestController
@RequestMapping(path = "/users")
public class UserController {

	@Autowired
	private ProfessorRepository professorRepository;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private MyUserDetailsService userDetailsService;
	@Autowired
	private JwtUtils jwtTokenUtils;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private EmailNotificationService emailNotificationService;
	@Autowired
	private FileManager fileManager;
	@Autowired
	private FCMService fmcService;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private Environment env;

	private Logger logger = LoggerFactory.getLogger(UserController.class);

	private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid Username or password";

	@PostMapping(path = "/addStudent", consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<User> addNewStudent(@RequestBody UserDTO user) {
		String legajo = credentialsValidation(user);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		User us = new Student(legajo, user.getEmail(), user.getName(), "ROLE_STUDENT",
				passwordEncoder.encode(user.getPassword()), user.getMobile(), user.getDeviceToken(), user.getSurname());
		return BasicEntityUtils.save(us, userRepository);
	}

	private String credentialsValidation(@RequestBody UserDTO user) {
		validateNotNullableFields(user);
		validateEmail(user.getEmail());
		String legajo = user.getLegajo();
		checkLegajoAvailability(legajo);
		return legajo;
	}

	@PostMapping(path = "/addAdmin", consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<User> addNewAdmin(@RequestBody UserDTO user) {
		String legajo = credentialsValidation(user);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		User us = new Admin(legajo, user.getEmail(), user.getName(), "ROLE_ADMIN",
				passwordEncoder.encode(user.getPassword()), user.getMobile(), user.getDeviceToken(), user.getSurname());
		return BasicEntityUtils.save(us, userRepository);
	}

	private void checkLegajoAvailability(String legajo) {
		Optional<User> user = userRepository.findByLegajo(legajo);
		if (user.isPresent()) {
			throw new IllegalStateException("There's an existing user");
		}
	}

	private void validateNotNullableFields(UserDTO user) {
		if (StringUtils.isBlank(user.getEmail()) || StringUtils.isBlank(user.getLegajo())
				|| StringUtils.isBlank(user.getName()) || StringUtils.isBlank(user.getSurname())
				|| StringUtils.isBlank(user.getPassword())) {
			throw new IllegalStateException("There are missing values");
		}
	}
	
	private void validateEmail(String email) {
		if(!email.matches("[^@\\s]+@[\\w\\d]+\\.\\w+")) {
			throw new IllegalStateException("InvalidEmailAddress");
		}
	}

	@PostMapping(path = "/addProfessor", consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<User> addNewProfessor(@RequestBody UserDTO user) throws IOException {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String randomPassword = BasicEntityUtils.randomPassword();
		String legajo = user.getLegajo();
		checkLegajoAvailability(legajo);
		User us = new Professor(legajo, user.getEmail(), user.getName(), "ROLE_PROFESSOR",
				passwordEncoder.encode(randomPassword), user.getMobile(), user.getDeviceToken(), user.getSurname());
		userRepository.save(us);
		NewProfessorPasswordEmailNotification newProfessorPasswordEmailNotificationMessage = new NewProfessorPasswordEmailNotification(
				us.getName(), randomPassword, us.getEmail(), us.getLegajo());
		emailNotificationService.sendEmail(newProfessorPasswordEmailNotificationMessage);
		return BasicEntityUtils.save(us, userRepository);
	}

	@PutMapping(path = "/modify", consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<User> modifyUser(@RequestHeader("Authorization") String jwt,
			@RequestBody UserDTO user) throws InvalidCredentialsException {
		String userToken = jwt.split(StringUtils.SPACE)[1];
		User userDb = BasicEntityUtils
				.entityFinder(userRepository.findByLegajo(jwtTokenUtils.extractUsername(userToken)));
		if(StringUtils.isNotBlank(user.getEmail())) {
			validateEmail(user.getEmail());
		}
		userDb.updateData(user);
		return BasicEntityUtils.save(userDb, userRepository);
	}

	@PutMapping(path = "/modifyPassword", consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<String> modifyPassword(@RequestHeader("Authorization") String jwt,
			@RequestBody PasswordChangeDTO passwordChangeDto) throws InvalidCredentialsException {
		String newPassword = passwordChangeDto.getNewPassword();
		if (StringUtils.isBlank(newPassword)) {
			return new ResponseEntity<>("{\"message\": \"new password cannot be blank\"}", HttpStatus.BAD_REQUEST);
		}
		String userToken = jwt.split(StringUtils.SPACE)[1];
		User userDb = BasicEntityUtils
				.entityFinder(userRepository.findByLegajo(jwtTokenUtils.extractUsername(userToken)));
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		validatePassword(passwordChangeDto, userDb, passwordEncoder);
		userDb.updatePassword(passwordChangeDto.getNewPassword());
		userRepository.save(userDb);
		return new ResponseEntity<>("{\"message\": \"Password updated\"}", HttpStatus.OK);
	}

	private void validatePassword(PasswordChangeDTO passwordChangeDto, User userDb,
			BCryptPasswordEncoder passwordEncoder) throws InvalidCredentialsException {
		if (!passwordEncoder.matches(passwordChangeDto.getPassword(), userDb.getPassword())) {
			throw new InvalidCredentialsException(INVALID_CREDENTIALS_MESSAGE);
		}
	}

	@PostMapping(path = "/login", consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<AuthenticationResponse> login(
			@RequestBody AuthenticationRequest authenticationRequest) {
		String legajo = authenticationRequest.getLegajo();
		try {
			authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(legajo, authenticationRequest.getPassword()));
		} catch (BadCredentialsException e) {
			throw new IllegalStateException("Bad username or password", e);
		}
		final UserDetails userDetails = userDetailsService.loadUserByUsername(legajo);
		User user = BasicEntityUtils.entityFinder(userRepository.findByLegajo(legajo));
		String deviceToken = authenticationRequest.getDeviceToken();
		user.setDeviceToken(deviceToken);
		userRepository.save(user);
		BiConsumer<String, String> consumer = (token, topic) -> FirebaseMessaging.getInstance()
				.subscribeToTopicAsync(Arrays.asList(token), topic);
		SubscriptionDTO subscriptionDto = new SubscriptionDTO(user.getDeviceToken(), user.getSubscriptions());
		fmcService.deviceSuscriptionManager(consumer, subscriptionDto);
		final String jwt = jwtTokenUtils.generateToken(userDetails);
		return new ResponseEntity<>(new AuthenticationResponse(jwt), HttpStatus.OK);
	}

	@GetMapping(path = "/all")
	public @ResponseBody Iterable<User> getAllUsers() {
		return userRepository.findAll();
	}

	@GetMapping(path = "/getUser")
	public @ResponseBody User getUserFromJwt(@RequestHeader("Authorization") String jwt)
			throws EntityNotFoundException {
		String userToken = jwt.split(StringUtils.SPACE)[1];
		return BasicEntityUtils.entityFinder(userRepository.findByLegajo(jwtTokenUtils.extractUsername(userToken)));
	}

	@PostMapping(path = "/images/upload", consumes = { "application/json" })
	public @ResponseBody ResponseEntity<String> uploadProfileImage(@RequestHeader("Authorization") String jwt,
			@RequestBody String imageFile) throws EntityNotFoundException, IOException {
		ImageFileDTO file = BasicEntityUtils.convertToEntityFromString(ImageFileDTO.class, imageFile);
		String userToken = jwt.split(StringUtils.SPACE)[1];
		User user = BasicEntityUtils
				.entityFinder(userRepository.findByLegajo(jwtTokenUtils.extractUsername(userToken)));
		String originalFilename = file.getFileName();
		String oldFileName = user.getProfileImagePath();
		String path = String.format("%s/%s/%s",
				env.getProperty("images.path") + BucketName.PROFILE_IMAGE_BUCKET.getBucketName(), user.getId(),
				originalFilename);
		if (StringUtils.isNotBlank(oldFileName)) {
			try {
				fileManager.deleteImage(String.format("%s/%s/%s",
						env.getProperty("images.path") + BucketName.PROFILE_IMAGE_BUCKET.getBucketName(), user.getId(),
						oldFileName));
			} catch (Exception e) {
				logger.error("Unable to delete file", e);
			}

		}
		fileManager.saveImage(file, path);
		user.setProfileImagePath(originalFilename);
		userRepository.save(user);
		return new ResponseEntity<>("{\"message\": \"Succed\"}", HttpStatus.OK);
	}

	@GetMapping(path = "/images/profileImages/{id}", produces = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE,
			MediaType.IMAGE_GIF_VALUE })
	public @ResponseBody ResponseEntity<byte[]> getProfileImage(@PathVariable("id") String id) throws IOException {
		User user = BasicEntityUtils.entityFinder(userRepository.findById(id));
		if (StringUtils.isBlank(user.getProfileImagePath())) {
			return new ResponseEntity<>(new byte[0], HttpStatus.OK);
		}
		byte[] readImage = fileManager.readImage(String.format("%s/%s/%s",
				env.getProperty("images.path") + BucketName.PROFILE_IMAGE_BUCKET.getBucketName(), id,
				user.getProfileImagePath()));
		return new ResponseEntity<>(readImage, HttpStatus.OK);
	}

	@GetMapping(path = "/logout")
	public @ResponseBody ResponseEntity<String> doLogout(@RequestHeader("Authorization") String jwt) {
		String userToken = jwt.split(StringUtils.SPACE)[1];
		User user = BasicEntityUtils.entityFinder(userRepository.findByLegajo(jwtTokenUtils.extractUsername(userToken)));
		BiConsumer<String, String> consumer = (us, topic) -> FirebaseMessaging.getInstance()
				.unsubscribeFromTopicAsync(Arrays.asList(us), topic);
		SubscriptionDTO subscriptionDto = new SubscriptionDTO(user.getDeviceToken(), user.getSubscriptions());
		fmcService.deviceSuscriptionManager(consumer, subscriptionDto);
		user.setDeviceToken(StringUtils.EMPTY);
		userRepository.save(user);
		return new ResponseEntity<>("{\"message\": \"Logout\"}", HttpStatus.OK);
	}

	@GetMapping(path = "/getAllProfessors")
	public @ResponseBody ResponseEntity<List<Professor>> getAllProfessors() {
		List<Professor> professors = professorRepository.findAll();
		professors.parallelStream().forEach(BasicEntityUtils::validateProfessorMobileStatus);
		return new ResponseEntity<>(professors, HttpStatus.OK);
	}

	@GetMapping(path = "/getProfessorSubjects/{id}")
	public @ResponseBody ResponseEntity<Set<Subject>> getProfessorSubjects(@PathVariable("id") String id) {
		Professor professor = BasicEntityUtils.entityFinder(professorRepository.findById(id));
		return new ResponseEntity<>(professor.getSubjectProfessors(), HttpStatus.OK);
	}

	@GetMapping(path = "/getStudentInscriptions")
	public @ResponseBody ResponseEntity<MappingJacksonValue> getStudentInscriptions(@RequestHeader("Authorization") String jwt) {
		String userToken = jwt.split(StringUtils.SPACE)[1];
		Student student = BasicEntityUtils.entityFinder(studentRepository.findByLegajo(jwtTokenUtils.extractUsername(userToken)));
		List<UserInscriptionResponse> userInscriptions = student.getBooks().stream()
			.filter(turno -> turno.getClase().getInitTime().isAfter(LocalDateTime.now().minusDays(7)))
			.map(UserInscriptionResponse::new)
			.sorted(Comparator.comparing(userInscription -> userInscription.getClasse().getInitTime()))
			.collect(Collectors.toList());
		MappingJacksonValue response = BasicEntityUtils.applyFilter("classFilter", userInscriptions, new String[] {"id", "initTime", "endTime", "hasSingleTurnos", "status"});
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
