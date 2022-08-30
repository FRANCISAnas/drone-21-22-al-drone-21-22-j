package com.jolydrone.flightmonitor;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@AutoConfigureEmbeddedDatabase
@ExtendWith(SpringExtension.class)
class FlightmonitorApplicationTests {

    @Test
    void contextLoads() {
    }

}
