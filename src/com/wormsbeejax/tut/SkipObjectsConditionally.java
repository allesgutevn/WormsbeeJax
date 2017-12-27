package com.wormsbeejax.tut;


import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

public class SkipObjectsConditionally
{

	@JsonIgnoreProperties("hidden")
	public static interface Hidable
	{
		boolean isHidden();
	}

	public static class Person implements Hidable
	{
		public String name;
		public Address address;
		public boolean hidden;

		public Person(String name, Address address, boolean hidden)
		{
			this.name = name;
			this.address = address;
			this.hidden = hidden;
		}

		@Override
		public boolean isHidden()
		{
			return hidden;
		}
	}

	public static class Address implements Hidable
	{
		public String city;
		public String country;
		public boolean hidden;

		public Address(String city, String country, boolean hidden)
		{
			this.city = city;
			this.country = country;
			this.hidden = hidden;
		}

		@Override
		public boolean isHidden()
		{
			return hidden;
		}
	}

	// Custom Serializer
	public static class HidableSerializer extends JsonSerializer<Hidable>
	{
		private JsonSerializer<Object> defaultSerializer;
		public HidableSerializer(JsonSerializer<Object> serializer)
		{
			defaultSerializer = serializer;
		}

		@Override
		public void serialize(Hidable value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException
		{
			if (value.isHidden())
			{
				return;
			}
			defaultSerializer.serialize(value, jgen, provider);
		}

		// overridden the method isEmpty() – to make sure that in case of Hidable object is a property, property name is also
		// excluded from JSON
		@Override
		public boolean isEmpty(SerializerProvider provider, Hidable value)
		{
			return (value == null || value.isHidden());
		}
	}

	public static void main(String[] args) throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.registerModule(new SimpleModule(){
			private static final long serialVersionUID = 1L;

			@Override
			public void setupModule(SetupContext context)
			{
				super.setupModule(context);
				// use BeanSerializerModifier to inject default serializer in our custom HidableSerializer 
				context.addBeanSerializerModifier(new BeanSerializerModifier()
				{
					@SuppressWarnings("unchecked")
					@Override
					public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription desc,
						JsonSerializer<?> serializer)
					{
						if (Hidable.class.isAssignableFrom(desc.getBeanClass()))
						{
							return new HidableSerializer((JsonSerializer<Object>) serializer);
						}
						return serializer;
					}
				});
			}
		});

		Address ad1 = new Address("tokyo", "jp", true);
		Address ad2 = new Address("london", "uk", false);
		Address ad3 = new Address("new york", "usa", false);
		
		Person p1 = new Person("john", ad1, false);
		Person p2 = new Person("tom", ad2, true);
		Person p3 = new Person("adam", ad3, false);

		System.out.println(mapper.writeValueAsString(Arrays.asList(p1, p2, p3)));
	}
}
