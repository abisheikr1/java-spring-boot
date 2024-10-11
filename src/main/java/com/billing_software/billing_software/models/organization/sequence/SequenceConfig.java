package com.billing_software.billing_software.models.organization.sequence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SequenceConfig {

    private String sequencePrefix; // Prefix for the sequence number (e.g., "INV-")
    private Integer sequenceLength; // No of leading zeros to maintain length
    private Integer sequenceNumber; // Current sequence number (e.g., 1, 2, 3, ...)

    private ClearenceType clearenceType;
}
