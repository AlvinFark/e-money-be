package com.project.emoney.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailToken {
  long id;
  long userId;
  String token;
  LocalDateTime time;
}
