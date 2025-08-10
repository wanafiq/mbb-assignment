package com.example.assignment.domains;

import jakarta.persistence.*;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "job_logs")
public class JobLog extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "filename",  length = 100, nullable = false)
    private String fileName;

    @Column(name = "status",  length = 20, nullable = false)
    private String status;
}
