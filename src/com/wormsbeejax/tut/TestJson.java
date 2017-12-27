package com.wormsbeejax.tut;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJson
{
	private static void treeJson()
	{
		//Create an ObjectMapper instance
		ObjectMapper mapper = new ObjectMapper();	
		String jsonString = "{\"name\":\"Mahesh Kumar\", \"age\":21,\"verified\":false,\"marks\":[100,90,85], \"Tel\":{\"Cellphone\":\"+1432094802342\"}}";

		//create tree from JSON
		try
		{
			JsonNode rootNode = mapper.readTree(jsonString);
			JsonNode cellphone = rootNode.path("Tel").path("Cellphone");
			System.out.println(cellphone.asText());
			
			System.out.println(rootNode.findValues("Cellphone"));
			System.out.println(rootNode.findParents("Cellphone"));
			System.out.println(rootNode.path("Cellphone").traverse().getCurrentName());
			System.out.println(rootNode.at("/Tel/Cellphone2").asText());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main (String []args)
	{
		TestJson.treeJson();
	}
}
