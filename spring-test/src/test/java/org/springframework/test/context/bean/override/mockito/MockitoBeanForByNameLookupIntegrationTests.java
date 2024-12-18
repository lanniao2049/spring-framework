/*
 * Copyright 2002-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.context.bean.override.mockito;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.bean.override.example.ExampleService;
import org.springframework.test.context.bean.override.example.RealExampleService;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.mockito.MockitoAssertions;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link MockitoBean} that use by-name lookup.
 */
@SpringJUnitConfig
public class MockitoBeanForByNameLookupIntegrationTests {

	@MockitoBean("field")
	ExampleService field;

	@MockitoBean("nestedField")
	ExampleService nestedField;

	@MockitoBean("field")
	ExampleService renamed1;

	@MockitoBean("nestedField")
	ExampleService renamed2;

	@MockitoBean("nonExistingBean")
	ExampleService nonExisting1;

	@MockitoBean("nestedNonExistingBean")
	ExampleService nonExisting2;


	@Test
	void fieldAndRenamedFieldHaveSameOverride(ApplicationContext ctx) {
		assertThat(ctx.getBean("field"))
				.isInstanceOf(ExampleService.class)
				.satisfies(MockitoAssertions::assertIsMock)
				.isSameAs(this.field)
				.isSameAs(this.renamed1);

		assertThat(this.field.greeting()).as("mocked greeting").isNull();
		assertThat(this.renamed1.greeting()).as("mocked greeting").isNull();
	}

	@Test
	void fieldIsMockedWhenNoOriginalBean(ApplicationContext ctx) {
		assertThat(ctx.getBean("nonExistingBean"))
				.isInstanceOf(ExampleService.class)
				.satisfies(MockitoAssertions::assertIsMock)
				.isSameAs(this.nonExisting1);

		assertThat(this.nonExisting1.greeting()).as("mocked greeting").isNull();
	}


	@Nested
	@DisplayName("With @MockitoBean in enclosing class")
	public class MockitoBeanNestedTests {

		@Test
		void fieldAndRenamedFieldHaveSameOverride(ApplicationContext ctx) {
			assertThat(ctx.getBean("nestedField"))
					.isInstanceOf(ExampleService.class)
					.satisfies(MockitoAssertions::assertIsMock)
					.isSameAs(nestedField)
					.isSameAs(renamed2);
		}

		@Test
		void fieldIsMockedWhenNoOriginalBean(ApplicationContext ctx) {
			assertThat(ctx.getBean("nestedNonExistingBean"))
					.isInstanceOf(ExampleService.class)
					.satisfies(MockitoAssertions::assertIsMock)
					.isSameAs(nonExisting2);
		}
	}


	@Configuration(proxyBeanMethods = false)
	static class Config {

		@Bean("field")
		ExampleService bean1() {
			return new RealExampleService("Hello Field");
		}

		@Bean("nestedField")
		ExampleService bean2() {
			return new RealExampleService("Hello Nested Field");
		}
	}

}
