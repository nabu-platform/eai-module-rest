package be.nabu.eai.module.rest;

import be.nabu.libs.types.TypeUtils;
import be.nabu.libs.types.api.Element;
import be.nabu.libs.types.structure.Structure;

public class RESTUtils {

	public static Structure clean(Structure input) {
		for (Element<?> element : TypeUtils.getAllChildren(input)) {
			input.remove(element);
		}
		return input;
	}
	
	public static String headerToField(String headerName) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < headerName.length(); i++) {
			if (i == 0) {
				builder.append(headerName.substring(i, i + 1).toLowerCase());
			}
			else if (headerName.charAt(i) == '-') {
				builder.append(headerName.substring(i + 1, i + 2).toUpperCase());
				i++;
			}
			else {
				builder.append(headerName.substring(i, i + 1).toLowerCase());
			}
		}
		return builder.toString();
	}
	
	public static String fieldToHeader(String fieldName) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < fieldName.length(); i++) {
			if (i == 0) {
				builder.append(fieldName.substring(i, i + 1).toUpperCase());
			}
			else if (!fieldName.substring(i, i + 1).equals(fieldName.substring(i, i + 1).toLowerCase())) {
				builder.append("-").append(fieldName.substring(i, i + 1).toUpperCase());
			}
			else {
				builder.append(fieldName.substring(i, i + 1).toLowerCase());
			}
		}
		return builder.toString();
	}
}
