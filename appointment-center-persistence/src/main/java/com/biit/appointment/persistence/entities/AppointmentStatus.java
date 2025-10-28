package com.biit.appointment.persistence.entities;

/*-
 * #%L
 * AppointmentCenter (Persistence)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

public enum AppointmentStatus {

    SUGGESTED(0),

    NOT_STARTED(1),

    CANCELLED(2),

    NO_SHOW(2),

    STARTED(4),

    FINISHED(6);

    private final int order;

    AppointmentStatus(int order) {
        this.order = order;
    }

    /**
     * Checks if the current status is greater than.
     *
     * @param status status to compare.
     * @return true if it is passed.
     */
    public boolean isStatusPassed(AppointmentStatus status) {
        return order >= status.order;
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
     * @param status status to compare.
     * @return a list of AppointmentStatus.
     */
    public static List<AppointmentStatus> getAllStatusSmallerThan(AppointmentStatus status) {
        final List<AppointmentStatus> statusList = new ArrayList<>();
        for (final AppointmentStatus compareStatus : AppointmentStatus.values()) {
            if (!compareStatus.isStatusPassed(status)) {
                statusList.add(compareStatus);
            }
        }

        return statusList;

    }

    public static List<AppointmentStatus> getAllStatusEqualOrHigherThan(AppointmentStatus status) {
        final List<AppointmentStatus> statusList = new ArrayList<>();
        for (final AppointmentStatus compareStatus : AppointmentStatus.values()) {
            if (compareStatus.isStatusPassed(status)) {
                statusList.add(compareStatus);
            }
        }
        return statusList;

    }
}
