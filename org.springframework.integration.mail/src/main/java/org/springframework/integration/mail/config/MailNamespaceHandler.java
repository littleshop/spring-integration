/*
 * Copyright 2002-2008 the original author or authors.
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

package org.springframework.integration.mail.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.integration.mail.MailHeaders;
import org.springframework.integration.transformer.config.SimpleHeaderEnricherParser;

/**
 * Namespace handler for the 'mail' namespace.
 * 
 * @author Mark Fisher
 */
public class MailNamespaceHandler extends NamespaceHandlerSupport {

	public void init() {
		this.registerBeanDefinitionParser("outbound-channel-adapter", new MailOutboundChannelAdapterParser());
		this.registerBeanDefinitionParser("polling-mail-source", new PollingMailSourceParser());
		this.registerBeanDefinitionParser("imap-idle-mail-source", new SubscribableImapIdleMailSourceParser());
		this.registerBeanDefinitionParser("header-enricher", new SimpleHeaderEnricherParser(MailHeaders.TRANSPORT_PREFIX));
		this.registerBeanDefinitionParser("mail-to-string-transformer", new MailToStringTransformerParser());
	}

}
