package com.schegolevalex.mm.mmparser;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class MmParserApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(MmParserApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
