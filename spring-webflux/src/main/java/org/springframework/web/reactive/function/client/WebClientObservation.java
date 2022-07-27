/*
 * Copyright 2002-2022 the original author or authors.
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

package org.springframework.web.reactive.function.client;

import io.micrometer.common.KeyValue;
import io.micrometer.common.docs.KeyName;
import io.micrometer.observation.Observation;
import io.micrometer.observation.docs.DocumentedObservation;

import org.springframework.web.client.RestTemplateObservation;

/**
 * Documented {@link io.micrometer.common.KeyValue KeyValues} for the HTTP client observations
 * with {@link WebClient}.
 * <p>This class is used by automated tools to document KeyValues attached to the HTTP client observations.
 * @author Brian Clozel
 * @since 6.0
 */
public enum WebClientObservation implements DocumentedObservation {

	/**
	 * Observation created for an {@link WebClient} HTTP exchange.
	 */
	HTTP_REQUEST {
		@Override
		public Class<? extends Observation.ObservationConvention<? extends Observation.Context>> getDefaultConvention() {
			return DefaultWebClientObservationConvention.class;
		}

		@Override
		public KeyName[] getLowCardinalityKeyNames() {
			return WebClientObservation.LowCardinalityKeyNames.values();
		}

		@Override
		public KeyName[] getHighCardinalityKeyNames() {
			return WebClientObservation.HighCardinalityKeyNames.values();
		}

	};

	public enum LowCardinalityKeyNames implements KeyName {

		/**
		 * Name of HTTP request method or {@code "None"} if the request could not be created.
		 */
		METHOD {
			@Override
			public String asString() {
				return "method";
			}

		},

		/**
		 * URI template used for HTTP request, or {@code ""} if none was provided.
		 */
		URI {
			@Override
			public String asString() {
				return "uri";
			}
		},

		/**
		 * HTTP response raw status code, or {@code "IO_ERROR"} in case of {@code IOException},
		 * or {@code "CLIENT_ERROR"} if no response was received.
		 */
		STATUS {
			@Override
			public String asString() {
				return "status";
			}
		},

		/**
		 * Name of the exception thrown during the exchange, or {@code "None"} if no exception happened.
		 */
		EXCEPTION {
			@Override
			public String asString() {
				return "exception";
			}
		},

		/**
		 * Outcome of the HTTP client exchange.
		 *
		 * @see Outcome
		 */
		OUTCOME {
			@Override
			public String asString() {
				return "outcome";
			}
		}

	}

	public enum HighCardinalityKeyNames implements KeyName {

		/**
		 * HTTP request URI.
		 */
		URI_EXPANDED {
			@Override
			public String asString() {
				return "uri.expanded";
			}
		},

		/**
		 * Client name derived from the request URI host.
		 */
		CLIENT_NAME {
			@Override
			public String asString() {
				return "client.name";
			}
		}

	}

	/**
	 * Outcome of the HTTP client exchange.
	 * @author Andy Wilkinson
	 */
	public enum Outcome {

		/**
		 * Outcome of the request was informational.
		 */
		INFORMATIONAL,

		/**
		 * Outcome of the request was success.
		 */
		SUCCESS,

		/**
		 * Outcome of the request was redirection.
		 */
		REDIRECTION,

		/**
		 * Outcome of the request was client error.
		 */
		CLIENT_ERROR,

		/**
		 * Outcome of the request was server error.
		 */
		SERVER_ERROR,

		/**
		 * Outcome of the request was unknown.
		 */
		UNKNOWN;

		private final KeyValue keyValue;

		Outcome() {
			this.keyValue = KeyValue.of(RestTemplateObservation.LowCardinalityKeyNames.OUTCOME.asString(), name());
		}

		/**
		 * Returns the {@code Outcome} as a {@link KeyValue} named {@code outcome}.
		 * @return the {@code outcome} {@code KeyValue}
		 */
		public KeyValue asKeyValue() {
			return this.keyValue;
		}

		/**
		 * Return the {@code Outcome} for the given HTTP {@code status} code.
		 * @param status the HTTP status code
		 * @return the matching Outcome
		 */
		public static Outcome forStatus(int status) {
			if (status >= 100 && status < 200) {
				return INFORMATIONAL;
			}
			else if (status >= 200 && status < 300) {
				return SUCCESS;
			}
			else if (status >= 300 && status < 400) {
				return REDIRECTION;
			}
			else if (status >= 400 && status < 500) {
				return CLIENT_ERROR;
			}
			else if (status >= 500 && status < 600) {
				return SERVER_ERROR;
			}
			return UNKNOWN;
		}

	}

}
