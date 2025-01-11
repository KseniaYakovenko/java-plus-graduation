package ru.practicum.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.enums.RequestStatus;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@EqualsAndHashCode(of = "id")
@Table(name = "REQUESTS")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUEST_ID")
    private Long id;

    @Column(name = "EVENT_ID")
    private Long eventId;

    @Column(name = "REQUESTER_ID")
    private Long requesterId;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(name = "CREATED")
    private LocalDateTime created;

}
