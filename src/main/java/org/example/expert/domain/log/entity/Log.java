package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.example.expert.domain.common.entity.Timestamped;

@Entity
@NoArgsConstructor
@Table(name = "log")
public class Log extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    public Log(String message) {
        this.message = message;
    }
}
