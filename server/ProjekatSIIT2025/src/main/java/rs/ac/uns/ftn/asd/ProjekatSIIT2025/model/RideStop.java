package rs.ac.uns.ftn.asd.ProjekatSIIT2025.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class RideStop {
    @Id
    @GeneratedValue
    private Long id;

    private String address;     // "Bulevar kralja Aleksandra 73"
    private double latitude;
    private double longitude;

    private int orderIndex;     // 0=start, n=destination

    @ManyToOne
    private Ride ride;
}
