package com.project.consultas.controllers;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import com.project.consultas.dto.*;
import com.project.consultas.entities.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.consultas.notifications.ClassCancelledPushNotification;
import com.project.consultas.notifications.ClassStartPushNotification;
import com.project.consultas.notifications.FCMService;
import com.project.consultas.notifications.FirstStudentSubscribedPushNotification;
import com.project.consultas.notifications.NewClassPushNotification;
import com.project.consultas.notifications.NewCommentNotification;
import com.project.consultas.notifications.NoStudentPushNotification;
import com.project.consultas.repositories.ClaseRepository;
import com.project.consultas.repositories.CommentRepository;
import com.project.consultas.repositories.ProfessorRepository;
import com.project.consultas.repositories.StudentRepository;
import com.project.consultas.repositories.SubjectRepository;
import com.project.consultas.repositories.TurnoRepository;
import com.project.consultas.security.JwtUtils;
import com.project.consultas.utils.BasicEntityUtils;

@CrossOrigin
@RestController
@RequestMapping(path = "/clases")
public class ClaseController {

	private static final String CONFIRMADA = "confirmada";
	private static final String MESSAGE_SUCCED = "{\"message\": \"Suceed\"}";

	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private SubjectRepository subjectRepository;
	@Autowired
	private ClaseRepository claseRepository;
	@Autowired
	private ProfessorRepository professorRepository;
	@Autowired
	private TurnoRepository turnoRepository;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private FCMService fcmService;
	@Autowired
	private CommentRepository commentRepository;

	private static final List<DayOfWeek> dayOfWeekList = Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
	private Logger logger = LoggerFactory.getLogger(ClaseController.class);

