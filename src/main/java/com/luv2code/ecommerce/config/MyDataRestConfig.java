package com.luv2code.ecommerce.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;

import com.luv2code.ecommerce.entity.Product;
import com.luv2code.ecommerce.entity.ProductCategory;

// import org.hibernate.mapping.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;

@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer {

    private EntityManager entitymanager;

    @Autowired
    public MyDataRestConfig(EntityManager theEntityManager) {
        entitymanager = theEntityManager;

    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {

        HttpMethod[] theUnsuportedActions = { HttpMethod.PUT, HttpMethod.POST, HttpMethod.DELETE };

        // disable HTTP methods for Product: PUT, POST and DELETE
        config.getExposureConfiguration().forDomainType(Product.class)
                .withItemExposure((metdata, httpmethods) -> httpmethods.disable(theUnsuportedActions))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(theUnsuportedActions));

        // disable HTTP methods for ProductCategory: PUT, POST and DELETE
        config.getExposureConfiguration().forDomainType(ProductCategory.class)
                .withItemExposure((metdata, httpmethods) -> httpmethods.disable(theUnsuportedActions))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(theUnsuportedActions));

        // call an internal helper method
        exposeIds(config);
    }

    private void exposeIds(RepositoryRestConfiguration config) {
        // expose entity ids
        //

        // - get a list o fall entity classes from the entity manager
        Set<EntityType<?>> entities = entitymanager.getMetamodel().getEntities();

        // - create an array of the entity types
        List<Class> entityClasses = new ArrayList<>();

        // - get the entity types for the entities
        for (EntityType tempEntityType : entities) {
            entityClasses.add(tempEntityType.getJavaType());
        }

        // - expose the entity ids for the array of entity/domain types
        Class[] domainTypes = entityClasses.toArray(new Class[0]);
        config.exposeIdsFor(domainTypes);

    }

}
