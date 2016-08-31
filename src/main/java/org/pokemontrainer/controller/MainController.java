package org.pokemontrainer.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.RemoteServerException;
import okhttp3.OkHttpClient;
import org.pokemontrainer.component.SessionStore;
import org.pokemontrainer.model.PokemonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

/**
 * Created by Dias Nurul Arifin on 7/26/16.
 * Copyright (c) 2016 Temansoft. All rights reserved.
 */

@Controller
public class MainController {

    @Autowired
    SessionStore sessionStore;

    @RequestMapping("/")
    String home() {
        return "index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    String login(@RequestParam(name = "login") String username, @RequestParam(name = "password") String password) {
        OkHttpClient httpClient = new OkHttpClient();
        try {
            sessionStore.setPokemonGo(new PokemonGo(new PtcCredentialProvider(httpClient, username, password), httpClient));
        } catch (LoginFailedException e) {
            e.printStackTrace();
            return "redirect:/";
        } catch (RemoteServerException e) {
            e.printStackTrace();
            return "redirect:/";
        }
        return "redirect:/panel";
    }

    @RequestMapping(value = "/google", method = RequestMethod.GET)
    String google(Model model) throws GeneralSecurityException, IOException, LoginFailedException, RemoteServerException {
        OkHttpClient http = new OkHttpClient();
        GoogleUserCredentialProvider provider = new GoogleUserCredentialProvider(http);
        model.addAttribute("url", provider.LOGIN_URL);
        return "/gauth_step2";
    }

    @RequestMapping(value = "/gauth", method = RequestMethod.POST)
    String gauth(@RequestParam(value = "code") String code) throws LoginFailedException, RemoteServerException {
        OkHttpClient http = new OkHttpClient();
        GoogleUserCredentialProvider provider = new GoogleUserCredentialProvider(http);
        provider.login(code);
        try {
            PokemonGo go = new PokemonGo(new GoogleUserCredentialProvider(http, provider.getRefreshToken()), http);
            sessionStore.setPokemonGo(go);
        } catch (LoginFailedException e) {
            e.printStackTrace();
            return "redirect:/?login_fail";
        } catch (RemoteServerException e) {
            e.printStackTrace();
            return "redirect:/?remote_fail";
        }
        return "redirect:/panel";
    }

//    @ResponseBody
//    @RequestMapping(value = "/google", method = RequestMethod.GET)
//    String google(@RequestParam("code") String code) throws GeneralSecurityException, IOException {
//        GoogleClientSecrets clientSecrets =
//                GoogleClientSecrets.load(
//                        JacksonFactory.getDefaultInstance(), new FileReader("/Users/rin/Labs/infovesta/PokemonTrainer/src/main/java/org/pokemontrainer/controller/client_secrets.json"));
//        GoogleTokenResponse tokenResponse =
//                new GoogleAuthorizationCodeTokenRequest(
//                        new NetHttpTransport(),
//                        JacksonFactory.getDefaultInstance(),
//                        "https://www.googleapis.com/oauth2/v4/token",
//                        clientSecrets.getDetails().getClientId(),
//                        clientSecrets.getDetails().getClientSecret(),
//                        code,
//                        "http://www.stevetest.com:8080/googletoken")
//                        .execute();
//
//        return tokenResponse.getRefreshToken();
//    }
//
//    @ResponseBody
//    @RequestMapping(method = RequestMethod.GET, value = "/googletoken")
//    String googleToken(@RequestParam Map<String,String> params) {
//        OkHttpClient httpClient = new OkHttpClient();
//        try {
//            PokemonGo go = new PokemonGo(new GoogleUserCredentialProvider(httpClient, token), httpClient);
//            sessionStore.setPokemonGo(go);
//        } catch (LoginFailedException e) {
//            e.printStackTrace();
//            return "redirect:/?login_fail";
//        } catch (RemoteServerException e) {
//            e.printStackTrace();
//            return "redirect:/?remote_fail";
//        }
//        return params.toString();
//    }

    @RequestMapping(value = "/panel", method = RequestMethod.GET)
    String panel(Model model) throws LoginFailedException, RemoteServerException {
        model.addAttribute("playerdata", sessionStore.getPokemonGo().getPlayerProfile());
        model.addAttribute("pokedata", new PokemonData(sessionStore.getPokemonGo().getInventories().getPokebank().getPokemons()));
        return "panel";
    }

    @RequestMapping(value = "logout")
    String logout() {
        sessionStore.setPokemonGo(null);
        return "redirect:/";
    }
}
