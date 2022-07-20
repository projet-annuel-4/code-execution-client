package com.projet_annuel.rec.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeRequest implements Serializable {

    private Long id;
    private String code;
    private String language;
    private CompilationMode mode;
    private boolean test;

    public CodeRequest(String code) {
        this.code = code;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isTest() {
        return test;
    }

    public Long getId() {
        return id;
    }

    public CompilationMode getMode() {
        return mode;
    }

    public String getCode() {
        return code;
    }

    public String getLanguage() {
        return language;
    }

}
