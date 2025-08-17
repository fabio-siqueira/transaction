package com.test.transaction.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Meta {
    private int count;

    @JsonProperty("total-count")
    private int totalCount;

    @JsonProperty("total-pages")
    private int totalPages;

}
