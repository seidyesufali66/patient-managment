package com.pm.patientservice.dto;
import java.util.List;
public class PagedPatientresponseDTO {
    private List<PatientResponseDTO> patients;
    private int page;
    private int size;
    private long totalPages;
    private int totalElements;
    public PagedPatientresponseDTO(List<PatientResponseDTO> patients, int page, int size, long totalPages, int totalElements) {
        this.patients = patients;
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
    public PagedPatientresponseDTO() {

    }

    public List<PatientResponseDTO> getPatients() {
        return patients;
    }

    public void setPatients(List<PatientResponseDTO> patients) {
        this.patients = patients;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }
}
