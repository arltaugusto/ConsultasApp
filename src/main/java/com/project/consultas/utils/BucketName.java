package com.project.consultas.utils;

public enum BucketName {
	
	PROFILE_IMAGE_BUCKET("profileImages"),
	SUBJECT_IMAGE_BUCKET("subjectImages");
	
	private final String bucketName;
	
	BucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	
	public String getBucketName() {
		return bucketName;
	}
}