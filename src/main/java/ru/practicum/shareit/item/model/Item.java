package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.shareit.user.model.User;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor

public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Необходимо указать название вещи")
    private String name;

    @Column(name = "description", nullable = false)
    @NotBlank(message = "Необходимо оставить описание вещи")
    @Length(max = 200)
    private String description;

    @Column(name = "available", nullable = false)
    private boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    //  private ItemRequest request;

    public Item(String name, String description, boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }

}
