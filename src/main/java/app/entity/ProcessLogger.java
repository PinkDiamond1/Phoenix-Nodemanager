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
    private String name;

    private long pid;

    private long timestamp;

    private String status;

}
