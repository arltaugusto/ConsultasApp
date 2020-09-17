package com.project.consultas.utils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.project.consultas.entities.Professor;

public abstract class BasicEntityUtils {

    private static final Logger logger = LoggerFactory.getLogger(BasicEntityUtils.class);

    private BasicEntityUtils() {}

    public static <U> ResponseEntity<U> save(U entity, JpaRepository<U, ?> repository) {
        repository.save(entity);
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    public static <T> T convertToEntityFromString(Class<T> object, String jsonString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, object);
    }

    public static <T> T entityFinder(Optional<T> entity) {
        if(!entity.isPresent()) {
            logger.error("Entity not found");
        }
        return entity.get();
    }

    public static String randomPassword() {
        return RandomStringUtils.random(5, true, true);
    }
    
    public static <T> Set<T> mergeSet(Set<T> a, Set<T> b) { 
       Set<T> mergedSet = new HashSet<>();
       mergedSet.addAll(a);
       mergedSet.addAll(b);
       return mergedSet;
    }
    
    public static String[] dateFormat(LocalDateTime date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return date.format(formatter).split(StringUtils.SPACE);
    }
    
    public static MappingJacksonValue applyFilter(String filter, Object values, String[] fields) {
    	Set<String> fieldsSet = new HashSet<>();
    	Arrays.stream(fields).forEach(fieldsSet::add);
    	FilterProvider filterProvider = new SimpleFilterProvider().addFilter(filter, SimpleBeanPropertyFilter.filterOutAllExcept(fields));
    	MappingJacksonValue mapper = new MappingJacksonValue(values);
    	mapper.setFilters(filterProvider);
    	return mapper;
    }
    
    public static void validateProfessorMobileStatus(Professor professor) {
    	if(!professor.isShowMobile()) {
    		professor.setMobile("secret");
    	}
    }
}