package com.biit.appointment.persistence.exceptions;

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

import com.biit.appointment.logger.AppointmentCenterLogger;

import java.io.Serial;

public class InvalidScheduleException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 4546925557940053858L;

    public InvalidScheduleException(Class<?> clazz, String message) {
        super(message);
        AppointmentCenterLogger.severe(clazz, message);
    }

    public InvalidScheduleException(Class<?> clazz, Throwable e) {
        super(e);
        AppointmentCenterLogger.errorMessage(clazz, e);
    }
}
