package com.billing_software.billing_software.models.item;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BatchDetails {

    private Date batchDate; // Date of the batch
    private String batchTitle; // Batch title
    private String batchDescription; // Batch description
    
}
