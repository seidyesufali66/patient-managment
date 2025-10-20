package com.pm.patientservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.patientservice.dto.PatientRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for PatientController, with a real PostgreSQL DB.
 * Uses @SpringBootTest to start the full app, and @AutoConfigureMockMvc for HTTP simulation.
 */
@SpringBootTest // Bootstraps the whole Spring context (includes DB, web layer, etc.)
@AutoConfigureMockMvc // Sets up MockMvc, so you can simulate HTTP requests/responses
@ActiveProfiles("test") // Uses application-test.properties config (connects to test_db)
@Transactional // Rolls back DB changes after each test (keeps DB clean between tests)
public class PatientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // Main test tool for simulating HTTP requests

    @Autowired
    private ObjectMapper objectMapper; // Converts Java objects to JSON, and vice versa
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanTable() {
        jdbcTemplate.execute("DELETE FROM patient");
    }

    /**
     * Test creating a patient and then retrieving all patients.
     * Demonstrates POST (creation) and GET (reading) endpoints.
     */
    @Test
    void testCreateAndGetAllPatients() throws Exception {
        // Build a PatientRequestDTO with valid data
        PatientRequestDTO patientRequestDTO = new PatientRequestDTO();
        patientRequestDTO.setName("Seid Ali");
        patientRequestDTO.setEmail("seid@test.com");
        patientRequestDTO.setAddress("456 Avenue");
        patientRequestDTO.setDateOfBirth(LocalDate.parse("1985-05-05"));
        patientRequestDTO.setRegisteredDate(LocalDate.parse("2025-10-05")); // today or before
        // --- Act: Create patient using POST /patients ---
        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON) // Request body is JSON
                        .content(objectMapper.writeValueAsString(patientRequestDTO))) // Convert DTO to JSON
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)) // JSON response
                .andExpect(jsonPath("$.id").exists()) // Response contains generated patient id
                .andExpect(jsonPath("$.name").value("Seid Ali")) // Name matches
                .andExpect(jsonPath("$.email").value("seid@test.com")); // Email matches

        // --- Assert: Fetch all patients using GET /patients ---
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)) // JSON array
                .andExpect(jsonPath("$").isArray()) // Response is an array
                .andExpect(jsonPath("$[0].name").value("Seid Ali")) // First patient's name matches
                .andExpect(jsonPath("$[0].email").value("seid@test.com")); // First patient's email matches
    }
    @Test
    void testCreateAndGetPatientById() throws Exception{
        PatientRequestDTO patientRequestDTO = new PatientRequestDTO();
        patientRequestDTO.setName("Jane Doe");
        patientRequestDTO.setEmail("jane@test.com");
        patientRequestDTO.setAddress("123 Main St");
        patientRequestDTO.setDateOfBirth(LocalDate.parse("1990-01-01"));
        patientRequestDTO.setRegisteredDate(LocalDate.parse("2025-10-05"));

        // -- Act: POST /patients, get the response JSON, extract the id --
        String response = mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequestDTO)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText(); // Extract UUID
        mockMvc.perform(get("/patients/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@test.com"));
    }
    @Test
    void testUpdatePatient() throws Exception{
        PatientRequestDTO patientRequestDTO = new PatientRequestDTO();

        patientRequestDTO.setName("Original Name");
        patientRequestDTO.setEmail("update@test.com");
        patientRequestDTO.setAddress("999 Old St");
        patientRequestDTO.setDateOfBirth(LocalDate.parse("1980-01-01"));
        patientRequestDTO.setRegisteredDate(LocalDate.parse("2025-10-05"));

        String response = mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequestDTO)))
                .andReturn().getResponse().getContentAsString();
        String id = objectMapper.readTree(response).get("id").asText();

        // -- Prepare update DTO (registeredDate may be optional for update) --
        PatientRequestDTO updateDTO = new PatientRequestDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setEmail("update@test.com");
        updateDTO.setAddress("1000 New St");
        updateDTO.setDateOfBirth(LocalDate.parse("1980-01-01"));

        // -- PUT /patients/{id} updates patient --
        mockMvc.perform(put("/patients/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.address").value("1000 New St"));
    }
    @Test
    void testDeletePatient() throws Exception {
        // -- Create a patient --
        PatientRequestDTO patientRequestDTO = new PatientRequestDTO();
        patientRequestDTO.setName("Delete Me");
        patientRequestDTO.setEmail("delete@test.com");
        patientRequestDTO.setAddress("404 Gone St");
        patientRequestDTO.setDateOfBirth(LocalDate.parse("1988-12-12"));
        patientRequestDTO.setRegisteredDate(LocalDate.parse("2025-10-05"));

        String response = mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientRequestDTO)))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        // -- DELETE /patients/{id} --
        mockMvc.perform(delete("/patients/" + id))
                .andExpect(status().isNoContent()); // 204 No Content

        // -- GET /patients/{id} should now return 404 or not found --
        mockMvc.perform(get("/patients/" + id))
                .andExpect(status().isNotFound());
    }


}