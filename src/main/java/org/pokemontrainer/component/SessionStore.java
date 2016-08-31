package org.pokemontrainer.component;

import com.pokegoapi.api.PokemonGo;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * Created by Dias Nurul Arifin on 7/26/16.
 * Copyright (c) 2016 Temansoft. All rights reserved.
 */

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionStore {

    private PokemonGo pokemonGo;

    public SessionStore() {
    }

    public SessionStore(PokemonGo pokemonGo) {
        this.pokemonGo = pokemonGo;
    }

    public PokemonGo getPokemonGo() {
        return pokemonGo;
    }

    public void setPokemonGo(PokemonGo pokemonGo) {
        this.pokemonGo = pokemonGo;
    }
}
