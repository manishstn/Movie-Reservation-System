package com.moviereservationsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="roles")
@NoArgsConstructor @Data @AllArgsConstructor @Builder
public class Role {

    private Long id;

    private String name;

    private String description;
}
