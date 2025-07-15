package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
public class Item {
    private long id;

    private String name;
    private String description;
    private boolean available;
    private User owner;
    //  private ItemRequest request;

    public Item(String name, String description, boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
