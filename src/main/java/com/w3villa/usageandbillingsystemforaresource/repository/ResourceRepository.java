package com.w3villa.usageandbillingsystemforaresource.repository;

import com.w3villa.usageandbillingsystemforaresource.model.Resource;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ResourceRepository {
    private final Map<Integer, Resource> store = new LinkedHashMap<>();

//    Save and Update the Resources
    public Resource save(Resource resource) {
        store.put(resource.getResourceId(), resource);
        return resource;
    }
//    Find the Resource by Id
    public Optional<Resource> findById(Integer id) {
        return Optional.ofNullable(store.get(id));
    }
//    Find all Resource
    public List<Resource> findAll() {
        return new ArrayList<>(store.values());
    }
//    Check if Resource is Exist of not
    public boolean existsById(Integer id) {
        return store.containsKey(id);
    }
//    Delete Resource by Id
    public void deleteById(Integer id) {
        store.remove(id);
    }
//    clear all data
    public void clear() {
        store.clear();
    }
}
