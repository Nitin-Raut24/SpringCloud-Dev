package com.example.graph;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@RestController
public class controller {

	@Autowired
	private EmployeeReposotory empRepo;

	@Value("classpath:employee.graphqls")
	Resource schemaResource;
	
	 GraphQL graphQl;
	
	 @PostConstruct
	public void loadSchema() throws IOException{
		File schemaFile=schemaResource.getFile();
		TypeDefinitionRegistry registry=new SchemaParser().parse(schemaFile);
	    RuntimeWiring wiring=buildWiring();	
	    GraphQLSchema graphQlSchema=new SchemaGenerator().makeExecutableSchema(registry, wiring);
	    graphQl=GraphQL.newGraphQL(graphQlSchema).build();
	}
	
	private RuntimeWiring buildWiring() {
		
		DataFetcher<Optional<Employee>> fetcher1=data->{
			return  empRepo.findById(data.getArgument("id"));
		};
		
		return RuntimeWiring.newRuntimeWiring().type("Query",typeWriting->typeWriting.dataFetcher("getEmployee", fetcher1)).build();
	}

	
	// graphql api
	@PostMapping("/get-emp")
	public  ResponseEntity<Object> getEmployee(@RequestBody  String query) {
		ExecutionResult result=graphQl.execute(query);
		return new ResponseEntity<Object>(result,HttpStatus.OK);
	}
	
	// save object rest api
	@PostMapping("/")
	public  ResponseEntity<Object> getEmployee(@RequestBody  Employee employee) {
		empRepo.save(employee);
		return new ResponseEntity<Object>(employee,HttpStatus.OK);
	}
	
	
}