	@PostMapping(path = "/add", consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<Set<LocalDateTime>> addNewClase(@RequestHeader("Authorization") String jwt,
			@RequestBody ClaseDTO claseDTO) {
		Subject subject = BasicEntityUtils.entityFinder(subjectRepository.findById(claseDTO.getSubjectId()));
		String userToken = jwt.split(StringUtils.SPACE)[1];
		Professor user = BasicEntityUtils
				.entityFinder(professorRepository.findByLegajo(jwtUtils.extractUsername(userToken)));
		boolean hasSingleTurnos = claseDTO.isHasSingleTurnos();
		validateData(claseDTO, subject, user, hasSingleTurnos);
		LocalDateTime claseInitTime = claseDTO.getInitTime();
		LocalDateTime currentTime = LocalDateTime.now();
		int year = currentTime.getYear();
		if (claseInitTime.getYear() != year || dayOfWeekList.contains(claseDTO.getInitTime().getDayOfWeek())
				|| claseInitTime.compareTo(currentTime) < 0) {
			throw new IllegalStateException("Bad date");
		}
		long duration = hasSingleTurnos ? claseDTO.getDurationInMinutes()
				: claseDTO.getTurnoDuration() * claseDTO.getCantidadTurnos() ;
		Set<LocalDateTime> notAdded = new HashSet<>();
		if (claseDTO.isRegular()) {
			LocalDateTime localDateTimeLimit = LocalDateTime.of(year, Month.DECEMBER, 23, 0, 0, 0);
			List<LocalDateTime> consultDates = new ArrayList<>();
			while (claseInitTime.compareTo(localDateTimeLimit) < 0) {
				if (user.checkAvailability(null, claseInitTime, claseInitTime.plusMinutes(duration))) {
					consultDates.add(claseInitTime);
				} else {
					notAdded.add(claseInitTime);
				}
				claseInitTime = claseInitTime.plusDays(7);
			}
			consultDates.parallelStream().forEach(date -> buildClase(claseDTO, date, subject, user, duration, currentTime));
		} else {
			if (user.checkAvailability(null, claseInitTime, claseInitTime.plusMinutes(duration))) {
				buildClase(claseDTO, claseInitTime, subject, user, duration, currentTime);
			} else {
				throw new IllegalStateException("You have a class at that time");
			}
		}
		return new ResponseEntity<>(notAdded, HttpStatus.OK);
	}

	private void sendNotification(Clase clase) {
		LocalDateTime currentTime = LocalDateTime.now();
		if (clase.getInitTime().isBefore(currentTime.plusWeeks(2))) {
			Subject subject = clase.getSubject();
			fcmService.sendMessageWithData(new NewClassPushNotification(subject.getTopic(), clase.getProfessor().getName(),
					subject.getName(), clase.getInitTime(), clase.getClassNotificationData()));
		}
	}

	private void validateData(ClaseDTO claseDTO, Subject subject, Professor user, boolean hasSingleTurnos) {
		if(hasSingleTurnos &&  claseDTO.getDurationInMinutes() == 0) {
			throw new IllegalStateException("Incomplete Data for class without turns");
		}
		if(!hasSingleTurnos && (claseDTO.getCantidadTurnos() == 0 || claseDTO.getTurnoDuration() == 0)) {
			throw new IllegalStateException("Incomplete Data for class with turns");
		}
		if(!user.getSubjectProfessors().contains(subject)) {
			throw new IllegalStateException("You are not allowed to create a class of this subject");
		}
	}

	private void buildClase(ClaseDTO claseDTO, LocalDateTime dateTime, Subject subject, Professor professor,
			long duration, LocalDateTime currentTime) {
		String claseId = UUID.randomUUID().toString().replace("-", StringUtils.EMPTY);
		Clase clase = new Clase.Builder()
				.setId(claseId)
				.setInitTime(dateTime)
				.setDurationInMinutes(duration)
				.setHasSingleTurnos(claseDTO.isHasSingleTurnos())
				.setSubject(subject)
				.setProfessor(professor)
				.setCreationDate(currentTime)
				.build();
		claseRepository.save(clase);
		Set<Turno> turnos = new HashSet<>();
		if (claseDTO.isHasSingleTurnos()) {
			TurnoPK turnoPk = new TurnoPK(claseId, claseDTO.getInitTime());
			Turno turno = new Turno(turnoPk, clase.getEndTime());
			turno.setClase(clase);
			turnos.add(turno);
		} else {
			LocalDateTime turnoInitTime = dateTime;
			for (int i = 0; i < claseDTO.getCantidadTurnos(); i++) {
				TurnoPK turnoPk = new TurnoPK(claseId, turnoInitTime);
				LocalDateTime endTime = turnoInitTime.plusMinutes(claseDTO.getTurnoDuration());
				Turno turno = new Turno(turnoPk, endTime);
				turno.setClase(clase);
				turnos.add(turno);
				turnoInitTime = endTime;
			}
		}
		sendNotification(clase);
		turnoRepository.saveAll(turnos);
	}

	@PostMapping(path = "/subscribe", consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<String> addNewSubscription(@RequestHeader("Authorization") String jwt,
			@RequestBody ClaseDTO claseDTO) {
		String consultaId = claseDTO.getConsultaId();
		Clase clase = BasicEntityUtils.entityFinder(claseRepository.findById(consultaId));
		TurnoPK turnoPk = new TurnoPK(consultaId, claseDTO.getInitTime());
		Turno turno = BasicEntityUtils.entityFinder(turnoRepository.findById(turnoPk));
		String userToken = jwt.split(StringUtils.SPACE)[1];
		Student student = BasicEntityUtils
				.entityFinder(studentRepository.findByLegajo(jwtUtils.extractUsername(userToken)));
		Set<Student> subcribedStudents = turno.getStudents();
		if (clase.getClassStudents().contains(student)) {
			return new ResponseEntity<>("{\"message\": \"You're already subscribed in this class\"}", HttpStatus.BAD_REQUEST);
		}
			if (!student.checkAvailability(null, clase.getInitTime(), clase.getEndTime())) {
			return new ResponseEntity<>("{\"message\": \"You have a booking at this time\"}", HttpStatus.BAD_REQUEST);
		}
		if (!clase.getStatus().equalsIgnoreCase(CONFIRMADA)) {
			return new ResponseEntity<>("{\"message\": \"You can't book now\"}", HttpStatus.BAD_REQUEST);
		}
		if (clase.isHasSingleTurnos()) {
			validateNotification(clase, turno, student, subcribedStudents);
		} else {
			if (subcribedStudents.isEmpty()) {
				validateNotification(clase, turno, student, subcribedStudents);
			} else {
				return new ResponseEntity<>("{\"message\": \"Already booked\"}", HttpStatus.BAD_REQUEST);
			}
		}
		fcmService.subscribeToTopic(student.getDeviceToken(), clase.getTopic());
		return new ResponseEntity<>(MESSAGE_SUCCED, HttpStatus.OK);
	}

	@PostMapping(path = "/unsubscribe", consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<String> removeSubscription(@RequestHeader("Authorization") String jwt,
			@RequestBody ClaseDTO claseDTO) {
		String consultaId = claseDTO.getConsultaId();
		Clase clase = BasicEntityUtils.entityFinder(claseRepository.findById(consultaId));
		TurnoPK turnoPk = new TurnoPK(consultaId, claseDTO.getInitTime());
		Turno turno = BasicEntityUtils.entityFinder(turnoRepository.findById(turnoPk));
		String userToken = jwt.split(StringUtils.SPACE)[1];
		Student student = BasicEntityUtils
				.entityFinder(studentRepository.findByLegajo(jwtUtils.extractUsername(userToken)));
		Set<Student> students = turno.getStudents();
		if (Arrays.asList("Finalizada", "En curso").contains(clase.getStatus())) {
			return new ResponseEntity<>("{\"message\": \"You cant' unsuscribe now\"}",
					HttpStatus.BAD_REQUEST);
		}
		if (!students.contains(student)) {
			return new ResponseEntity<>("{\"message\": \"You're not subscribed to this subject\"}",
					HttpStatus.BAD_REQUEST);
		}
		students.remove(student);
		if (students.isEmpty()) {
			turno.setHasUsers(false);
		}
		turnoRepository.save(turno);
		if (clase.countStudentsSubscribed() == 0) {
			NoStudentPushNotification noStudentPushNotification = new NoStudentPushNotification(
					clase.getProfessor().getDeviceToken(), clase.getSubject().getName(), clase.getInitTime(), clase.getClassNotificationData());
			fcmService.sendMessageToToken(noStudentPushNotification);
		}
		fcmService.unsubscribeFromTopic(student.getDeviceToken(), clase.getTopic());
		return new ResponseEntity<>("{\"message\": \"Subscription remove\"}", HttpStatus.OK);
	}

	private void validateNotification(Clase clase, Turno turno, Student student, Set<Student> subcribedStudents) {
		if (clase.countStudentsSubscribed() == 0) {
			turno.setHasUsers(true);
			subcribedStudents.add(student);
			turnoRepository.save(turno);
			FirstStudentSubscribedPushNotification firstStudentNotification = new FirstStudentSubscribedPushNotification(
					clase.getSubject().getName(), clase.getInitTime(), clase.getProfessor().getDeviceToken(),
					"userSubscription", clase.getClassNotificationData());
			fcmService.sendMessageToToken(firstStudentNotification);
		} else {
			subcribedStudents.add(student);
			turnoRepository.save(turno);
		}
	}

	@PostMapping(path = "/addComment", consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<String> addNewComment(@RequestHeader("Authorization") String jwt,
			@RequestBody CommentDTO commentDTO) {
		List<String> validStatus = Arrays.asList("Confirmada", "En Curso");
		String id = commentDTO.getId();
		Clase clase = BasicEntityUtils.entityFinder(claseRepository.findById(id));
		String userToken = jwt.split(StringUtils.SPACE)[1];
		Professor professor = BasicEntityUtils
				.entityFinder(professorRepository.findByLegajo(jwtUtils.extractUsername(userToken)));
		String commentString = commentDTO.getComment();
		if (!professor.getClases().contains(clase)) {
			return new ResponseEntity<>("{\"message\": \"This class isn't yours!\"}", HttpStatus.BAD_REQUEST);
		}
		if (StringUtils.isBlank(commentString)) {
			return new ResponseEntity<>("{\"message\": \"Cannot add empty comment\"}", HttpStatus.BAD_REQUEST);
		}
		if (!validStatus.contains(clase.getStatus())) {
			return new ResponseEntity<>("{\"message\": \"Cannot add comment to a cancelled class\"}",
					HttpStatus.BAD_REQUEST);
		}
		CommentPK commentPk = new CommentPK(id, LocalDateTime.now());
		Comment comment = new Comment(commentPk, commentString);
		comment.setClase(clase);
		commentRepository.save(comment);
		NewCommentNotification newCommentNotification = new NewCommentNotification(clase.getTopic(),
				clase.getSubject().getName(), clase.getInitTime(), clase.getProfessor().getName(), commentString, clase.getClassNotificationData());
		fcmService.sendMessageWithData(newCommentNotification);
		return new ResponseEntity<>(MESSAGE_SUCCED, HttpStatus.OK);
	}

	@PostMapping(path = "/deleteComment", consumes = "application/json", produces = "application/json")
	public @ResponseBody ResponseEntity<String> deleteComment(@RequestHeader("Authorization") String jwt,
															  @RequestBody CommentDTO commentDTO) {
		CommentPK commentPK = new CommentPK(commentDTO.getId(), commentDTO.getDateTime());
		Comment comment = BasicEntityUtils.entityFinder(commentRepository.findById(commentPK));
		commentRepository.delete(comment);
		return new ResponseEntity<>(MESSAGE_SUCCED, HttpStatus.OK);
	}

	@GetMapping(path = "/findAll/{id}")
	public @ResponseBody ResponseEntity<Set<Clase>> findClases(@PathVariable("id") long id) {
		Subject subject = BasicEntityUtils.entityFinder(subjectRepository.findById(id));
		return new ResponseEntity<>(subject.getClases().stream()
				.filter(clase -> clase.getInitTime().compareTo(LocalDateTime.now().plusHours(2)) > 0)
				.collect(Collectors.toSet()), HttpStatus.OK);
	}

	@GetMapping(path = "/startClass/{id}")
	public @ResponseBody ResponseEntity<String> startClass(@RequestHeader("Authorization") String jwt,
			@PathVariable("id") String id) {
		Clase clase = BasicEntityUtils.entityFinder(claseRepository.findById(id));
		String classStatus = clase.getStatus();
		String userToken = jwt.split(StringUtils.SPACE)[1];
		Professor professor = BasicEntityUtils
				.entityFinder(professorRepository.findByLegajo(jwtUtils.extractUsername(userToken)));
		if(!clase.getStatus().equalsIgnoreCase(CONFIRMADA)) {
			return new ResponseEntity<>("{\"message\": \"No podes empezar la clase ahora\"}", HttpStatus.BAD_REQUEST);
		}
		if (!professor.getClases().contains(clase)) {
			return new ResponseEntity<>("{\"message\": \"Esta clase no es tuya!\"}", HttpStatus.BAD_REQUEST);
		}
		if (LocalDateTime.now().isBefore(clase.getInitTime().minusMinutes(5))) {
			return new ResponseEntity<>("{\"message\": \"No podes empezar la clase ahora\"}", HttpStatus.BAD_REQUEST);
		}
		clase.setStatus("En Curso");
		claseRepository.save(clase);
		fcmService.sendMessageWithData(new ClassStartPushNotification(clase.getSubject().getName(), clase.getTopic(), clase.getClassNotificationData()));
		return new ResponseEntity<>(MESSAGE_SUCCED, HttpStatus.OK);
	}

	@PostMapping(path = "/cancelClass", produces = "application/json")
	public @ResponseBody ResponseEntity<String> cancelClass(@RequestHeader("Authorization") String jwt, @RequestBody ClassesToRemoveDTO classesToRemove) {
		List<Clase> clases = claseRepository.findAllById(classesToRemove.getClassesToRemove()); 
		String userToken = jwt.split(StringUtils.SPACE)[1];
		Professor professor = BasicEntityUtils
				.entityFinder(professorRepository.findByLegajo(jwtUtils.extractUsername(userToken)));
		clases.forEach(clase -> {
			if (professor.getClases().contains(clase)) {
				clase.setStatus("Cancelada");
				claseRepository.save(clase);
				ClassCancelledPushNotification classCanceledPushNotification = new ClassCancelledPushNotification(
						clase.getTopic(), clase.getSubject().getName(), clase.getProfessor().getName(),
						clase.getInitTime(), clase.getClassNotificationData());
				fcmService.sendMessageWithData(classCanceledPushNotification);
			}
		});
		return new ResponseEntity<>(MESSAGE_SUCCED, HttpStatus.OK);
	}

	@GetMapping(path = "/findClassData/{id}")
	public @ResponseBody ResponseEntity<ClassDataDTO> findClassTurns(@RequestHeader("Authorization") String jwt,
			@PathVariable("id") String id) {
		Clase clase = BasicEntityUtils.entityFinder(claseRepository.findById(id));
		List<TurnosResponse> turnos = clase.getTurnos().stream()
			.map(turno -> new TurnosResponse(turno.getTurnoPk().getStartTime(), turno.getStudents()))
			.sorted(Comparator.comparing(TurnosResponse::getTurnoTime))
			.collect(Collectors.toList());
		return new ResponseEntity<>(new ClassDataDTO(turnos, clase.getComments(), clase.getProfessor(), clase.getSubject(), clase.getStatus(), clase.isHasSingleTurnos(), clase.getInitTime()),
				HttpStatus.OK);
	}

	@GetMapping(path = "/findProfessorClasses", params = {"professorId", "subjectId"})
	public @ResponseBody ResponseEntity<MappingJacksonValue> findSubjectProfessorClasses(
			@RequestParam String professorId, @RequestParam long subjectId) {
		Subject subject = BasicEntityUtils.entityFinder(subjectRepository.findById(subjectId));
		Professor professor = BasicEntityUtils.entityFinder(professorRepository.findById(professorId));
		BasicEntityUtils.validateProfessorMobileStatus(professor);
		List<Clase> classes = subject.getClases().stream()
			.filter(clase -> clase.getProfessor().equals(professor) && !clase.getStatus().equalsIgnoreCase("Cancelada") && clase.getInitTime().isAfter(LocalDateTime.now()))
			.sorted(Comparator.comparing(Clase::getInitTime))
			.collect(Collectors.toList());
		ProfessorClassesDTO professorClassesDto = new ProfessorClassesDTO(professor, classes);
		MappingJacksonValue response = BasicEntityUtils.applyFilter("classFilter", professorClassesDto, new String[] {"id", "initTime", "endTime", "hasSingleTurnos", "status"});
		return new ResponseEntity<>(response , HttpStatus.OK);
	}
}
