package ru.practicum.ewm.main.model.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.main.model.Category;
import ru.practicum.ewm.main.model.Location;
import ru.practicum.ewm.main.model.User;
import ru.practicum.ewm.main.model.compilation.Compilation;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "event", schema = "public")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "category_id")
    private Long categoryId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;
    @Column(name = "created")
    private LocalDateTime createdOn;
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    private String annotation;
    private String title;
    @Column(name = "initiator_id")
    private Long initiatorId;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "initiator_id", insertable = false, updatable = false)
    private User initiator;
    @Column(name = "location_id")
    private Long locationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", insertable = false, updatable = false)
    private Location location;
    private Boolean paid;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    private LocalDateTime published;
    @Enumerated(EnumType.STRING)
    private EventState state;

    @JsonIgnore
    @ManyToMany(mappedBy = "events")
    private List<Compilation> compilations = new ArrayList<>();

    @Transient
    private Long views;
    @Transient
    private Long confirmedRequests;

    public String getDescription() {
        return description != null ? description.trim() : null;
    }

    public String getAnnotation() {
        if (annotation != null) return annotation.trim();
        return null;
    }

    public String getTitle() {
        if (title != null) return title.trim();
        return null;
    }
}
