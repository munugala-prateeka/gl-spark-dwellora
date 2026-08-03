package com.dwellora.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "APARTMENT-SERVICE")
public interface ApartmentClient {

    @GetMapping("/apartments/{id}")
    Object getApartmentById(@PathVariable("id") Integer id);
}