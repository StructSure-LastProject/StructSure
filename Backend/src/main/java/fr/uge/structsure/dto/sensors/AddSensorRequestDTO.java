package fr.uge.structsure.dto.sensors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record AddSensorRequestDTO(long id,
                                  String controlChip,
                                  String measureChip,
                                  String name,
                                  String note,
                                  String installationDate,
                                  double x,
                                  double y) {
}
