package com.computefx.example.wallet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class WalletApplicationTests extends WithMysqlContainer {

	@Test
	void contextLoads() {
	}

}
