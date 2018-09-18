package com.zulkar.facedetection.enums;

public enum CascadeMapping {
	Eyes("C:/Users/zulkar.nain/eclipse-workspace/FaceDetection/ObjectDetection/resourcesfiles/eyes_haarcascade.xml"),
	Face("C:/Users/zulkar.nain/eclipse-workspace/FaceDetection/ObjectDetection/resourcesfiles/haarcascade_frontalface_default.xml"), 
	LicensePlate("C:/Users/zulkar.nain/eclipse-workspace/FaceDetection/ObjectDetection/resourcesfiles/license_plate_detector.xml"), 
	Car("C:/Users/zulkar.nain/eclipse-workspace/FaceDetection/ObjectDetection/resourcesfiles/eyes_haarcascade.xml");
	String objectType;

	CascadeMapping(String objectType) {
		this.objectType = objectType;
	}

	public String getObjectType() {
		return objectType;
	}
}
