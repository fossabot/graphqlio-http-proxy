package com.graphqlio.server.http.proxy.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.graphqlio.server.http.proxy.resolver.ProxyRootSubscriptionResolver;

@Service
public class GraphqlioHttpProxyService {
	
	@Autowired
	ProxyRootSubscriptionResolver subscriptionResolver;
	
	
	GraphQLResolver<Void> resolver = subscriptionResolver;
		
	
	public List<GraphQLResolver<Void>> getResolvers() {
		List<GraphQLResolver<Void>> resolvers = new ArrayList<>();		
		resolvers.add(subscriptionResolver);
		return resolvers;
	}
	
}
