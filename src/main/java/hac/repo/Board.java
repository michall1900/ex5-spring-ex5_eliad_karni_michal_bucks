package hac.repo;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    @Column(unique = true)
    @NotBlank(message="User's id must be inserted.")
    private String username;



}