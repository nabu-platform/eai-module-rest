/*
* Copyright (C) 2016 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package be.nabu.eai.module.rest;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import be.nabu.libs.http.HTTPException;
import be.nabu.libs.http.api.HTTPRequest;
import be.nabu.libs.types.TypeUtils;
import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.api.Element;
import be.nabu.libs.types.binding.api.MarshallableBinding;
import be.nabu.libs.types.binding.form.FormBinding;
import be.nabu.libs.types.binding.json.JSONBinding;
import be.nabu.libs.types.binding.xml.XMLBinding;
import be.nabu.libs.types.structure.Structure;
import be.nabu.utils.mime.impl.MimeUtils;

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
	
	public static String getContentTypeFor(MarshallableBinding binding) {
		if (binding instanceof JSONBinding) {
			return "application/json";
		}
		else if (binding instanceof XMLBinding) {
			return "application/xml";
		}
		else if (binding instanceof FormBinding) {
			return "application/x-www-form-urlencoded";
		}
		SPIBindingProvider bindingProvider = SPIBindingProvider.getInstance();
		return bindingProvider.getContentType(binding);
	}
	
	public static MarshallableBinding getOutputBinding(HTTPRequest request, ComplexType type, Charset charset, String preferredResponseType, boolean allowRawJson, boolean allowJsonRootArray) {
		SPIBindingProvider bindingProvider = SPIBindingProvider.getInstance();
		MarshallableBinding binding = request.getContent() == null ? null : bindingProvider.getMarshallableBinding(type, charset, request.getContent().getHeaders());
		if (binding == null) {
			List<String> acceptedContentTypes = request.getContent() != null
				? MimeUtils.getAcceptedContentTypes(request.getContent().getHeaders())
				: new ArrayList<String>();
			acceptedContentTypes.retainAll(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML));
			if (preferredResponseType == null) {
				preferredResponseType = WebResponseType.JSON.getMimeType();
			}
			if (acceptedContentTypes.isEmpty()) {
				acceptedContentTypes.add(preferredResponseType);
			}
			for (String contentType : acceptedContentTypes) {
				if (contentType.equalsIgnoreCase(WebResponseType.XML.getMimeType())) {
					// XML can't handle multiple roots, so we leave the wrapper in place in case we have a root array
					binding = new XMLBinding(type, charset);
				}
				else if (contentType.equalsIgnoreCase(WebResponseType.JSON.getMimeType())) {
					binding = new JSONBinding(type, charset);
					if (allowRawJson) {
						((JSONBinding) binding).setAllowRaw(true);
					}
					// JSON can handle root arrays, but we only want it explicitly in this scenario
					((JSONBinding) binding).setIgnoreRootIfArrayWrapper(allowJsonRootArray);
				}
				else if (contentType.equalsIgnoreCase(WebResponseType.FORM_ENCODED.getMimeType())) {
					binding = new FormBinding(type, charset);
				}
				else {
					throw new HTTPException(500, "Unsupported response content type: " + contentType);
				}
			}
		}
		return binding;
	}
}
