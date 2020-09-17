package com.project.consultas.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.swing.*;
import javax.validation.Valid;

import com.project.consultas.dto.ImageFileDTO;
import com.project.consultas.dto.ModifySubjectResponse;
import com.project.consultas.entities.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.consultas.dto.SubjectDTO;
import com.project.consultas.notifications.FCMService;
import com.project.consultas.repositories.ProfessorRepository;
import com.project.consultas.repositories.StudentRepository;
import com.project.consultas.repositories.SubjectRepository;
import com.project.consultas.repositories.UserRepository;
import com.project.consultas.security.JwtUtils;
import com.project.consultas.utils.BasicEntityUtils;
import com.project.consultas.utils.BucketName;
import com.project.consultas.utils.FileManager;

import static java.util.stream.Collectors.groupingBy;

@CrossOrigin
@RestController
@RequestMapping(path = "/subjects")
public class SubjectController {

	private static final String MESSAGE_SUCCED = "{\"message\": \"Succed\"}";

	private Logger logger = LoggerFactory.getLogger(SubjectController.class);

	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private ProfessorRepository professorRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private SubjectRepository subjectRepository;
	@Autowired
	private FCMService fcmService;
	@Autowired
	private Environment env;
	@Autowired
	private FileManager fileManager;

	@PostMapping(path = "/add", consumes = { "multipart/form-data" })
	public @ResponseBody ResponseEntity<String> addNewSubject(@RequestPart("subject") @Valid String subjectStr,
			@RequestPart("imageFile") @Nullable String imageFileString) throws IOException {
		SubjectDTO subjectDto = BasicEntityUtils.convertToEntityFromString(SubjectDTO.class, subjectStr);
		ImageFileDTO imageFile = BasicEntityUtils.convertToEntityFromString(ImageFileDTO.class, imageFileString);
		String name = subjectDto.getName();
		if (StringUtils.isBlank(name)) {
			return new ResponseEntity<>("{\"message\": \"invalid name\"}", HttpStatus.BAD_REQUEST);
		}
		Set<Professor> professors = professorRepository.findByUserIds(subjectDto.getSubjectProfessors());
		Subject newSubject = new Subject(subjectDto.getName(), professors);
		if (imageFile != null) {
			uploadImage(imageFile, newSubject);
		}
		subjectRepository.save(newSubject);
		return new ResponseEntity<>(MESSAGE_SUCCED, HttpStatus.OK);
	}

	private void uploadImage(ImageFileDTO imageFile, Subject newSubject) {
		String originalFilename = imageFile.getFileName();
		String path = String.format("%s/%s/%s",
				env.getProperty("images.path") + BucketName.SUBJECT_IMAGE_BUCKET.getBucketName(), newSubject.getName(),
				originalFilename);
		try {
			fileManager.saveImage(imageFile, path);
			newSubject.setImagePath(originalFilename);
		} catch (IOException e) {
			logger.error("Unable to upload picture");
		}
	}

	@GetMapping(path = "/images/{id}", produces = { MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE,
			MediaType.IMAGE_GIF_VALUE })
	public @ResponseBody ResponseEntity<byte[]> getProfileImage(@PathVariable("id") long id)
			throws EntityNotFoundException, IOException {
		Subject subject = BasicEntityUtils.entityFinder(subjectRepository.findById(id));
		if (StringUtils.isBlank(subject.getImagePath())) {
			return new ResponseEntity<>(new byte[0], HttpStatus.OK);
		}
		byte[] readImage = fileManager.readImage(String.format("%s/%s/%s",
				env.getProperty("images.path") + BucketName.SUBJECT_IMAGE_BUCKET.getBucketName(), subject.getName(),
				subject.getImagePath()));
		return new ResponseEntity<>(readImage, HttpStatus.OK);
	}

