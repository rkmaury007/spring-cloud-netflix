/*
 * Copyright 2013-2019 the original author or authors.
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

package org.springframework.cloud.netflix.zuul;

import org.junit.Test;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.netflix.zuul.filters.route.RestClientRibbonCommandFactory;
import org.springframework.cloud.netflix.zuul.filters.route.apache.HttpClientRibbonCommandFactory;
import org.springframework.cloud.netflix.zuul.filters.route.okhttp.OkHttpRibbonCommandFactory;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Spencer Gibb
 * @author Biju Kunjummen
 */
public class ZuulProxyConfigurationTests {

	@Test
	public void testDefaultsToApacheHttpClient() {
		testClient(HttpClientRibbonCommandFactory.class, null);
		testClient(HttpClientRibbonCommandFactory.class,
				"ribbon.httpclient.enabled=true");
	}

	@Test
	public void testEnableRestClient() {
		testClient(RestClientRibbonCommandFactory.class,
				"ribbon.restclient.enabled=true");
	}

	@Test
	public void testEnableOkHttpClient() {
		testClient(OkHttpRibbonCommandFactory.class, "ribbon.okhttp.enabled=true");
	}

	void testClient(Class<?> clientType, String property) {
		if (property == null) {
			property = "aaa=bbb";
		}
		new WebApplicationContextRunner().withUserConfiguration(TestConfig.class)
				.withPropertyValues(property)
				.run(c -> assertThat(c).hasSingleBean(clientType));
	}

	@SpringBootConfiguration
	@EnableAutoConfiguration
	@EnableZuulProxy
	static class TestConfig {

		@Bean
		SpringClientFactory springClientFactory() {
			return mock(SpringClientFactory.class);
		}

		@Bean
		DiscoveryClient discoveryClient() {
			return mock(DiscoveryClient.class);
		}

	}

}
