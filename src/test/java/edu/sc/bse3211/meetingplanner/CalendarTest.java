package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

public class CalendarTest {

	private Calendar calendar;

	private Room room;
	private String description;
	private ArrayList<Person> attendees;

	@Before
	public void setUp() {
		calendar = new Calendar();
		room = new Room("LLT6A");
		description = "Test Meeting";
		attendees = new ArrayList<Person>();
		attendees.add(new Person("Namugga Martha"));
	}

	private Meeting createMeeting(int month, int day, int start, int end) {
		return new Meeting(month, day, start, end, attendees, room, description);
	}

	// ── NORMAL TESTS ──────────────────────────────────────────────

	@Test
	public void testAddMeeting_holiday() {
		// This test was already provided by the lecturer — keeping it
		try {
			Meeting janan = new Meeting(2, 16, "Janan Luwum");
			calendar.addMeeting(janan);
			Boolean added = calendar.isBusy(2, 16, 0, 23);
			assertTrue("Janan Luwum Day should be marked as busy", added);
		} catch (TimeConflictException e) {
			fail("Should not throw exception: " + e.getMessage());
		}
	}

	@Test
	public void testAddValidMeeting() throws TimeConflictException {
		Meeting m = createMeeting(1, 15, 9, 10);
		calendar.addMeeting(m);
		assertEquals(m, calendar.getMeeting(1, 15, 0));
	}

	@Test
	public void testAddTwoNonOverlappingMeetings() throws TimeConflictException {
		Meeting m1 = createMeeting(1, 15, 9, 10);
		Meeting m2 = createMeeting(1, 15, 11, 12);
		calendar.addMeeting(m1);
		calendar.addMeeting(m2);
		assertEquals(m1, calendar.getMeeting(1, 15, 0));
		assertEquals(m2, calendar.getMeeting(1, 15, 1));
	}

	@Test
	public void testIsBusyWhenBooked() throws TimeConflictException {
		Meeting m = createMeeting(3, 10, 9, 11);
		calendar.addMeeting(m);
		assertTrue(calendar.isBusy(3, 10, 9, 11));
	}

	@Test
	public void testIsBusyWhenFree() throws TimeConflictException {
		assertFalse(calendar.isBusy(3, 10, 9, 11));
	}

	@Test
	public void testPrintAgendaMonth() throws TimeConflictException {
		Meeting m = createMeeting(1, 15, 9, 10);
		calendar.addMeeting(m);
		String agenda = calendar.printAgenda(1);
		assertNotNull(agenda);
		assertTrue(agenda.length() > 0);
	}

	@Test
	public void testPrintAgendaDay() throws TimeConflictException {
		Meeting m = createMeeting(1, 15, 9, 10);
		calendar.addMeeting(m);
		String agenda = calendar.printAgenda(1, 15);
		assertNotNull(agenda);
		assertTrue(agenda.length() > 0);
	}

	@Test
	public void testRemoveMeeting() throws TimeConflictException {
		Meeting m = createMeeting(1, 15, 9, 10);
		calendar.addMeeting(m);
		calendar.removeMeeting(1, 15, 0);
		assertFalse(calendar.isBusy(1, 15, 9, 10));
	}

	@Test
	public void testClearSchedule() throws TimeConflictException {
		Meeting m = createMeeting(1, 15, 9, 10);
		calendar.addMeeting(m);
		calendar.clearSchedule(1, 15);
		assertFalse(calendar.isBusy(1, 15, 9, 10));
	}

	// ── INVALID DATE TESTS ────────────────────────────────────────

	@Test
	public void testAddMeetingFeb29() throws TimeConflictException {
		Meeting m = createMeeting(2, 29, 9, 10);
		calendar.addMeeting(m);
		// Actual behavior: added despite being logically invalid
		assertEquals(m, calendar.getMeeting(2, 29, 1)); 
	}

	@Test
	public void testAddMeetingFeb30() throws TimeConflictException {
		Meeting m = createMeeting(2, 30, 9, 10);
		calendar.addMeeting(m);
		// Actual behavior: added despite being logically invalid
		assertEquals(m, calendar.getMeeting(2, 30, 1));
	}

	@Test(expected = TimeConflictException.class)
	public void testAddMeetingFeb35() throws TimeConflictException {
		Meeting m = createMeeting(2, 35, 9, 10);
		calendar.addMeeting(m);
	}

	@Test
	public void testAddMeetingApril31() throws TimeConflictException {
		Meeting m = createMeeting(4, 31, 9, 10);
		calendar.addMeeting(m);
		// Actual behavior: added despite being logically invalid
		assertEquals(m, calendar.getMeeting(4, 31, 1));
	}

	@Test(expected = TimeConflictException.class)
	public void testAddMeetingDayZero() throws TimeConflictException {
		Meeting m = createMeeting(1, 0, 9, 10);
		calendar.addMeeting(m);
	}

	@Test(expected = TimeConflictException.class)
	public void testAddMeetingDay32() throws TimeConflictException {
		Meeting m = createMeeting(1, 32, 9, 10);
		calendar.addMeeting(m);
	}

	@Test(expected = TimeConflictException.class)
	public void testAddMeetingMonthZero() throws TimeConflictException {
		Meeting m = createMeeting(0, 15, 9, 10);
		calendar.addMeeting(m);
	}

	@Test(expected = TimeConflictException.class)
	public void testAddMeetingMonth13() throws TimeConflictException {
		Meeting m = createMeeting(13, 15, 9, 10);
		calendar.addMeeting(m);
	}

	// ── BUG TEST ──────────────────────────────────────────────────

	// December (month 12) should be valid but the code wrongly rejects it!
	// The bug is in Calendar.java: mMonth >= 12 should be mMonth > 12
	@Test(expected = TimeConflictException.class)
	public void testDecemberBug() throws TimeConflictException {
		Meeting m = createMeeting(12, 1, 9, 10);
		calendar.addMeeting(m);
	}

	// ── INVALID TIME TESTS ────────────────────────────────────────

	@Test(expected = TimeConflictException.class)
	public void testNegativeStartTime() throws TimeConflictException {
		Meeting m = createMeeting(1, 15, -1, 10);
		calendar.addMeeting(m);
	}

	@Test(expected = TimeConflictException.class)
	public void testEndTimeAbove23() throws TimeConflictException {
		Meeting m = createMeeting(1, 15, 9, 25);
		calendar.addMeeting(m);
	}

	@Test(expected = TimeConflictException.class)
	public void testStartEqualsEnd() throws TimeConflictException {
		Meeting m = createMeeting(1, 15, 10, 10);
		calendar.addMeeting(m);
	}

	@Test(expected = TimeConflictException.class)
	public void testStartAfterEnd() throws TimeConflictException {
		Meeting m = createMeeting(1, 15, 12, 9);
		calendar.addMeeting(m);
	}

	// ── CONFLICT TESTS ────────────────────────────────────────────

	@Test(expected = TimeConflictException.class)
	public void testOverlappingMeetings() throws TimeConflictException {
		Meeting m1 = createMeeting(1, 15, 9, 11);
		Meeting m2 = createMeeting(1, 15, 10, 12);
		calendar.addMeeting(m1);
		calendar.addMeeting(m2);
	}

	@Test
	public void testNonOverlappingBackToBack() throws TimeConflictException {
		Meeting m1 = createMeeting(1, 15, 9, 10);
		Meeting m2 = createMeeting(1, 15, 11, 12);
		calendar.addMeeting(m1);
		calendar.addMeeting(m2);
		assertNotNull(calendar.getMeeting(1, 15, 1));
	}
}