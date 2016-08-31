package org.pokemontrainer.model;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.util.PokeNames;

import java.util.List;
import java.util.Locale;

/**
 * Created by Dias Nurul Arifin on 8/1/16.
 * Copyright (c) 2016 Temansoft. All rights reserved.
 */

public class PokemonData {

    public List<Pokemon> pokemons;

    public PokemonData(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }

    public static String getOwnerName(int pokedexNum){
        return PokeNames.getDisplayName(pokedexNum, Locale.ENGLISH);
    }

}
