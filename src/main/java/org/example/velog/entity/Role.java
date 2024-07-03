package org.example.velog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "roles")
@Getter@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;
    @Column(nullable = false)
    private String roleName;
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
}
