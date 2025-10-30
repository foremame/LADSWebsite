package lads.lads_website.controller;

import lads.lads_website.domain.ActivityLoveInterest;
import lads.lads_website.domain.ActivityRunPeriod;
import lads.lads_website.domain.Event;
import lads.lads_website.forms.ActivityForm;
import lads.lads_website.forms.subforms.ActivitySubForm;
import lads.lads_website.service.ActivityLoveInterestService;
import lads.lads_website.service.ActivityRunPeriodService;
import lads.lads_website.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@Controller
public class EventController {

    private final EventService eventService;
    private final ActivityRunPeriodService activityRunPeriodService;
    private final ActivityLoveInterestService activityLoveInterestService;

    @Autowired
    public EventController(EventService eventService, ActivityRunPeriodService activityRunPeriodService, ActivityLoveInterestService activityLoveInterestService) {
        this.eventService = eventService;
        this.activityRunPeriodService = activityRunPeriodService;
        this.activityLoveInterestService = activityLoveInterestService;
    }

    @GetMapping("/event/add")
    public String getEventForm(Model model) {
        model.addAttribute("eventTypes", eventService.getAllEventTypes());
        model.addAttribute("loveInterests", activityLoveInterestService.getAllLoveInterests());
        return "/event/addEvent";
    }

    @PostMapping("/event/add")
    public String addNewEvent(ActivityForm eventForm) {
        Event event = new Event();
        event.setEventType(eventForm.getMainType());
        event.setName(eventForm.getName());
        if (eventForm.getActivitySubForms() == null) { throw new RuntimeException("No love interest selected"); }

        event = eventService.addNewEvent(event);

        ActivityRunPeriod arp = new ActivityRunPeriod();
        LocalDate startDate = LocalDate.parse(eventForm.getStartDate());
        LocalDate endDate = LocalDate.parse(eventForm.getEndDate());

        arp.setEndDate(endDate);
        arp.setStartDate(startDate);
        arp.setEvent(event);
        arp.setActivityRunType("Event");
        arp.setRunNum(0);
        arp.setRerun(false);

        activityRunPeriodService.addNewActivityRunPeriod(arp);

        for (ActivitySubForm activitySubForm : eventForm.getActivitySubForms()) {
            if (activitySubForm.getLoveInterest() != null) {
                ActivityLoveInterest activityLoveInterest = new ActivityLoveInterest();
                activityLoveInterest.setLoveInterestType(activitySubForm.getLoveInterest());
                activityLoveInterest.setActivityRunType("Event");
                activityLoveInterest.setEvent(event);
                activityLoveInterestService.addNewActivityLoveInterest(activityLoveInterest);
            }
        }

        return "redirect:/home";
    }

    @RequestMapping(value="/event/getAll", method= RequestMethod.GET)
    @ResponseBody
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @RequestMapping(value="/event/validateEventForm", method= RequestMethod.GET)
    @ResponseBody
    public List<String> validateEventForm(String eventName, String startDate, String endDate) {
        List<String> validationResults = new ArrayList<>();

        Optional<Event> event = eventService.getEventByName(eventName);
        if (event.isPresent()) {
            validationResults.add("Event " + eventName + " already exists.");
        }

        try {
            LocalDate startDateParsed = LocalDate.parse(startDate);
        } catch (DateTimeParseException e) {
            validationResults.add("Start date provided: " + startDate + " is not in the expected format (yyyy/[m]m/[d]d) and could not be parsed.");
        }

        try {
            LocalDate endDateParsed = LocalDate.parse(endDate);
        } catch (DateTimeParseException e) {
            validationResults.add("End date provided: " + endDate + " is not in the expected format (yyyy/[m]m/[d]d) and could not be parsed.");
        }

        return validationResults;
    }
}
