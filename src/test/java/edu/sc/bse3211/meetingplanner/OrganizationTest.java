package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class OrganizationTest {

    private Organization org;

    @Before
    public void setUp() {
        org = new Organization();
    }

    // ── EMPLOYEES TESTS ───────────────────────────────────────────

    // Organization should have 5 employees by default
    @Test
    public void testGetEmployeesCount() {
        assertEquals(5, org.getEmployees().size());
    }

    // Employees list should not be null
    @Test
    public void testGetEmployeesNotNull() {
        assertNotNull(org.getEmployees());
    }

    // Should find an existing employee by name
    @Test
    public void testGetEmployeeExists() throws Exception {
        Person p = org.getEmployee("Namugga Martha");
        assertNotNull(p);
        assertEquals("Namugga Martha", p.getName());
    }

    // Should find all 5 employees by name
    @Test
    public void testGetAllEmployeesByName() throws Exception {
        assertNotNull(org.getEmployee("Namugga Martha"));
        assertNotNull(org.getEmployee("Shema Collins"));
        assertNotNull(org.getEmployee("Acan Brenda"));
        assertNotNull(org.getEmployee("Kazibwe Julius"));
        assertNotNull(org.getEmployee("Kukunda Lynn"));
    }

    // Should throw exception for employee that does not exist
    @Test(expected = Exception.class)
    public void testGetEmployeeNotFound() throws Exception {
        org.getEmployee("John Doe");
    }

    // Exception message should say employee does not exist
    @Test
    public void testGetEmployeeNotFoundMessage() {
        try {
            org.getEmployee("John Doe");
            fail("Expected exception was not thrown");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("does not exist"));
        }
    }

    // Empty name should throw exception
    @Test(expected = Exception.class)
    public void testGetEmployeeEmptyName() throws Exception {
        org.getEmployee("");
    }

    // ── ROOMS TESTS ───────────────────────────────────────────────

    // Organization should have 5 rooms by default
    @Test
    public void testGetRoomsCount() {
        assertEquals(5, org.getRooms().size());
    }

    // Rooms list should not be null
    @Test
    public void testGetRoomsNotNull() {
        assertNotNull(org.getRooms());
    }

    // Should find an existing room by ID
    @Test
    public void testGetRoomExists() throws Exception {
        Room r = org.getRoom("LLT6A");
        assertNotNull(r);
        assertEquals("LLT6A", r.getID());
    }

    // Should find all 5 rooms by ID
    @Test
    public void testGetAllRoomsByID() throws Exception {
        assertNotNull(org.getRoom("LLT6A"));
        assertNotNull(org.getRoom("LLT6B"));
        assertNotNull(org.getRoom("LLT3A"));
        assertNotNull(org.getRoom("LLT2C"));
        assertNotNull(org.getRoom("LAB2"));
    }

    // Should throw exception for room that does not exist
    @Test(expected = Exception.class)
    public void testGetRoomNotFound() throws Exception {
        org.getRoom("ROOM999");
    }

    // Exception message should say room does not exist
    @Test
    public void testGetRoomNotFoundMessage() {
        try {
            org.getRoom("ROOM999");
            fail("Expected exception was not thrown");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("does not exist"));
        }
    }

    // Empty room ID should throw exception
    @Test(expected = Exception.class)
    public void testGetRoomEmptyID() throws Exception {
        org.getRoom("");
    }

    // ── INTEGRATION TESTS ─────────────────────────────────────────

    // Should be able to get an employee and book a meeting for them
    @Test
    public void testBookMeetingForEmployee() throws Exception {
        Person p = org.getEmployee("Acan Brenda");
        Room r = org.getRoom("LLT6A");
        Meeting m = new Meeting(3, 15, 9, 10, org.getEmployees(), r, "Team Meeting");
        p.addMeeting(m);
        assertTrue(p.isBusy(3, 15, 9, 10));
    }

    // Should be able to check room availability after booking
    @Test
    public void testCheckRoomAvailabilityAfterBooking() throws Exception {
        Room r = org.getRoom("LAB2");
        Meeting m = new Meeting(3, 15, 9, 10, org.getEmployees(), r, "Lab Session");
        r.addMeeting(m);
        assertTrue(r.isBusy(3, 15, 9, 10));
    }

    // Two different employees can have meetings at the same time
    @Test
    public void testTwoEmployeesCanMeetSameTime() throws Exception {
        Person p1 = org.getEmployee("Namugga Martha");
        Person p2 = org.getEmployee("Shema Collins");
        Room r1 = org.getRoom("LLT6A");
        Room r2 = org.getRoom("LLT6B");

        Meeting m1 = new Meeting(4, 10, 9, 10, org.getEmployees(), r1, "Meeting A");
        Meeting m2 = new Meeting(4, 10, 9, 10, org.getEmployees(), r2, "Meeting B");

        p1.addMeeting(m1);
        p2.addMeeting(m2);

        assertTrue(p1.isBusy(4, 10, 9, 10));
        assertTrue(p2.isBusy(4, 10, 9, 10));
    }
}