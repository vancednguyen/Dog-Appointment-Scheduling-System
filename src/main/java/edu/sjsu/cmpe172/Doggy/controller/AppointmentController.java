package edu.sjsu.cmpe172.Doggy.controller;

import edu.sjsu.cmpe172.Doggy.model.Appointment;
import edu.sjsu.cmpe172.Doggy.model.AppointmentBookingResponse;
import edu.sjsu.cmpe172.Doggy.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    //@GetMapping
    //public List<Appointment> list() {
     //   return service.getAllAppointments();
   // }

    //@GetMapping("/{id}")
   // public Appointment getOne(@PathVariable Long id) {
    //    try {
      //      return service.getAppointmentOrThrow(id);
      //  } catch (IllegalArgumentException e) {
       //     throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        //}
    //}

    @PostMapping
    public Appointment createAppointment(@RequestBody Appointment appointment) {
        try {
            return service.addAppointment(appointment);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/book-with-confirmation")
    public AppointmentBookingResponse bookWithConfirmation(@RequestBody Appointment appointment) {
        try {
            return service.bookAndSendConfirmation(appointment);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
