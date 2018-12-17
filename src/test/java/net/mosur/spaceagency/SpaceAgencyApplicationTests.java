package net.mosur.spaceagency;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SpaceAgencyApplicationTests {

	private MockMvc mockMvc;

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private WebApplicationContext context;

	@Before
	public void setUp(){
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}


}

