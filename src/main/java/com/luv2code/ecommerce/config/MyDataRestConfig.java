package com.luv2code.ecommerce.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;

import com.luv2code.ecommerce.entity.Country;
import com.luv2code.ecommerce.entity.Order;
import com.luv2code.ecommerce.entity.Product;
import com.luv2code.ecommerce.entity.ProductCategory;
import com.luv2code.ecommerce.entity.State;

// import org.hibernate.mapping.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer {

    @Value("${allowed.origins}")
    private String[] theAllowedOrigins;

    private EntityManager entitymanager;

    @Autowired
    public MyDataRestConfig(EntityManager theEntityManager) {
        entitymanager = theEntityManager;

    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {

        HttpMethod[] theUnsuportedActions = { HttpMethod.PUT, HttpMethod.POST, HttpMethod.DELETE, HttpMethod.PATCH };

        disableHttpMethods(Product.class, config, theUnsuportedActions);
        disableHttpMethods(ProductCategory.class, config, theUnsuportedActions);
        disableHttpMethods(Country.class, config, theUnsuportedActions);
        disableHttpMethods(State.class, config, theUnsuportedActions);
        disableHttpMethods(Order.class, config, theUnsuportedActions);

        // call an internal helper method
        exposeIds(config);

        // configure cors mapping
        cors.addMapping(config.getBasePath() + "/**").allowedOrigins(theAllowedOrigins);
    }

    private void disableHttpMethods(Class<?> theClass, RepositoryRestConfiguration config,
            HttpMethod[] theUnsuportedActions) {
        // disable HTTP methods for ProductCategory: PUT, POST and DELETE
        config.getExposureConfiguration().forDomainType(theClass)
                .withItemExposure((metdata, httpmethods) -> httpmethods.disable(theUnsuportedActions))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(theUnsuportedActions));
    }

    private void exposeIds(RepositoryRestConfiguration config) {
        // expose entity ids
        //

        // - get a list o fall entity classes from the entity manager
        Set<EntityType<?>> entities = entitymanager.getMetamodel().getEntities();

        // - create an array of the entity types
        List<Class<?>> entityClasses = new ArrayList<>();

        // - get the entity types for the entities
        for (EntityType<?> tempEntityType : entities) {
            entityClasses.add(tempEntityType.getJavaType());
        }

        // - expose the entity ids for the array of entity/domain types
        Class<?>[] domainTypes = entityClasses.toArray(new Class[0]);
        config.exposeIdsFor(domainTypes);

    }

}
