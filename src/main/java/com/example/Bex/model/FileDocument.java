package com.example.Bex.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "files")
public class FileDocument {

    @Id
    private String id;
    private String fileName;
    private String b2Key;
    private Date uploadDate;
    private long size;
    private String contentType;
}
