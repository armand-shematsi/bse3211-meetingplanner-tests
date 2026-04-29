package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

public class MeetingTest {

    private Meeting meeting;
    private Room room;
    private ArrayList<Person> attendees;

    @Before
    public void setUp() {
        room = new Room("LLT6A");
        attendees = new ArrayList<Person>();
        attendees.add(new Person("Namugga Martha"));
        attendees.add(new Person("Shema Collins"));
        meeting = new Meeting(3, 15, 9, 11, attendees, room, "Team Standup");
    }

    // ── NORMAL TESTS ──────────────────────────────────────────────

    @Test
    public void testGetMonth() {
        assertEquals(3, meeting.getMonth());
    }

    @Test
    public void testGetDay() {
        assertEquals(15, meeting.getDay());
    }

    @Test
    public void testGetStartTime() {
        assertEquals(9, meeting.getStartTime());
    }

    @Test
    public void testGetEndTime() {
        assertEquals(11, meeting.getEndTime());
    }

    @Test
    public void testGetDescription() {
        assertEquals("Team Standup", meeting.getDescription());
    }

    @Test
    public void testGetRoom() {
        assertEquals("LLT6A", meeting.getRoom().getID());
    }

    @Test
    public void testGetAttendees() {
        assertEquals(2, meeting.getAttendees().size());
    }

    // ── CONSTRUCTOR TESTS ─────────────────────────────────────────

    @Test
    public void testDefaultConstructor() {
        Meeting m = new Meeting();
        assertNotNull(m);
    }

    @Test
    public void testConstructorMonthDay() {
        Meeting m = new Meeting(5, 10);
        assertEquals(5, m.getMonth());
        assertEquals(10, m.getDay());
        assertEquals(0, m.getStartTime());
        assertEquals(23, m.getEndTime());
    }

    @Test
    public void testConstructorMonthDayDescription() {
        Meeting m = new Meeting(6, 20, "Conference");
        assertEquals(6, m.getMonth());
        assertEquals(20, m.getDay());
        assertEquals("Conference", m.getDescription());
        assertEquals(0, m.getStartTime());
        assertEquals(23, m.getEndTime());
    }

    // ── SETTER TESTS ──────────────────────────────────────────────

    @Test
    public void testSetDescription() {
        meeting.setDescription("Updated Meeting");
        assertEquals("Updated Meeting", meeting.getDescription());
    }

    @Test
    public void testSetMonth() {
        meeting.setMonth(7);
        assertEquals(7, meeting.getMonth());
    }

    @Test
    public void testSetDay() {
        meeting.setDay(25);
        assertEquals(25, meeting.getDay());
    }

    @Test
    public void testSetStartTime() {
        meeting.setStartTime(14);
        assertEquals(14, meeting.getStartTime());
    }

    @Test
    public void testSetEndTime() {
        meeting.setEndTime(16);
        assertEquals(16, meeting.getEndTime());
    }

    @Test
    public void testSetRoom() {
        Room newRoom = new Room("LAB2");
        meeting.setRoom(newRoom);
        assertEquals("LAB2", meeting.getRoom().getID());
    }

    // ── EDGE CASE TESTS ───────────────────────────────────────────

    @Test
    public void testMeetingAtMidnight() {
        Meeting m = new Meeting(1, 1, 0, 1);
        assertEquals(0, m.getStartTime());
        assertEquals(1, m.getEndTime());
    }

    @Test
    public void testMeetingAtLatestHour() {
        Meeting m = new Meeting(1, 1, 22, 23);
        assertEquals(22, m.getStartTime());
        assertEquals(23, m.getEndTime());
    }

    @Test
    public void testMeetingOnJan1() {
        Meeting m = new Meeting(1, 1, 8, 9);
        assertEquals(1, m.getMonth());
        assertEquals(1, m.getDay());
    }

    // ── TOSTRING TESTS ────────────────────────────────────────────

    @Test
    public void testToStringNotEmpty() {
        String result = meeting.toString();
        assertNotNull(result);
        assertTrue(result.length() > 0);
    }

    @Test
    public void testToStringContainsDescription() {
        String result = meeting.toString();
        assertTrue(result.contains("Team Standup"));
    }

    @Test
    public void testToStringContainsRoom() {
        String result = meeting.toString();
        assertTrue(result.contains("LLT6A"));
    }
}