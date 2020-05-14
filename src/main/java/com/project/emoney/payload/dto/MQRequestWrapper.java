package com.project.emoney.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//for putting message and queue name into one data
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MQRequestWrapper {
  String queue;
  String message;
}
