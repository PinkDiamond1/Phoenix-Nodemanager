package app.entity;

import lombok.*;

import javax.persistence.*;
import javax.transaction.Transactional;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Transactional
public class ProcessLogger {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private long pid;

    private long timestamp;

    private String name;

    private String status;

    private int exitCode;

}