	@PostMapping(path = "/followSubject", consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<String> followSubject(@RequestHeader("Authorization") String jwt,
			@RequestBody SubjectDTO subjectDto) {
		manageSubjectFollowStatus(subjectDto, (subject, student) -> {
			subject.getStudentsFollowing().add(student);
			fcmService.subscribeToTopic(student.getDeviceToken(), subject.getTopic());
		}, jwt);
		return new ResponseEntity<>("{\"message\": \"subject follow added\"}", HttpStatus.OK);
	}

	private void manageSubjectFollowStatus(SubjectDTO subjectDto, BiConsumer<Subject, Student> dbOperation,
			String jwt) {
		String userToken = jwt.split(StringUtils.SPACE)[1];
		Student student = BasicEntityUtils
				.entityFinder(studentRepository.findByLegajo(jwtUtils.extractUsername(userToken)));
		Subject subject = BasicEntityUtils.entityFinder(subjectRepository.findById(subjectDto.getId()));
		dbOperation.accept(subject, student);
		subjectRepository.save(subject);
	}

	@PostMapping(path = "/unfollowSubject", consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<String> unfollowSubject(@RequestHeader("Authorization") String jwt,
			@RequestBody SubjectDTO subjectDto) {
		manageSubjectFollowStatus(subjectDto, (subject, student) -> {
			subject.getStudentsFollowing().remove(student);
			fcmService.unsubscribeFromTopic(student.getDeviceToken(), subject.getTopic());
		}, jwt);
		return new ResponseEntity<>("{\"message\": \"Unfollow subject\"}", HttpStatus.OK);
	}

	@PostMapping(path = "/modify", consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<String> modifySubject(@RequestBody SubjectDTO subjectDto) {
		Subject subject = BasicEntityUtils.entityFinder(subjectRepository.findById(subjectDto.getId()));
		Set<Professor> currentProfessors = subject.getSubjectProfessors();
		List<String> ids = subjectDto.getSubjectProfessorsToRemove();
		if (!CollectionUtils.isEmpty(ids)) {
			Iterator<Professor> currentProfessorsIterator = currentProfessors.iterator();
			while (currentProfessorsIterator.hasNext()) {
				Professor us = currentProfessorsIterator.next();
				if (ids.contains(us.getId())) {
					currentProfessorsIterator.remove();
				}
			}
		}
		List<String> professorsToAddIdList = subjectDto.getSubjectProfessors();
		if (!CollectionUtils.isEmpty(professorsToAddIdList)) {
			Set<Professor> professorsToAdd = professorRepository.findByUserIds(professorsToAddIdList);
			currentProfessors = BasicEntityUtils.mergeSet(currentProfessors, professorsToAdd);
		}
		String name = subjectDto.getName();
		if (StringUtils.isNotBlank(name)) {
			subject.setName(name);
		}
		subject.setSubjectProfessors(currentProfessors);
		subjectRepository.save(subject);
		return new ResponseEntity<>(MESSAGE_SUCCED, HttpStatus.OK);
	}

	@GetMapping(path = "/findAll")
	public @ResponseBody ResponseEntity<List<Subject>> findSubjects() {
		return new ResponseEntity<>(subjectRepository.findAll(), HttpStatus.OK);
	}

	@GetMapping(path = "/findClasses/{id}")
	public @ResponseBody ResponseEntity<MappingJacksonValue> findSubjectsClass(@PathVariable("id") long id) {
		List<String> validStatus = Arrays.asList("Confirmada", "En Curso");
		Subject subject = BasicEntityUtils.entityFinder(subjectRepository.findById(id));
		List<Clase> clases = subject.getClases().stream()
				.filter(clase -> clase.getInitTime().isAfter(LocalDateTime.now())
						&& clase.getInitTime().isBefore(LocalDateTime.now().plusDays(14)) && validStatus.contains(clase.getStatus()))
				.sorted(Comparator.comparing(Clase::getInitTime))
				.collect(Collectors.toList());
		clases.parallelStream().forEach(clase -> BasicEntityUtils.validateProfessorMobileStatus(clase.getProfessor()));
		MappingJacksonValue mappingJacksonValue = BasicEntityUtils.applyFilter("classFilter", clases, new String[] {
				"id", "initTime", "endTime", "hasSingleTurnos", "status", "professor", "professor.subjectProfessors" });
		return new ResponseEntity<>(mappingJacksonValue, HttpStatus.OK);
	}

	@PostMapping(path = "/deleteSubject", consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<String> deleteSubject(@RequestBody SubjectDTO subjectDto) {
		Subject subject = BasicEntityUtils.entityFinder(subjectRepository.findById(subjectDto.getId()));
		Set<Clase> clases = subject.getClases();
		clases.parallelStream().forEach(clase -> {
			Set<Turno> turnos = clase.getTurnos();
			turnos.forEach(turno -> turno.getStudents().clear());
			clase.getComments().clear();
			turnos.clear();
		});
		clases.clear();
		subject.getSubjectProfessors().clear();
		subject.getStudentsFollowing().clear();
		subjectRepository.save(subject);
		subjectRepository.delete(subject);
		return new ResponseEntity<>("{\"message\": \"OK\"}", HttpStatus.OK);
	}

	@GetMapping(path = "/modifySubjectInfo/{id}")
	public @ResponseBody ResponseEntity<ModifySubjectResponse> adminSubjectInfo(@PathVariable("id") long id) {
		Subject subject = BasicEntityUtils.entityFinder(subjectRepository.findById(id));
		List<Professor> allProfessors = professorRepository.findAll();
		return new ResponseEntity<>(new ModifySubjectResponse(subject, allProfessors), HttpStatus.OK);
	}

	@GetMapping(path = "/professorClasses/{id}")
	public @ResponseBody ResponseEntity<MappingJacksonValue> adminProfessorClasses(@RequestHeader("Authorization") String jwt, @PathVariable("id") long id) {
		Subject subject = BasicEntityUtils.entityFinder(subjectRepository.findById(id));
		String userToken = jwt.split(StringUtils.SPACE)[1];
		Professor professor = BasicEntityUtils.entityFinder(professorRepository.findByLegajo(jwtUtils.extractUsername(userToken)));
		Map<LocalDateTime, List<Clase>> professorClases = subject.getClases().stream()
			.filter(clase -> clase.getProfessor().equals(professor))
			.sorted(Comparator.comparing(Clase::getInitTime))
			.collect(groupingBy(Clase::getCreationDate));
		MappingJacksonValue mappingJacksonValue = BasicEntityUtils.applyFilter("classFilter", professorClases, new String[] {
				"id", "initTime", "endTime", "hasSingleTurnos", "status", "professor" });
		return new ResponseEntity<>(mappingJacksonValue, HttpStatus.OK);
	}
}
