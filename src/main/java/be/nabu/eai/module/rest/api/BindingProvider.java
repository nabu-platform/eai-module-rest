package be.nabu.eai.module.rest.api;

import java.nio.charset.Charset;

import be.nabu.libs.types.api.ComplexType;
import be.nabu.libs.types.binding.api.MarshallableBinding;
import be.nabu.libs.types.binding.api.UnmarshallableBinding;
import be.nabu.utils.mime.api.Header;

public interface BindingProvider {
	public UnmarshallableBinding getUnmarshallableBinding(ComplexType type, Charset charset, Header...headers);
	public MarshallableBinding getMarshallableBinding(ComplexType type, Charset charset, Header...headers);
	public String getContentType(MarshallableBinding binding);
}
