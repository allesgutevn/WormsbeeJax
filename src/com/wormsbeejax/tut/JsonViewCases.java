package com.wormsbeejax.tut;


import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.SerializerFactory;

/**
 * http://www.baeldung.com/jackson-json-view-annotation
 * 
 * @author Thanh.Lam
 *
 */
public class JsonViewCases
{
	public static class Views
	{
		public static class Public
		{
		}

		public static class Internal extends Public
		{
		}
	}

	public static class User
	{
		// @JsonView(Views.Internal.class)
		public int id;

		@JsonView(Views.Public.class)
		public String name;

		public User(int id, String name)
		{
			this.id = id;
			this.name = name;
		}

		public User()
		{

		}

		@Override
		public String toString()
		{
			return "User [id=" + id + ", name=" + name + "]";
		}

	}

	public static class Item
	{

		@JsonView(Views.Public.class)
		public int id;

		@JsonView(Views.Public.class)
		public String itemName;

		@JsonView(Views.Internal.class)
		public String ownerName;

		public Item(int id, String itemName, String ownerName)
		{
			super();
			this.id = id;
			this.itemName = itemName;
			this.ownerName = ownerName;
		}
	}

	public static class UpperCasingWriter extends BeanPropertyWriter
	{
		BeanPropertyWriter _writer;

		public UpperCasingWriter(BeanPropertyWriter w)
		{
			super(w);
			_writer = w;
		}

		@Override
		public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception
		{
			String value = ((User) bean).name;
			value = (value == null) ? "" : value.toUpperCase();
			gen.writeStringField("name", value);
		}
	}

	public class MyBeanSerializerModifier extends BeanSerializerModifier
	{

		@Override
		public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
			List<BeanPropertyWriter> beanProperties)
		{
			for (int i = 0; i < beanProperties.size(); i++)
			{
				BeanPropertyWriter writer = beanProperties.get(i);
				if (writer.getName() == "name")
				{
					beanProperties.set(i, new UpperCasingWriter(writer));
				}
			}
			return beanProperties;
		}
	}

	public void whenUseJsonViewToSerialize_thenCorrect() throws JsonProcessingException
	{

		User user = new User(1, "John");

		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);

		String result = mapper.writerWithView(Views.Public.class).writeValueAsString(user);

		System.out.println("result: " + result);
	}

	public void whenUsePublicView_thenOnlyPublicSerialized() throws JsonProcessingException
	{

		Item item = new Item(2, "book", "John");

		ObjectMapper mapper = new ObjectMapper();
		String result = mapper.writerWithView(Views.Public.class).writeValueAsString(item);

		System.out.println("result: " + result);
	}

	public void whenUseInternalView_thenAllSerialized() throws JsonProcessingException
	{

		Item item = new Item(2, "book", "John");

		ObjectMapper mapper = new ObjectMapper();
		String result = mapper.writerWithView(Views.Internal.class).writeValueAsString(item);

		System.out.println("result: " + result);
	}

	public void whenUseJsonViewToDeserialize_thenCorrect() throws IOException
	{
		String json = "{\"id\":1,\"name\":\"John\"}";

		ObjectMapper mapper = new ObjectMapper();
		User user = mapper.readerWithView(Views.Public.class).forType(User.class).readValue(json);

		System.out.println("user: " + user);
	}
	
	public void whenUseCustomJsonViewToSerialize_thenCorrect() throws JsonProcessingException
	{
		User user = new User(1, "John Carther ii");
		SerializerFactory serializerFactory =
				BeanSerializerFactory.instance.withSerializerModifier(new MyBeanSerializerModifier());

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setSerializerFactory(serializerFactory);

		String result = mapper.writerWithView(Views.Public.class).writeValueAsString(user);

		System.out.println("result: " + result);
	}

	public static void main(String[] args) throws IOException
	{
		JsonViewCases jvc = new JsonViewCases();
		
		jvc.whenUseJsonViewToSerialize_thenCorrect();

		jvc.whenUsePublicView_thenOnlyPublicSerialized();

		jvc.whenUseInternalView_thenAllSerialized();

		jvc.whenUseJsonViewToDeserialize_thenCorrect();

		jvc.whenUseCustomJsonViewToSerialize_thenCorrect();
	}
}
