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
