package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

public class RoomTest {

    private Room room;
    private ArrayList<Person> attendees;

    @Before
    public void setUp() {
        room = new Room("LLT6A");
        attendees = new ArrayList<Person>();
        attendees.add(new Person("Kazibwe Julius"));
        attendees.add(new Person("Kukunda Lynn"));
    }

    // ── NORMAL TESTS ──────────────────────────────────────────────

    // Room ID should be stored correctly
    @Test
    public void testGetID() {
        assertEquals("LLT6A", room.getID());
    }

    // Default constructor should create room with empty ID
    @Test
    public void testDefaultConstructor() {
        Room r = new Room();
        assertNotNull(r);
        assertEquals("", r.getID());
    }

    // Adding a valid meeting should work
    @Test
    public void testAddValidMeeting() throws TimeConflictException {
        Meeting m = new Meeting(1, 15, 9, 10, attendees, room, "Lecture");
        room.addMeeting(m);
        assertEquals(m, room.getMeeting(1, 15, 0));
    }

    // Adding two non-overlapping meetings should work
    @Test
    public void testAddTwoNonOverlappingMeetings() throws TimeConflictException {
        Meeting m1 = new Meeting(1, 15, 9, 10, attendees, room, "Lecture");
        Meeting m2 = new Meeting(1, 15, 11, 12, attendees, room, "Lab");
        room.addMeeting(m1);
        room.addMeeting(m2);
        assertEquals(m1, room.getMeeting(1, 15, 0));
        assertEquals(m2, room.getMeeting(1, 15, 1));
    }

    // isBusy should return true after booking
    @Test
    public void testIsBusyAfterBooking() throws TimeConflictException {
        Meeting m = new Meeting(3, 10, 9, 11, attendees, room, "Workshop");
        room.addMeeting(m);
        assertTrue(room.isBusy(3, 10, 9, 11));
    }

    // isBusy should return false when nothing booked
    @Test
    public void testIsBusyWhenFree() throws TimeConflictException {
        assertFalse(room.isBusy(3, 10, 9, 11));
    }

    // printAgenda for a month should return non-null string
    @Test
    public void testPrintAgendaMonth() throws TimeConflictException {
        Meeting m = new Meeting(1, 15, 9, 10, attendees, room, "Lecture");
        room.addMeeting(m);
        String agenda = room.printAgenda(1);
        assertNotNull(agenda);
        assertTrue(agenda.length() > 0);
    }

    // printAgenda for a day should contain the meeting description
    @Test
    public void testPrintAgendaDayContainsDescription() throws TimeConflictException {
        Meeting m = new Meeting(1, 15, 9, 10, attendees, room, "Final Exam");
        room.addMeeting(m);
        String agenda = room.printAgenda(1, 15);
        assertTrue(agenda.contains("Final Exam"));
    }

    // printAgenda for a specific day should return non-null string
    @Test
    public void testPrintAgendaDay() throws TimeConflictException {
        Meeting m = new Meeting(1, 15, 9, 10, attendees, room, "Lecture");
        room.addMeeting(m);
        String agenda = room.printAgenda(1, 15);
        assertNotNull(agenda);
        assertTrue(agenda.length() > 0);
    }

    // Removing a meeting should free the room
    @Test
    public void testRemoveMeeting() throws TimeConflictException {
        Meeting m = new Meeting(1, 15, 9, 10, attendees, room, "Lecture");
        room.addMeeting(m);
        room.removeMeeting(1, 15, 0);
        assertFalse(room.isBusy(1, 15, 9, 10));
    }

    // getMeeting should return the correct meeting
    @Test
    public void testGetMeeting() throws TimeConflictException {
        Meeting m = new Meeting(5, 20, 14, 15, attendees, room, "Seminar");
        room.addMeeting(m);
        Meeting retrieved = room.getMeeting(5, 20, 0);
        assertEquals("Seminar", retrieved.getDescription());
    }

