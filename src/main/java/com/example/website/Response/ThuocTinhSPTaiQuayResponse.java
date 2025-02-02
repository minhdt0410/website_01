package com.example.website.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThuocTinhSPTaiQuayResponse {
    private List<String> names;
    private List<String> sizes;
    private List<String> colors;
}
