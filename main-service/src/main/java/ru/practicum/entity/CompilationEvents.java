package ru.practicum.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "compilation_events")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CompilationEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "compilation_id")
    private Compilation compilation;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompilationEvents)) return false;
        return id != null && id.equals(((CompilationEvents) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
