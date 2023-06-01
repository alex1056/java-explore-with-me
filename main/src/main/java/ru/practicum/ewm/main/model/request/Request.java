package ru.practicum.ewm.main.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "request", schema = "public")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime created;
    @Column(name = "requester_id")
    private Long requester;
    @Column(name = "event_id")
    private Long event;
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
