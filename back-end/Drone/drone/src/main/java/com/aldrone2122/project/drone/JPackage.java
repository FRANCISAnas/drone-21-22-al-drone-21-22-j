package com.aldrone2122.project.drone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JPackage {
    private Long id;
    private Double weight;
    private Coordinates destination;
    private Dimensions dimensions;
}
