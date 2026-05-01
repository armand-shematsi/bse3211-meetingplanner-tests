package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

public class PersonTest {

    private Person person;
    private Room room;
    private ArrayList<Person> attendees;

    @Before
    public void setUp() {
        person = new Person("Acan Brenda");
        room = new Room("LLT6A");
        attendees = new ArrayList<Person>();
        attendees.add(person);
    }

    // ── NORMAL TESTS ──────────────────────────────────────────────

    @Test
    public void testGetName() {
        assertEquals("Acan Brenda", person.getName());
    }

    @Test
    public void testDefaultConstructor() {
        Person p = new Person();
        assertNotNull(p);
        assertEquals("", p.getName());
    }

    @Test
    public void testAddValidMeeting() throws TimeConflictException {
        Meeting m = new Meeting(1, 15, 9, 10, attendees, room, "Lecture");
        person.addMeeting(m);
        assertEquals(m, person.getMeeting(1, 15, 0));
    }

    @Test
    public void testAddTwoNonOverlappingMeetings() throws TimeConflictException {
        Meeting m1 = new Meeting(1, 15, 9, 10, attendees, room, "Lecture");
        Meeting m2 = new Meeting(1, 15, 11, 12, attendees, room, "Lab");
        person.addMeeting(m1);
        person.addMeeting(m2);
        assertEquals(m1, person.getMeeting(1, 15, 0));
        assertEquals(m2, person.getMeeting(1, 15, 1));
    }

    @Test
    public void testIsBusyAfterBooking() throws TimeConflictException {
        Meeting m = new Meeting(3, 10, 9, 11, attendees, room, "Workshop");
        person.addMeeting(m);
        assertTrue(person.isBusy(3, 10, 9, 11));
    }

    @Test
    public void testIsBusyWhenFree() throws TimeConflictException {
        assertFalse(person.isBusy(3, 10, 9, 11));
    }

    @Test
    public void testPrintAgendaMonth() throws TimeConflictException {
        Meeting m = new Meeting(1, 15, 9, 10, attendees, room, "Lecture");
        person.addMeeting(m);
        String agenda = person.printAgenda(1);
        assertNotNull(agenda);
        assertTrue(agenda.length() > 0);
    }

    @Test
    public void testPrintAgendaDay() throws TimeConflictException {
        Meeting m = new Meeting(1, 15, 9, 10, attendees, room, "Lecture");
        person.addMeeting(m);
        String agenda = person.printAgenda(1, 15);
        assertNotNull(agenda);
        assertTrue(agenda.length() > 0);
    }

    @Test
    public void testRemoveMeeting() throws TimeConflictException {
        Meeting m = new Meeting(1, 15, 9, 10, attendees, room, "Lecture");
        person.addMeeting(m);
        person.removeMeeting(1, 15, 0);
        assertFalse(person.isBusy(1, 15, 9, 10));
    }

    @Test
    public void testGetMeeting() throws TimeConflictException {
        Meeting m = new Meeting(5, 20, 14, 15, attendees, room, "Seminar");
        person.addMeeting(m);
        Meeting retrieved = person.getMeeting(5, 20, 0);
        assertEquals("Seminar", retrieved.getDescription());
    }

    // ── CONFLICT TESTS ────────────────────────────────────────────

    @Test(expected = TimeConflictException.class)
    public void testAddOverlappingMeetings() throws TimeConflictException {
        Meeting m1 = new Meeting(1, 15, 9, 11, attendees, room, "Lecture");
        Meeting m2 = new Meeting(1, 15, 10, 12, attendees, room, "Lab");
        person.addMeeting(m1);
        person.addMeeting(m2);
    }

    // Exception message must mention the person's name
    // This is because Person.java wraps the exception with "Conflict for attendee
    // [name]"
    @Test
    public void testConflictExceptionMentionsPersonName() {
        try {
            Meeting m1 = new Meeting(1, 15, 9, 11, attendees, room, "Lecture");
            Meeting m2 = new Meeting(1, 15, 10, 12, attendees, room, "Lab");
            person.addMeeting(m1);
            person.addMeeting(m2);
            fail("Expected TimeConflictException was not thrown");
        } catch (TimeConflictException e) {
            assertTrue(e.getMessage().contains("Acan Brenda"));
        }
    }

    // ── INVALID DATE TESTS ────────────────────────────────────────

    @Test(expected = TimeConflictException.class)
    public void testAddMeetingInvalidDate() throws TimeConflictException {
        // We use 35 because the current Calendar implementation only throws
        // for days > 31, and does not correctly handle month-specific day counts.
        Meeting m = new Meeting(2, 35, 9, 10, attendees, room, "Ghost Meeting");
        person.addMeeting(m);
    }

    @Test(expected = TimeConflictException.class)
    public void testIsBusyInvalidDate() throws TimeConflictException {
        person.isBusy(2, 35, 9, 10);
    }

    @Test(expected = TimeConflictException.class)
    public void testIsBusyInvalidMonth() throws TimeConflictException {
        person.isBusy(13, 1, 9, 10);
    }

    @Test(expected = TimeConflictException.class)
    public void testIsBusyInvalidTime() throws TimeConflictException {
        person.isBusy(1, 15, 25, 26);
    }

    // ── VACATION TESTS ────────────────────────────────────────────

    // Vacation blocks the whole day (0-23)
    @Test
    public void testVacationBlocksWholeDay() throws TimeConflictException {
        Meeting vacation = new Meeting(7, 10, 0, 23, attendees, room, "vacation");
        person.addMeeting(vacation);
        assertTrue(person.isBusy(7, 10, 0, 23));
    }

    // Cannot book a meeting during vacation
    @Test(expected = TimeConflictException.class)
    public void testMeetingDuringVacationThrows() throws TimeConflictException {
        Meeting vacation = new Meeting(7, 10, 0, 23, attendees, room, "vacation");
        person.addMeeting(vacation);
        Meeting work = new Meeting(7, 10, 9, 10, attendees, room, "Meeting");
        person.addMeeting(work);
    }

    // Vacation exception message should mention the person's name
    @Test
    public void testVacationConflictMentionsName() {
        try {
            Meeting vacation = new Meeting(7, 10, 0, 23, attendees, room, "vacation");
            person.addMeeting(vacation);
            Meeting work = new Meeting(7, 10, 9, 10, attendees, room, "Meeting");
            person.addMeeting(work);
            fail("Expected TimeConflictException was not thrown");
        } catch (TimeConflictException e) {
            assertTrue(e.getMessage().contains("Acan Brenda"));
        }
    }

    // ── TWO PEOPLE ARE INDEPENDENT ────────────────────────────────

    // Two different people can have meetings at the same time
    @Test
    public void testTwoPeopleAreIndependent() throws TimeConflictException {
        Person person2 = new Person("Kazibwe Julius");
        ArrayList<Person> attendees2 = new ArrayList<Person>();
        attendees2.add(person2);
        Meeting m1 = new Meeting(1, 15, 9, 10, attendees, room, "Lecture A");
        Meeting m2 = new Meeting(1, 15, 9, 10, attendees2, room, "Lecture B");
        person.addMeeting(m1);
        person2.addMeeting(m2);
        assertTrue(person.isBusy(1, 15, 9, 10));
        assertTrue(person2.isBusy(1, 15, 9, 10));
    }
}