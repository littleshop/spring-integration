/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.json;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.integration.mapping.support.JsonHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.support.json.JacksonJsonObjectMapper;
import org.springframework.integration.support.json.JacksonJsonObjectMapperProvider;
import org.springframework.integration.support.json.JsonObjectMapper;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Transformer implementation that converts a JSON string payload into an instance of the provided target Class.
 * By default this transformer uses {@linkplain JacksonJsonObjectMapperProvider} factory
 * to get an instance of Jackson 1 or Jackson 2 JSON-processor {@linkplain JsonObjectMapper} implementation
 * depending on the jackson-databind or jackson-mapper-asl libs on the classpath.
 * Any other {@linkplain JsonObjectMapper} implementation can be provided.
 *
 * @author Mark Fisher
 * @author Artem Bilan
 * @see JsonObjectMapper
 * @see JacksonJsonObjectMapperProvider
 * @since 2.0
 */
public class JsonToObjectTransformer extends AbstractTransformer implements BeanClassLoaderAware {

	private final Class<?> targetClass;

	private final JsonObjectMapper<?> jsonObjectMapper;

	public JsonToObjectTransformer() {
		this((Class<?>) null);
	}

	public JsonToObjectTransformer(Class<?> targetClass) {
		this(targetClass, null);
	}

	/**
	 * Backward compatibility - allows existing configurations using Jackson 1.x to inject
	 * an ObjectMapper directly.
	 * @deprecated in favor of {@link #JsonToObjectTransformer(Class, JsonObjectMapper)}
	 */
	@Deprecated
	public JsonToObjectTransformer(Class<?> targetClass, Object objectMapper) throws ClassNotFoundException {
		this.targetClass = targetClass;
		if (objectMapper != null) {
			try {
				Class<?> objectMapperClass = ClassUtils.forName("org.codehaus.jackson.map.ObjectMapper", ClassUtils.getDefaultClassLoader());
				Assert.isTrue(objectMapperClass.isAssignableFrom(objectMapper.getClass()));
				this.jsonObjectMapper = new JacksonJsonObjectMapper((org.codehaus.jackson.map.ObjectMapper) objectMapper);
			}
			catch (ClassNotFoundException e) {
				throw new IllegalArgumentException(e);
			}
		}
		else {
			this.jsonObjectMapper = JacksonJsonObjectMapperProvider.newInstance();
		}
	}

	public JsonToObjectTransformer(JsonObjectMapper<?> jsonObjectMapper) {
		this(null, jsonObjectMapper);
	}

	public JsonToObjectTransformer(Class<?> targetClass, JsonObjectMapper<?> jsonObjectMapper) {
		this.targetClass = targetClass;
		this.jsonObjectMapper = (jsonObjectMapper != null) ? jsonObjectMapper : JacksonJsonObjectMapperProvider.newInstance();
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		if (this.jsonObjectMapper instanceof BeanClassLoaderAware) {
			((BeanClassLoaderAware) this.jsonObjectMapper).setBeanClassLoader(classLoader);
		}
	}

	@Override
	protected Object doTransform(Message<?> message) throws Exception {
		if (this.targetClass != null) {
			return this.jsonObjectMapper.fromJson(message.getPayload(), this.targetClass);
		}
		else {
			Object result = this.jsonObjectMapper.fromJson(message.getPayload(), message.getHeaders());
			MessageBuilder<Object> messageBuilder = MessageBuilder.withPayload(result)
					.copyHeaders(message.getHeaders())
					.removeHeaders(JsonHeaders.HEADERS.toArray(new String[3]));
			return messageBuilder.build();
		}
	}

}
