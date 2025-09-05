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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compilation_id")
    @ToString.Exclude
    private Compilation compilation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @ToString.Exclude
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