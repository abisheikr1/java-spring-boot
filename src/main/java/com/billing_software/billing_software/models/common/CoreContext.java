package com.billing_software.billing_software.models.common;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoreContext {

    private Date createdDate; // Date when the record was created in the system
    private Date lastUpdatedDate; // Date when the record was last updated
    private String createdBy; // User or system identifier who created the record
    private String updatedBy; // User or system identifier who last updated the record
    private String isDeleted; // Flag to indicate if the record is deleted ("Yes" or "No")
    private String deletedBy; // User or system identifier who deleted the record
    private Date deletedOn; // Date when the record was marked as deleted

}
