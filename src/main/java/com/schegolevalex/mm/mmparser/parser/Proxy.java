package com.schegolevalex.mm.mmparser.parser;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Proxy {
    private String host;
    private String port;
    private String username;
    private String password;
}
