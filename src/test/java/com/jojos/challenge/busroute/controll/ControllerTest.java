package com.jojos.challenge.busroute.controll;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Basic integration test for the controller
 *
 * @author karanikasg@gmail.com.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG)
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testApiWithoutBothRequestParams() throws Exception {
        mockMvc.perform(get("/api/direct")).
                andExpect(status().isBadRequest());
    }

    @Test
    public void testApiWithoutOneRequestParams() throws Exception {
        mockMvc.perform(get("/api/direct").param("dep_sid", "5")).
                andExpect(status().isBadRequest());
    }

    @Test
    public void testApiWithAllRequestParams() throws Exception {
        mockMvc.perform(get("/api/direct").param("dep_sid", "5").param("arr_sid", "6")).
                andExpect(status().isOk());
    }

}