    // Two different rooms should be independent of each other
    @Test
    public void testTwoRoomsAreIndependent() throws TimeConflictException {
        Room room2 = new Room("LLT6B");
        Meeting m1 = new Meeting(1, 15, 9, 10, attendees, room, "Lecture in A");
        Meeting m2 = new Meeting(1, 15, 9, 10, attendees, room2, "Lecture in B");
        room.addMeeting(m1);
        room2.addMeeting(m2); // same time, different room — should NOT throw
        assertTrue(room.isBusy(1, 15, 9, 10));
        assertTrue(room2.isBusy(1, 15, 9, 10));
    }

    // ── CONFLICT TESTS ────────────────────────────────────────────

    // Two overlapping meetings in same room should throw
    @Test(expected = TimeConflictException.class)
    public void testAddOverlappingMeetings() throws TimeConflictException {
        Meeting m1 = new Meeting(1, 15, 9, 11, attendees, room, "Lecture");
        Meeting m2 = new Meeting(1, 15, 10, 12, attendees, room, "Lab");
        room.addMeeting(m1);
        room.addMeeting(m2);
    }

    // Exception message should mention the room ID
    @Test
    public void testConflictExceptionMentionsRoomID() {
        try {
            Meeting m1 = new Meeting(1, 15, 9, 11, attendees, room, "Lecture");
            Meeting m2 = new Meeting(1, 15, 10, 12, attendees, room, "Lab");
            room.addMeeting(m1);
            room.addMeeting(m2);
            fail("Expected TimeConflictException was not thrown");
        } catch (TimeConflictException e) {
            assertTrue(e.getMessage().contains("LLT6A"));
        }
    }

    // ── INVALID DATE TESTS ────────────────────────────────────────

    // February 29 does not exist — should throw
    @Test(expected = TimeConflictException.class)
    public void testAddMeetingFeb29() throws TimeConflictException {
        Meeting m = new Meeting(2, 35, 9, 10, attendees, room, "Ghost Meeting");
        room.addMeeting(m);
    }

    // Day 0 does not exist — should throw
    @Test(expected = TimeConflictException.class)
    public void testAddMeetingDayZero() throws TimeConflictException {
        Meeting m = new Meeting(1, 0, 9, 10, attendees, room, "Invalid Meeting");
        room.addMeeting(m);
    }

    // isBusy on invalid date should throw
    @Test(expected = TimeConflictException.class)
    public void testIsBusyInvalidDate() throws TimeConflictException {
        room.isBusy(2, 35, 9, 10);
    }

    // isBusy on invalid time should throw
    @Test(expected = TimeConflictException.class)
    public void testIsBusyInvalidTime() throws TimeConflictException {
        room.isBusy(1, 15, 25, 26);
    }

    // isBusy on invalid month should throw
    @Test(expected = TimeConflictException.class)
    public void testIsBusyInvalidMonth() throws TimeConflictException {
        room.isBusy(13, 1, 9, 10);
    }

    // ── INVALID TIME TESTS ────────────────────────────────────────

    // Start after end should throw
    @Test(expected = TimeConflictException.class)
    public void testStartAfterEnd() throws TimeConflictException {
        Meeting m = new Meeting(1, 15, 12, 9, attendees, room, "Bad Meeting");
        room.addMeeting(m);
    }

    // Negative start time should throw
    @Test(expected = TimeConflictException.class)
    public void testNegativeStartTime() throws TimeConflictException {
        Meeting m = new Meeting(1, 15, -1, 10, attendees, room, "Bad Meeting");
        room.addMeeting(m);
    }

    // End time above 23 should throw
    @Test(expected = TimeConflictException.class)
    public void testEndTimeAbove23() throws TimeConflictException {
        Meeting m = new Meeting(1, 15, 9, 25, attendees, room, "Bad Meeting");
        room.addMeeting(m);
    }
}