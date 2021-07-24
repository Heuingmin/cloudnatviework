package com.nju.cloudnative_final;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@AutoConfigureMockMvc
@SpringBootTest(classes = CloudNativeFinalApplication.class)
@RunWith(SpringRunner.class)
class CloudNativeFinalApplicationTests {
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private MockMvc mvc;

    @Before
    public void setupMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void contextLoads() {
        try {
            for (int i = 0; i < 100; i++) {
                System.out.println("第" + (i + 1) + "次");
                mvc.perform(MockMvcRequestBuilders
                        .get("/hello"))
                        .andExpect(MockMvcResultMatchers.status().isOk());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
