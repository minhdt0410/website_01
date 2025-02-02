package com.example.website.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilterRequest {
    private String name;
    private String size;
    private String color;
    private String id;
}
