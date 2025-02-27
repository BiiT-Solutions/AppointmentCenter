package com.biit.appointment.core.providers;


import com.biit.appointment.persistence.entities.Availability;
import com.biit.appointment.persistence.entities.AvailabilityRange;
import com.biit.appointment.persistence.repositories.AvailabilityRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class AvailabilityProvider extends ElementProvider<Availability, Long, AvailabilityRepository> {

    public AvailabilityProvider(AvailabilityRepository repository) {
        super(repository);
    }


    public Optional<Availability> findByUser(UUID userId) {
        return getRepository().findByUser(userId);
    }


    public Availability set(Collection<AvailabilityRange> availabilityRanges, UUID user) {
        final Availability userAvailability = getRepository().findByUser(user).orElse(new Availability(user));
        userAvailability.setRanges(new ArrayList<>(availabilityRanges));
        return getRepository().save(userAvailability);
    }


    public Availability add(AvailabilityRange availabilityRange, UUID user) {
        final Availability userAvailability = getRepository().findByUser(user).orElse(new Availability(user));
        userAvailability.addRange(availabilityRange);
        return getRepository().save(userAvailability);
    }


    public Availability remove(AvailabilityRange availabilityRange, UUID user) {
        final Availability userAvailability = getRepository().findByUser(user).orElse(new Availability(user));
        //No availability defined. Nothing to remove.
        if (userAvailability.getId() == null) {
            return null;
        }
        userAvailability.removeRange(availabilityRange);
        return getRepository().save(userAvailability);
    }

    public Availability removeAll(UUID user) {
        final Availability userAvailability = getRepository().findByUser(user).orElse(new Availability(user));
        //No availability defined. Nothing to remove.
        if (userAvailability.getId() == null) {
            return null;
        }
        userAvailability.getRanges().clear();
        return getRepository().save(userAvailability);
    }
}
