package com.aldrone2122.project.jolydrone;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@AutoConfigureEmbeddedDatabase
@ExtendWith(SpringExtension.class)
class SpringBootSi5alProjectApplicationTests {

    @Test
    void contextLoads() {
    }

}
