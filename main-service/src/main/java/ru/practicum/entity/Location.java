package ru.practicum.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "locations")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonIgnore
    private Long id;

    @JoinColumn(name = "lat")
    private float lat;

    @JoinColumn(name = "lon")
    private float lon;

    public Location(float lat, float lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        return id != null && id.equals(((Location) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}