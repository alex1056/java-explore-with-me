package ru.practicum.ewm.main.model.compilation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.main.model.event.Event;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "compilation", schema = "public")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Boolean pinned;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "compilation_event",
            joinColumns = {@JoinColumn(name = "comp_id")},
            inverseJoinColumns = {@JoinColumn(name = "event_id")}
    )
    List<Event> events = new ArrayList();

    public String getTitle() {
        if (title != null) return title.trim();
        return null;
    }
}
