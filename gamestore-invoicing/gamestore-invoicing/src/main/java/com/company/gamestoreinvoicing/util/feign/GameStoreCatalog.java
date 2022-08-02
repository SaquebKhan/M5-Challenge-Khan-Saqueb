package com.company.gamestoreinvoicing.util.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "gamestore-catalog")
public interface GameStoreCatalog {

    @GetMapping(value = "/game")
    public String getAllGames();

    @GetMapping(value = "/game/{id}")
    public com.company.gamestoreinvoicing.model.Game getGameById(@PathVariable("id") Long gameId);

    @GetMapping(value = "/console/{id}")
    public com.company.gamestoreinvoicing.model.Console getConsoleById(@PathVariable("id") Long consoleId);

    @GetMapping(value = "/tshirt/{id}")
    public com.company.gamestoreinvoicing.model.TShirt getTShirtById(@PathVariable("id") Long tshirtId);

}