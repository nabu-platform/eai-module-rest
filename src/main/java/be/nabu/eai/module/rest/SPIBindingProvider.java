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
import java.util.List;
import java.util.ServiceLoader;

import be.nabu.eai.module.rest.api.BindingProvider;
import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.binding.api.MarshallableBinding;
import be.nabu.libs.types.binding.api.UnmarshallableBinding;
import be.nabu.utils.mime.api.Header;

public class SPIBindingProvider implements BindingProvider {
	
	private static SPIBindingProvider instance = new SPIBindingProvider();
	
	public static SPIBindingProvider getInstance() {
		return instance;
	}
	
	private List<BindingProvider> providers;
	
	@Override
	public UnmarshallableBinding getUnmarshallableBinding(ComplexType type, Charset charset, Header... headers) {
		for (BindingProvider provider : getProviders()) {
			UnmarshallableBinding unmarshallableBinding = provider.getUnmarshallableBinding(type, charset, headers);
			if (unmarshallableBinding != null) {
				return unmarshallableBinding;
			}
		}
		return null;
	}

	@Override
	public MarshallableBinding getMarshallableBinding(ComplexType type, Charset charset, Header... headers) {
		for (BindingProvider provider : getProviders()) {
			MarshallableBinding marshallableBinding = provider.getMarshallableBinding(type, charset, headers);
			if (marshallableBinding != null) {
				return marshallableBinding;
			}
		}
		return null;
	}

	public List<BindingProvider> getProviders() {
		if (providers == null) {
			synchronized(this) {
				if (providers == null) {
					List<BindingProvider> providers = new ArrayList<BindingProvider>();
					for (BindingProvider provider : ServiceLoader.load(BindingProvider.class)) {
						providers.add(provider);
					}
					this.providers = providers;
				}
			}
		}
		return providers;
	}

	@Override
	public String getContentType(MarshallableBinding binding) {
		for (BindingProvider provider : getProviders()) {
			String contentType = provider.getContentType(binding);
			if (contentType != null) {
				return contentType;
			}
		}
		return null;
	}
}
