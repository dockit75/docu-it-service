package com.docuitservice.exception;

import java.io.Serializable;

public class ResponseErrors implements Serializable {

	private static final long serialVersionUID = 1L;

	private String errorType;

	private String errorCode;

	private String shortMessage;

	private String longMessage;

	private String logMessage;

	private String notificationRequired;

	private String notificationType;

	private int resourceObjectIndex = 0;

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the shortMessage
	 */
	public String getShortMessage() {
		return shortMessage;
	}

	/**
	 * @param shortMessage the shortMessage to set
	 */
	public void setShortMessage(String shortMessage) {
		this.shortMessage = shortMessage;
	}

	/**
	 * @return the longMessage
	 */
	public String getLongMessage() {
		return longMessage;
	}

	/**
	 * @param longMessage the longMessage to set
	 */
	public void setLongMessage(String longMessage) {
		this.longMessage = longMessage;
	}

	/**
	 * @return the logMessage
	 */
	public String getLogMessage() {
		return logMessage;
	}

	/**
	 * @param logMessage the logMessage to set
	 */
	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}

	/**
	 * @return the notificationRequired
	 */
	public String getNotificationRequired() {
		return notificationRequired;
	}

	/**
	 * @param notificationRequired the notificationRequired to set
	 */
	public void setNotificationRequired(String notificationRequired) {
		this.notificationRequired = notificationRequired;
	}

	/**
	 * @return the notificationType
	 */
	public String getNotificationType() {
		return notificationType;
	}

	/**
	 * @param notificationType the notificationType to set
	 */
	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public ResponseErrors clone() {
		ResponseErrors error = new ResponseErrors();
		error.setErrorCode(this.errorCode);
		error.setErrorType(this.errorType);
		error.setLogMessage(this.logMessage);
		error.setLongMessage(this.longMessage);
		error.setNotificationRequired(this.notificationRequired);
		error.setNotificationType(this.notificationType);
		error.setShortMessage(this.shortMessage);
		return error;
	}

	/**
	 * @return the resourceObjectIndex
	 */
	public int getResourceObjectIndex() {
		return resourceObjectIndex;
	}

	/**
	 * @param resourceObjectIndex the resourceObjectIndex to set
	 */
	public void setResourceObjectIndex(int resourceObjectIndex) {
		this.resourceObjectIndex = resourceObjectIndex;
	}
}
