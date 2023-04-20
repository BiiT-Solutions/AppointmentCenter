package com.biit.appointment.persistence.entities;

import java.util.ArrayList;
import java.util.List;

public enum AppointmentStatus {
	
	SUGGESTED(0),

	NOT_STARTED(1),

	CANCELLED(2),

	NO_SHOW(2),

	STARTED(4),

	EDITION_STARTED(4),

	EXAMINATION_CLOSED(5),

	EXAMINATION_EDITIONS_CLOSED(5),

	REPORT_CLOSED(6),

	EDITION_CLOSED(6),

	;

	private final int order;

	private AppointmentStatus(int order) {
		this.order = order;
	}

	/**
	 * Checks if the current status is greater than.
	 * 
	 * @param status
	 *            status to compare.
	 * @return true if it is passed.
	 */
	public boolean isStatusPassed(AppointmentStatus status) {
		return order >= status.order;
	}

	public boolean arePendingActions() {
		return order < REPORT_CLOSED.order;
	}

	public static AppointmentStatus getStatus(String name) {
		for (final AppointmentStatus status : AppointmentStatus.values()) {
			if (status.name().equalsIgnoreCase(name)) {
				return status;
			}
		}
		return null;
	}

	/**
	 * Returns a list of Appointments
	 * 
	 * @param status
	 *            status to compare.
	 * @return a list of AppointmentStatus.
	 */
	public static List<AppointmentStatus> getAllStatusSmallerThan(AppointmentStatus status) {
		final List<AppointmentStatus> statusList = new ArrayList<AppointmentStatus>();
		for (final AppointmentStatus compareStatus : AppointmentStatus.values()) {
			if (!compareStatus.isStatusPassed(status)) {
				statusList.add(compareStatus);
			}
		}

		return statusList;

	}

	public static List<AppointmentStatus> getAllStatusEqualOrHigherThan(AppointmentStatus status) {
		final List<AppointmentStatus> statusList = new ArrayList<AppointmentStatus>();
		for (final AppointmentStatus compareStatus : AppointmentStatus.values()) {
			if (compareStatus.isStatusPassed(status)) {
				statusList.add(compareStatus);
			}
		}
		return statusList;

	}
}
