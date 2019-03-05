/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package guru.bonacci.olingo.client;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEnumValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.commons.api.data.Property;

/**
 *
 */
public class Printer {

	public static void print(String content) {
		System.out.println(content);
	}

	public static void print(String content, List<?> list) {
		System.out.println(content);
		for (Object o : list) {
			System.out.println("    " + o);
		}
		System.out.println();
	}

	public static String prettyPrint(Map<String, Object> properties, int level) {
		StringBuilder b = new StringBuilder();
		Set<Entry<String, Object>> entries = properties.entrySet();

		for (Entry<String, Object> entry : entries) {
			intend(b, level);
			b.append(entry.getKey()).append(": ");
			Object value = entry.getValue();
			if (value instanceof Map) {
				value = prettyPrint((Map<String, Object>) value, level + 1);
			} else if (value instanceof Calendar) {
				Calendar cal = (Calendar) value;
				value = SimpleDateFormat.getInstance().format(cal.getTime());
			}
			b.append(value).append("\n");
		}
		// remove last line break
		b.deleteCharAt(b.length() - 1);
		return b.toString();
	}

	public static String prettyPrint(Collection<ClientProperty> properties, int level) {
		StringBuilder b = new StringBuilder();

		for (ClientProperty entry : properties) {
			intend(b, level);
			ClientValue value = entry.getValue();
			if (value.isCollection()) {
				ClientCollectionValue cclvalue = value.asCollection();
				b.append(prettyPrint(cclvalue.asJavaCollection(), level + 1));
			} else if (value.isComplex()) {
				ClientComplexValue cpxvalue = value.asComplex();
				b.append(prettyPrint(cpxvalue.asJavaMap(), level + 1));
			} else if (value.isEnum()) {
				ClientEnumValue cnmvalue = value.asEnum();
				b.append(entry.getName()).append(": ");
				b.append(cnmvalue.getValue()).append("\n");
			} else if (value.isPrimitive()) {
				b.append(entry.getName()).append(": ");
				b.append(entry.getValue()).append("\n");
			}
		}
		System.out.println(b.toString());
		return b.toString();
	}

	public static void intend(StringBuilder builder, int intendLevel) {
		for (int i = 0; i < intendLevel; i++) {
			builder.append("  ");
		}
	}
}
