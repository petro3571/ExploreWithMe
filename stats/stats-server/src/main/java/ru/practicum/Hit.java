package ru.practicum;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hits")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "app")
    private String app;

    @Column(name = "uri")
    private String uri;

    @Column(name = "ip")
    private String ip;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hit)) return false;
        return id != null && id.equals(((Hit) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}