package edu.sjsu.cmpe172.Doggy.repository;

import edu.sjsu.cmpe172.Doggy.model.Appointment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AppointmentRepository {
    private final Map<Long, Appointment> store = new LinkedHashMap<>();
    private Long nextId = 1L;

    public AppointmentRepository() {
        save(new Appointment(1L, "U1", "P1", "S1", "SLOT1", "BOOKED"));
        save(new Appointment(2L, "U2", "P2", "S2", "SLOT2", "BOOKED"));
    }

    public List<Appointment> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Appointment> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Appointment save(Appointment appt) {
        if (appt.getId() == null) {
            appt.setId(nextId++);
        } else if (appt.getId() >= nextId) {
            nextId = appt.getId() + 1;
        }
        store.put(appt.getId(), appt);
        return appt;
    }
}
