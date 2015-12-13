package org.barag.textspansion.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Expansion {
    @Getter private String key;
    @Getter private String value;
    @Getter private String description;
}
